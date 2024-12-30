package ci.cnam.aws.deployment.cdk

import software.amazon.awscdk.Stack
import software.amazon.awscdk.StackProps
import software.amazon.awscdk.services.ec2.AmazonLinuxEdition
import software.amazon.awscdk.services.ec2.AmazonLinuxGeneration
import software.amazon.awscdk.services.ec2.AmazonLinuxImage
import software.amazon.awscdk.services.ec2.AmazonLinuxStorage
import software.amazon.awscdk.services.ec2.AmazonLinuxVirt
import software.amazon.awscdk.services.ec2.BlockDevice
import software.amazon.awscdk.services.ec2.BlockDeviceVolume
import software.amazon.awscdk.services.ec2.CfnEIP
import software.amazon.awscdk.services.ec2.CfnEIPAssociation
import software.amazon.awscdk.services.ec2.Instance
import software.amazon.awscdk.services.ec2.InstanceClass
import software.amazon.awscdk.services.ec2.InstanceSize
import software.amazon.awscdk.services.ec2.InstanceType
import software.amazon.awscdk.services.ec2.SecurityGroup
import software.amazon.awscdk.services.ec2.SubnetConfiguration
import software.amazon.awscdk.services.ec2.SubnetSelection
import software.amazon.awscdk.services.ec2.SubnetType
import software.amazon.awscdk.services.ec2.SubnetType.PUBLIC
import software.amazon.awscdk.services.ec2.Vpc
import software.constructs.Construct

class Ec2StackProps(
    private val stackProps: StackProps,
    val vpc: Vpc,
    val securityGroup: SecurityGroup,
    val elasticIp: CfnEIP,
) : StackProps by stackProps

class Ec2Stack(
    scope: Construct,
    id: String,
    props: Ec2StackProps,
) : Stack(scope, "$id-ec2-stack", props) {
    companion object {
        private fun subnetConfiguration(name: String, type: SubnetType, cidrMask: Short = 24): SubnetConfiguration =
            SubnetConfiguration.builder()
                .name(name)
                .subnetType(type)
                .cidrMask(cidrMask)
                .build()
    }

    init {
        //addDependency(vpcStack)
        val machineImage: AmazonLinuxImage = AmazonLinuxImage.Builder.create()
            .generation(AmazonLinuxGeneration.AMAZON_LINUX_2)
            .edition(AmazonLinuxEdition.STANDARD)
            .virtualization(AmazonLinuxVirt.HVM)
            .storage(AmazonLinuxStorage.GENERAL_PURPOSE)
            .build()

        val rootVolume = BlockDevice.builder()
            .deviceName("/dev/xvda")
            .volume(BlockDeviceVolume.ebs(24))
            .build()

        val ec2Instance = Instance.Builder.create(this, "$id-ec2")
            .vpc(props.vpc)
            .instanceType(InstanceType.of(InstanceClass.T2, InstanceSize.SMALL))
            .keyName("cnam")
            .machineImage(machineImage)
            .blockDevices(mutableListOf(rootVolume))
            .securityGroup(props.securityGroup)
            .vpcSubnets(SubnetSelection.builder().subnetType(PUBLIC).build())
            .build()

        val userDataScript = this::class.java.getResource("./ec2-user-data.sh")?.readText()
        if (userDataScript != null) {
            ec2Instance.addUserData(userDataScript)
        }

        CfnEIPAssociation.Builder.create(this, "$id-eip-association")
            .eip(props.elasticIp.ref)
            .instanceId(ec2Instance.instanceId)
            .build()
    }
}
