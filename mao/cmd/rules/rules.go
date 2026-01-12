package rules

type Rules interface {
	GetAllRules() []RuleData
	// 模板名称
	GetRules(string) (RuleData, error)
	AddRules(name string, rule RuleData)
}

type RuleData struct {
	Name string `json:"name"`
	// 忽略json中的fileName字段,不带文件扩展名的文件名
	FileName string `json:"-"`
	// 变量替换规则（对应 JSON 中的 "variable" 字段）
	Variable []VariableRule `json:"variable"`
	// 路径/文件名替换（对应 JSON 中的 "path" 字段）
	Path []PathRule `json:"path"`
}

// type Type string
type Type string

const (
	TypeText Type = "text"
	TypeFile Type = "file"
)

// VariableRule 单个文件的变量替换规则
type VariableRule struct {
	FilePath string `json:"filePath"` // 目标文件路径
	Key      string `json:"key"`      // 变量键名
	Type     Type   `json:"type"`     // 变量类型
}

// Substitution 单个变量的替换详情
type Substitution struct {
	Key string `json:"key"` // 变量键名
}

// PathRule 路径/文件名替换规则
type PathRule struct {
	Source string `json:"source"` // 原始路径/文件名
	Target string `json:"target"` // 替换后的路径/文件名
}
