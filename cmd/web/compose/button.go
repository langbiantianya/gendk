package compose

import "github.com/maxence-charriere/go-app/v10/pkg/app"

// Button 通用按钮组件（Tailwind版，类名拆分）
// btnType 可选值："primary"（默认）、"secondary"、"tertiary"、"warning"、"danger"
func Button(name, btnType string) app.HTMLButton {
	button := app.Button().
		Type("submit").
		Text(name).
		// 通用基础样式（每个类名单独作为字符串参数）
		Class(
			"rounded-md",
			"px-4",
			"py-2",
			"text-sm",
			"font-medium",
			"border",
			"border-transparent",
			"cursor-pointer",
			"transition-all",
			"duration-200",
			"ease-in-out",
		)

	// 根据类型设置具体样式（每个类名单独作为字符串参数）
	switch btnType {
	case "primary": // 主按钮
		button = button.Class(
			"bg-[#5072a7]",
			"text-white",
			"hover:bg-[#466492]",
			"active:bg-[#3d577c]",
		)

	case "secondary": // 次要按钮
		button = button.Class(
			"bg-white",
			"text-[#6c757d]",
			"border-[#6c757d]",
			"hover:bg-[#f8f9fa]",
			"active:bg-[#e9ecef]",
		)

	case "tertiary": // 第三级按钮
		button = button.Class(
			"bg-transparent",
			"text-[#5072a7]",
			"hover:bg-[#f0f4f8]",
			"active:bg-[#e6edf3]",
		)

	case "warning": // 警告按钮
		button = button.Class(
			"bg-[#ffc107]",
			"text-[rgba(0,0,0,0.85)]",
			"hover:bg-[#ffca2c]",
			"active:bg-[#ffb700]",
		)

	case "danger": // 危险按钮
		button = button.Class(
			"bg-[#dc3545]",
			"text-white",
			"hover:bg-[#e04b59]",
			"active:bg-[#c82333]",
		)

	default: // 默认主按钮
		button = button.Class(
			"bg-[#5072a7]",
			"text-white",
			"hover:bg-[#466492]",
			"active:bg-[#3d577c]",
		)
	}
	return button
}
