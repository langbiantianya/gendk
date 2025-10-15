package template

import (
	"archive/zip"
	"bytes"
	"fmt"
	"io/fs"
	"log"
	"strings"
	"text/template"
)

type WebTemplateData struct {
	SpringBootVersion int
	Libs              string
	ProjectName       string
	ModuleName        string
	JdkVersion        string
	JdkVersionNumber  int
	EnableNginx       bool
	Language          string
}

func NewWebTemplateData(
	springBootVersion int,
	libs string,
	projectName string,
	moduleName string,
	jdkVersion string,
	enableNginx bool,
	language string) WebTemplateData {
	var JdkVersionNumber int
	switch jdkVersion {
	case "JDK 1.8":
		jdkVersion = "VERSION_1_8"
		JdkVersionNumber = 8
	case "JDK 17":
		jdkVersion = "VERSION_17"
		JdkVersionNumber = 17
	default:
		jdkVersion = "VERSION_1_8"
		JdkVersionNumber = 8
	}

	return WebTemplateData{
		springBootVersion,
		libs,
		projectName,
		moduleName,
		jdkVersion,
		JdkVersionNumber,
		enableNginx,
		language,
	}

}

func (data WebTemplateData) GenReadme() (string, error) {
	// 读取嵌入的模板文件
	buildBytes, err := distFS.ReadFile(fmt.Sprintf("assets/gradle/web/%s/%d/README.md", data.Language, data.SpringBootVersion))
	if err != nil {
		return "", err // 改为返回错误而非 panic
	}

	// 解析模板内容
	tpl, err := template.New("readme").Parse(string(buildBytes))
	if err != nil {
		return "", err
	}

	var result strings.Builder
	if err := tpl.Execute(&result, data); err != nil {
		return "", err
	}
	return result.String(), nil // 返回生成后的内容和错误

}

func (data WebTemplateData) GenBuildKts() (string, error) {
	// 读取嵌入的模板文件
	buildBytes, err := distFS.ReadFile(fmt.Sprintf("assets/gradle/web/%s/%d/build.gradle.kts", data.Language, data.SpringBootVersion))
	if err != nil {
		return "", err // 改为返回错误而非 panic
	}

	// 解析模板内容
	tpl, err := template.New("buildGradle").Parse(string(buildBytes))
	if err != nil {
		return "", err
	}

	var result strings.Builder
	if err := tpl.Execute(&result, data); err != nil {
		return "", err
	}
	return result.String(), nil // 返回生成后的内容和错误

}

func (data WebTemplateData) GenSettingsKts() (string, error) {
	// 读取嵌入的模板文件
	buildBytes, err := distFS.ReadFile(fmt.Sprintf("assets/gradle/web/%s/%d/settings.gradle.kts", data.Language, data.SpringBootVersion))
	if err != nil {
		return "", err // 改为返回错误而非 panic
	}

	// 解析模板内容
	tpl, err := template.New("buildGradle").Parse(string(buildBytes))
	if err != nil {
		return "", err
	}

	var result strings.Builder
	if err := tpl.Execute(&result, data); err != nil {
		return "", err
	}
	return result.String(), nil // 返回生成后的内容和错误
}

func (data WebTemplateData) GenApplication() (string, error) {
	// 读取嵌入的模板文件
	buildBytes, err := distFS.ReadFile(fmt.Sprintf("assets/gradle/web/%s/%d/src/main/resources/application.yaml", data.Language, data.SpringBootVersion))
	if err != nil {
		return "", err // 改为返回错误而非 panic
	}

	// 解析模板内容
	tpl, err := template.New("application").Parse(string(buildBytes))
	if err != nil {
		return "", err
	}

	var result strings.Builder
	if err := tpl.Execute(&result, data); err != nil {
		return "", err
	}
	return result.String(), nil // 返回生成后的内容和错误
}

