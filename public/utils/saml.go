package utils

import (
	"encoding/xml"
	"strings"
)

// EntityDescriptor 表示SAML元数据的根元素
type EntityDescriptor struct {
	XMLName         xml.Name        `xml:"urn:oasis:names:tc:SAML:2.0:metadata EntityDescriptor"`
	EntityID        string          `xml:"entityID,attr"`
	SPSSODescriptor SPSSODescriptor `xml:"urn:oasis:names:tc:SAML:2.0:metadata SPSSODescriptor"`
}

// SPSSODescriptor 表示服务提供者的SSO描述符
type SPSSODescriptor struct {
	XMLName                    xml.Name                 `xml:"urn:oasis:names:tc:SAML:2.0:metadata SPSSODescriptor"`
	ProtocolSupportEnumeration string                   `xml:"protocolSupportEnumeration,attr"`
	KeyDescriptors             []KeyDescriptor          `xml:"urn:oasis:names:tc:SAML:2.0:metadata KeyDescriptor"`
	SingleLogoutService        SingleLogoutService      `xml:"urn:oasis:names:tc:SAML:2.0:metadata SingleLogoutService"`
	NameIDFormat               string                   `xml:"urn:oasis:names:tc:SAML:2.0:metadata NameIDFormat"`
	AssertionConsumerService   AssertionConsumerService `xml:"urn:oasis:names:tc:SAML:2.0:metadata AssertionConsumerService"`
}

// KeyDescriptor 表示密钥描述符
type KeyDescriptor struct {
	XMLName xml.Name `xml:"urn:oasis:names:tc:SAML:2.0:metadata KeyDescriptor"`
	Use     string   `xml:"use,attr"`
	KeyInfo KeyInfo  `xml:"http://www.w3.org/2000/09/xmldsig# KeyInfo"`
}

// KeyInfo 包含密钥信息
type KeyInfo struct {
	XMLName  xml.Name `xml:"http://www.w3.org/2000/09/xmldsig# KeyInfo"`
	X509Data X509Data `xml:"http://www.w3.org/2000/09/xmldsig# X509Data"`
}

// X509Data 包含X.509证书数据
type X509Data struct {
	XMLName         xml.Name `xml:"http://www.w3.org/2000/09/xmldsig# X509Data"`
	X509Certificate string   `xml:"http://www.w3.org/2000/09/xmldsig# X509Certificate"`
}

// SingleLogoutService 表示单点登出服务
type SingleLogoutService struct {
	XMLName          xml.Name `xml:"urn:oasis:names:tc:SAML:2.0:metadata SingleLogoutService"`
	Binding          string   `xml:"Binding,attr"`
	Location         string   `xml:"Location,attr"`
	ResponseLocation string   `xml:"ResponseLocation,attr"`
}

// AssertionConsumerService 表示断言消费者服务
type AssertionConsumerService struct {
	XMLName  xml.Name `xml:"urn:oasis:names:tc:SAML:2.0:metadata AssertionConsumerService"`
	Binding  string   `xml:"Binding,attr"`
	Location string   `xml:"Location,attr"`
	Index    int      `xml:"index,attr"`
}

// GenerateEntityDescriptor 根据输入参数生成EntityDescriptor结构体
func GenerateEntityDescriptor(pemCA, entityID, endpoint, logoutEndpoint string) EntityDescriptor {
	// 清理PEM证书中的头部和尾部
	cleanedCert := strings.TrimPrefix(pemCA, "-----BEGIN CERTIFICATE-----")
	cleanedCert = strings.TrimSuffix(cleanedCert, "-----END CERTIFICATE-----")
	cleanedCert = strings.TrimSpace(cleanedCert)

	return EntityDescriptor{
		EntityID: entityID,
		SPSSODescriptor: SPSSODescriptor{
			ProtocolSupportEnumeration: "urn:oasis:names:tc:SAML:2.0:protocol",
			KeyDescriptors: []KeyDescriptor{
				{
					Use: "signing",
					KeyInfo: KeyInfo{
						X509Data: X509Data{
							X509Certificate: cleanedCert,
						},
					},
				},
				{
					Use: "encryption",
					KeyInfo: KeyInfo{
						X509Data: X509Data{
							X509Certificate: cleanedCert,
						},
					},
				},
			},
			SingleLogoutService: SingleLogoutService{
				Binding:          "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST",
				Location:         logoutEndpoint,
				ResponseLocation: logoutEndpoint,
			},
			NameIDFormat: "urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified",
			AssertionConsumerService: AssertionConsumerService{
				Binding:  "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST",
				Location: endpoint,
				Index:    1,
			},
		},
	}
}

// EntityDescriptorToXML 将EntityDescriptor转换为XML字符串
func EntityDescriptorToXML(ed EntityDescriptor) (string, error) {
	// 生成XML数据
	xmlData, err := xml.MarshalIndent(ed, "", "  ")
	if err != nil {
		return "", err
	}

	// 添加XML声明并返回
	return `<?xml version="1.0" encoding="UTF-8"?>` + "\n" + string(xmlData), nil
}
