package view

import (
	"fmt"
	"gendk/cmd/template"
	"gendk/cmd/web/compose"

	"github.com/maxence-charriere/go-app/v10/pkg/app"
)

type DataReport struct {
	projectName string
	moduleName  string
	enableNginx string
}

func NewDataReport() GenView {
	return &DataReport{
		projectName: "",
		moduleName:  "",
		enableNginx: "否",
	}
}

func (s *DataReport) View() app.HTMLDiv {
	return app.Div().Class("rounded-lg", "space-y-4").Body(
		compose.Input("项目名称",
			"项目名称: dk_xx_data_report",
			s.projectName, func(v string) {
				s.projectName = v
			}),
		compose.Input("模块名称",
			"模块名称: xx_data_report",
			s.moduleName, func(v string) {
				s.moduleName = v
			}),

		compose.Select(
			"nginx渲染模板",
			[]string{"是", "否"},
			s.enableNginx,
			true,
			"是否启用nginx渲染模板",
			func(v string) {
				s.enableNginx = v
			},
		),
	)
}

func (s *DataReport) GenZip() ([]byte, string, error) {
	var data template.TemplateData
	if s.projectName == "" {
		return nil, "", fmt.Errorf("请输入项目名")
	}

	if s.moduleName == "" {
		return nil, "", fmt.Errorf("请输入模块名称")
	}
	data = template.NewDataReportTemplateData(s.projectName, s.moduleName, s.enableNginx)

	byte, err := data.GenZip()
	return byte, s.projectName + ".zip", err
}
