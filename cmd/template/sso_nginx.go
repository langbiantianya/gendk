package template

import (
	"archive/zip"
	"bytes"
	"io/fs"
	"strings"
	"text/template"
)

type SSONginxTemplateData struct {
	ProjectName string
	Redirect    string
}

func NewSSoNginxTemplateData(projectName string, redirect string) SSONginxTemplateData {
	return SSONginxTemplateData{
		projectName,
		redirect,
	}
}

func (data SSONginxTemplateData) GenReadme() (string, error) {
	// 读取嵌入的模板文件
	buildBytes, err := distFS.ReadFile("assets/nginx/sso/README.md")
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

func (data SSONginxTemplateData) GenConf() (string, error) {
	// 读取嵌入的模板文件
	buildBytes, err := distFS.ReadFile("assets/nginx/sso/cros_change_cookie_sso.conf")
	if err != nil {
		return "", err // 改为返回错误而非 panic
	}

	// 解析模板内容
	tpl, err := template.New("conf").Parse(string(buildBytes))
	if err != nil {
		return "", err
	}

	var result strings.Builder
	if err := tpl.Execute(&result, data); err != nil {
		return "", err
	}
	return result.String(), nil // 返回生成后的内容和错误

}

func (data SSONginxTemplateData) GenZip() ([]byte, error) {
	var buf bytes.Buffer
	zw := zip.NewWriter(&buf)
	defer zw.Close()

	// 遍历assets/gradle/sso/2目录下所有文件
	err := fs.WalkDir(distFS, "assets/nginx/sso", func(path string, d fs.DirEntry, walkErr error) error {
		if walkErr != nil {
			return walkErr
		}

		// 计算相对路径（去除assets/gradle/sso/2前缀）
		relPath := strings.TrimPrefix(path, "assets/nginx/sso/")
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
		if strings.Contains(relPath, "cros_change_cookie_sso.conf") {
			strData, err := data.GenConf()
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
