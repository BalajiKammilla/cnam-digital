package ci.cnam.aws.deployment.cdk

import software.amazon.awscdk.Stack
import software.amazon.awscdk.StackProps
import software.amazon.awscdk.services.ec2.IpAddresses
import software.amazon.awscdk.services.ec2.Peer
import software.amazon.awscdk.services.ec2.Port
import software.amazon.awscdk.services.ec2.SecurityGroup
import software.amazon.awscdk.services.ec2.SubnetConfiguration
import software.amazon.awscdk.services.ec2.SubnetType
import software.amazon.awscdk.services.ec2.SubnetType.PUBLIC
import software.amazon.awscdk.services.ec2.Vpc
import software.constructs.Construct

class VpcStack(
    scope: Construct,
    id: String,
    props: StackProps,
) : Stack(scope, "$id-vpc-stack", props) {
    companion object {
        private fun subnetConfiguration(name: String, type: SubnetType, cidrMask: Short = 24): SubnetConfiguration =
            SubnetConfiguration.builder()
                .name(name)
                .subnetType(type)
                .cidrMask(cidrMask)
                .build()
    }

    val vpc: Vpc
    val securityGroup: SecurityGroup

    init {
        vpc = Vpc.Builder.create(this, "$id-vpc")
            .maxAzs(3)
            .ipAddresses(IpAddresses.cidr("10.0.0.0/24"))
            .subnetConfiguration(listOf(subnetConfiguration("$id-subnet-public", PUBLIC, 28)))
            .vpcName("$id-vpc")
            .build()

        securityGroup = SecurityGroup.Builder.create(this, "$id-sg-public")
            .allowAllOutbound(true)
            .securityGroupName("$id-sg-public")
            .vpc(vpc)
            .build()

        securityGroup.addIngressRule(Peer.ipv4("86.7.4.142/32"), Port.tcp(22), "Allow SSH access")
        securityGroup.addIngressRule(Peer.anyIpv4(), Port.tcp(9191), "Allow HTTP access")
        securityGroup.addIngressRule(Peer.anyIpv4(), Port.tcp(15211), "Allow Oracle access")
    }
}
