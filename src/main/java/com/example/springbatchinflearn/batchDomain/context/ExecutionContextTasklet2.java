package com.example.springbatchinflearn.batchDomain.context;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class ExecutionContextTasklet2 implements Tasklet {
    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        ExecutionContext jobExecutionContext = stepContribution.getStepExecution().getJobExecution().getExecutionContext();
        ExecutionContext stepExecutionContext = stepContribution.getStepExecution().getExecutionContext();

        // ExecutionContextTasklet1에서 제공받은 ExecutionContext 값 확인
        System.out.println("jobName : " + jobExecutionContext.get("jobName"));    // job  ExecutionContext는 step끼리 공유
        System.out.println("stepName : " + stepExecutionContext.get("stepName")); // step ExecutionContext는 step끼리 공유 안됨

        String stepName = chunkContext.getStepContext().getStepExecution().getStepName();

        if(stepExecutionContext.get("stepName") == null) {
            stepExecutionContext.put("stepName", stepName);
        }

        System.out.println("ExecutionContextConfiguration >> Step2 was executed");
        return RepeatStatus.FINISHED; // tasklet은 기본적으로 무한 실행되므로 FINISHED를 리턴해줘야 실행 멈춤
    }
}
