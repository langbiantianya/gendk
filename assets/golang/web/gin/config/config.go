package config

import (
	"fmt"
	"os"

	"github.com/BurntSushi/toml"
	"github.com/gin-gonic/gin"
	"github.com/sirupsen/logrus"
)

type Config struct {
	Server struct {
		Host string
		Port int
		Mode string
	}
}

var Conf Config

func init() {
	profileActive := os.Getenv("PROFILE_ACTIVE")
	if profileActive != "" {
		profileActive = "-" + profileActive
	}
	profileActive = fmt.Sprintf("application%s.toml", profileActive)
	_, err := toml.DecodeFile(profileActive, &Conf)
	if err != nil {
		panic(err)
	}

	if Conf.Server.Host == "" {
		Conf.Server.Host = "0.0.0.0"
	}
	if Conf.Server.Port == 0 {
		Conf.Server.Port = 8080
	}
	if Conf.Server.Mode == "" {
		Conf.Server.Mode = gin.DebugMode
	}
}

func PrintInfo() {
	profileActive := os.Getenv("PROFILE_ACTIVE")
	if profileActive != "" {
		profileActive = "-" + profileActive
	}
	profileActive = fmt.Sprintf("application%s.toml", profileActive)
	logrus.Infof("activity profile : %s", profileActive)
}
