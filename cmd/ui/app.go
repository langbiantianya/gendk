package ui

import (
	"gendk/cmd/template"

	"fyne.io/fyne/v2"
	"fyne.io/fyne/v2/app"
	"fyne.io/fyne/v2/container"
	"fyne.io/fyne/v2/dialog"
	"fyne.io/fyne/v2/storage"
	"fyne.io/fyne/v2/widget"
)

func App() {
	a := app.NewWithID("com.lbty.gendk")
	w := a.NewWindow("模板生成工具")
	w.Resize(fyne.NewSize(400, 700))

	// 项目名称输入框（新增标题）
	projectNameEntry := widget.NewEntry()
	projectNameEntry.SetPlaceHolder("请输入项目名 dk_demo")
	projectNameContainer := container.NewVBox(
		widget.NewLabel("项目名称："), // 新增标题
		projectNameEntry,
	)
	// 模块名称输入框（新增标题）
	moduleNameEntry := widget.NewEntry()
	moduleNameEntry.SetPlaceHolder("请输入模块名 demo")
	moduleNameContainer := container.NewVBox(
		widget.NewLabel("模块名称："), // 新增标题
		moduleNameEntry,
	)

	// JDK版本单选（新增标题） "JDK 17"
	javaVersionRadio := widget.NewRadioGroup([]string{"JDK 1.8"}, func(s string) {})
	javaVersionRadio.Horizontal = true
	javaVersionRadio.SetSelected("JDK 1.8")
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
		widget.NewLabel("额外依赖："), // 新增标题
		// libSelectBadge,
		libSelectCheckGroup,
	)

	// 导出按钮保持不变
	exportButton := widget.NewButton("导出", func() {
		projectName := projectNameEntry.Text
		moduleName := moduleNameEntry.Text
		javaVersion := javaVersionRadio.Selected
		// buildTool := buildToolRadio.Selected
		// rojectType := projectTypeSelect.Selected
		selectedLibs := libSelectCheckGroup.Selected
		if projectName == "" {
			dialog.ShowInformation("提示", "请输入项目名", w)
			return
		}
		if moduleName == "" {
			dialog.ShowInformation("提示", "请输入模块名", w)
			return
		}

		var data template.WebTemplateData
		switch javaVersion {
		case "JDK 1.8":
			libStr := ""
			for _, v := range selectedLibs {
				libStr += "    implementation (\"" + template.LibsGradleMapJDK8[v] + "\")\n"
			}

			data = template.NewWebTemplateData(2, libStr, projectName, moduleName, "VERSION_1_8")
			// application, _ := data.GenApplication()
			// blueprint, _ := data.GenBlueprint()
			// buildKts, _ := data.GenBuildKts()
			// runSh, _ := data.GenRunSh()
			// startWebSh, _ := data.GenStartWebSh()
			// settingsKts, _ := data.GenSettingsKts()
			// log.Default().Println("application:", application)
			// log.Default().Println("blueprint:", blueprint)
			// log.Default().Println("buildKts:", buildKts)
			// log.Default().Println("runSh:", runSh)
			// log.Default().Println("startWebSh:", startWebSh)
			// log.Default().Println("settingsKts:", settingsKts)
		case "JDK 17":
			// template.NewWebTemplateData(2, strings.Join(selectedLibs, ","), projectName, "VERSION_17")
			// 处理 JDK 17 相关逻辑
		default:
			// 处理其他情况
			// template.NewWebTemplateData(2, strings.Join(selectedLibs, ","), projectName, moduleName, "VERSION_1_8")

		}
		if data != (template.WebTemplateData{}) {
			zipData, err := data.GenWebZip() // 获取生成的zip字节流
			if err != nil {
				dialog.ShowError(err, w) // 显示生成错误
				return
			}
			dlg := dialog.NewFileSave(func(writer fyne.URIWriteCloser, err error) {
				if err != nil {
					dialog.ShowError(err, w)
					return
				}
				if writer == nil {
					return // 用户取消保存
				}
				defer writer.Close()
				// 写入zip数据到目标文件
				_, writeErr := writer.Write(zipData)
				if writeErr != nil {
					dialog.ShowError(writeErr, w)
					return
				}
				dialog.ShowInformation("成功", "项目模板已保存为zip文件", w)
			}, w)
			// 设置默认文件名和文件过滤（仅显示zip）
			dlg.SetFileName(projectName + ".zip")
			dlg.SetFilter(storage.NewExtensionFileFilter([]string{".zip"}))
			dlg.Show()
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
