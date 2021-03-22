package com.myorg;

import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.pipelines.CdkPipeline;
import software.amazon.awscdk.pipelines.CdkPipelineProps;
import software.amazon.awscdk.pipelines.SimpleSynthAction;
import software.amazon.awscdk.pipelines.SimpleSynthActionProps;
import software.amazon.awscdk.services.codecommit.Repository;
import software.amazon.awscdk.services.codecommit.RepositoryProps;
import software.amazon.awscdk.services.codepipeline.Artifact;
import software.amazon.awscdk.services.codepipeline.actions.CodeCommitSourceAction;
import software.amazon.awscdk.services.codepipeline.actions.CodeCommitSourceActionProps;
import software.constructs.Construct;

import java.util.Collections;

public class WorkshopPipelineStack extends Stack {
    public WorkshopPipelineStack(final software.amazon.awscdk.core.Construct parent, final String id) {
        this(parent, id, null);
    }

    public WorkshopPipelineStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Repository repo = new Repository(this, "WorkshopRepo", RepositoryProps.builder()
                .repositoryName("WorkshopRepo")
                .build()
        );

        Artifact sourceArtifact = new Artifact();
        Artifact cloudAssemblyArtifact = new Artifact();

        new CdkPipeline(this, "Pipeline", CdkPipelineProps.builder()
                .pipelineName("WorkshopPipeline")
                .cloudAssemblyArtifact(cloudAssemblyArtifact)
                .sourceAction(new CodeCommitSourceAction(CodeCommitSourceActionProps.builder()
                        .actionName("CodeCommit")
                        .output(sourceArtifact)
                        .repository(repo)
                        .build()))
                .synthAction(new SimpleSynthAction(SimpleSynthActionProps.builder()
                        .sourceArtifact(sourceArtifact)
                        .cloudAssemblyArtifact(cloudAssemblyArtifact)
                        .buildCommands(Collections.singletonList("mvn clean package"))
                        .synthCommand("mvn clean package")
                        .build()))
                .build());
    }
}
