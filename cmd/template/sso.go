package template

import (
	"archive/zip"
	"bytes"
	"fmt"
	"gendk/public/utils"
	"io/fs"
	"strings"
	"text/template"
)

type SSOTemplateData struct {
	SpringBootVersion int
	ProjectName       string
	JdkVersion        string
	JdkVersionNumber  int
	Idp               string
	Sp                string
	Pem               string
	EntityID          string
	RegistrationId    string
}

func NewSSOTemplateData(springBootVersion int, projectName, jdkVersion, idpXml, pemCA, entityID, endpoint, logoutEndpoint string) SSOTemplateData {
	// 生成sp
	sp := utils.GenerateEntityDescriptor(pemCA, entityID, endpoint, logoutEndpoint)
	spStr, _ := utils.EntityDescriptorToXML(sp)
	registrationId, _ := utils.GetLastPathSegment(endpoint)
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
	return SSOTemplateData{
		springBootVersion,
		projectName,
		jdkVersion,
		JdkVersionNumber,
		idpXml,
		spStr,
		pemCA,
		entityID,
		registrationId,
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
func (data SSOTemplateData) GenBuildKts() (string, error) {
	// 读取嵌入的模板文件
	buildBytes, err := distFS.ReadFile(fmt.Sprintf("assets/gradle/sso/%d/build.gradle.kts", data.SpringBootVersion))
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

func (data SSOTemplateData) GenApplicationProd() (string, error) {
	// 读取嵌入的模板文件
	buildBytes, err := distFS.ReadFile(fmt.Sprintf("assets/gradle/sso/%d/src/main/resources/application-prod.yml", data.SpringBootVersion))
	if err != nil {
		return "", err // 改为返回错误而非 panic
	}

	// 解析模板内容
	tpl, err := template.New("application-prod").Parse(string(buildBytes))
	if err != nil {
		return "", err
	}

	var result strings.Builder
	if err := tpl.Execute(&result, data); err != nil {
		return "", err
	}
	return result.String(), nil // 返回生成后的内容和错误
}

func (data SSOTemplateData) GenApplicationDev() (string, error) {
	// 读取嵌入的模板文件
	buildBytes, err := distFS.ReadFile(fmt.Sprintf("assets/gradle/sso/%d/src/main/resources/application-dev.yml", data.SpringBootVersion))
	if err != nil {
		return "", err // 改为返回错误而非 panic
	}

	// 解析模板内容
	tpl, err := template.New("application-dev").Parse(string(buildBytes))
	if err != nil {
		return "", err
	}

	var result strings.Builder
	if err := tpl.Execute(&result, data); err != nil {
		return "", err
	}
	return result.String(), nil // 返回生成后的内容和错误
}
func (data SSOTemplateData) GenSecurityConfiguration() (string, error) {

	// 读取嵌入的模板文件
	buildBytes, err := distFS.ReadFile(fmt.Sprintf("assets/gradle/sso/%d/src/main/java/com/sensorsdata/analytics/sso/config/SecurityConfiguration.java", data.SpringBootVersion))
	if err != nil {
		return "", err // 改为返回错误而非 panic
	}

	// 解析模板内容
	tpl, err := template.New("SecurityConfiguration").Parse(string(buildBytes))
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
		} else if strings.Contains(relPath, "application-dev.yml") {
			strData, err := data.GenApplicationDev()
			if err != nil {
				return err
			}
			fileData = []byte(strData)
		} else if strings.Contains(relPath, "application-prod.yml") {
			strData, err := data.GenApplicationProd()
			if err != nil {
				return err
			}
			fileData = []byte(strData)
		} else if strings.Contains(relPath, "SecurityConfiguration.java") {
			strData, err := data.GenSecurityConfiguration()
			if err != nil {
				return err
			}
			fileData = []byte(strData)
		} else if strings.Contains(relPath, "idp_metadata.xml") {
			fileData = []byte(data.Idp)
		} else if strings.Contains(relPath, "sp_metadata.xml") {
			fileData = []byte(data.Sp)
		} else if strings.Contains(relPath, "Sensor-Analytics.cer") {
			fileData = []byte(data.Pem)
		} else if strings.Contains(relPath, "build.gradle.kts") {
			strData, err := data.GenBuildKts()
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
