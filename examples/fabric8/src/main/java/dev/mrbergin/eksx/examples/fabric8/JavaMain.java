package dev.mrbergin.eksx.examples.fabric8;

import dev.mrbergin.eksx.AwsEksX;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;

public class JavaMain {

    /**
     * See kotlin version main.kt for documentation
     */
    public static void main(String[] args) {
        final var parsedArgs = new ParsedArgs(args);

        final var subject = new AwsEksX(
                parsedArgs.getAccessKeyId(),
                parsedArgs.getSecretAccessKey(),
                parsedArgs.getRegion()
        );

        final var result = subject.getToken(parsedArgs.getClusterName(), parsedArgs.getRoleArn());

        final var config = new ConfigBuilder().withTrustCerts(true)
                .withOauthToken(result)
                .withMasterUrl(parsedArgs.getMasterUrl())
                .build();

        try (final var client = new DefaultKubernetesClient(config)) {
            System.out.println(client.inNamespace("kube-system").configMaps().withName("aws-auth").get());
        }
    }
}
