FROM golang:1.23-bookworm AS builder

WORKDIR /app

COPY   . .
RUN go mod tidy &&\
    go install fyne.io/tools/cmd/fyne@latest &&\
    fyne package -os web

FROM nginx:stable-alpine
COPY  --from=builder /app/wasm /usr/share/nginx/html
EXPOSE 80