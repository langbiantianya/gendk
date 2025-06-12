rootProject.name = "{{.ProjectName}}"

val urlMaps = mapOf(
    "https://repo.maven.apache.org/maven2" to "https://maven.aliyun.com/repository/public",
    "https://repo1.maven.apache.org/maven2" to "https://mirrors.cloud.tencent.com/nexus/repository/maven-public/",
    "https://dl.google.com/dl/android/maven2" to "https://mirrors.cloud.tencent.com/nexus/repository/maven-public/",
    "https://plugins.gradle.org/m2" to "https://maven.aliyun.com/repository/gradle-plugin"
)

fun RepositoryHandler.enableMirror() {
    all {
        if (this is MavenArtifactRepository) {
            val originalUrl = this.url.toString().removeSuffix("/")
            urlMaps[originalUrl]?.let {
                logger.lifecycle("Repository[$url] is mirrored to $it")
                this.setUrl(it)
            }
        }
    }
}
gradle.allprojects {
    repositories {
        mavenCentral()
        maven("https://maven.aliyun.com/repository/gradle-plugin")

    }
    buildscript {
        repositories.enableMirror()
    }
    repositories.enableMirror()
}

gradle.beforeSettings {
    pluginManagement.repositories.enableMirror()
    dependencyResolutionManagement.repositories.enableMirror()
}