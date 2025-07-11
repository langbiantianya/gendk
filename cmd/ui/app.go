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
	w.Resize(fyne.NewSize(400, 600))
	// Spring boot 版本选择
	var springBootVersionContainer *fyne.Container
	var springBootVersionRadio *widget.Select
	// 项目类型选择（新增标题）
	var projectTypeSelect *widget.Select
	//  项目依赖选择（新增标题）
	var libSelectCheckGroup *widget.CheckGroup
	var libSelectContainer *fyne.Container
	// 单点登入类型选择
	var ssoTypeSelect *widget.Select
	var ssoTypeContainer *fyne.Container
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
	javaVersionRadio := widget.NewSelect([]string{"JDK 1.8", "JDK 17"}, func(s string) {
		if springBootVersionContainer != nil {
			springBootVersionContainer.Hidden = s == "JDK 1.8" || projectTypeSelect.Selected == "SSO"
			if springBootVersionContainer.Hidden {
				springBootVersionRadio.Selected = "Spring Boot 2"
			}
		}

	})
	javaVersionRadio.SetSelected("JDK 1.8")
	javaVersionContainer := container.NewVBox(
		widget.NewLabel("JDK版本："), // 新增标题
		javaVersionRadio,
	)

	// Spring boot 版本选择
	springBootVersionRadio = widget.NewSelect([]string{"Spring Boot 2", "Spring Boot 3"}, func(s string) {})
	springBootVersionRadio.SetSelected("Spring Boot 2")
	springBootVersionContainer = container.NewVBox(
		widget.NewLabel("Spring Boot 版本："), // 新增标题
		springBootVersionRadio,
	)

	// 构建工具单选（新增标题）
	buildToolRadio := widget.NewSelect([]string{"gradle"}, func(s string) {})
	buildToolRadio.SetSelected("gradle")
	buildToolContainer := container.NewVBox(
		widget.NewLabel("构建工具："), // 新增标题
		buildToolRadio,
	)

	// 项目类型选择（新增标题）
	projectTypeSelect = widget.NewSelect([]string{"Web", "SSO"}, func(s string) {
		if springBootVersionContainer != nil {
			springBootVersionContainer.Hidden = s == "SSO" || javaVersionRadio.Selected == "JDK 1.8"
			if springBootVersionContainer.Hidden {
				springBootVersionRadio.Selected = "Spring Boot 2"
			}
		}
		if moduleNameContainer != nil {
			moduleNameContainer.Hidden = s == "SSO"
		}
		if libSelectContainer != nil {
			libSelectContainer.Hidden = s == "SSO"
		}
		if ssoTypeContainer != nil {
			ssoTypeContainer.Hidden = s != "SSO"
		}
	})
	projectTypeSelect.SetSelected("Web")
	projectTypeContainer := container.NewVBox(
		widget.NewLabel("项目类型："), // 新增标题
		projectTypeSelect,
	)

	// 单点登入类型选择
	ssoTypeSelect = widget.NewSelect([]string{"SAML"}, func(s string) {})
	ssoTypeSelect.SetSelected("SAML")
	ssoTypeContainer = container.NewVBox(
		widget.NewLabel("单点登入类型："), // 新增标题
		ssoTypeSelect,
	)

	// 功能多选部分（新增标题）
	// libSelectBadge := widget.NewLabel("已选：无")
	libSelectCheckGroup = widget.NewCheckGroup([]string{
		"Hutool All",
		"OkHttp",
		"Spring Boot Starter JDBC",
		"Spring Boot Starter Data JPA",
		"MyBatis Plus",
		"MySQL Connector/J",
		"Microsoft JDBC Driver For SQL Server",
		"PostgreSQL JDBC Driver",
	}, func(selected []string) {

	})
	libSelectContainer = container.NewVBox(
		widget.NewLabel("额外依赖："), // 新增标题
		// libSelectBadge,
		libSelectCheckGroup,
	)

	// 导出按钮保持不变
	exportButton := widget.NewButton("导出", func() {
		projectName := projectNameEntry.Text
		moduleName := moduleNameEntry.Text
		jdkVersion := javaVersionRadio.Selected
		// buildTool := buildToolRadio.Selected
		projectType := projectTypeSelect.Selected
		selectedLibs := libSelectCheckGroup.Selected
		// ssoType := ssoTypeSelect.Selected
		var springBootVersion int

		switch springBootVersionRadio.Selected {

		case "Spring Boot 2":
			springBootVersion = 2
		case "Spring Boot 3":
			springBootVersion = 3

		}

		if projectName == "" {
			dialog.ShowInformation("提示", "请输入项目名", w)
			return
		}
		if !moduleNameContainer.Hidden {
			if moduleName == "" {
				dialog.ShowInformation("提示", "请输入模块名", w)
				return
			}
		}
		var data template.TemplateData
		switch projectType {
		case "SSO":
			// data = template.NewSSOTemplateData(springBootVersion, projectName, jdkVersion)
			break
		case "Web":
			libStr := template.GenGradleLibStr(jdkVersion, selectedLibs)

			data = template.NewWebTemplateData(springBootVersion, libStr, projectName, moduleName, jdkVersion)
			break
		}
		if data != (template.WebTemplateData{}) {
			zipData, err := data.GenZip() // 获取生成的zip字节流
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
	})
	// 默认隐藏
	springBootVersionContainer.Hide()
	ssoTypeContainer.Hide()
	// 调整主布局为各容器的垂直排列
	content := container.NewScroll(
		container.NewVBox(
			projectNameContainer,
			moduleNameContainer,
			javaVersionContainer,
			springBootVersionContainer,
			buildToolContainer,
			projectTypeContainer,
			ssoTypeContainer,
			libSelectContainer,
			exportButton,
		),
	)

	w.SetContent(content)
	w.ShowAndRun()
}
