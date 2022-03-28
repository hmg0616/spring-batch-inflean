package com.example.springbatchinflearn.batchExecution.flow;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

public class CustomJobExecutionDecider implements JobExecutionDecider {

    private int count = 0;

    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        count++;
        if(count % 2 == 0) {
            return new FlowExecutionStatus("EVEN"); // 짝수 일때
        } else {
            return new FlowExecutionStatus("ODD"); // 홀수 있떄
        }
    }
}
