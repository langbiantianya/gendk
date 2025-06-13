package main

import (
	"embed"
	"gendk/cmd"
)

//go:embed all:assets/**/*
var assets embed.FS // 新增文件系统变量保

func main() {
	cmd.Start(assets)
}
