package template

var LibsGradleMapJDK8 = map[string]string{
	"Hutool All":                           "cn.hutool:hutool-all:5.8.38",
	"OkHttp":                               "com.squareup.okhttp3:okhttp:4.12.0",
	"Spring Boot Starter JDBC":             "org.springframework.boot:spring-boot-starter-jdbc",
	"Spring Boot Starter Data JPA":         "org.springframework.boot:spring-boot-starter-data-jpa",
	"MyBatis Plus":                         "com.baomidou:mybatis-plus-boot-starter:3.5.12",
	"MySQL Connector/J":                    "com.mysql:mysql-connector-j:8.4.0",
	"Microsoft JDBC Driver For SQL Server": "com.microsoft.sqlserver:mssql-jdbc:12.10.0.jre8",
	"PostgreSQL JDBC Driver":               "org.postgresql:postgresql:42.7.6",
}

var LibsGradleMapJDK17 = map[string]string{
	"Hutool All":                           "cn.hutool:hutool-all:5.8.38",
	"OkHttp":                               "com.squareup.okhttp3:okhttp:4.12.0",
	"Spring Boot Starter JDBC":             "org.springframework.boot:spring-boot-starter-jdbc",
	"Spring Boot Starter Data JPA":         "org.springframework.boot:spring-boot-starter-data-jpa",
	"MyBatis Plus":                         "com.baomidou:mybatis-plus-spring-boot3-starter:3.5.12",
	"MySQL Connector/J":                    "com.mysql:mysql-connector-j:8.4.0",
	"Microsoft JDBC Driver For SQL Server": "com.microsoft.sqlserver:mssql-jdbc:12.10.0.jre11",
	"PostgreSQL JDBC Driver":               "org.postgresql:postgresql:42.7.6",
}

func GenGradleLibStr(jdkVersion string, extraLibs []string) string {
	switch jdkVersion {
	case "JDK 1.8":
		libStr := ""
		for _, v := range extraLibs {
			libStr += "    implementation (\"" + LibsGradleMapJDK8[v] + "\")\n"
		}
		return libStr
	case "JDK 17":
		libStr := ""
		for _, v := range extraLibs {
			libStr += "    implementation (\"" + LibsGradleMapJDK17[v] + "\")\n"
		}
		return libStr
	default:
		return ""
	}

}
