
plugins {
    id("org.springframework.boot").version("2.7.18")
    id("io.spring.dependency-management").version("1.1.7")
    id("java")
    kotlin("jvm") version "2.2.0"
}

group = "com.sensorsdata"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of({{.JdkVersionNumber}})
    }
    targetCompatibility = JavaVersion.{{.JdkVersion}}
    sourceCompatibility = JavaVersion.{{.JdkVersion}}
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
{{.Libs}}
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("cn.hutool:hutool-all:5.8.40")
    implementation("org.apache.commons:commons-lang3:3.19.0")
    implementation("commons-codec:commons-codec:1.19.0")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    developmentOnly("org.springframework.boot:spring-boot-starter-actuator")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// ... 已有任务配置 ...

tasks.register<Delete>("cleanLibs") {
    delete(fileTree("dingkai/${gradle.rootProject.name}/lib"))
}

tasks.register<Copy>("copyDependencies") {
    dependsOn("cleanLibs")
    from(configurations.runtimeClasspath)
    into("dingkai/${gradle.rootProject.name}/lib/")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.register<Delete>("cleanResources") {
    delete(fileTree("dingkai/${gradle.rootProject.name}/resources/"))
}

tasks.register<Delete>("cleanBuildJar") {
    delete(file("dingkai/${gradle.rootProject.name}/server.jar"))
}


tasks.register<Copy>("copyResources") {
    dependsOn("cleanResources")
    from("build/resources/main")
    into("dingkai/${gradle.rootProject.name}/resources/")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.register<Copy>("copyBuildJar") {
    dependsOn("cleanBuildJar")
    from(file("build/libs/server.jar"))
    into("dingkai/${gradle.rootProject.name}/")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

// 可选：将复制依赖任务绑定到 build 任务
tasks.named("jar") {
    finalizedBy("copyBuildJar")
    finalizedBy("copyDependencies")
    finalizedBy("copyResources")
}

tasks.named("clean") {
    finalizedBy("cleanLibs")
    finalizedBy("cleanResources")
    finalizedBy("cleanBuildJar")
}

// 禁用 Spring Boot 的 fat jar 打包
tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

// 启用标准 jar 打包（不包含依赖）
tasks.getByName<Jar>("jar") {
    enabled = true
    archiveFileName.set("server.jar")
    manifest {
        attributes(
            "Manifest-Version" to "1.0",
            "Main-Class" to "com.sensorsdata.Main",
            "Spring-Boot-Version" to "2.7.18",
            "Class-Path" to configurations.runtimeClasspath.get()
                .filter { it.name.endsWith(".jar") }
                .map { "lib/${it.name}" }
                .plus("resources/") // 添加 resources 目录
                .joinToString(" "),
            // 新增JDK版本和Gradle版本信息
            "Build-Jdk-Spec" to JavaVersion.current().toString(),
            "Gradle-Version" to project.gradle.gradleVersion
        )
    }
    // 添加资源排除规则

    exclude(
        "**/*.properties",
        "**/*.yml",
        "**/*.yaml",
        "**/*.xml",
        "static/**",
        "templates/**"
    )

}
