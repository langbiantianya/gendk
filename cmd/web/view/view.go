package view

import "github.com/maxence-charriere/go-app/v10/pkg/app"

type View interface {
	View() app.UI
}

type GenView interface {
	View
	GenZip() ([]byte, error)
}
