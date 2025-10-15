package main

import (
	"fmt"

	"{{.ModuleName}}/config"
	"{{.ModuleName}}/middleware"
	"{{.ModuleName}}/router"

	"github.com/gin-gonic/gin"
	"github.com/sirupsen/logrus"
)

func main() {
	gin.SetMode(config.Conf.Server.Mode)
	r := gin.Default()
	r.Use(middleware.Log())
	r.Use(middleware.Cors(middleware.DefaultCorsConfig()))
	router.RegisterRouter(r)
	config.PrintInfo()
	addr := fmt.Sprintf("%s:%d", config.Conf.Server.Host, config.Conf.Server.Port)
	if config.Conf.Server.Mode == gin.ReleaseMode {
		logrus.Infof("Listening and serving HTTP on %s", addr)
	}
	r.Run(addr)
}
