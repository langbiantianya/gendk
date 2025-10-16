package main

import (
	"fmt"

	"{{.ModuleName}}/config"
	"{{.ModuleName}}/lib"
	"{{.ModuleName}}/middleware"
	"{{.ModuleName}}/router"

	"github.com/gin-contrib/cors"
	"github.com/gin-gonic/gin"
	"github.com/sirupsen/logrus"
)

func main() {
	lib.Banner(config.Conf)
	gin.SetMode(config.Conf.Server.Mode)
	r := gin.Default()
	r.Use(middleware.Log())
	r.Use(cors.Default())
	router.RegisterRouter(r)
	addr := fmt.Sprintf("%s:%d", config.Conf.Server.Host, config.Conf.Server.Port)
	if config.Conf.Server.Mode == gin.ReleaseMode {
		logrus.Infof("Listening and serving HTTP on %s", addr)
	}
	r.Run(addr)
}
