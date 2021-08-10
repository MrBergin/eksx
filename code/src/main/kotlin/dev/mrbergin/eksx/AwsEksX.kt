package dev.mrbergin.eksx

import software.amazon.awssdk.auth.credentials.*
import software.amazon.awssdk.auth.signer.Aws4Signer
import software.amazon.awssdk.auth.signer.params.Aws4PresignerParams
import software.amazon.awssdk.http.SdkHttpFullRequest
import software.amazon.awssdk.http.SdkHttpMethod
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sts.StsClient
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest
import java.net.URI
import java.time.Clock
import java.time.Instant
import java.util.*

/**
 * The purpose of this class is to fulfil to functionality provided at the following URL
 *
 * https://awscli.amazonaws.com/v2/documentation/api/latest/reference/eks/get-token.html
 *
 * The constructor params are supposed to behave as if you have called "aws configure" on the command line, where
 * you are prompted to enter [accessKeyId], [secretAccessKey] & [region]
 */
class AwsEksX(
    private val accessKeyId: String,
    private val secretAccessKey: String,
    private val region: String,
) {
    private val credentialsProvider =
        StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey))

    /**
     * This function is to behave as if you called:
     *
     * aws eks get-token --cluster-name <value> [--role-arn <value>]
     *
     * The return value is a token that you may use to access your EKS managed Kubernetes API Endpoint
     */
    @JvmOverloads
    fun getToken(
        clusterName: String,
        roleArn: String? = null,
    ): String {
        val request = sdkHttpFullRequest(region, clusterName)

        val credentials = if (roleArn == null) {
            credentialsProvider.resolveCredentials()
        } else {
            credentialsProvider.assumeRole(roleArn, region)
        }

        val presignerParams = aws4PresignerParams(region, credentials)

        val signedRequest = Aws4Signer.create()
            .presign(request, presignerParams)
            .uri
            .toString()
            .toByteArray()

        val encodedUrl = Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(signedRequest)

        return "k8s-aws-v1.$encodedUrl"
    }

    private fun aws4PresignerParams(
        region: String,
        credentials: AwsCredentials
    ): Aws4PresignerParams {
        return Aws4PresignerParams.builder()
            .awsCredentials(credentials)
            .signingRegion(Region.of(region))
            .signingName("sts")
            .signingClockOverride(Clock.systemUTC())
            .expirationTime(Instant.now().plusSeconds(60))
            .build()
    }

    private fun sdkHttpFullRequest(
        region: String,
        clusterName: String,
    ): SdkHttpFullRequest = SdkHttpFullRequest
        .builder()
        .method(SdkHttpMethod.GET)
        .uri(URI("https", "sts.$region.amazonaws.com", "/", null))
        .appendHeader("x-k8s-aws-id", clusterName)
        .appendRawQueryParameter("Action", "GetCallerIdentity")
        .appendRawQueryParameter("Version", "2011-06-15")
        .build()

    private fun AwsCredentialsProvider.assumeRole(role: String, region: String): AwsCredentials {
        val stsCredentials = StsClient.builder()
            .region(Region.of(region))
            .credentialsProvider(this)
            .build()
            .assumeRole(AssumeRoleRequest.builder().roleSessionName("EKSGetTokenAuth").roleArn(role).build())
            .credentials()

        return AwsSessionCredentials.create(
            stsCredentials.accessKeyId(),
            stsCredentials.secretAccessKey(),
            stsCredentials.sessionToken()
        )
    }
}