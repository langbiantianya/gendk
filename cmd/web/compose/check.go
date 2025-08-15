package compose

import (
	"slices"

	"github.com/maxence-charriere/go-app/v10/pkg/app"
)

// Checkbox semi-ui风格复选框（Tailwind版）
// label: 复选框标签文本
// checked: 是否选中状态
func Checkbox(label string, checked bool, onChhange func(bool, string)) app.UI {
	return app.Div().Class(
		"flex", "items-center", "mb-3", // 容器：弹性布局+垂直居中+底部间距
	).Body(
		app.Input().
			Type("checkbox").
			Checked(checked).
			Class(
				"appearance-none", // 隐藏原生复选框样式
				"w-4", "h-4",      // 自定义尺寸（16px×16px，semi标准尺寸）
				"border", "border-gray-300", // 默认边框（semi默认边框色）
				"rounded",                                        // 圆角2px（semi标准圆角）
				"checked:bg-blue-500", "checked:border-blue-500", // 选中时背景/边框颜色（semi主色）
				"focus:outline-none", "focus:ring-2", "focus:ring-blue-200", "focus:ring-offset-1", // 聚焦效果（semi标准聚焦环）
				"hover:border-blue-500", // 悬停时边框颜色（semi交互反馈）
			).OnChange(func(ctx app.Context, e app.Event) {
			onChhange(ctx.JSSrc().Get("checked").Bool(), label)
			// app.Log(label, ctx.JSSrc().Get("checked").Bool())
		}),
		app.Label().
			Class(
				"ml-2", "text-sm", "text-gray-700", // 标签：左间距+小字体+深灰色（semi标签样式）
			).
			Text(label),
	)
}

// CheckboxGroup semi-ui风格复选框组（Tailwind版）
// label: 组标签文本
// options: 选项列表（格式：map[value]label）
// selected: 已选中的值列表
// CheckboxGroup semi-ui风格复选框组（支持值绑定）
// label: 组标签
// options: 选项（map[value]label）
// selected: 当前选中值（切片）
// onChange: 值变化时的回调（参数为新的选中切片）
func CheckboxGroup(label string, options []string, selected []string, onChange func([]string)) app.HTMLDiv {
	group := app.Div().Class("mb-3", "mt-3")
	var children []app.UI // 用于收集所有子元素
	currentSelected := make([]string, len(selected))
	copy(currentSelected, selected)
	// 组标签
	if label != "" {
		children = append(children,
			app.Div().Class("text-sm", "font-medium", "text-gray-700", "mb-3").Text(label),
		)
	}

	// 复选框项
	for _, label := range options {
		// 创建循环变量的副本（避免闭包捕获循环变量）
		currentVal := label
		checked := false
		checked = slices.Contains(selected, currentVal)
		// 单个复选框项
		checkboxItem := Checkbox(currentVal, checked, func(b bool, s string) {
			if b {
				currentSelected = append(currentSelected, s)
			} else {
				// 从currentSelected中删除s
				newSelected := []string{}
				for _, val := range currentSelected {
					if val != s {
						newSelected = append(newSelected, val)
					}
				}
				currentSelected = newSelected
			}
			// 触发值变化回调
			onChange(currentSelected)
		})
		children = append(children, checkboxItem)
	}

	// 一次性设置所有子元素
	return group.Body(children...)
}
