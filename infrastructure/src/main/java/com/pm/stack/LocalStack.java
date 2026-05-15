package com.pm.stack;

import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.*;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ecs.Protocol;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.msk.CfnCluster;
import software.amazon.awscdk.services.rds.*;
import software.amazon.awscdk.services.route53.CfnHealthCheck;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalStack extends Stack {

    private final Vpc vpc;
    private final Cluster ecsCluster;

    public LocalStack(final App scope, final String id, final StackProps props) {
        super(scope, id, props);
        this.vpc = createVpc();

        DatabaseInstance authServiceDb = createDatabaseInstance("AuthServiceDb", "auth-service-db");
        DatabaseInstance patientServiceDb = createDatabaseInstance("PatientServiceDb", "patient-service-db");  

        CfnHealthCheck authDbHealthCheck = createHealthCheck(authServiceDb, "AuthServiceDbHealthCheck");       
        CfnHealthCheck patientDbHealthCheck = createHealthCheck(patientServiceDb, "PatientServiceDbHealthCheck");

        CfnCluster mskCluster = createMskCluster();

        this.ecsCluster = createEcsCluster();

        FargateService authService = createFargateService(
                "AuthService",
                "auth-service",
                List.of(8084),
                authServiceDb,
                Map.of("JWT_SECRET", "+7FneszQDXWwcp/ifLPI4HBLz/H0Rvfp/LLM3fgru3A=")
        );

        authService.getNode().addDependency(authDbHealthCheck);
        authService.getNode().addDependency(authServiceDb);
    }

    private FargateService createFargateService(String id, String serviceName, List<Integer> ports,
                                                DatabaseInstance db, Map<String, String> additionalEnvVars) {

        FargateTaskDefinition taskDefinition =
                FargateTaskDefinition.Builder
                        .create(this, id + "TaskDef")
                        .cpu(256)
                        .memoryLimitMiB(512)
                        .build();

        ContainerDefinitionOptions.Builder containerOptions = ContainerDefinitionOptions.builder()
                .image(ContainerImage.fromRegistry(serviceName))
                .portMappings(ports.stream()
                        .map(port -> PortMapping.builder()
                                .containerPort(port)
                                .hostPort(port)
                                .protocol(Protocol.TCP)
                                .build())
                        .toList())
                .logging(LogDriver.awsLogs(AwsLogDriverProps.builder()
                        .logGroup(LogGroup.Builder.create(this, id + "LogGroup")
                                .logGroupName("/ecs/" + serviceName)
                                .removalPolicy(RemovalPolicy.DESTROY)
                                .retention(RetentionDays.ONE_DAY)
                                .build())
                        .build()));

        Map<String, String> env = new HashMap<>();
        env.put("SPRING_KAFKA_BOOTSTRAP_SERVERS", "localhost.localhost.cloud:4510, localhost.localhost.cloud:4511, localhost.localhost.cloud:4512");

        if (additionalEnvVars != null) {
            env.putAll(additionalEnvVars);
        }

        if(db != null) {
            env.put("SPRING_DATASOURCE_URL", "jdbc:postgresql://%s:%s/%s-db".formatted
                    (db.getDbInstanceEndpointAddress(), db.getDbInstanceEndpointPort(), serviceName));
            env.put("SPRING_DATASOURCE_USERNAME", "admin_user");
            env.put("SPRING_DATASOURCE_PASSWORD", db.getSecret().secretValueFromJson("password").toString());
            env.put("SPRING_DATASOURCE_DRIVER_CLASS_NAME", "org.postgresql.Driver");
            env.put("SPRING_JPA_HIBERNATE_DDL_AUTO", "update");
            env.put("SPRING_SQL_INIT_MODE", "always");
            env.put("SPRING_DATASOURCE_HIKARI_INITIALIZATION_FAIL_TIMEOUT", "60000");
        }

        containerOptions.environment(env);
        taskDefinition.addContainer(serviceName + "Container", containerOptions.build());

        return FargateService.Builder
                .create(this, id)
                .serviceName(serviceName)
                .cluster(this.ecsCluster)
                .taskDefinition(taskDefinition)
                .assignPublicIp(false)
//                .desiredCount(1)
                .build();

    }

    private Cluster createEcsCluster() {
        return Cluster.Builder.create(this, "PatientManagementCluster")
                .vpc(this.vpc)
                .defaultCloudMapNamespace(CloudMapNamespaceOptions.builder()
                        .name("patient-management.local")
                        .build())
                .build();
    }

    private Vpc createVpc() {
        return Vpc.Builder
                .create(this, "PatientManagementVpc")
                .vpcName("PatientManagementVpc")
                .maxAzs(2).build();
    }

    private DatabaseInstance createDatabaseInstance(String id, String dbName) {
        return DatabaseInstance.Builder
                .create(this, id)
                .instanceIdentifier(id)
                .databaseName(dbName)
                .engine(DatabaseInstanceEngine.postgres(PostgresInstanceEngineProps.builder()
                        .version(PostgresEngineVersion.VER_18).build()))
                .vpc(this.vpc)
                .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
                .allocatedStorage(20)
                .credentials(Credentials.fromGeneratedSecret("admin_user"))
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();
    }

    private CfnHealthCheck createHealthCheck(DatabaseInstance dbInstance, String id) {
        return CfnHealthCheck.Builder
                .create(this, id)
                .healthCheckConfig(CfnHealthCheck.HealthCheckConfigProperty.builder()
                        .type("TCP")
                        .port(Token.asNumber(dbInstance.getDbInstanceEndpointPort()))
                        .ipAddress(dbInstance.getDbInstanceEndpointAddress())
                        .requestInterval(30)
                        .failureThreshold(3)
//                        .resourcePath("/")
                        .build())
                .build();
    }

    private CfnCluster createMskCluster() {
        return CfnCluster.Builder
                .create(this, "MskCluster")
                .clusterName("kafka-cluster")
                .kafkaVersion("2.8.1")
                .numberOfBrokerNodes(1)
                .brokerNodeGroupInfo(CfnCluster.BrokerNodeGroupInfoProperty.builder()
                        .instanceType("kafka.m5.xlarge")
                        .clientSubnets(this.vpc.getPrivateSubnets().stream()
                                .map(ISubnet::getSubnetId)
                                .toList())
                        .brokerAzDistribution("DEFAULT")
                        .build())
                .build();
    }

    public static void main(String[] args) {
        App app = new App(AppProps.builder().outdir("./cdk.out").build());

        StackProps props = StackProps.builder().synthesizer(new BootstraplessSynthesizer()).build();

        new LocalStack(app, "LocalStack", props);
        app.synth();
        System.out.println("CDK app synthesized successfully.");
    }
}
