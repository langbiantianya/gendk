package config

import (
	"fmt"
	"log"
	"os"

	"github.com/BurntSushi/toml"
	"github.com/gin-gonic/gin"
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
	defer func() {
		log.SetOutput(os.Stdout)
		log.Default().Printf("activity profile : %s", profileActive)
		log.SetOutput(os.Stderr)
	}()
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
