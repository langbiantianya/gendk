package public

import (
	"encoding/json"
	"log/slog"
	"mao/cmd/msg"
	"mao/cmd/rules"
	"mao/cmd/template"
	"syscall/js"
)

// 定义可被JS调用的函数：处理参数并返回结果
func Add(this js.Value, args []js.Value) interface{} {
	// 检查参数数量和类型（避免panic）
	if len(args) < 2 {
		return js.ValueOf("参数不足，需要2个数字")
	}
	a := args[0].Float() // 转换JS数字为float64
	b := args[1].Float()
	return js.ValueOf(a + b) // 返回结果（转换为JS类型）
}

// 这个方法接收前端传递过来的json字符串
func GenZip(this js.Value, args []js.Value) interface{} {
	// 将data转换为字符串
	if len(args) < 1 {
		return js.ValueOf("参数不足，需要1个json字符串")
	}
	dataStr := args[0].String()
	// 反序列化为ExData
	var exData msg.ExData
	err := json.Unmarshal([]byte(dataStr), &exData)
	if err != nil {
		slog.Error("json反序列化失败", "异常", err)
		return js.ValueOf("json反序列化失败")
	}
	slog.Info("GenZip", "exData", exData)
	// 遍历ExData的data将data转为map[string]string
	data := make(map[string]string)

	for key, item := range exData.Data {
		switch item.Type {
		case rules.TypeText:
			data[key] = item.Value.(string)
		case rules.TypeFile:
			data[key] = item.Value.(string)
		default:
			slog.Error("目前只处理文本类型", "key", key, "type", item.Type)
			return js.ValueOf("目前只处理文本类型")
		}
	}
	slog.Info("收到", "exData", exData, "data", data)
	// 使用name获取规则
	rule, err := rules.MaoRules.GetRules(exData.Name)
	if err != nil {
		slog.Error("获取规则失败", "异常", err)
		return js.ValueOf(err.Error())
	}

	render := template.NewTempate(data, rule)
	b64Data, err := render.B64Zip()
	if err != nil {
		slog.Error("渲染失败", "异常", err)
		return js.ValueOf(err.Error())
	}
	return js.ValueOf(b64Data)
}
