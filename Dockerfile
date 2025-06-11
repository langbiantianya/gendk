FROM docker.1ms.run/golang:1.24-bookworm AS builder

WORKDIR /app

COPY   . .
RUN go env -w GO111MODULE=on &&\
    go env -w GOPROXY=https://goproxy.cn,direct &&\
    go mod tidy &&\
    go install fyne.io/tools/cmd/fyne@latest &&\
    GOARCH=wasm GOOS=js go build -o web/app.wasm &&\
	go build &&\
    ./gendk

FROM docker.1ms.run/nginx:stable-alpine
COPY  --from=builder /app/*.html /usr/share/nginx/html
COPY  --from=builder /app/*.js /usr/share/nginx/html
COPY  --from=builder /app/manifest.webmanifest /usr/share/nginx/html
COPY  --from=builder /app/web/ /usr/share/nginx/html/web
EXPOSE 80