package compose

import (
	"github.com/maxence-charriere/go-app/v10/pkg/app"
)

// Select semi-ui风格单选选择框（支持值绑定）
// options: 选项（map[value]label）
// value: 当前选中值（双向绑定）
// required: 是否必选
// placeholder: 占位提示
// onChange: 值变化时的回调（参数为新值）
func Select(options []string, value string, required bool, placeholder string, onChange func(string)) app.HTMLSelect {
	var optionsSlice []app.UI

	// 占位符
	if placeholder != "" {
		optionsSlice = append(optionsSlice,
			app.Option().Value("").Text(placeholder).Disabled(true).Selected(value == ""),
		)
	}

	// 选项
	for _, val := range options {
		optionsSlice = append(optionsSlice,
			app.Option().Value(val).Text(val).Selected(val == value),
		)
	}

	return app.Select().
		Class(
			"w-full", "px-3", "py-2", "border", "border-gray-300", "rounded-md", "text-sm",
			"focus:outline-none", "focus:border-blue-500", "focus:ring-blue-500", "focus:ring-1", "hover:border-blue-500",
		).
		Required(required).
		Body(optionsSlice...).
		OnChange(func(ctx app.Context, e app.Event) { // 监听选择变化
			onChange(ctx.JSSrc().Get("value").String())
		})
}
