build:
	mkdir -p build
	cp -r static build/
	cp *.toml build
	go mod tidy
	CGO_ENABLED=0 go build -o build/server.exe
	cp -r build/* dingkai/{{.ModuleName}}
	rm -r build
run:
	go run main.go