func (data WebTemplateData) GenRunSh() (string, error) {
	// 读取嵌入的模板文件
	buildBytes, err := distFS.ReadFile(fmt.Sprintf("assets/gradle/web/%s/%d/dingkai/moduleName/bin/run.sh", data.Language, data.SpringBootVersion))
	if err != nil {
		return "", err // 改为返回错误而非 panic
	}

	// 解析模板内容
	tpl, err := template.New("run").Parse(string(buildBytes))
	if err != nil {
		return "", err
	}

	var result strings.Builder
	if err := tpl.Execute(&result, data); err != nil {
		return "", err
	}
	return result.String(), nil // 返回生成后的内容和错误
}
func (data WebTemplateData) GenStartWebSh() (string, error) {
	// 读取嵌入的模板文件
	buildBytes, err := distFS.ReadFile(fmt.Sprintf("assets/gradle/web/%s/%d/dingkai/moduleName/bin/start_web.sh", data.Language, data.SpringBootVersion))
	if err != nil {
		return "", err // 改为返回错误而非 panic
	}

	// 解析模板内容
	tpl, err := template.New("run").Parse(string(buildBytes))
	if err != nil {
		return "", err
	}

	var result strings.Builder
	if err := tpl.Execute(&result, data); err != nil {
		return "", err
	}
	return result.String(), nil // 返回生成后的内容和错误
}
func (data WebTemplateData) GenBlueprint() (string, error) {
	// 读取嵌入的模板文件
	buildBytes, err := distFS.ReadFile(fmt.Sprintf("assets/gradle/web/%s/%d/dingkai/construction_blueprint/blueprint_2_1/declarative_desc/dk_product_component.yaml.j2", data.Language, data.SpringBootVersion))
	if err != nil {
		return "", err // 改为返回错误而非 panic
	}

	// 解析模板内容
	tpl, err := template.New("dk_product_component").Parse(string(buildBytes))
	if err != nil {
		return "", err
	}

	var result strings.Builder
	if err := tpl.Execute(&result, data); err != nil {
		return "", err
	}
	return result.String(), nil // 返回生成后的内容和错误
}

func (data WebTemplateData) GenNginxBackendJ2() (string, error) {
	// 读取嵌入的模板文件
	buildBytes, err := distFS.ReadFile(fmt.Sprintf("assets/gradle/web/%s/%d/dingkai/construction_blueprint/blueprint_2_1/data/template/nginx/dingkai_ProjectName-api-backend-conf.j2", data.Language, data.SpringBootVersion))
	if err != nil {
		return "", err // 改为返回错误而非 panic
	}

	// 解析模板内容
	tpl, err := template.New("dingkai_ProjectName-api-backend-conf").Parse(string(buildBytes))
	if err != nil {
		return "", err
	}

	var result strings.Builder
	if err := tpl.Execute(&result, data); err != nil {
		return "", err
	}
	return result.String(), nil // 返回生成后的内容和错误
}

func (data WebTemplateData) GenNginxJ2() (string, error) {
	// 读取嵌入的模板文件
	buildBytes, err := distFS.ReadFile(fmt.Sprintf("assets/gradle/web/%s/%d/dingkai/construction_blueprint/blueprint_2_1/data/template/nginx/dingkai_ProjectName-nginx-conf.j2", data.Language, data.SpringBootVersion))
	if err != nil {
		return "", err // 改为返回错误而非 panic
	}

	// 解析模板内容
	tpl, err := template.New("dingkai_ProjectName-nginx-conf").Parse(string(buildBytes))
	if err != nil {
		return "", err
	}

	var result strings.Builder
	if err := tpl.Execute(&result, data); err != nil {
		return "", err
	}
	return result.String(), nil // 返回生成后的内容和错误
}

