package main

import (
	"log/slog"
	"mao/assets"
	"mao/lib"
	"mao/public"
	"syscall/js"
)

func init() {
	slog.SetLogLoggerLevel(slog.LevelDebug)
}
func main() {
	content, err := assets.StaticFs.ReadFile("banner.txt")
	if err == nil {
		lib.Banner(content, nil)
	}
	// 将Go函数注册到window全局对象，JS可通过window.add()调用
	js.Global().Set("add", js.FuncOf(public.Add))
	js.Global().Set("genZip", js.FuncOf(public.GenZip))
	// 阻塞main函数，防止程序退出
	done := make(chan struct{})
	<-done // 无发送者，永久阻塞
}
