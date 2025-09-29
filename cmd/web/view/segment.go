package view

import (
	"fmt"
	"gendk/cmd/template"
	"gendk/cmd/web/compose"

	"github.com/maxence-charriere/go-app/v10/pkg/app"
)

type Segment struct {
	projectName  string
	pluginCName  string
	pluginName   string
	webServerUrl string
}

func NewSegment() GenView {
	return &Segment{
		pluginCName:  "",
		projectName:  "",
		pluginName:   "",
		webServerUrl: "",
	}
}

func (s *Segment) View() app.HTMLDiv {
	return app.Div().Class("rounded-lg", "space-y-4").Body(
		// 项目名称输入框（绑定projectName）
		compose.Input("项目名称: segment-xxxxx-plugin", s.projectName, func(v string) {
			s.projectName = v
		}),
		// 项目名称输入框（绑定projectName）
		compose.Input("插件中文名称: xx分群推送", s.pluginCName, func(v string) {
			s.pluginCName = v
		}),
		// 插件名称输入框（绑定pluginName）
		compose.Input("插件名称: segment-plugin-xxx", s.pluginName, func(v string) {
			s.pluginName = v
		}),
		// 插件名称输入框（绑定webServerUrl）
		compose.Input("web服务地址: http://localhost:8107/api/xxx/xxx", s.webServerUrl, func(v string) {
			s.webServerUrl = v
		}),
	)
}

func (s *Segment) GenZip() ([]byte, string, error) {
	if s.projectName == "" {
		return nil, "", fmt.Errorf("请输入项目名")
	}
	var data template.TemplateData
	data = template.NewSegmentPluginTemplateData(s.projectName, s.pluginName, s.pluginCName, s.webServerUrl)
	byte, err := data.GenZip()
	return byte, s.projectName + ".zip", err
}
