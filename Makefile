web_build:
	GOARCH=wasm GOOS=js go build -o web/app.wasm
	go build

web_run:
	./gendk

build:web_build web_run
