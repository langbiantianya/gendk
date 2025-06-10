package template

import (
	"syscall/js"
)

func SaveZipFile(zipData []byte, filename string) {
	// 创建 ArrayBuffer
	jsData := js.Global().Get("ArrayBuffer").New(len(zipData))
	jsBytes := js.Global().Get("Uint8Array").New(jsData)

	// 将 Go 字节切片复制到 JavaScript 的 Uint8Array
	for i, b := range zipData {
		jsBytes.SetIndex(i, b)
	}

	// 创建 Blob 对象
	blob := js.Global().Get("Blob").New(
		js.ValueOf([]interface{}{jsData}),
		js.ValueOf(map[string]interface{}{
			"type": "application/zip",
		}),
	)

	// 创建下载链接
	downloadURL := js.Global().Get("URL").Call("createObjectURL", blob)
	anchor := js.Global().Get("document").Call("createElement", "a")
	anchor.Set("href", downloadURL)
	anchor.Set("download", filename) // 设置默认文件名

	// 模拟点击下载链接
	document := js.Global().Get("document")
	body := document.Get("body")
	body.Call("appendChild", anchor)
	anchor.Call("click")

	// 清理资源
	body.Call("removeChild", anchor)
	js.Global().Get("URL").Call("revokeObjectURL", downloadURL)
}
