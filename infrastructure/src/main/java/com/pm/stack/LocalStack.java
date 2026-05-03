package com.pm.stack;

import software.amazon.awscdk.*;
import software.constructs.Construct;

public class LocalStack extends Stack {

    public LocalStack(final App scope, final String id, final StackProps props) {
        super(scope, id, props);
    }

    public static void main(String[] args) {
        App app = new App(AppProps.builder().outdir("./cdk.out").build());

        StackProps props = StackProps.builder().synthesizer(new BootstraplessSynthesizer()).build();
    }
}
