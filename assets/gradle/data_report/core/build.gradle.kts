plugins {
    id("java")
    id("buildsrc.convention.kotlin-jvm")
}

group = "com.sensorsdata.core"
version = "1.0.0"



dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    implementation(libs.sensorsAnalyticsSDK)
    implementation(libs.bundles.springBoot)
}

tasks.test {
    useJUnitPlatform()
}