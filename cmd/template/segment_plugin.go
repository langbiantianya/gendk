package template

import (
	"archive/zip"
	"bytes"
	"io/fs"
	"strings"
	"text/template"
)

type SegmentPluginTemplateData struct {
	ProjectName  string
	PluginName   string
	PluginCName  string
	WebServerUrl string
}

func NewSegmentPluginTemplateData(projectName string, pluginName string, pluginCName string, webServerUrl string) SegmentPluginTemplateData {
	return SegmentPluginTemplateData{
		projectName,
		pluginCName,
		pluginName,
		webServerUrl,
	}
}

func (data SegmentPluginTemplateData) GenReadme() (string, error) {
	// 读取嵌入的模板文件
	buildBytes, err := distFS.ReadFile("assets/maven/segment-plugin-demo/README.md")
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

func (data SegmentPluginTemplateData) GenPom() (string, error) {
	// 读取嵌入的模板文件
	buildBytes, err := distFS.ReadFile("assets/maven/segment-plugin-demo/pom.xml")
	if err != nil {
		return "", err // 改为返回错误而非 panic
	}

	// 解析模板内容
	tpl, err := template.New("pom").Parse(string(buildBytes))
	if err != nil {
		return "", err
	}

	var result strings.Builder
	if err := tpl.Execute(&result, data); err != nil {
		return "", err
	}
	return result.String(), nil // 返回生成后的内容和错误
}

func (data SegmentPluginTemplateData) GenSegmentPluginDemoPlugin() (string, error) {
	// 读取嵌入的模板文件
	buildBytes, err := distFS.ReadFile("assets/maven/segment-plugin-demo/src/main/java/com/sensorsdata/horizon/segment/plugin/demo/SegmentPluginDemoPlugin.java")
	if err != nil {
		return "", err // 改为返回错误而非 panic
	}

	// 解析模板内容
	tpl, err := template.New("SegmentPluginDemoPlugin").Parse(string(buildBytes))
	if err != nil {
		return "", err
	}

	var result strings.Builder
	if err := tpl.Execute(&result, data); err != nil {
		return "", err
	}
	return result.String(), nil // 返回生成后的内容和错误
}

func (data SegmentPluginTemplateData) GenSensorsdataPlugin() (string, error) {
	// 读取嵌入的模板文件
	buildBytes, err := distFS.ReadFile("assets/maven/segment-plugin-demo/src/main/resources/sensorsdata-plugin.yml")
	if err != nil {
		return "", err // 改为返回错误而非 panic
	}

	// 解析模板内容
	tpl, err := template.New("SegmentPluginDemoPlugin").Parse(string(buildBytes))
	if err != nil {
		return "", err
	}

	var result strings.Builder
	if err := tpl.Execute(&result, data); err != nil {
		return "", err
	}
	return result.String(), nil // 返回生成后的内容和错误
}
func (data SegmentPluginTemplateData) GenIndex() (string, error) {
	// 读取嵌入的模板文件
	buildBytes, err := distFS.ReadFile("assets/maven/segment-plugin-demo/front-end/src/index.ts")
	if err != nil {
		return "", err // 改为返回错误而非 panic
	}

	// 解析模板内容
	tpl, err := template.New("index").Parse(string(buildBytes))
	if err != nil {
		return "", err
	}

	var result strings.Builder
	if err := tpl.Execute(&result, data); err != nil {
		return "", err
	}
	return result.String(), nil // 返回生成后的内容和错误
}

func (data SegmentPluginTemplateData) GenPackage() (string, error) {
	// 读取嵌入的模板文件
	buildBytes, err := distFS.ReadFile("assets/maven/segment-plugin-demo/front-end/package.json")
	if err != nil {
		return "", err // 改为返回错误而非 panic
	}

	// 解析模板内容
	tpl, err := template.New("package").Parse(string(buildBytes))
	if err != nil {
		return "", err
	}

	var result strings.Builder
	if err := tpl.Execute(&result, data); err != nil {
		return "", err
	}
	return result.String(), nil // 返回生成后的内容和错误
}

func (data SegmentPluginTemplateData) GenZip() ([]byte, error) {
	var buf bytes.Buffer
	zw := zip.NewWriter(&buf)
	defer zw.Close()

	err := fs.WalkDir(distFS, "assets/maven/segment-plugin-demo", func(path string, d fs.DirEntry, walkErr error) error {
		if walkErr != nil {
			return walkErr
		}

		// 计算相对路径（去除maven/segment-plugin-demo前缀）
		relPath := strings.TrimPrefix(path, "assets/maven/segment-plugin-demo/")
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
		if strings.Contains(relPath, "README.md") {
			strData, err := data.GenReadme()
			if err != nil {
				return err
			}
			fileData = []byte(strData)
		} else if strings.Contains(relPath, "pom.xml") {
			strData, err := data.GenPom()
			if err != nil {
				return err
			}
			fileData = []byte(strData)
		} else if strings.Contains(relPath, "SegmentPluginDemoPlugin.java") {
			strData, err := data.GenSegmentPluginDemoPlugin()
			if err != nil {
				return err
			}
			fileData = []byte(strData)
		} else if strings.Contains(relPath, "sensorsdata-plugin.yml") {
			strData, err := data.GenSensorsdataPlugin()
			if err != nil {
				return err
			}
			fileData = []byte(strData)
		} else if strings.Contains(relPath, "front-end/src/index.ts") {
			strData, err := data.GenIndex()
			if err != nil {
				return err
			}
			fileData = []byte(strData)
		} else if strings.Contains(relPath, "front-end/package.json") {
			strData, err := data.GenPackage()
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
