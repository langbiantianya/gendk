package template

import (
	"embed"
	"strings"
	"text/template"
)

var distFS embed.FS

func SetdistFS(fs embed.FS) {
	distFS = fs
}

func GenBuildKts(data WebTemplateData) (string, error) {
	// 读取嵌入的模板文件
	buildBytes, err := distFS.ReadFile("assets/build.gradle.kts")
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

// func GenSettingsKts() string {

// }

// func GenApplication() string {

// }

// func GenRunSh() string {

// }

// func GenBlueprint() string {

// }

// func GenZip() []byte {

// }
