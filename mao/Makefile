build:
	GOOS=js GOARCH=wasm tinygo build -o ../static/wasm/app.wasm -no-debug
build_go:
	GOOS=js GOARCH=wasm CGO_ENABLED=0 go build -o ../static/wasm/app.wasm main.go