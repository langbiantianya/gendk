windwos:
	fyne-cross windows -arch=* -app-id com.lbty.gendk

linux:
	fyne-cross linux -arch=* -app-id com.lbty.gendk

mac:
	fyne-cross darwin --macosx-sdk-path 'C:\Users\langb\Downloads\SDKs'  -arch=* -app-id com.lbty.gendk

android:
	fyne-cross android -arch=* -app-id com.lbty.gendk

freebsd:
	fyne-cross freebsd -arch=* -app-id com.lbty.gendk