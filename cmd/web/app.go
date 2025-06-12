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
	moduleName           string // 模块名称
	hideModuleName       bool
	jdkVersion           string // JDK版本
	hideSelectSpringBoot bool
	springBootVersion    int    // Spring Boot版本（JDK 17时可选）
	buildTool            string // 构建工具
	projectType          string // 项目类型
	ssoProtocol          string
	hideSelectSSO        bool
	extraLibs            []string // 额外依赖
	hideExtraLibs        bool
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
		}),

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
				h.hideSelectSpringBoot = h.jdkVersion == "JDK 1.8" || h.projectType == "SSO"
				if h.hideSelectSpringBoot {
					h.springBootVersion = 2
				}
			},
		),
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
		),

		// 项目类型选择（绑定projectType）
		compose.Select(
			[]string{"Web", "SSO"},
			h.projectType,
			true,
			"请选择项目类型",
			func(v string) {
				h.projectType = v
				h.hideSelectSpringBoot = h.jdkVersion == "JDK 1.8" || h.projectType == "SSO"
				h.hideSelectSSO = h.projectType != "SSO"
				h.hideModuleName = h.projectType == "SSO"
				h.hideExtraLibs = h.projectType == "SSO"
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
				if h.projectName == "" {
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
					break
				case "Web":
					libStr := template.GenGradleLibStr(h.jdkVersion, h.extraLibs)
					data = template.NewWebTemplateData(h.springBootVersion, libStr, h.projectName, h.moduleName, h.jdkVersion)
					break
				}

				if data != nil {
					zipData, err := data.GenZip() // 获取生成的zip字节流
					if err != nil {
						app.Log(err)
						app.Window().Call("alert", err)
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
			hideModuleName:       false,
			jdkVersion:           "JDK 1.8",
			hideSelectSpringBoot: true,
			springBootVersion:    2,
			buildTool:            "Gradle",
			projectType:          "Web",
			ssoProtocol:          "SAML",
			hideSelectSSO:        true,
			extraLibs:            []string{},
			hideExtraLibs:        false,
		}
	})
	app.RunWhenOnBrowser()
	build := os.Getenv("BUILD")
	if build == "" {
		http.Handle("/", &app.Handler{
			Name:        "模板生成工具",
			Description: "快速生成定开项目",
			Scripts:     []string{"https://cdn.tailwindcss.com"},
		})
		if err := http.ListenAndServe(":8000", nil); err != nil {
			log.Fatal(err)
		}
	} else {
		err := app.GenerateStaticWebsite(".", &app.Handler{
			Name:        "模板生成工具",
			Description: "快速生成定开项目",
			Scripts:     []string{"https://cdn.tailwindcss.com"},
		})

		if err != nil {
			log.Fatal(err)
		}
	}
}
