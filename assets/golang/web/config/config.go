package config

import (
	"fmt"
	"log"
	"os"
	"reflect"
	"runtime"

	"github.com/BurntSushi/toml"
	"github.com/gin-gonic/gin"
)

type Config struct {
	Application struct {
		Name        string
		Version     string
		Description string
	}
	Gin struct {
		Version string
	}
	Runtime struct {
		Version string
	}
	Server struct {
		Host string
		Port int
		Mode string
	}
}

var Conf Config

// LoadConfig 加载配置文件，先加载基础配置，再用环境配置覆盖（仅覆盖非空值）
// baseFile: 基础配置文件路径，如 "application.toml"
// envFile: 环境配置文件路径，如 "application-dev.toml"
// cfg: 配置结构体指针，用于接收解析结果
func loadConfig(baseFile, envFile string, cfg interface{}) error {
	// 检查配置结构体是否为指针
	if reflect.TypeOf(cfg).Kind() != reflect.Ptr {
		return fmt.Errorf("配置参数必须是指针类型")
	}

	// 1. 加载基础配置文件到目标结构体
	if _, err := toml.DecodeFile(baseFile, cfg); err != nil {
		if os.IsNotExist(err) {
			return fmt.Errorf("基础配置文件 %s 不存在: %v", baseFile, err)
		}
		return fmt.Errorf("解析基础配置文件失败: %v", err)
	}

	// 2. 检查环境配置文件是否存在
	if _, err := os.Stat(envFile); os.IsNotExist(err) {
		return nil // 环境配置文件不存在，直接返回基础配置
	}

	// 3. 创建临时结构体用于加载环境配置
	envCfgVal := reflect.New(reflect.TypeOf(cfg).Elem())
	if _, err := toml.DecodeFile(envFile, envCfgVal.Interface()); err != nil {
		return fmt.Errorf("解析环境配置文件 %s 失败: %v", envFile, err)
	}

	// 4. 合并环境配置到基础配置（仅覆盖非空值）
	if err := mergeNonEmptyValues(envCfgVal.Elem(), reflect.ValueOf(cfg).Elem()); err != nil {
		return fmt.Errorf("合并配置失败: %v", err)
	}

	return nil
}

// mergeNonEmptyValues 将源值（环境配置）合并到目标值（基础配置），仅覆盖非空值
func mergeNonEmptyValues(src, dest reflect.Value) error {
	// 确保两者类型一致
	if src.Type() != dest.Type() {
		return fmt.Errorf("类型不匹配: %s vs %s", src.Type(), dest.Type())
	}

	// 根据不同类型处理
	switch src.Kind() {
	case reflect.Struct:
		// 处理结构体
		for i := 0; i < src.NumField(); i++ {
			srcField := src.Field(i)
			destField := dest.Field(i)

			// 检查字段是否可设置
			if !destField.CanSet() {
				continue
			}

			// 递归合并字段
			if err := mergeNonEmptyValues(srcField, destField); err != nil {
				return err
			}
		}

	case reflect.Ptr:
		// 处理指针类型
		if src.IsNil() {
			return nil // 源指针为空，不覆盖
		}
		if dest.IsNil() {
			dest.Set(src) // 目标指针为空，直接设置
			return nil
		}
		// 两者都不为空，递归处理指向的值
		return mergeNonEmptyValues(src.Elem(), dest.Elem())

	case reflect.Slice, reflect.Array:
		// 处理切片和数组（非空则覆盖）
		if src.Len() > 0 {
			dest.Set(src)
		}

	case reflect.Map:
		// 处理映射
		if src.IsNil() {
			return nil
		}
		if dest.IsNil() {
			dest.Set(reflect.MakeMap(src.Type()))
		}
		// 遍历源映射的键值对
		for _, key := range src.MapKeys() {
			srcVal := src.MapIndex(key)
			destVal := dest.MapIndex(key)

			if destVal.IsValid() {
				// 键已存在，递归合并
				if err := mergeNonEmptyValues(srcVal, destVal); err != nil {
					return err
				}
			} else {
				// 键不存在，直接设置
				dest.SetMapIndex(key, srcVal)
			}
		}

	default:
		// 处理基本类型（检查是否为零值）
		if !isZeroValue(src) {
			dest.Set(src)
		}
	}

	return nil
}

// isZeroValue 判断值是否为其类型的零值
func isZeroValue(v reflect.Value) bool {
	return reflect.DeepEqual(v.Interface(), reflect.Zero(v.Type()).Interface())
}

func init() {
	profileActive := os.Getenv("PROFILE_ACTIVE")
	if profileActive != "" {
		profileActive = "-" + profileActive
	}
	profileActive = fmt.Sprintf("application%s.toml", profileActive)
	defer func() {
		log.SetOutput(os.Stdout)
		log.Default().Printf("activity profile : %s", profileActive)
		log.SetOutput(os.Stderr)
	}()
	err := loadConfig("application.toml", profileActive, &Conf)
	if err != nil {
		panic(err)
	}

	if Conf.Server.Host == "" {
		Conf.Server.Host = "0.0.0.0"
	}
	if Conf.Server.Port == 0 {
		Conf.Server.Port = 8080
	}
	if Conf.Server.Mode == "" {
		Conf.Server.Mode = gin.DebugMode
	}
	Conf.Gin.Version = gin.Version
	Conf.Runtime.Version = runtime.Version()
}
