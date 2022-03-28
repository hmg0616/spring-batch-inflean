package com.example.springbatchinflearn.batchExecution.step;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class CustomTaslketStepTasklet implements Tasklet {
    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        String stepName = stepContribution.getStepExecution().getStepName();
        String jobName = chunkContext.getStepContext().getJobName();

        System.out.println("stepName = " + stepName);
        System.out.println("jobName = " + jobName);

        return RepeatStatus.FINISHED;
    }
}