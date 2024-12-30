package ci.cnam.aws.deployment.cdk

import software.amazon.awscdk.Duration
import software.amazon.awscdk.Stack
import software.amazon.awscdk.StackProps
import software.amazon.awscdk.services.apigateway.LambdaIntegration
import software.amazon.awscdk.services.apigateway.LambdaIntegrationOptions
import software.amazon.awscdk.services.apigateway.RestApiProps
import software.amazon.awscdk.services.lambda.Code
import software.amazon.awscdk.services.lambda.Function
import software.amazon.awscdk.services.lambda.FunctionProps
import software.amazon.awscdk.services.lambda.Runtime
import software.amazon.awsconstructs.services.apigatewaylambda.ApiGatewayToLambda
import software.amazon.awsconstructs.services.apigatewaylambda.ApiGatewayToLambdaProps
import software.constructs.Construct

class CnamLambdaAppStack(
    scope: Construct?,
    id: String?,
    props: StackProps? = null
) : Stack(scope, id, props) {
    init {
        val projectName = "cnam-api-serverless-aws"
        val functionName = "cnam-api"
        val code = Code.fromAsset("../$projectName/build/libs/$projectName.zip")
        val functionProps = FunctionProps.builder()
            .functionName(functionName)
            .code(code)
            .handler("ci.cnam.api.serverless.aws.CnamServerlessApi")
            .memorySize(256)
            .timeout(Duration.seconds(60))
            //.role(lambdaRole)
            .runtime(Runtime.JAVA_11)
            .build()
        val function = Function(this, functionName, functionProps)

        val lambdaIntegrationOption = LambdaIntegrationOptions.builder()
            .proxy(true)
            .build()

        val lambdaIntegration = LambdaIntegration(function, lambdaIntegrationOption)
        val apiGatewayProps = RestApiProps.Builder()
            .defaultIntegration(lambdaIntegration)
            .build()
        val apiGatewayToLambdaProps = ApiGatewayToLambdaProps.Builder()
            .existingLambdaObj(function)
            .apiGatewayProps(apiGatewayProps)
            .build()
        val pattern = ApiGatewayToLambda(this, "ApiGatewayToLambdaPattern", apiGatewayToLambdaProps)
        println(pattern.apiGateway.url)
    }
}
