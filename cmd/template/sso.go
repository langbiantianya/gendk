package template

import (
	"archive/zip"
	"bytes"
	"fmt"
	"io/fs"
	"strings"
	"text/template"
)

type SSOTemplateData struct {
	SpringBootVersion int
	ProjectName       string
	JdkVersion        string
}

func NewSSOTemplateData(SpringBootVersion int, ProjectName string, JdkVersion string) SSOTemplateData {
	switch JdkVersion {
	case "JDK 1.8":
		JdkVersion = "VERSION_1_8"
		break
	case "JDK 17":
		JdkVersion = "VERSION_17"
		break
	default:
		JdkVersion = "VERSION_1_8"
	}
	return SSOTemplateData{
		SpringBootVersion,
		ProjectName,
		JdkVersion,
	}
}

func (data SSOTemplateData) GenReadme() (string, error) {
	// 读取嵌入的模板文件
	buildBytes, err := distFS.ReadFile(fmt.Sprintf("assets/gradle/sso/%d/README.md", data.SpringBootVersion))
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

func (data SSOTemplateData) GenSettingsKts() (string, error) {
	// 读取嵌入的模板文件
	buildBytes, err := distFS.ReadFile(fmt.Sprintf("assets/gradle/sso/%d/settings.gradle.kts", data.SpringBootVersion))
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

func (data SSOTemplateData) GenBlueprint() (string, error) {
	// 读取嵌入的模板文件
	buildBytes, err := distFS.ReadFile(fmt.Sprintf("assets/gradle/sso/%d/dingkai/construction_blueprint/blueprint_2_1/declarative_desc/dk_product_component.yaml.j2", data.SpringBootVersion))
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

func (data SSOTemplateData) GenZip() ([]byte, error) {
	var buf bytes.Buffer
	zw := zip.NewWriter(&buf)
	defer zw.Close()

	// 遍历assets/gradle/sso/2目录下所有文件
	err := fs.WalkDir(distFS, fmt.Sprintf("assets/gradle/sso/%d", data.SpringBootVersion), func(path string, d fs.DirEntry, walkErr error) error {
		if walkErr != nil {
			return walkErr
		}

		// 计算相对路径（去除assets/gradle/sso/2前缀）
		relPath := strings.TrimPrefix(path, fmt.Sprintf("assets/gradle/sso/%d/", data.SpringBootVersion))
		if relPath == path { // 处理根目录情况
			relPath = ""
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
		if strings.Contains(relPath, "settings.gradle.kts") {
			strData, err := data.GenSettingsKts()
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
