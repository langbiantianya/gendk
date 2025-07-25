package web

import (
	"gendk/cmd/template"
	"gendk/cmd/web/compose"
	"gendk/cmd/web/view"
	"log"
	"net/http"
	"os"

	"github.com/maxence-charriere/go-app/v10/pkg/app"
)

// home 页面组件（支持表单值获取）
type home struct {
	app.Compo
	projectType string // 项目类型
	hideSso     bool
	hideWeb     bool
}

func (h *home) Render() app.UI {
	webView := view.NewWeb()
	ssoView := view.NewSso()
	var selectView view.GenView = webView
	return app.Div().Class(
		"max-w-md", "mx-auto", "p-6", "bg-white", "rounded-lg", "shadow-md", "flex", "flex-col", "gap-y-4",
	).Body(
		app.H1().Class(
			"text-2xl", "font-bold", "text-gray-800", "text-center", // 添加 text-center 类
		).Text("定开项目生成器"),

		// 项目类型选择（绑定projectType）
		compose.Select(
			[]string{"Web", "SSO"},
			h.projectType,
			true,
			"请选择项目类型",
			func(v string) {
				h.projectType = v
				h.hideWeb = v != "Web"
				h.hideSso = v != "SSO"
				if !h.hideWeb {
					selectView = webView
				}
				if !h.hideSso {
					selectView = ssoView
				}
			},
		),
		webView.View().Hidden(h.hideWeb),
		ssoView.View().Hidden(h.hideSso),
		// 导出按钮（点击时打印所有值）
		compose.Button("导出", "primary").
			OnClick(func(ctx app.Context, e app.Event) {
				zipData, fileName, err := selectView.GenZip()
				if err != nil {
					app.Log(err)
					app.Window().Call("alert", err.Error())
					return
				}
				template.SaveZipDataLocally(ctx, e, zipData, fileName)
			}),
	)
}

func App() {
	app.Route("/", func() app.Composer {
		return &home{
			projectType: "Web",
			hideSso:     true,
			hideWeb:     false,
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
