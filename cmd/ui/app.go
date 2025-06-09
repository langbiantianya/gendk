package ui

import (
	"gendk/cmd/template"

	"fyne.io/fyne/v2"
	"fyne.io/fyne/v2/app"
	"fyne.io/fyne/v2/container"
	"fyne.io/fyne/v2/dialog"
	"fyne.io/fyne/v2/widget"
)

func App() {
	a := app.NewWithID("com.lbty.gendk")
	// 加载自定义字体（新增）
	// 假设已将中文字体文件（如 simhei.ttf）放入 assets/fonts 目录

	w := a.NewWindow("模板生成工具")
	w.Resize(fyne.NewSize(400, 700))

	// 项目名称输入框（新增标题）
	projectNameEntry := widget.NewEntry()
	projectNameEntry.SetPlaceHolder("Please enter the project name: dk_demo")
	projectNameContainer := container.NewVBox(
		widget.NewLabel("Project Name:"), // 新增标题
		projectNameEntry,
	)
	// 模块名称输入框（新增标题）
	moduleNameEntry := widget.NewEntry()
	moduleNameEntry.SetPlaceHolder("Please enter the module name: demo")
	moduleNameContainer := container.NewVBox(
		widget.NewLabel("Module Name:"), // 新增标题
		moduleNameEntry,
	)

	// JDK版本单选（新增标题） "JDK 17"
	javaVersionRadio := widget.NewRadioGroup([]string{"JDK 1.8"}, func(s string) {})
	javaVersionRadio.Horizontal = true
	javaVersionRadio.SetSelected("JDK 1.8")
	javaVersionContainer := container.NewVBox(
		widget.NewLabel("JDK version:"), // 新增标题
		javaVersionRadio,
	)

	// 构建工具单选（新增标题）
	buildToolRadio := widget.NewSelect([]string{"gradle"}, func(s string) {})
	buildToolRadio.SetSelected("gradle")
	buildToolContainer := container.NewVBox(
		widget.NewLabel("Building tools:"), // 新增标题
		buildToolRadio,
	)

	// 项目类型选择（新增标题）
	projectTypeSelect := widget.NewSelect([]string{"Web Services"}, func(s string) {})
	projectTypeSelect.SetSelected("Web Services")
	projectTypeContainer := container.NewVBox(
		widget.NewLabel("Project type:"), // 新增标题
		projectTypeSelect,
	)

	// 功能多选部分（新增标题）
	// libSelectBadge := widget.NewLabel("已选：无")
	libSelectCheckGroup := widget.NewCheckGroup([]string{
		"Hutool All",
		"OkHttp",
		"Spring Boot Starter JDBC",
		"Spring Boot Starter Data JPA",
		"MyBatis Plus",
		"MySQL Connector/J",
		"Microsoft JDBC Driver For SQL Server",
		"PostgreSQL JDBC Driver",
	}, func(selected []string) {
		// if len(selected) == 0 {
		// 	libSelectBadge.SetText("已选：无")
		// } else {
		// 	libSelectBadge.SetText("已选：" + strings.Join(selected, "、"))
		// }
	})
	libSelectContainer := container.NewVBox(
		widget.NewLabel("Additional dependencies:"), // 新增标题
		// libSelectBadge,
		libSelectCheckGroup,
	)

	// 导出按钮保持不变
	exportButton := widget.NewButton("export", func() {
		projectName := projectNameEntry.Text
		moduleName := moduleNameEntry.Text
		javaVersion := javaVersionRadio.Selected
		// buildTool := buildToolRadio.Selected
		// rojectType := projectTypeSelect.Selected
		selectedLibs := libSelectCheckGroup.Selected
		if projectName == "" {
			dialog.ShowInformation("Tips", "Please enter the project name", w)
			return
		}
		if moduleName == "" {
			dialog.ShowInformation("Tips", "Please enter the module name", w)
			return
		}

		var data template.WebTemplateData
		switch javaVersion {
		case "JDK 1.8":
			libStr := ""
			for _, v := range selectedLibs {
				libStr += "    implementation (\"" + template.LibsMapJDK8[v] + "\")\n"
			}

			data = template.NewWebTemplateData(2, libStr, projectName, moduleName, "VERSION_1_8")
		case "JDK 17":
			// template.NewWebTemplateData(2, strings.Join(selectedLibs, ","), projectName, "VERSION_17")
			// 处理 JDK 17 相关逻辑
		default:
			// 处理其他情况
			// template.NewWebTemplateData(2, strings.Join(selectedLibs, ","), projectName, moduleName, "VERSION_1_8")

		}
		if data != (template.WebTemplateData{}) {
			zipData, err := data.GenZip() // 获取生成的zip字节流
			if err != nil {
				dialog.ShowError(err, w) // 显示生成错误
				return
			}
			template.SaveZipFile(zipData, projectName+".zip")
			// 调用JavaScript的下载函数
		}

		// log.Default().Println("项目名:", projectName)
		// log.Default().Println("Java版本:", javaVersion)
		// log.Default().Println("构建工具:", buildTool)
		// log.Default().Println("项目类型:", rojectType)
		// log.Default().Println("选中功能:", selectedLibs)
	})

	// 调整主布局为各容器的垂直排列
	container.NewVBox()
	content := container.NewScroll(
		container.NewVBox(
			projectNameContainer,
			moduleNameContainer,
			javaVersionContainer,
			buildToolContainer,
			projectTypeContainer,
			libSelectContainer,
			exportButton,
		),
	)

	w.SetContent(content)
	w.ShowAndRun()
}
