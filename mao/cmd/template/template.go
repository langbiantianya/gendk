package template

type TemplateData interface {
	GenZip() ([]byte, error)
}

type ExportData interface {
	B64Zip() (string, error)
}

type Template interface {
	TemplateData
	ExportData
}
