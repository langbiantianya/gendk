package view

import (
	"fmt"
	"gendk/cmd/template"
	"gendk/cmd/web/compose"

	"github.com/maxence-charriere/go-app/v10/pkg/app"
)

type Web struct {
	projectName          string // 项目名称
	moduleName           string // 模块名称
	jdkVersion           string // JDK版本
	springBootVersion    int    // Spring Boot版本（JDK 17时可选）
	hideSelectSpringBoot bool
	buildTool            string   // 构建工具
	extraLibs            []string // 额外依赖
}

func NewWeb() GenView {
	return &Web{
		jdkVersion:           "JDK 1.8",
		hideSelectSpringBoot: true,
		buildTool:            "Gradle",
	}
}

func (w *Web) View() app.UI {
	return app.Div().Class().Body(
		// 项目名称输入框（绑定projectName）
		compose.Input("项目名称", w.projectName, func(v string) {
			w.projectName = v
		}),
		// 模块名称输入框（绑定moduleName）
		compose.Input("模块名称", w.moduleName, func(v string) {
			w.moduleName = v
		}),
		// JDK版本单选（绑定jdkVersion）
		compose.Select(
			[]string{"JDK 1.8", "JDK 17"},
			w.jdkVersion,
			true,
			"请选择JDK版本",
			func(v string) {
				app.Log(v)
				w.jdkVersion = v
				w.hideSelectSpringBoot = w.jdkVersion == "JDK 1.8"
				if w.hideSelectSpringBoot {
					w.springBootVersion = 2
				}
			},
		),
		// 构建工具单选（绑定buildTool）
		compose.Select(
			[]string{"Gradle"},
			w.buildTool,
			true,
			"请选择构建工具",
			func(v string) {
				w.buildTool = v
			},
		),
		// 额外依赖多选（绑定extraLibs）
		compose.CheckboxGroup(
			"额外依赖",
			[]string{
				"Hutool All",
				"OkHttp",
				"Spring Boot Starter JDBC",
				"Spring Boot Starter Data JPA",
				"MyBatis Plus",
				"MySQL Connector/J",
				"Microsoft JDBC Driver For SQL Server",
				"PostgreSQL JDBC Driver",
			},
			w.extraLibs,
			func(v []string) {
				w.extraLibs = v
			},
		),
	)
}

func (w *Web) GenZip() ([]byte, error) {
	if w.projectName == "" {
		return nil, fmt.Errorf("请输入项目名")
	}

	if w.moduleName == "" {
		return nil, fmt.Errorf("请输入模块名")
	}

	libStr := template.GenGradleLibStr(w.jdkVersion, w.extraLibs)
	data := template.NewWebTemplateData(w.springBootVersion, libStr, w.projectName, w.moduleName, w.jdkVersion)
	return data.GenZip()
}
