build:web_build web_run web_cp

web_build:
	GOARCH=wasm GOOS=js go build -o web/app.wasm
	go build

web_run:web_build
	./gendk

web_cp:
	mv *.html out
	mv *.js out
	mv *.css out
	mv *.webmanifest out
	cp web/* out/web