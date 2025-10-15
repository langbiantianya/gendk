package router

import (
	"{{.ModuleName}}/public"

	"github.com/gin-contrib/static"
	"github.com/gin-gonic/gin"
)

func RegisterRouter(r *gin.Engine) {
	fs, err := static.EmbedFolder(public.StaticFs, "static")
	if err != nil {
		panic(err)
	}
	r.Use(static.Serve("/static", fs))

	// API 路由组（版本控制）
	api := r.Group("/api/v1")

	defaultRouter := api.Group("")
	registerDefault(defaultRouter)
}
