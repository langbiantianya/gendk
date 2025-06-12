package template

import (
	"embed"
)

var distFS embed.FS

func SetdistFS(fs embed.FS) {
	distFS = fs
}
