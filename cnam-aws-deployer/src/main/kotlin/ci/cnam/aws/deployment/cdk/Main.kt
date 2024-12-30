package ci.cnam.aws.deployment.cdk

import software.amazon.awscdk.App
import software.amazon.awscdk.Environment
import software.amazon.awscdk.StackProps
import software.amazon.awscdk.services.ec2.Peer

fun main() {
    val app = App()

    val env = Environment.builder()
        .account(
            System.getenv("CDK_DEFAULT_ACCOUNT")
                ?: throw IllegalStateException("CDK_DEFAULT_ACCOUNT environment variable not set")
        )
        .region(System.getenv("CDK_DEFAULT_REGION") ?: "eu-west-2")
        .build()
    val props = StackProps.builder()
        .env(env)
        .stackName("cnam-stack")
        .description("CNAM Stack")
        .build()

    //val vpcStack = VpcStack(app, props)
    //Ec2Stack(app, props)
    CnamEc2App(app, CnamEc2AppProps(props, Peer.ipv4("86.7.4.142/32")))

    app.synth()
}
