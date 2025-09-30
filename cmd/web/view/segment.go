package view

import (
	"fmt"
	"gendk/cmd/template"
	"gendk/cmd/web/compose"

	"github.com/maxence-charriere/go-app/v10/pkg/app"
)

type Segment struct {
	segmentProtocol      string
	projectName          string
	pluginCName          string
	pluginName           string
	webServerUrl         string
	moduleName           string // 模块名称
	jdkVersion           string // JDK版本
	springBootVersion    int    // Spring Boot版本（JDK 17时可选）
	hideSelectSpringBoot bool
	buildTool            string   // 构建工具
	extraLibs            []string // 额外依赖
}

func NewSegment() GenView {
	return &Segment{
		segmentProtocol:      "插件",
		pluginCName:          "",
		projectName:          "",
		pluginName:           "",
		webServerUrl:         "",
		jdkVersion:           "JDK 1.8",
		springBootVersion:    2,
		hideSelectSpringBoot: true,
		buildTool:            "Gradle",
	}
}

func (s *Segment) View() app.HTMLDiv {
	projectInputFiled := compose.Input("项目名称",
		"项目名称: segment-xxxxx-plugin",
		s.projectName, func(v string) {
			s.projectName = v
		})
	dkProjectInputFiled := compose.Input("项目名称",
		"项目名称: dk_segment-demo-manager",
		s.projectName, func(v string) {
			s.projectName = v
		}).Style("display", "none")
	pluginCNameInputFiled := compose.Input("插件中文名",
		"插件中文名称: xx分群推送",
		s.pluginCName, func(v string) {
			s.pluginCName = v
		})
	pluginNameInputFiled := compose.Input("插件名称",
		"插件名称: segment-plugin-xxx",
		s.pluginName, func(v string) {
			s.pluginName = v
		})
	webServerUrlInputFiled := compose.Input("web服务地址",
		"web服务地址: http://localhost:8107/api/xxx/acceptMessage", s.webServerUrl, func(v string) {
			s.webServerUrl = v
		})
	moduleNameInputFiled := compose.Input("模块名称",
		"模块名称：demo",
		s.moduleName, func(v string) {
			s.moduleName = v
		}).Style("display", "none")
	springBootVersionSelect := compose.Select(
		"Spring Boot版本",
		[]string{"Spring Boot 2", "Spring Boot 3"},
		func() string {
			return fmt.Sprintf("Spring Boot %d", s.springBootVersion)
		}(),
		true,
		"Spring Boot版本",
		func(v string) {
			app.Log(v)
			switch v {
			case "Spring Boot 2":
				s.springBootVersion = 2
			case "Spring Boot 3":
				s.springBootVersion = 3
			}
		},
	).Style("display", "none")

	jdkVersionSelect := compose.Select(
		"Java版本",
		[]string{"JDK 1.8", "JDK 17"},
		s.jdkVersion,
		true,
		"请选择JDK版本",
		func(v string) {
			app.Log(v)
			s.jdkVersion = v
			s.hideSelectSpringBoot = s.jdkVersion == "JDK 1.8"
			if s.hideSelectSpringBoot {
				s.springBootVersion = 2
				springBootVersionSelect.JSValue().Get("style").Set("display", "none")
			} else {
				springBootVersionSelect.JSValue().Get("style").Set("display", "inline")
			}
		},
	).Style("display", "none")

	buildToolSelect := compose.Select(
		"构建工具",
		[]string{"Gradle"},
		s.buildTool,
		true,
		"请选择构建工具",
		func(v string) {
			s.buildTool = v
		},
	).Style("display", "none")

	extraLibsCheckBoxs := compose.CheckboxGroup(
		"额外依赖",
		[]string{
			"Apache Commons IO",
			"Spring Boot Starter JDBC",
			"Spring Boot Starter Data JPA",
			"MyBatis",
			"MyBatis Plus",
			"Java ORM Bee",
			"Hive JDBC",
			"MySQL Connector/J",
			"Microsoft JDBC Driver For SQL Server",
			"PostgreSQL JDBC Driver",
			"Oracle JDBC Driver",
		},
		s.extraLibs,
		func(v []string) {
			s.extraLibs = v
		},
	).Style("display", "none")
	return app.Div().Class("rounded-lg", "space-y-4").Body(
		// 生成项目选择 插件 or 实际推送服务
		compose.Select(
			"项目类型",
			[]string{"插件", "推送服务"},
			s.segmentProtocol,
			true,
			"请选择分群项目类型",
			func(v string) {
				s.segmentProtocol = v
				switch v {
				case "插件":
					s.projectName = ""
					dkProjectInputFiled.JSValue().Set("value", nil)
					projectInputFiled.JSValue().Set("value", nil)
					dkProjectInputFiled.JSValue().Get("style").Set("display", "none")
					moduleNameInputFiled.JSValue().Get("style").Set("display", "none")
					jdkVersionSelect.JSValue().Get("style").Set("display", "none")
					springBootVersionSelect.JSValue().Get("style").Set("display", "none")
					buildToolSelect.JSValue().Get("style").Set("display", "none")
					extraLibsCheckBoxs.JSValue().Get("style").Set("display", "none")
					pluginCNameInputFiled.JSValue().Get("style").Set("display", "inline")
					pluginNameInputFiled.JSValue().Get("style").Set("display", "inline")
					webServerUrlInputFiled.JSValue().Get("style").Set("display", "inline")
				case "推送服务":
					s.projectName = ""
					projectInputFiled.JSValue().Set("value", nil)
					dkProjectInputFiled.JSValue().Set("value", nil)
					projectInputFiled.JSValue().Get("style").Set("display", "none")
					pluginCNameInputFiled.JSValue().Get("style").Set("display", "none")
					pluginNameInputFiled.JSValue().Get("style").Set("display", "none")
					webServerUrlInputFiled.JSValue().Get("style").Set("display", "none")
					dkProjectInputFiled.JSValue().Get("style").Set("display", "inline")
					moduleNameInputFiled.JSValue().Get("style").Set("display", "inline")
					jdkVersionSelect.JSValue().Get("style").Set("display", "inline")
					if s.jdkVersion != "JDK 1.8" {
						springBootVersionSelect.JSValue().Get("style").Set("display", "inline")
					}

					buildToolSelect.JSValue().Get("style").Set("display", "inline")
					extraLibsCheckBoxs.JSValue().Get("style").Set("display", "inline")
				}
			},
		),
		// 项目名称输入框（绑定projectName）
		projectInputFiled,
		dkProjectInputFiled,
		// 项目名称输入框（绑定projectName）
		pluginCNameInputFiled,
		// 插件名称输入框（绑定pluginName）
		pluginNameInputFiled,
		// 插件名称输入框（绑定webServerUrl）
		webServerUrlInputFiled,
		// 模块名称输入框（绑定moduleName）
		moduleNameInputFiled,
		jdkVersionSelect,
		springBootVersionSelect,
		// 构建工具单选（绑定buildTool）
		buildToolSelect,
		extraLibsCheckBoxs,
	)
}

func (s *Segment) GenZip() ([]byte, string, error) {
	var data template.TemplateData
	switch s.segmentProtocol {
	case "插件":
		if s.projectName == "" {
			return nil, "", fmt.Errorf("请输入项目名")
		} else if s.pluginName == "" {
			return nil, "", fmt.Errorf("请输入插件名")
		} else if s.pluginCName == "" {
			return nil, "", fmt.Errorf("请输插件中文名")
		} else if s.webServerUrl == "" {
			return nil, "", fmt.Errorf("请输web服务地址")
		}
		data = template.NewSegmentPluginTemplateData(s.projectName, s.pluginName, s.pluginCName, s.webServerUrl)
	case "推送服务":
		if s.projectName == "" {
			return nil, "", fmt.Errorf("请输入项目名")
		} else if s.moduleName == "" {
			return nil, "", fmt.Errorf("请输入模块名称")
		}
		libStr := template.GenGradleLibStr(s.jdkVersion, s.extraLibs)
		data = template.NewSegmentWebServerTemplateData(
			s.projectName,
			s.springBootVersion,
			libStr,
			s.moduleName,
			s.jdkVersion,
		)
	}

	byte, err := data.GenZip()
	return byte, s.projectName + ".zip", err
}
