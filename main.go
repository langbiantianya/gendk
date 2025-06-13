package main

import (
	"embed"
	"gendk/cmd/template"
	"gendk/cmd/ui"
	"gendk/cmd/web"
	"os"
)

//go:embed all:assets/**/*
var assets embed.FS // 新增文件系统变量保

func main() {
	template.SetdistFS(assets)
	webEnv := os.Getenv("WEB")
	if webEnv == "" {
		ui.App()
	} else {
		web.App()
	}
}
