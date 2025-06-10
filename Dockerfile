FROM golang:1.23-bookworm AS builder

WORKDIR /app

COPY   . .
RUN go env -w GO111MODULE=on &&\
    go env -w GOPROXY=https://goproxy.cn,direct &&\
    go mod tidy &&\
    go install fyne.io/tools/cmd/fyne@latest &&\
    GOARCH=wasm GOOS=js go build -o web/app.wasm &&\
	go build &&\
    ./gendk

FROM nginx:stable-alpine
COPY  --from=builder /app /usr/share/nginx/html
EXPOSE 80