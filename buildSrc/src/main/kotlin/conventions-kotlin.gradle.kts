import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(Versions.java))
        vendor.set(JvmVendorSpec.ADOPTOPENJDK)
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = Versions.java
}

tasks.test {
    useJUnitPlatform()
}