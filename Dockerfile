FROM docker.1ms.run/golang:1.24-bookworm AS builder

RUN apt-get update &&\
    apt-get upgrade -y &&\
    apt install libgl1-mesa-dev xorg-dev mesa-utils -y

FROM builder AS build

WORKDIR /app

ENV BUILD=1 WEB=1

COPY   . .

RUN go env -w GO111MODULE=on &&\
    go env -w GOPROXY=https://goproxy.cn,direct &&\
    go mod tidy &&\
    go install fyne.io/tools/cmd/fyne@latest &&\
    GOARCH=wasm GOOS=js go build -o web/app.wasm &&\
	go build &&\
    ./gendk

FROM docker.1ms.run/nginx:stable-alpine
COPY  --from=build /app/*.html /usr/share/nginx/html/
COPY  --from=build /app/*.js /usr/share/nginx/html/
COPY  --from=build /app/web/ /usr/share/nginx/html/web/
EXPOSE 80