package web

import (
	"fmt"
	"gendk/cmd/template"
	"gendk/cmd/web/compose"
	"log"
	"net/http"
	"os"

	"github.com/maxence-charriere/go-app/v10/pkg/app"
)

// home 页面组件（支持表单值获取）
type home struct {
	app.Compo

	// 表单状态（用于存储各个组件的值）
	projectName          string // 项目名称
	hideProjectName      bool
	moduleName           string // 模块名称
	hideModuleName       bool
	jdkVersion           string // JDK版本
	hideJdkVersion       bool
	hideSelectSpringBoot bool
	springBootVersion    int    // Spring Boot版本（JDK 17时可选）
	buildTool            string // 构建工具
	hideSelectBuildTool  bool
	projectType          string // 项目类型
	ssoProtocol          string
	etlProjectType       string
	hideSelectSSO        bool
	hideSelectETL        bool
	extraLibs            []string // 额外依赖
	hideExtraLibs        bool
	idpXml               string
	hideIdpxml           bool
	cerKey               string
	hideCerKey           bool
}

func (h *home) Render() app.UI {

	return app.Div().Class(
		"max-w-md", "mx-auto", "p-6", "bg-white", "rounded-lg", "shadow-md", "flex", "flex-col", "gap-y-4",
	).Body(
		app.H1().Class(
			"text-2xl", "font-bold", "text-gray-800", "text-center", // 添加 text-center 类
		).Text("定开项目生成器"),

		compose.Button("下载本机程序", "secondary").OnClick(func(ctx app.Context, e app.Event) {
			// 跳转链接到 https://github.com/langbiantianya/gendk/releases
			ctx.Defer(func(ctx app.Context) {
				// 创建下载链接
				a := app.Window().Get("document").Call("createElement", "a")
				a.Set("href", "https://github.com/langbiantianya/gendk/releases")
				// 添加到DOM并触发点击
				app.Window().Get("document").Get("body").Call("appendChild", a)
				a.Call("click")
				// 移除链接
				a.Call("remove")

			})
		}),

		// 项目名称输入框（绑定projectName）
		compose.Input("项目名称", h.projectName, func(v string) {
			h.projectName = v
		}).Hidden(h.hideProjectName),

		// 模块名称输入框（绑定moduleName）
		compose.Input("模块名称", h.moduleName, func(v string) {
			h.moduleName = v
		}).Hidden(h.hideModuleName),

		// JDK版本单选（绑定jdkVersion）
		compose.Select(
			[]string{"JDK 1.8", "JDK 17"},
			h.jdkVersion,
			true,
			"请选择JDK版本",
			func(v string) {
				app.Log(v)
				h.jdkVersion = v
				h.hideSelectSpringBoot = h.jdkVersion == "JDK 1.8" || h.projectType == "SSO" || h.projectType == "ETL"
				if h.hideSelectSpringBoot {
					h.springBootVersion = 2
				}
			},
		).Hidden(h.hideJdkVersion),
		// Spring Boot版本单选（绑定springBootVersion），仅在 JDK 17 时显示
		compose.Select(
			[]string{"Spring Boot 2", "Spring Boot 3"},
			func() string {
				return fmt.Sprintf("Spring Boot %d", h.springBootVersion)
			}(),
			true,
			"Spring Boot版本",
			func(v string) {
				app.Log(v)
				switch v {
				case "Spring Boot 2":
					h.springBootVersion = 2
				case "Spring Boot 3":
					h.springBootVersion = 3
				}
			},
		).Hidden(h.hideSelectSpringBoot),

		// 构建工具单选（绑定buildTool）
		compose.Select(
			[]string{"Gradle"},
			h.buildTool,
			true,
			"请选择构建工具",
			func(v string) {
				h.buildTool = v
			},
		).Hidden(h.hideSelectBuildTool),

		// 项目类型选择（绑定projectType）
		compose.Select(
			[]string{"Web", "SSO", "ETL"},
			h.projectType,
			true,
			"请选择项目类型",
			func(v string) {
				h.projectType = v
				h.hideSelectSpringBoot = h.jdkVersion == "JDK 1.8" || h.projectType == "SSO" || h.projectType == "ETL"
				h.hideSelectSSO = h.projectType != "SSO"
				h.hideModuleName = h.projectType == "SSO" || h.projectType == "ETL"
				h.hideExtraLibs = h.projectType == "SSO" || h.projectType == "ETL"
				h.hideSelectETL = h.projectType != "ETL"
				h.hideJdkVersion = h.projectType == "ETL"
				h.hideSelectBuildTool = h.projectType == "ETL"
				if h.hideSelectSpringBoot {
					h.springBootVersion = 2
				}
			},
		),

		// 单点登入协议类型选择（绑定ssoProtocol）
		compose.Select(
			[]string{"SAML"},
			h.ssoProtocol,
			true,
			"请选择单点登入协议类型",
			func(v string) {
				h.ssoProtocol = v
			},
		).Hidden(h.hideSelectSSO),

		// etl 项目类型选择（绑定etlProjectType）
		compose.Select(
			[]string{"SDD Kafka to Kafka"},
			h.etlProjectType,
			true,
			"请选择 ETL 项目类型",
			func(v string) {
				h.etlProjectType = v
			},
		).Hidden(h.hideSelectETL),

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
			h.extraLibs,
			func(v []string) {
				h.extraLibs = v
			},
		).Hidden(h.hideExtraLibs),

		// 导出按钮（点击时打印所有值）
		compose.Button("导出", "primary").
			OnClick(func(ctx app.Context, e app.Event) {
				if h.projectName == "" && !h.hideProjectName {
					app.Window().Call("alert", "提示：请输入项目名") // 添加弹窗提示
					return
				}

				if h.moduleName == "" && !h.hideModuleName {
					app.Window().Call("alert", "提示：请输入模块名") // 添加弹窗提示
					return
				}

				var data template.TemplateData

				switch h.projectType {
				case "SSO":
					data = template.NewSSOTemplateData(h.springBootVersion, h.projectName, h.jdkVersion)
				case "Web":
					libStr := template.GenGradleLibStr(h.jdkVersion, h.extraLibs)
					data = template.NewWebTemplateData(h.springBootVersion, libStr, h.projectName, h.moduleName, h.jdkVersion)
				case "ETL":
					data = template.NewEtlKafka2KafkaTemplateData(h.projectName)
				}

				if data != nil {
					zipData, err := data.GenZip() // 获取生成的zip字节流
					if err != nil {
						app.Log(err)
						app.Window().Call("alert", err.Error())
						return
					}
					template.SaveZipDataLocally(ctx, e, zipData, h.projectName+".zip")
				}

				// // 这里可以获取所有表单值（示例：打印到控制台）
				app.Log("表单值：", map[string]interface{}{
					"projectName":       h.projectName,
					"moduleName":        h.moduleName,
					"jdkVersion":        h.jdkVersion,
					"springBootVersion": h.springBootVersion,
					"buildTool":         h.buildTool,
					"projectType":       h.projectType,
					"extraLibs":         h.extraLibs,
				})
			}),
	)
}

