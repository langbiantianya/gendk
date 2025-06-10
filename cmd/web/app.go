package web

import (
	"gendk/cmd/web/compose"
	"log"
	"net/http"

	"github.com/maxence-charriere/go-app/v10/pkg/app"
)

// home 页面组件（支持表单值获取）
type home struct {
	app.Compo

	// 表单状态（用于存储各个组件的值）
	projectName string   // 项目名称
	moduleName  string   // 模块名称
	jdkVersion  string   // JDK版本
	buildTool   string   // 构建工具
	projectType string   // 项目类型
	extraLibs   []string // 额外依赖
}

func (h *home) Render() app.UI {
	return app.Div().Class(
		"max-w-md", "mx-auto", "p-6", "bg-white", "rounded-lg", "shadow-md", "flex", "flex-col", "gap-y-4",
	).Body(
		// 项目名称输入框（绑定projectName）
		compose.Input("项目名称", h.projectName, func(v string) {
			h.projectName = v
		}),

		// 模块名称输入框（绑定moduleName）
		compose.Input("模块名称", h.moduleName, func(v string) {
			h.moduleName = v
		}),

		// JDK版本单选（绑定jdkVersion）
		compose.Select(
			map[string]string{"JDK 1.8": "JDK 1.8"},
			"JDK 1.8",
			true,
			"请选择JDK版本",
			func(v string) {
				h.jdkVersion = v
			},
		),

		// 构建工具单选（绑定buildTool）
		compose.Select(
			map[string]string{"Gradle": "Gradle"},
			"Gradle",
			true,
			"请选择构建工具",
			func(v string) {
				h.buildTool = v
			},
		),

		// 项目类型选择（绑定projectType）
		compose.Select(
			map[string]string{"Web": "Web"},
			"Web",
			true,
			"请选择项目类型",
			func(v string) {
				h.projectType = v
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
			[]string{},
			func(v []string) {
				h.extraLibs = v
			},
		),

		// 导出按钮（点击时打印所有值）
		compose.Button("导出", "primary").
			OnClick(func(ctx app.Context, e app.Event) {
				// 这里可以获取所有表单值（示例：打印到控制台）
				app.Log("表单值：", map[string]interface{}{
					"projectName": h.projectName,
					"moduleName":  h.moduleName,
					"jdkVersion":  h.jdkVersion,
					"buildTool":   h.buildTool,
					"projectType": h.projectType,
					"extraLibs":   h.extraLibs,
				})
			}),
	)
}

func App() {
	app.Route("/", func() app.Composer { return &home{} })
	app.RunWhenOnBrowser()
	http.Handle("/", &app.Handler{
		Name:        "模板生成工具",
		Description: "快速生成定开项目",
		Scripts:     []string{"https://cdn.tailwindcss.com"},
	})
	if err := http.ListenAndServe(":8000", nil); err != nil {
		log.Fatal(err)
	}
}
