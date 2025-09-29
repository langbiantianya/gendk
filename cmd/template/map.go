package template

var LibsGradleMapJDK8 = map[string]string{
	"Hutool All":                           "cn.hutool:hutool-all:5.8.38",
	"Apache Commons IO":                    "commons-io:commons-io:2.20.0",
	"Apache Commons Lang":                  "org.apache.commons:commons-lang3:3.19.0",
	"Apache Commons Codec":                 "commons-codec:commons-codec:1.19.0",
	"OkHttp":                               "com.squareup.okhttp3:okhttp:4.12.0",
	"Spring Boot Starter JDBC":             "org.springframework.boot:spring-boot-starter-jdbc",
	"Spring Boot Starter Data JPA":         "org.springframework.boot:spring-boot-starter-data-jpa",
	"MyBatis":                              "org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.5",
	"MyBatis Plus":                         "com.baomidou:mybatis-plus-boot-starter:3.5.14",
	"Java ORM Bee":                         "org.teasoft:bee-all:2.5.2",
	"Hive JDBC":                            "org.apache.hive:hive-jdbc:4.1.0",
	"MySQL Connector/J":                    "com.mysql:mysql-connector-j:8.4.0",
	"Microsoft JDBC Driver For SQL Server": "com.microsoft.sqlserver:mssql-jdbc:12.10.0.jre8",
	"PostgreSQL JDBC Driver":               "org.postgresql:postgresql:42.7.6",
	"Oracle JDBC Driver":                   "com.oracle.database.jdbc:ojdbc8:23.9.0.25.07",
}

var LibsGradleMapJDK17 = map[string]string{
	"Hutool All":                           "cn.hutool:hutool-all:5.8.38",
	"Apache Commons IO":                    "commons-io:commons-io:2.20.0",
	"Apache Commons Lang":                  "org.apache.commons:commons-lang3:3.19.0",
	"Apache Commons Codec":                 "commons-codec:commons-codec:1.19.0",
	"OkHttp":                               "com.squareup.okhttp3:okhttp:4.12.0",
	"Spring Boot Starter JDBC":             "org.springframework.boot:spring-boot-starter-jdbc",
	"Spring Boot Starter Data JPA":         "org.springframework.boot:spring-boot-starter-data-jpa",
	"MyBatis":                              "org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.5",
	"MyBatis Plus":                         "com.baomidou:mybatis-plus-boot-starter:3.5.14",
	"Java ORM Bee":                         "org.teasoft:bee-all:2.5.2",
	"Hive JDBC":                            "org.apache.hive:hive-jdbc:4.1.0",
	"MySQL Connector/J":                    "com.mysql:mysql-connector-j:8.4.0",
	"Microsoft JDBC Driver For SQL Server": "com.microsoft.sqlserver:mssql-jdbc:12.10.0.jre11",
	"PostgreSQL JDBC Driver":               "org.postgresql:postgresql:42.7.6",
	"Oracle JDBC Driver":                   "com.oracle.database.jdbc:ojdbc11:23.9.0.25.07",
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
