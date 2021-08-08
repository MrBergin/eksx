plugins {
    id("conventions-kotlin")
    id("conventions-publish")
}

project.name

repositories {
    mavenCentral()
}

dependencies {
    implementation(Libs.awsAuth)
    implementation(Libs.awsSts)
}
