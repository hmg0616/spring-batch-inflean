package com.example.springbatchinflearn.batchDomain;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Date;

// JobParameters 테스트
// 어플리케이션 실행 시 run 메서드에서 직접 Job 실행 시키기
// application.yml에서 spring.batch.job.enabled=false로 바꿔야함
//@Component
public class JobRunner implements ApplicationRunner {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("jobRepositoryJob")
    private Job job; // 실행할 Job 빈 객체 가져오기

    @Override
    public void run(ApplicationArguments args) throws Exception {

        JobParameters jobParameters = new JobParametersBuilder()
//                .addString("name", "user1") // 파라미터 테스트
                .addString("requestDate", "20210102")
//                .addLong("seq", 2L)
//                .addDate("date", new Date())
//                .addDouble("age", 16.5)
                .toJobParameters();

        jobLauncher.run(job, jobParameters); // Job 실행

    }
}
