import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

version = "2021.1"

project {
    buildType(Build)
    buildType(Release)
}

object Build : BuildType({
    name = "Build"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        gradle {
            tasks = "clean build"
            buildFile = "buildSrc/build.gradle.kts"
            gradleWrapperPath = ""
        }
    }

    triggers {
        vcs {
        }
    }
})

object Release : BuildType({
    name = "Release"
    allowExternalStatus = true

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        gradle {
            tasks = ":eksx:publishMavenPublicationToSonatypeStagingRepository"
            jdkHome = "%env.JDK_11_0_x64%"
        }
    }

    params {
        text(
            name = "env.ORG_GRADLE_PROJECT_ossrhUsername",
            value = "mrbergin",
            readOnly = true,
            allowEmpty = true,
        )
        password(
            name = "env.ORG_GRADLE_PROJECT_ossrhPassword",
            value = "credentialsJSON:f51c6ca9-d418-4fc8-a59e-a8719a3cdb18",
            display = ParameterDisplay.HIDDEN,
            readOnly = true,
        )
        password(
            name = "env.ORG_GRADLE_PROJECT_signingKeyId",
            value = "credentialsJSON:be0884e6-8e04-4d8a-91a3-630a905d0ea1",
            display = ParameterDisplay.HIDDEN,
        )
        password(
            name = "env.ORG_GRADLE_PROJECT_signingPassword",
            value = "credentialsJSON:f51c6ca9-d418-4fc8-a59e-a8719a3cdb18",
            display = ParameterDisplay.HIDDEN,
        )
        password(
            name = "env.ORG_GRADLE_PROJECT_signingKey",
            value = "credentialsJSON:ac4d325d-1d86-4fce-ac10-3302efd2445f",
            display = ParameterDisplay.HIDDEN,
        )
    }
})
