package view

import (
	"fmt"
	"gendk/cmd/template"
	"gendk/cmd/web/compose"

	"github.com/maxence-charriere/go-app/v10/pkg/app"
)

type Sso struct {
	projectName       string // 项目名称
	jdkVersion        string // JDK版本
	springBootVersion int    // Spring Boot版本（JDK 17时可选）
	buildTool         string // 构建工具
	ssoProtocol       string // 单点登入协议类型
	idpXml            string // IDP配置文件
	pemCA             string // PEM CA证书
	entityID          string // Entity ID
	endpoint          string // login Endpoint
	logoutEndpoint    string // logout Endpoint
	redirect          string //重定向地址
}

func NewSso() GenView {
	return &Sso{
		jdkVersion:        "JDK 1.8",
		springBootVersion: 2,
		buildTool:         "Gradle",
		ssoProtocol:       "SAML",
	}
}
func (s *Sso) View() app.HTMLDiv {
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
		},
	)
	buildTools := compose.Select(
		"构建工具",
		[]string{"Gradle"},
		s.buildTool,
		true,
		"请选择构建工具",
		func(v string) {
			s.buildTool = v
		},
	)
	entityIDInput := compose.Input("entityID", "请输入entityID: 域名", s.entityID, func(v string) {
		s.entityID = v
	})
	endpointInput := compose.Input("endpoint", "请输入endpoint: https://域名/login/saml2/sso/xx", s.endpoint, func(v string) {
		s.endpoint = v
	})
	logoutEndpointInput := compose.Input("logoutEndpoint", "请输入logoutEndpoint: https://域名/logout/saml2/slo", s.logoutEndpoint, func(v string) {
		s.logoutEndpoint = v
	})
	idpxmlInput := compose.FileInput("标识提供者 (IdP)", "选择IDP xml文件", func(v string) {
		s.idpXml = v
		app.Log(v)
	}, "text/xml")
	pemCAInput := compose.FileInput("证书", "选择pem CA证书文件", func(v string) {
		s.pemCA = v
		app.Log(v)
	}, "application/x-x509-ca-cert")
	redirectInput := compose.Input("重定向地址", "请输入重定向接口uri: /redirect", s.redirect, func(v string) {
		s.redirect = v
	}).Style("display", "none")

	return app.Div().Class("rounded-lg", "space-y-4").Body(
		// 项目名称输入框（绑定projectName）
		compose.Input("项目名称", "项目名称：dk_sso", s.projectName, func(v string) {
			s.projectName = v
		}),
		// JDK版本单选（绑定jdkVersion）
		jdkVersionSelect,
		// Spring Boot版本单选（绑定springBootVersion），仅在 JDK 17 时显示
		springBootVersionSelect,
		// 构建工具单选（绑定buildTool）
		buildTools,
		// 单点登入协议类型选择（绑定ssoProtocol）
		compose.Select(
			"单点登入协议",
			[]string{"SAML", "token重定向"},
			s.ssoProtocol,
			true,
			"请选择单点登入协议",
			func(v string) {
				switch v {
				case "SAML":
					redirectInput.JSValue().Get("style").Set("display", "none")
					jdkVersionSelect.JSValue().Get("style").Set("display", "inline")
					buildTools.JSValue().Get("style").Set("display", "inline")
					entityIDInput.JSValue().Get("style").Set("display", "inline")
					endpointInput.JSValue().Get("style").Set("display", "inline")
					logoutEndpointInput.JSValue().Get("style").Set("display", "inline")
					idpxmlInput.JSValue().Get("style").Set("display", "inline")
					pemCAInput.JSValue().Get("style").Set("display", "inline")
				case "token重定向":
					redirectInput.JSValue().Get("style").Set("display", "inline")
					jdkVersionSelect.JSValue().Get("style").Set("display", "none")
					buildTools.JSValue().Get("style").Set("display", "none")
					entityIDInput.JSValue().Get("style").Set("display", "none")
					endpointInput.JSValue().Get("style").Set("display", "none")
					logoutEndpointInput.JSValue().Get("style").Set("display", "none")
					idpxmlInput.JSValue().Get("style").Set("display", "none")
					pemCAInput.JSValue().Get("style").Set("display", "none")
				}
				s.ssoProtocol = v
			},
		),
		// entityID
		entityIDInput,
		// endpoint
		endpointInput,
		// logoutEndpoint
		logoutEndpointInput,
		// idpxml 选择器
		idpxmlInput,
		// pemCA 选择器
		pemCAInput,
		redirectInput,
	)
}

func (s *Sso) GenZip() ([]byte, string, error) {
	if s.projectName == "" {
		return nil, "", fmt.Errorf("请输入项目名")
	}
	var data template.TemplateData

	switch s.ssoProtocol {
	case "SAML":
		data = template.NewSSOTemplateData(s.springBootVersion, s.projectName, s.jdkVersion, s.idpXml, s.pemCA, s.entityID, s.endpoint, s.logoutEndpoint)
	case "token重定向":
		data = template.NewSSoNginxTemplateData(s.projectName, s.redirect)
	}

	byte, err := data.GenZip()
	return byte, s.projectName + ".zip", err
}
