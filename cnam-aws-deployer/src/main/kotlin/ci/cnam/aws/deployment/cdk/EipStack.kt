package ci.cnam.aws.deployment.cdk

import software.amazon.awscdk.Stack
import software.amazon.awscdk.StackProps
import software.amazon.awscdk.services.ec2.CfnEIP
import software.constructs.Construct

class EipStack(
    scope: Construct,
    id: String,
    props: StackProps,
) : Stack(scope, "$id-eip-stack", props) {
    val eip: CfnEIP

    init {
        eip = CfnEIP(this, "$id-eip")
    }
}
