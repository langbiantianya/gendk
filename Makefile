windows:
	fyne-cross windows -arch=* -app-id com.lbty.gendk -name gendk

linux:
	fyne-cross linux -arch=* -app-id com.lbty.gendk -name gendk

mac:
	fyne-cross darwin -arch=* --macosx-sdk-path ~/SDKs/MacOSX11.sdk -app-id com.lbty.gendk -name gendk

android:
	fyne-cross android -arch=* -app-id com.lbty.gendk -name gendk

freebsd:
	fyne-cross freebsd -arch=* -app-id com.lbty.gendk -name gendk

web:
	fyne-cross web