func App() {
	app.Route("/", func() app.Composer {
		return &home{
			hideProjectName:      false,
			hideModuleName:       false,
			jdkVersion:           "JDK 1.8",
			hideJdkVersion:       false,
			hideSelectSpringBoot: true,
			springBootVersion:    2,
			buildTool:            "Gradle",
			hideSelectBuildTool:  false,
			projectType:          "Web",
			ssoProtocol:          "SAML",
			hideSelectSSO:        true,
			hideSelectETL:        true,
			extraLibs:            []string{},
			hideExtraLibs:        false,
			hideIdpxml:           true,
			hideCerKey:           true,
		}
	})
	app.RunWhenOnBrowser()
	build := os.Getenv("BUILD")
	if build == "" {
		http.Handle("/", &app.Handler{
			Icon: app.Icon{
				Default: "/web/logo.gif", // Specify default favicon.
			},
			Name:        "定开项目生成器",
			Description: "摸个鱼吧",
			Scripts:     []string{"https://cdn.tailwindcss.com"},
		})
		log.Default().Println("start at :8000")
		if err := http.ListenAndServe(":8000", nil); err != nil {
			log.Fatal(err)
		}
	} else {
		err := app.GenerateStaticWebsite(".", &app.Handler{
			Icon: app.Icon{
				Default: "/web/logo.gif", // Specify default favicon.
			},
			Name:        "定开项目生成器",
			Description: "摸个鱼吧",
			Scripts:     []string{"https://cdn.tailwindcss.com"},
		})

		if err != nil {
			log.Fatal(err)
		}
	}
}
