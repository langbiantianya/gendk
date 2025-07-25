package view

import "github.com/maxence-charriere/go-app/v10/pkg/app"

type View interface {
	View() app.HTMLDiv
}

type GenView interface {
	View
	GenZip() ([]byte, string, error)
}
