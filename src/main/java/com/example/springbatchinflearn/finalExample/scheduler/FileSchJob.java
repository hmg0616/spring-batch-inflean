package com.example.springbatchinflearn.finalExample.scheduler;

import lombok.SneakyThrows;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

// Quartz 관련 로직
@Component
public class FileSchJob extends QuartzJobBean {

    @Autowired
    private Job fileJob;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobExplorer jobExplorer;

    @SneakyThrows
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        String requestDate = (String)jobExecutionContext.getJobDetail().getJobDataMap().get("requestDate");

        // 파라미터 세팅
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("id", new Date().getTime())
                .addString("requestDate", requestDate)
                .toJobParameters();

        // 같은 날짜로 실행되지 않도록 방지
        int jobInstanceCount = jobExplorer.getJobInstanceCount(fileJob.getName());
        List<JobInstance> jobInstances = jobExplorer.getJobInstances(fileJob.getName(), 0, jobInstanceCount);

        if(jobInstances.size() > 0) {
            for (JobInstance jobInstance : jobInstances) {
                List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobInstance);
                List<JobExecution> jobExecutionList = jobExecutions.stream()
                        .filter(jobExecution ->
                                jobExecution.getJobParameters().getString("requestDate").equals(requestDate))
                        .collect(Collectors.toList()); // 같은 날짜로 실행된 JobExecution List
                if(jobExecutionList.size() > 0) {
                    throw new JobExecutionException(requestDate + " already exists"); 
                }
            }
        }

        jobLauncher.run(fileJob, jobParameters); // job 살행
    }
}
