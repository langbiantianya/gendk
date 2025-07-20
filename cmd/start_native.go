//go:build !wasm

package cmd

import (
	"embed"
	"gendk/cmd/template"
	"gendk/cmd/ui"
	"gendk/cmd/web"
	"os"
)

func Start(assets embed.FS) {
	template.SetdistFS(assets)
	// webEnv := os.Getenv("WEB")
	// if webEnv == "" {
	// 	ui.App()
	// } else {
		web.App()
	// }
}
