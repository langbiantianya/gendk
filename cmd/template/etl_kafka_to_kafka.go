package template

import (
	"archive/zip"
	"bytes"
	"io/fs"
	"log"
	"strings"
	"text/template"
)

type EtlKafka2KafkaTemplateData struct {
	ProjectName string
}

func NewEtlKafka2KafkaTemplateData(projectName string) EtlKafka2KafkaTemplateData {
	return EtlKafka2KafkaTemplateData{
		ProjectName: projectName,
	}
}

func (data EtlKafka2KafkaTemplateData) GenReadme() (string, error) {
	// 读取嵌入的模板文件
	buildBytes, err := distFS.ReadFile("assets/maven/etl/sdd/kafka_to_kafka/README.md")
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

func (data EtlKafka2KafkaTemplateData) GenBlueprint() (string, error) {
	// 读取嵌入的模板文件
	buildBytes, err := distFS.ReadFile("assets/maven/etl/sdd/kafka_to_kafka/app/stream_import.yml")
	if err != nil {
		return "", err // 改为返回错误而非 panic
	}

	// 解析模板内容
	tpl, err := template.New("stream_import").Parse(string(buildBytes))
	if err != nil {
		return "", err
	}

	var result strings.Builder
	if err := tpl.Execute(&result, data); err != nil {
		return "", err
	}
	return result.String(), nil // 返回生成后的内容和错误
}

func (data EtlKafka2KafkaTemplateData) GenRunSh() (string, error) {
	// 读取嵌入的模板文件
	buildBytes, err := distFS.ReadFile("assets/maven/etl/sdd/kafka_to_kafka/app/bin/run.sh")
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

func (data EtlKafka2KafkaTemplateData) GenPom() (string, error) {
	// 读取嵌入的模板文件
	buildBytes, err := distFS.ReadFile("assets/maven/etl/sdd/kafka_to_kafka/pom.xml")
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
func (data EtlKafka2KafkaTemplateData) GenCustomPom() (string, error) {
	// 读取嵌入的模板文件
	buildBytes, err := distFS.ReadFile("assets/maven/etl/sdd/kafka_to_kafka/custom/pom.xml")
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
func (data EtlKafka2KafkaTemplateData) GenCommonPom() (string, error) {
	// 读取嵌入的模板文件
	buildBytes, err := distFS.ReadFile("assets/maven/etl/sdd/kafka_to_kafka/common/pom.xml")
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

func (data EtlKafka2KafkaTemplateData) GenZip() ([]byte, error) {
	var buf bytes.Buffer
	zw := zip.NewWriter(&buf)
	defer zw.Close()

	// 遍历assets/maven/etl/sdd/kafka_to_kafka目录下所有文件
	err := fs.WalkDir(distFS, "assets/maven/etl/sdd/kafka_to_kafka", func(path string, d fs.DirEntry, walkErr error) error {
		if walkErr != nil {
			return walkErr
		}

		// 计算相对路径（去除assets/gradle/web/2前缀）
		relPath := strings.TrimPrefix(path, "assets/maven/etl/sdd/kafka_to_kafka/")
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
		if strings.Contains(relPath, "run.sh") {
			strData, err := data.GenRunSh()
			if err != nil {
				return err
			}
			fileData = []byte(strData)
		} else if strings.Contains(relPath, "stream_import.yml") {
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
		} else if relPath == "pom.xml" {
			strData, err := data.GenPom()
			if err != nil {
				return err
			}
			fileData = []byte(strData)
		} else if strings.Contains(relPath, "custom/pom.xml") {
			strData, err := data.GenCustomPom()
			if err != nil {
				return err
			}
			fileData = []byte(strData)
		} else if strings.Contains(relPath, "common/pom.xml") {
			strData, err := data.GenCommonPom()
			if err != nil {
				return err
			}
			fileData = []byte(strData)
		} else {
			log.Default().Println(relPath)
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
