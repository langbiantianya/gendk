web_build:
	GOARCH=wasm GOOS=js go build -o web/app.wasm
	go build

web:web_build
	./gendk
