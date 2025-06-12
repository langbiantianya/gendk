package template

import (
	"embed"
)

var distFS embed.FS

func SetdistFS(fs embed.FS) {
	distFS = fs
}

type TemplateData interface {
	GenZip() ([]byte, error)
}
