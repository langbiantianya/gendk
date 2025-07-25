//go:build !wasm

package cmd

import (
	"embed"
	"gendk/cmd/template"
	"gendk/cmd/web"
)

func Start(assets embed.FS) {
	template.SetdistFS(assets)
	web.App()
}
