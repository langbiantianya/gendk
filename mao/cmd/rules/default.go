package rules

import (
	"encoding/json"
	"fmt"
	"log/slog"
	"mao/assets"
	"path/filepath"
	"strings"
)

type DefaultRules struct {
	ruleData map[string]RuleData
}

var MaoRules Rules = &DefaultRules{
	map[string]RuleData{},
}

func init() {
	dir, err := assets.StaticFs.ReadDir("rules")
	if err != nil {
		panic(err)
	}
	for _, d := range dir {
		if !d.IsDir() && strings.ToLower(filepath.Ext(d.Name())) == ".json" {
			// 这里读取json
			filePath := filepath.Join("rules", d.Name())
			// 读取 JSON 文件内容
			content, err := assets.StaticFs.ReadFile(filePath)
			if err != nil {
				slog.Default().Error(fmt.Sprintf("读取文件%s失败", d.Name()), "err", err)
				continue
			}
			var ruleData RuleData
			if err := json.Unmarshal(content, &ruleData); err != nil {
				slog.Default().Error(fmt.Sprintf("反序列化%s失败", d.Name()), "err", err)
				continue // 处理 JSON 格式错误
			}
			name := ruleData.Name
			// 1. 获取文件名（如 "data.json"）
			base := filepath.Base(d.Name())
			// 2. 获取扩展名（如 ".json"）
			ext := filepath.Ext(base)
			if name == "" {
				// 3. 截取文件名，去掉扩展名
				name = base[:len(base)-len(ext)]
				ruleData.FileName = name
			} else {
				ruleData.FileName = base[:len(base)-len(ext)]
			}

			MaoRules.AddRules(name, ruleData)
			slog.Default().Info(fmt.Sprintf("加载规则%s成功", d.Name()))
		}
	}
	slog.Default().Info("加载规则成功", "rules", MaoRules.GetAllRules())
}

func (r DefaultRules) GetAllRules() []RuleData {
	values := make([]RuleData, 0, len(r.ruleData))
	for _, value := range r.ruleData {
		values = append(values, value)
	}
	return values
}

func (r DefaultRules) GetRules(name string) (RuleData, error) {
	if rule, exists := r.ruleData[name]; exists {
		return rule, nil
	}
	return RuleData{}, fmt.Errorf("规则 %s 不存在", name)
}

func (r *DefaultRules) AddRules(name string, rule RuleData) {
	r.ruleData[name] = rule
}
