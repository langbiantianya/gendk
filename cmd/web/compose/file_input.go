package compose

import "github.com/maxence-charriere/go-app/v10/pkg/app"

// Input semi-ui风格输入框（支持值绑定）
// placeholder: 提示文本
// value: 当前输入值（双向绑定）
// onChange: 值变化时的回调（参数为新值）
func FileInput(label string, onChange func(string), format string) app.HTMLDiv {
	return app.Div().Class(
		"items-center", "mb-3", // 容器：弹性布局+垂直居中+底部间距
	).Body(
		app.Div().
			Class(
				"text-sm", "font-medium", "text-gray-700", "mb-3", "w-full", // 标签：左间距+小字体+深灰色（semi标签样式）
			).
			Text(label),
		app.Input().
			Type("file").
			Accept(format).
			Class(
				"w-full", "px-3", "py-2", "border", "border-gray-300", "rounded-md", "text-sm",
				"focus:outline-none", "focus:border-blue-500", "focus:ring-blue-500", "focus:ring-1", "hover:border-blue-500",
			).
			OnChange(func(ctx app.Context, e app.Event) { // 监听输入变化
				// 获取文件列表
				files := ctx.JSSrc().Get("files")
				if files.Length() == 0 {
					onChange("")
					return
				}

				// 读取第一个文件
				file := files.Index(0)
				reader := app.Window().Get("FileReader").New()

				// 设置文件读取完成回调
				reader.Set("onload", app.FuncOf(func(this app.Value, args []app.Value) interface{} {
					// 获取文件内容并传递给回调函数
					content := args[0].Get("target").Get("result").String()
					onChange(content)
					return nil
				}))

				// 以文本形式读取文件
				reader.Call("readAsText", file)
			}),
	)

}
