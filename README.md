![TeamCity Full Build Status](https://img.shields.io/teamcity/build/s/Result4kKotestMatchers_Build?server=https%3A%2F%2Fmrbergin.beta.teamcity.com&style=for-the-badge)
![TeamCity Coverage](https://img.shields.io/teamcity/coverage/Result4kKotestMatchers_Build?server=https%3A%2F%2Fmrbergin.beta.teamcity.com&style=for-the-badge)

# EksX

AWS EKS utility library born out of a need to replicate the following functionality, but without relying on the command
line:

https://awscli.amazonaws.com/v2/documentation/api/latest/reference/eks/get-token.html

## Example Gradle usage:

```kotlin
repositories {
    mavenCentral()
}
dependencies {
    testImplementation("dev.mrbergin:eksx:0.0.1")
}
```

## Example Usage:

```kotlin
val eksx = AwsEksX(
    accessKeyId = yourAccessKeyId,
    secretAccessKey = yourSecretAccessKey,
    region = yourRegion,
)

val token = eksx.getToken(
    clusterName = yourClusterName,
    roleArn = yourRoleArn, //optional
)

//use the token with your favourite K8s client!
```

For further examples (including Java, if you're into that) see the examples folder

## You have no tests, what kind of dev are you?!

I found the effort to value ratio unappealing. The logic in this code is mostly just stitching together a bunch of AWS
APIs, and the unit tests I started writing were a monstrosity of white box testing mocks which I'm not a fan of...

I wanted to write some integration tests with docker and localstack, but it appears EKS is only available with the pro
version of localstack, so I just decided to write examples and test them against a real EKS cluster instead.

PRs that educate me on how this could actually be unit / integration tested are welcome!
