package template

type WebTemplateData struct {
	SpringBootVersion int
	Libs              string
	ProjectName       string
	ModuleName        string
	JdkVersion        string
}

func NewWebTemplateData(
	SpringBootVersion int,
	Libs string,
	ProjectName string,
	ModuleName string,
	JdkVersion string) WebTemplateData {

	return WebTemplateData{
		SpringBootVersion: SpringBootVersion,
		Libs:              Libs,
		ProjectName:       ProjectName,
		ModuleName:        ModuleName,
		JdkVersion:        JdkVersion,
	}

}

var LibsMapJDK8 = map[string]string{
	"Hutool All":                           "cn.hutool:hutool-all:5.8.38",
	"OkHttp":                               "com.squareup.okhttp3:okhttp:4.12.0",
	"Spring Boot Starter JDBC":             "org.springframework.boot:spring-boot-starter-jdbc",
	"Spring Boot Starter Data JPA":         "org.springframework.boot:spring-boot-starter-data-jpa",
	"MyBatis Plus":                         "com.baomidou:mybatis-plus-boot-starter:3.5.12",
	"MySQL Connector/J":                    "com.mysql:mysql-connector-j:8.4.0",
	"Microsoft JDBC Driver For SQL Server": "com.microsoft.sqlserver:mssql-jdbc:12.10.0.jre8",
	"PostgreSQL JDBC Driver":               "org.postgresql:postgresql:42.7.6",
}

var LibsMapJDK17 = map[string]string{
	"Hutool All":                           "cn.hutool:hutool-all:5.8.38",
	"OkHttp":                               "com.squareup.okhttp3:okhttp:4.12.0",
	"Spring Boot Starter JDBC":             "org.springframework.boot:spring-boot-starter-jdbc",
	"Spring Boot Starter Data JPA":         "org.springframework.boot:spring-boot-starter-data-jpa",
	"MyBatis Plus":                         "com.baomidou:mybatis-plus-spring-boot3-starter:3.5.12",
	"MySQL Connector/J":                    "com.mysql:mysql-connector-j:8.4.0",
	"Microsoft JDBC Driver For SQL Server": "com.microsoft.sqlserver:mssql-jdbc:12.10.0.jre11",
	"PostgreSQL JDBC Driver":               "org.postgresql:postgresql:42.7.6",
}
