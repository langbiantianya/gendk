package lib

import "github.com/maxence-charriere/go-app/v10/pkg/app"

func Base64Download(fileName, bz64Str string) {

	dataURL := "data:application/zip;base64," + bz64Str
	// 确保DOM操作在安全的上下文中执行

	// 创建下载链接
	a := app.Window().Get("document").Call("createElement", "a")
	a.Set("href", dataURL)
	a.Set("download", fileName)

	// 添加到DOM并触发点击
	app.Window().Get("document").Get("body").Call("appendChild", a)
	a.Call("click")

	// 移除链接
	a.Call("remove")
}
