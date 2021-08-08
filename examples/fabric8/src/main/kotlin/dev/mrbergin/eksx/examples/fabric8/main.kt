package dev.mrbergin.eksx.examples.fabric8

import dev.mrbergin.eksx.AwsEksX
import io.fabric8.kubernetes.client.ConfigBuilder
import io.fabric8.kubernetes.client.DefaultKubernetesClient

/**
 * A simple program which prints the aws-auth configmap in a Kubernetes cluster managed by EKS
 *
 * See [ParsedArgs] for command line parameters
 */
fun main(args: Array<String>) {
    val parsedArgs = ParsedArgs(args)

    val subject = AwsEksX(
        accessKeyId = parsedArgs.accessKeyId,
        secretAccessKey = parsedArgs.secretAccessKey,
        region = parsedArgs.region,
    )

    val result = subject.getToken(parsedArgs.clusterName, parsedArgs.roleArn)

    val config = ConfigBuilder().withTrustCerts(true)
        .withOauthToken(result)
        .withMasterUrl(parsedArgs.masterUrl)
        .build()

    DefaultKubernetesClient(config).use {
        it.inNamespace("kube-system").configMaps().withName("aws-auth").get().run(::println)
    }
}

/**
 * Input parameters to make the example work
 *
 * [accessKeyId] - The access key of your IAM user (e.g. what you would enter with aws configure on the CLI)
 * [secretAccessKey] - The access key of your IAM user (e.g. what you would enter with aws configure on the CLI)
 * [region] - The access key of your IAM user (e.g. what you would enter with aws configure on the CLI)
 * [clusterName] - The cluster you wish to access (e.g. what you enter with aws eks get-token)
 * [masterUrl] - Your clusters API End point (arguably this could be deduced, but convenient to provide it instead)
 * [roleArn] - The cluster you wish to access (e.g. what you enter with aws eks get-token)
 */
class ParsedArgs(args: Array<String>) {
    val accessKeyId = args.mandatoryAt(0, "accessKeyId")
    val secretAccessKey = args.mandatoryAt(1, "secretAccessKey")
    val region = args.mandatoryAt(2, "region")
    val clusterName = args.mandatoryAt(3, "clusterName")
    val masterUrl = args.mandatoryAt(4, "masterUrl")
    val roleArn = args.optionalAt(5)

    private fun Array<String>.mandatoryAt(index: Int, fieldName: String): String =
        elementAtOrNull(index) ?: throw IllegalArgumentException("You must provide [$fieldName] at index [$index]")

    private fun Array<String>.optionalAt(index: Int): String? =
        elementAtOrNull(index)
}