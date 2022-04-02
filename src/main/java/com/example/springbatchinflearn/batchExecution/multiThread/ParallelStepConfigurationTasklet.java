package com.example.springbatchinflearn.batchExecution.multiThread;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class ParallelStepConfigurationTasklet implements Tasklet {

    private Object lock = new Object();
    private long sum; // 동기화처리 안하면 정확한 값 출력이 안됨 (여러 스레드에서 동시접근하므로)

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        synchronized (lock) {
            for (int i = 0; i < 1000000000; i++) {
                sum++;
            }
            System.out.println(String.format("%s has been exectued on thread %s"
                    , chunkContext.getStepContext().getStepName()
                    , Thread.currentThread().getName()));
            System.out.println(String.format("sum : %d", sum));
        }
        return RepeatStatus.FINISHED;
    }
}
