package msg

import "mao/cmd/rules"

type ExData struct {
	Name string `json:"name"`
	Data map[string]struct {
		Type  rules.Type `json:"type"`
		Value any        `json:"value"`
	} `json:"data"`
}
