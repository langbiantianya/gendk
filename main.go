package main

import (
	"embed"
	"gendk/cmd/template"
	"gendk/cmd/web"
)

//go:embed all:assets/**/*
var assets embed.FS // 新增文件系统变量保

func main() {
	template.SetdistFS(assets)
	web.App()
}
