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
	enableNginx          string   //是否启用nginx
	language             string
}

func NewWeb() GenView {
	return &Web{
		projectName:          "",
		moduleName:           "",
		jdkVersion:           "JDK 1.8",
		springBootVersion:    2,
		hideSelectSpringBoot: true,
		buildTool:            "Gradle",
		enableNginx:          "否",
		language:             "Java",
	}
}

func (w *Web) View() app.HTMLDiv {
	springBootVersionSelect := compose.Select(
		"Spring Boot版本",
		[]string{"Spring Boot 2", "Spring Boot 3"},
		func() string {
			return fmt.Sprintf("Spring Boot %d", w.springBootVersion)
		}(),
		true,
		"Spring Boot版本",
		func(v string) {
			app.Log(v)
			switch v {
			case "Spring Boot 2":
				w.springBootVersion = 2
			case "Spring Boot 3":
				w.springBootVersion = 3
			}
		},
	).Style("display", "none")
	jdkVersionSelect := compose.Select(
		"Java版本",
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
				springBootVersionSelect.JSValue().Get("style").Set("display", "none")
			} else {
				springBootVersionSelect.JSValue().Get("style").Set("display", "inline")
			}
		},
	)
	buildToolSelect := compose.Select(
		"构建工具",
		[]string{"Gradle"},
		w.buildTool,
		true,
		"请选择构建工具",
		func(v string) {
			w.buildTool = v
		},
	)
	nginxSelect := compose.Select(
		"nginx渲染模板",
		[]string{"是", "否"},
		w.enableNginx,
		true,
		"是否启用nginx渲染模板",
		func(v string) {
			w.enableNginx = v
		},
	)
	extraLibsCheckGroup := compose.CheckboxGroup(
		"额外依赖",
		[]string{
			"Hutool All",
			"Apache Commons IO",
			"Apache Commons Lang",
			"Apache Commons Codec",
			"OkHttp",
			"Spring Boot Starter JDBC",
			"Spring Boot Starter Data JPA",
			"MyBatis",
			"MyBatis Plus",
			"Java ORM Bee",
			"Hive JDBC",
			"MySQL Connector/J",
			"Microsoft JDBC Driver For SQL Server",
			"PostgreSQL JDBC Driver",
			"Oracle JDBC Driver",
		},
		w.extraLibs,
		func(v []string) {
			w.extraLibs = v
		},
	)
	return app.Div().Class("rounded-lg", "space-y-4").Body(
		// 项目名称输入框（绑定projectName）
		compose.Input("项目名称", "项目名称：dk_demo", w.projectName, func(v string) {
			w.projectName = v
			app.Log(w.projectName)
		}),
		// 模块名称输入框（绑定moduleName）
		compose.Input("模块名称", "模块名称：demo", w.moduleName, func(v string) {
			w.moduleName = v
			app.Log(w.moduleName)
		}),
		// 语言选择
		compose.Select(
			"开发语言",
			[]string{"Java", "Kotlin", "Golang"},
			w.jdkVersion,
			true,
			"请选择开发语言",
			func(v string) {
				w.language = v
				switch w.language {
				case "Golang":
					springBootVersionSelect.JSValue().Get("style").Set("display", "none")
					jdkVersionSelect.JSValue().Get("style").Set("display", "none")
					buildToolSelect.JSValue().Get("style").Set("display", "none")
					nginxSelect.JSValue().Get("style").Set("display", "none")
					extraLibsCheckGroup.JSValue().Get("style").Set("display", "none")
				default:
					if w.hideSelectSpringBoot {
						w.springBootVersion = 2
						springBootVersionSelect.JSValue().Get("style").Set("display", "none")
					} else {
						springBootVersionSelect.JSValue().Get("style").Set("display", "inline")
					}
					jdkVersionSelect.JSValue().Get("style").Set("display", "inline")
					buildToolSelect.JSValue().Get("style").Set("display", "inline")
					nginxSelect.JSValue().Get("style").Set("display", "inline")
					extraLibsCheckGroup.JSValue().Get("style").Set("display", "inline")
				}
			},
		),
		// JDK版本单选（绑定jdkVersion）
		jdkVersionSelect,
		// Spring Boot版本单选（绑定springBootVersion），仅在 JDK 17 时显示
		springBootVersionSelect,
		// 构建工具单选（绑定buildTool）
		buildToolSelect,
		// 是否启用nginx
		nginxSelect,
		// 额外依赖多选（绑定extraLibs）
		extraLibsCheckGroup,
	)
}

func (w *Web) GenZip() ([]byte, string, error) {
	app.Log(w)
	if w.projectName == "" {
		return nil, "", fmt.Errorf("请输入项目名")
	} else if w.moduleName == "" {
		return nil, "", fmt.Errorf("请输入模块名")
	}

	libStr := template.GenGradleLibStr(w.jdkVersion, w.extraLibs)
	var data template.TemplateData
	switch w.language {
	case "Golang":
		data = template.NewWebGoTemplateData(w.projectName, w.moduleName)
	default:
		data = template.NewWebTemplateData(w.springBootVersion, libStr, w.projectName, w.moduleName, w.jdkVersion, w.enableNginx == "是", w.language)
	}

	bytes, err := data.GenZip()
	return bytes, w.projectName + ".zip", err
}
