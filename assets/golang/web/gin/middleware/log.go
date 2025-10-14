package middleware

import (
	"io"
	"os"
	"strings"

	"github.com/gin-gonic/gin"
	"github.com/sirupsen/logrus"
)

type LevelWriter struct {
	infoWriter  io.Writer // 用于info及以下级别
	errorWriter io.Writer // 用于error及以上级别
}

func (w *LevelWriter) Write(p []byte) (n int, err error) {
	// 简单判断日志级别，实际应用中可能需要更健壮的判断
	if strings.Contains(string(p), "level=error") ||
		strings.Contains(string(p), "level=fatal") ||
		strings.Contains(string(p), "level=panic") {
		return w.errorWriter.Write(p)
	}
	return w.infoWriter.Write(p)
}
func init() {
	logrus.SetFormatter(&logrus.TextFormatter{})
	multiWriter := &LevelWriter{
		infoWriter:  os.Stdout,
		errorWriter: os.Stderr,
	}

	// 设置logrus的输出为我们自定义的多输出器
	logrus.SetOutput(multiWriter)

	// 设置日志格式为文本格式（也可以设置为JSON格式）
	logrus.SetFormatter(&logrus.TextFormatter{
		FullTimestamp: true,
	})

	// 设置日志级别为info，这样info及以上级别都会被记录
	logrus.SetLevel(logrus.InfoLevel)
}

func Log() gin.HandlerFunc {
	return gin.LoggerWithWriter(logrus.StandardLogger().Out)
}
