package utils

import (
	"net/url"
	"strings"
)

// ... existing code ...

// GetLastPathSegment 从URL中获取最后一个路径段
func GetLastPathSegment(urlStr string) (string, error) {
	// 解析URL
	parsedURL, err := url.Parse(urlStr)
	if err != nil {
		return "", err
	}

	// 分割路径并获取最后一个非空段
	pathSegments := strings.Split(parsedURL.Path, "/")
	for i := len(pathSegments) - 1; i >= 0; i-- {
		if pathSegments[i] != "" {
			return pathSegments[i], nil
		}
	}

	// 如果没有路径段，返回空字符串
	return "", nil
}