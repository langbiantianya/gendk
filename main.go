package main

import (
	"fyne.io/fyne/v2/app"
	"fyne.io/fyne/v2/widget"
	"fyne.io/fyne/v2/container"
)

func main() {
	a := app.New()
	w := a.NewWindow("项目配置工具")

	// 项目名输入框
	projectNameEntry := widget.NewEntry()
	projectNameEntry.SetPlaceHolder("请输入项目名")

	// Java版本单选框（默认选中java1.8）
	javaVersionRadio := widget.NewRadioGroup([]string{"java1.8", "java 17"}, nil)
	javaVersionRadio.SetSelected("java1.8")

	// 功能复选框
	springBootCheck := widget.NewCheck("spring boot", nil)
	springDataCheck := widget.NewCheck("spring data jdbc", nil)

	// 导出按钮
	exportButton := widget.NewButton("导出", func() {
		// 这里可以添加导出逻辑，比如收集输入框/单选框/复选框的状态
	})

	// 使用垂直容器布局所有组件
	content := container.NewVBox(
		projectNameEntry,
		javaVersionRadio,
		springBootCheck,
		springDataCheck,
		exportButton,
	)

	w.SetContent(content)
	w.ShowAndRun()
}