package com.example.springbatchinflearn.batchDomain.jobLauncher;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.batch.BasicBatchConfigurer;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

// Runner에 의해 자동실행되는게 아니라
// web 요청이 들어왔을때 동기적 or 비동기적으로 배치 실행 시키기
@RestController
public class JobLauncherController {

    @Autowired
    @Qualifier("jobLauncherJob")
    private Job job;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private BasicBatchConfigurer basicBatchConfigurer;

    // 요청이 들어오면 동기적으로 배치 실행 (Runner에 의해 자동실행되는게 아니라)
    @PostMapping("/batch")
    public String launch(@RequestBody Member member) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("id", member.getId())
                .addDate("date", new Date())
                .toJobParameters();

        jobLauncher.run(job, jobParameters); // 배치 실행 (기본적으로 SyncTaskExecutor 사용)

        return "batch completed";
    }

    // 요청이 들어오면 비동기적으로 배치 실행 (Runner에 의해 자동실행되는게 아니라)
    @PostMapping("/batchAsync")
    public String launchAsync(@RequestBody Member member) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("id", member.getId())
                .addDate("date", new Date())
                .toJobParameters();

        SimpleJobLauncher simpleJobLauncher = (SimpleJobLauncher)basicBatchConfigurer.getJobLauncher();
        simpleJobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor()); // 비동기
        simpleJobLauncher.run(job, jobParameters); // 배치 실행 (SimpleAsyncTaskExecutor 사용)

        return "batch completed";
    }
}
