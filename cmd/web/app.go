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
	hideSegment bool
}

func (h *home) Render() app.UI {
	webView := view.NewWeb()
	ssoView := view.NewSso()
	segmentView := view.NewSegment()
	var selectView view.GenView = webView

	return app.Div().Class("bg-[url(/web/683017.jpg)]", "bg-fixed", "w-full", "bg-no-repeat", "min-h-dvh", "bg-center", "bg-cover", "flex", "items-center", "flex-col").Body(
		app.Div().Class("w-full", "bg-white/30", "shadow-md", "backdrop-blur-md", "mb-4", "px-2", "leading-12", "h-12", "flex", "justify-between", "items-center").Body(
			app.Span().Style("line-height", "3rem").Class(
				"text-2xl", "font-bold", "text-gray-800", "h-12", "inline",
			).Text("定开应用脚手架"),
			app.Span().Style("line-height", "3rem").Class().Body(
				app.A().Class().Href("https://github.com/langbiantianya/gendk/issues").Body(app.Img().Src("/web/issues.svg").Alt("issues").Class("h-8", "inline")),
				app.A().Class().Href("https://github.com/langbiantianya/gendk").Body(app.Img().Src("/web/github.svg").Alt("github").Class("h-8", "inline")),
			),
		),
		app.Div().Class(
			"p-6", "backdrop-blur-md", "rounded-sm", "shadow-md", "flex", "flex-col", "bg-white/30", "mb-12",
		).Style("max-width", "36rem").Style("min-width", "28rem").Body(
			// 项目类型选择（绑定projectType）
			compose.Select(
				"请选择项目类型",
				[]string{"Web", "SSO", "分群推送"},
				h.projectType,
				true,
				"请选择项目类型",
				func(v string) {
					h.projectType = v
					h.hideWeb = v != "Web"
					h.hideSso = v != "SSO"
					h.hideSegment = v != "分群推送"
					if !h.hideWeb {
						selectView = webView
					}
					if !h.hideSso {
						selectView = ssoView
					}
					if !h.hideSegment {
						selectView = segmentView
					}
				},
			).Class("mb-4"),
			webView.View().Hidden(h.hideWeb),
			ssoView.View().Hidden(h.hideSso),
			segmentView.View().Hidden(h.hideSegment),
			// 导出按钮（点击时打印所有值）
			compose.Button("导出", "primary").Class("mt-4").
				OnClick(func(ctx app.Context, e app.Event) {
					zipData, fileName, err := selectView.GenZip()
					if err != nil {
						app.Log(err)
						app.Window().Call("alert", err.Error())
						return
					}
					template.SaveZipDataLocally(ctx, e, zipData, fileName)
				}),
		),
		app.Div().Class("w-full", "bg-white/30", "shadow-md", "backdrop-blur-md", "mt-4", "px-2", "fixed", "bottom-0").Body(
			app.P().Class("text-right", "text-sm/8").ID("hitokoto").Body(
				app.A().Href("#").ID("hitokoto_text").Text(":D 获取中..."),
				app.Script().Text(`fetch('https://v1.hitokoto.cn?c=a&c=b&c=c&c=d&c=h&c=i&c=j')
  .then(response => response.json())
  .then(data => {
    const hitokoto = document.querySelector('#hitokoto_text')
    hitokoto.href = 'https://hitokoto.cn/?uuid='+data.uuid
    hitokoto.innerText = "『" + data.hitokoto+"』\t\t————\t\t"+data.from
    data.from_who && (hitokoto.innerText += "\t「"+ data.from_who + "」")
  })
  .catch(console.error)`),
			),
		),
	)

}

func App() {
	app.Route("/", func() app.Composer {
		return &home{
			projectType: "Web",
			hideSso:     true,
			hideWeb:     false,
			hideSegment: true,
		}
	})
	app.RunWhenOnBrowser()
	build := os.Getenv("BUILD")
	if build == "" {
		http.Handle("/", &app.Handler{
			Icon: app.Icon{
				SVG:     "/web/logo.gif",
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
				SVG:     "/web/logo.gif",
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
