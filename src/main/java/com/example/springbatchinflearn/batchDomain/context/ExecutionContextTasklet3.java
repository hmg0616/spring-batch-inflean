package com.example.springbatchinflearn.batchDomain.context;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class ExecutionContextTasklet3 implements Tasklet {
    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        Object name = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get("name");

        // 첫 실행시에는 name이 존재하지 않아 에러 발생후 Step(Job) FAILED
        // 재 실행 시 Job의 ExecutionContext에 name 존재하여 Step COMPLETED
        // Job 재실행 시 JobExecution은 새로 생성되지만, 이전 실행의 ExecutionContext를 재사용하는듯
        if(name == null) {
            chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("name", "user1");
            throw new RuntimeException("Step3 was failed"); // 의도적으로 오류 발생
        }

        System.out.println("ExecutionContextConfiguration >> Step3 was executed");
        return RepeatStatus.FINISHED; // tasklet은 기본적으로 무한 실행되므로 FINISHED를 리턴해줘야 실행 멈춤

    }
}