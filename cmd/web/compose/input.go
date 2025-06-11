package compose

import "github.com/maxence-charriere/go-app/v10/pkg/app"

// Input semi-ui风格输入框（支持值绑定）
// placeholder: 提示文本
// value: 当前输入值（双向绑定）
// onChange: 值变化时的回调（参数为新值）
func Input(placeholder, value string, onChange func(string)) app.UI {
	return app.Input().
		Type("text").
		Placeholder(placeholder).
		Value(value). // 绑定当前值
		Class(
			"w-full", "px-3", "py-2", "border", "border-gray-300", "rounded-md", "text-sm",
			"focus:outline-none", "focus:border-blue-500", "focus:ring-blue-500", "focus:ring-1", "hover:border-blue-500",
		).
		OnChange(func(ctx app.Context, e app.Event) { // 监听输入变化
			onChange(ctx.JSSrc().Get("value").String())
		})
}
