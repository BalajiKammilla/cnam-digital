plugins {
    id("cnam.aws-cdk")
    id("application")
}

dependencies {
    implementation("software.amazon.awscdk:lambda:${Versions.cdkConstructs}")
    implementation("software.amazon.awscdk:s3:${Versions.cdkConstructs}")
    implementation("software.amazon.awscdk:ec2:${Versions.cdkConstructs}")
    implementation("software.amazon.awsconstructs:apigatewaylambda:2.41.0")
}

application {
    mainClass.set("ci.cnam.aws.deployment.cdk.MainKt")
}