func (data WebTemplateData) GenZip() ([]byte, error) {
	var buf bytes.Buffer
	zw := zip.NewWriter(&buf)
	defer zw.Close()

	// 遍历assets/gradle/web/%s/2目录下所有文件
	err := fs.WalkDir(distFS, fmt.Sprintf("assets/gradle/web/%s/%d", data.Language, data.SpringBootVersion), func(path string, d fs.DirEntry, walkErr error) error {
		if walkErr != nil {
			return walkErr
		}

		// 计算相对路径（去除assets/gradle/web/%s/2前缀）
		relPath := strings.TrimPrefix(path, fmt.Sprintf("assets/gradle/web/%s/%d/", data.Language, data.SpringBootVersion))
		if relPath == path { // 处理根目录情况
			relPath = ""
		}

		// 替换文件路径
		if strings.Contains(relPath, "moduleName") {
			relPath = strings.ReplaceAll(relPath, "moduleName", data.ModuleName)
		}

		if d.IsDir() {
			// 跳过根目录的创建（relPath为空时不创建）
			if relPath != "" {
				// 创建目录条目（zip目录需要以/结尾）
				_, err := zw.Create(relPath + "/")
				return err
			}
			return nil
		}

		// 判断文件是否需要替换
		var fileData []byte
		if strings.Contains(relPath, "build.gradle.kts") {
			strData, err := data.GenBuildKts()
			if err != nil {
				return err
			}
			fileData = []byte(strData)
		} else if strings.Contains(relPath, "settings.gradle.kts") {
			strData, err := data.GenSettingsKts()
			if err != nil {
				return err
			}
			fileData = []byte(strData)
		} else if strings.Contains(relPath, "application.yaml") {
			strData, err := data.GenApplication()
			if err != nil {
				return err
			}
			fileData = []byte(strData)
		} else if strings.Contains(relPath, "run.sh") {
			strData, err := data.GenRunSh()
			if err != nil {
				return err
			}
			fileData = []byte(strData)
		} else if strings.Contains(relPath, "start_web.sh") {
			strData, err := data.GenStartWebSh()
			if err != nil {
				return err
			}
			fileData = []byte(strData)
		} else if strings.Contains(relPath, "dk_product_component.yaml.j2") {
			strData, err := data.GenBlueprint()
			if err != nil {
				return err
			}
			fileData = []byte(strData)
		} else if strings.Contains(relPath, "README.md") {
			strData, err := data.GenReadme()
			if err != nil {
				return err
			}
			fileData = []byte(strData)
		} else if strings.Contains(relPath, "blueprint_2_1/data/template/nginx") {
			// 判断是否跳过nginx文件的生成
			if data.EnableNginx {
				if strings.Contains(relPath, "dingkai_ProjectName-api-backend-conf.j2") {
					strData, err := data.GenNginxBackendJ2()
					if err != nil {
						return err
					}
					fileData = []byte(strData)
				} else if strings.Contains(relPath, "dingkai_ProjectName-nginx-conf.j2") {
					strData, err := data.GenNginxJ2()
					if err != nil {
						return err
					}
					fileData = []byte(strData)
				} else {
					// 读取文件内容
					fileDataS, err := distFS.ReadFile(path)
					if err != nil {
						return err
					}
					fileData = fileDataS
				}
				relPath = strings.ReplaceAll(relPath, "ProjectName", data.ProjectName)
				log.Default().Println(relPath)
			} else {
				return nil
			}
		} else if strings.Contains(relPath, "gitignore") {
			fileDataS, err := distFS.ReadFile(path)
			if err != nil {
				return err
			}
			fileData = fileDataS
			relPath = strings.ReplaceAll(relPath, "gitignore", ".gitignore")
		} else {
			// 读取文件内容
			fileDataS, err := distFS.ReadFile(path)
			if err != nil {
				return err
			}
			fileData = fileDataS
		}

		info, err := d.Info()
		if err != nil {
			return err
		}
		// 创建文件头
		fh, err := zip.FileInfoHeader(info)
		if err != nil {
			return err
		}
		fh.Name = relPath       // 设置文件在zip中的路径
		fh.Method = zip.Deflate // 使用默认压缩算法

		// 写入文件内容
		fileWriter, err := zw.CreateHeader(fh)
		if err != nil {
			return err
		}
		_, err = fileWriter.Write(fileData)
		return err
	})

	if err != nil {
		return nil, err
	}

	// 确保所有数据写入buffer
	if err := zw.Close(); err != nil {
		return nil, err
	}

	return buf.Bytes(), nil
}
