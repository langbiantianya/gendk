package middleware

import (
	"net/http"
	"strings"
	"time"

	"github.com/gin-gonic/gin"
)

// CorsConfig 跨域配置
type CorsConfig struct {
	AllowOrigins     []string
	AllowMethods     []string
	AllowHeaders     []string
	ExposeHeaders    []string
	AllowCredentials bool
	MaxAge           time.Duration
}

// DefaultCorsConfig 默认跨域配置
func DefaultCorsConfig() CorsConfig {
	return CorsConfig{
		AllowOrigins:     []string{"*"},
		AllowMethods:     []string{"GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"},
		AllowHeaders:     []string{"Origin", "Content-Type", "Accept", "Authorization"},
		ExposeHeaders:    []string{"Content-Length"},
		AllowCredentials: false,
		MaxAge:           12 * time.Hour,
	}
}

// Cors 跨域中间件
func Cors(config CorsConfig) gin.HandlerFunc {
	return func(c *gin.Context) {
		// 获取请求来源
		origin := c.Request.Header.Get("Origin")

		// 设置允许的来源
		if len(config.AllowOrigins) > 0 {
			if origin != "" && (config.AllowOrigins[0] == "*" || contains(config.AllowOrigins, origin)) {
				c.Header("Access-Control-Allow-Origin", origin)
			}
		}

		// 设置允许的方法
		if len(config.AllowMethods) > 0 {
			c.Header("Access-Control-Allow-Methods", strings.Join(config.AllowMethods, ","))
		}

		// 设置允许的请求头
		if len(config.AllowHeaders) > 0 {
			c.Header("Access-Control-Allow-Headers", strings.Join(config.AllowHeaders, ","))
		}

		// 设置允许暴露的响应头
		if len(config.ExposeHeaders) > 0 {
			c.Header("Access-Control-Expose-Headers", strings.Join(config.ExposeHeaders, ","))
		}

		// 设置是否允许携带cookie
		if config.AllowCredentials {
			c.Header("Access-Control-Allow-Credentials", "true")
		}

		// 设置预检请求的缓存时间
		if config.MaxAge > 0 {
			c.Header("Access-Control-Max-Age", config.MaxAge.String())
		}

		// 处理预检请求
		if c.Request.Method == "OPTIONS" {
			c.AbortWithStatus(http.StatusNoContent)
			return
		}

		c.Next()
	}
}

// 检查切片中是否包含指定元素
func contains(slice []string, item string) bool {
	for _, s := range slice {
		if s == item {
			return true
		}
	}
	return false
}
