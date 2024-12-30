package ci.cnam.aws.deployment.cdk

import software.amazon.awscdk.Stack
import software.amazon.awscdk.StackProps
import software.amazon.awscdk.services.ec2.IPeer
import software.constructs.Construct

class CnamEc2AppProps(
    private val stackProps: StackProps,
    val allowedSshPeer: IPeer,
) : StackProps by stackProps

class CnamEc2App(scope: Construct, props: CnamEc2AppProps) : Stack(scope, "$ID-ec2-app-stack", props) {
    companion object {
        const val ID = "cnam"
    }

    init {
        //addDependency(vpcStack)
        val eipStack = EipStack(this, ID, props)
        val vpcStack = VpcStack(this, ID, props)
        Ec2Stack(this, ID, Ec2StackProps(props, vpcStack.vpc, vpcStack.securityGroup, eipStack.eip))
    }
}
