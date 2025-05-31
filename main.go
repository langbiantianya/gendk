package main

import (
	"log"
	"strings" // 新增：用于拼接选中项字符串

	"fyne.io/fyne/v2"
	"fyne.io/fyne/v2/app"
	"fyne.io/fyne/v2/container"
	"fyne.io/fyne/v2/widget"
)

func main() {
	a := app.New()
	w := a.NewWindow("模板生成工具")
	w.Resize(fyne.NewSize(400, 300))

	// 项目名称输入框（新增标题）
	projectNameEntry := widget.NewEntry()
	projectNameEntry.SetPlaceHolder("请输入项目名")
	projectNameContainer := container.NewVBox(
		widget.NewLabel("项目名称："), // 新增标题
		projectNameEntry,
	)

	// JDK版本单选（新增标题）
	javaVersionRadio := widget.NewRadioGroup([]string{"JDK 1.8", "JDK 17"}, func(s string) {})
	javaVersionRadio.Horizontal = true
	javaVersionRadio.SetSelected("java1.8")
	javaVersionContainer := container.NewVBox(
		widget.NewLabel("JDK版本："), // 新增标题
		javaVersionRadio,
	)

	// 构建工具单选（新增标题）
	buildToolRadio := widget.NewSelect([]string{"gradle"}, func(s string) {})
	buildToolRadio.SetSelected("gradle")
	buildToolContainer := container.NewVBox(
		widget.NewLabel("构建工具："), // 新增标题
		buildToolRadio,
	)

	// 项目类型选择（新增标题）
	projectTypeSelect := widget.NewSelect([]string{"web 服务"}, func(s string) {})
	projectTypeSelect.SetSelected("web 服务")
	projectTypeContainer := container.NewVBox(
		widget.NewLabel("项目类型："), // 新增标题
		projectTypeSelect,
	)

	// 功能多选部分（新增标题）
	libSelectBadge := widget.NewLabel("已选：无")
	libSelectCheckGroup := widget.NewCheckGroup([]string{"Hutool All", "Spring Boot Starter JDBC", "OkHttp", "MyBatis Plus"}, func(selected []string) {
		if len(selected) == 0 {
			libSelectBadge.SetText("已选：无")
		} else {
			libSelectBadge.SetText("已选：" + strings.Join(selected, "、"))
		}
	})
	libSelectContainer := container.NewVBox(
		widget.NewLabel("额外依赖："), // 新增标题
		libSelectBadge,
		libSelectCheckGroup,
	)

	// 导出按钮保持不变
	exportButton := widget.NewButton("导出", func() {
		projectName := projectNameEntry.Text
		javaVersion := javaVersionRadio.Selected
		buildTool := buildToolRadio.Selected
		rojectType := projectTypeSelect.Selected
		selectedLibs := libSelectCheckGroup.Selected

		log.Default().Println("项目名:", projectName)
		log.Default().Println("Java版本:", javaVersion)
		log.Default().Println("构建工具:", buildTool)
		log.Default().Println("项目类型:", rojectType)
		log.Default().Println("选中功能:", selectedLibs)
	})

	// 调整主布局为各容器的垂直排列
	content := container.NewVBox(
		projectNameContainer,
		javaVersionContainer,
		buildToolContainer,
		projectTypeContainer,
		libSelectContainer,
		exportButton,
	)

	w.SetContent(content)
	w.ShowAndRun()
}
