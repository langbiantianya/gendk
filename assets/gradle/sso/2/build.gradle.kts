plugins {
    id("org.springframework.boot").version("2.4.2")
    id("io.spring.dependency-management").version("1.0.11.RELEASE")
    id("java")
}

group = "com.sensorsdata.analytics.sso"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.{{.JdkVersion}}

repositories {
    maven("https://jfrog-internal.sensorsdata.cn/artifactory/maven-public/")
    mavenCentral()

}

dependencies {
    implementation("com.sensorsdata.analytics.sso:ssocommon:2.1.1") {
        exclude(group = "org.springframework", module = "spring-web")
        exclude(group = "org.springframework", module = "spring-webmvc")
//        exclude(group = "com.hazelcast", module = "hazelcast")
    }
    implementation("com.hazelcast:hazelcast:3.12.12")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("com.mysql:mysql-connector-j:8.2.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
//    implementation("com.sensorsdata.airline:guidance-dingkai-mysql:1.0.1-SNAPSHOT")
    compileOnly("org.projectlombok:lombok:1.18.16")
    annotationProcessor("org.projectlombok:lombok:1.18.16")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.security:spring-security-saml2-service-provider")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")


}

tasks.withType<Test> {
    useJUnitPlatform()
}

// ... 已有任务配置 ...

tasks.register<Delete>("cleanLibs") {
    delete(fileTree("dingkai/sso/lib"))
}

tasks.register<Copy>("copyDependencies") {
    dependsOn("cleanLibs")
    from(configurations.runtimeClasspath)
    into("dingkai/sso/lib/")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
tasks.register<Delete>("cleanConf") {
    delete(fileTree("dingkai/sso/conf"))
}
tasks.register<Delete>("cleanResources") {
    delete(fileTree("dingkai/sso/resources/"))
}

tasks.register<Delete>("cleanBuildJar") {
    delete(file("dingkai/sso/sso.jar"))
}

tasks.register<Copy>("copyConf") {
    dependsOn("cleanConf")
    from("build/resources/main/saml")
    into("dingkai/sso/conf/saml")
    exclude(
        "sensorsKeystore.jks",
        "idp.cer",
        "SensorsDataWebAnalytics-RAW.cer"

    )
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.register<Copy>("copyResources") {
    dependsOn("cleanResources")
    from("build/resources/main").exclude(
        "saml/*.xml"
    )
    into("dingkai/sso/resources/")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.register<Copy>("copyBuildJar") {
    dependsOn("cleanBuildJar")
    from(file("build/libs/sso.jar"))
    into("dingkai/sso/")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

// 可选：将复制依赖任务绑定到 build 任务
tasks.named("jar") {
    finalizedBy("copyBuildJar")
    finalizedBy("copyDependencies")
    finalizedBy("copyConf")
    finalizedBy("copyResources")
}
tasks.named("clean") {
    finalizedBy("cleanLibs")
    finalizedBy("cleanResources")
    finalizedBy("cleanConf")
    finalizedBy("cleanBuildJar")
}
// 禁用 Spring Boot 的 fat jar 打包
tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

// 启用标准 jar 打包（不包含依赖）
tasks.getByName<Jar>("jar") {
    enabled = true
    archiveFileName.set("sso.jar")
    manifest {
        attributes(
            "Manifest-Version" to "1.0",
            "Main-Class" to "com.sensorsdata.analytics.sso.Main",
            "Class-Path" to configurations.runtimeClasspath.get()
                .filter { it.name.endsWith(".jar") }
                .map { "lib/${it.name}" }
                .plus("resources/") // 添加 resources 目录
                .joinToString(" ")
        )
    }
    // 添加资源排除规则

    exclude(
        "saml/**",
        "**/*.properties",
        "**/*.yml",
        "static/**",
        "templates/**"
    )

}

