package com.example.springbatchinflearn.batchDomain.step;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class CustomTasklet implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        System.out.println("StepConfiguration >> Step1 was executed");
        return RepeatStatus.FINISHED; // tasklet은 기본적으로 무한 실행되므로 FINISHED를 리턴해줘야 실행 멈춤
    }
}
