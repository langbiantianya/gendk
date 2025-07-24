package view

import (
	"fmt"
	"gendk/cmd/template"
	"gendk/cmd/web/compose"

	"github.com/maxence-charriere/go-app/v10/pkg/app"
)

type Sso struct {
	projectName          string // 项目名称
	jdkVersion           string // JDK版本
	springBootVersion    int    // Spring Boot版本（JDK 17时可选）
	hideSelectSpringBoot bool
	buildTool            string // 构建工具
	hideSelectBuildTool  bool
	ssoProtocol          string // 单点登入协议类型
	idpXml               string // IDP配置文件
	pemCA                string // PEM CA证书
	entityID             string // Entity ID
	endpoint             string // login Endpoint
	logoutEndpoint       string // logout Endpoint
}

func NewSso() GenView {
	return &Sso{
		jdkVersion:           "JDK 1.8",
		springBootVersion:    2,
		hideSelectSpringBoot: true,
		buildTool:            "Gradle",
	}
}
func (s *Sso) View() app.UI {
	return app.Div().Class().Body(
		// 项目名称输入框（绑定projectName）
		compose.Input("项目名称", s.projectName, func(v string) {
			s.projectName = v
		}),
		// JDK版本单选（绑定jdkVersion）
		compose.Select(
			[]string{"JDK 1.8", "JDK 17"},
			s.jdkVersion,
			true,
			"请选择JDK版本",
			func(v string) {
				app.Log(v)
				s.jdkVersion = v
			},
		),
		// Spring Boot版本单选（绑定springBootVersion），仅在 JDK 17 时显示
		compose.Select(
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
		).Hidden(s.hideSelectSpringBoot),
		// 构建工具单选（绑定buildTool）
		compose.Select(
			[]string{"Gradle"},
			s.buildTool,
			true,
			"请选择构建工具",
			func(v string) {
				s.buildTool = v
			},
		).Hidden(s.hideSelectBuildTool),
		// 单点登入协议类型选择（绑定ssoProtocol）
		compose.Select(
			[]string{"SAML"},
			s.ssoProtocol,
			true,
			"请选择单点登入协议类型",
			func(v string) {
				s.ssoProtocol = v
			},
		),
		// entityID
		compose.Input("请输入entityID", s.entityID, func(v string) {
			s.entityID = v
		}),
		// endpoint
		compose.Input("请输入endpoint", s.endpoint, func(v string) {
			s.endpoint = v
		}),
		// logoutEndpoint
		compose.Input("请输入logoutEndpoint", s.logoutEndpoint, func(v string) {
			s.logoutEndpoint = v
		}),
		// idpxml 选择器
		compose.FileInput("选择IDP文件", func(v string) {
			s.idpXml = v
			app.Log(v)
		}, "text/xml"),
		// pemCA 选择器
		compose.FileInput("选择pem CA证书文件", func(v string) {
			s.pemCA = v
			app.Log(v)
		}, "application/x-x509-ca-cert"),
	)
}

func (s *Sso) GenZip() ([]byte, error) {
	if s.projectName == "" {
		return nil, fmt.Errorf("请输入项目名")
	}
	data := template.NewSSOTemplateData(s.springBootVersion, s.projectName, s.jdkVersion, s.idpXml, s.pemCA, s.entityID, s.endpoint, s.logoutEndpoint)

	return data.GenZip()
}
