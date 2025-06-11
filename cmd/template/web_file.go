package template

import (
	"encoding/base64"

	"github.com/maxence-charriere/go-app/v10/pkg/app"
)

func SaveZipDataLocally(ctx app.Context, e app.Event, zipBytes []byte, fileName string) {

	// 将ZIP文件内容编码为Base64
	b64Data := base64.StdEncoding.EncodeToString(zipBytes)

	// 创建Data URL
	dataURL := "data:application/zip;base64," + b64Data

	// 确保DOM操作在安全的上下文中执行
	ctx.Defer(func(ctx app.Context) {
		// 创建下载链接
		a := app.Window().Get("document").Call("createElement", "a")
		a.Set("href", dataURL)
		a.Set("download", fileName)

		// 添加到DOM并触发点击
		app.Window().Get("document").Get("body").Call("appendChild", a)
		a.Call("click")

		// 移除链接
		a.Call("remove")

	})
}
