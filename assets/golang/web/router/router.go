package router

import "github.com/gin-gonic/gin"

func RegisterRouter(r *gin.Engine) {
	// 静态资源路由
	r.Static("/static", "./static")
	// API 路由组（版本控制）
	api := r.Group("/api/v1")

	defaultRouter := api.Group("")
	registerDefault(defaultRouter)
}
