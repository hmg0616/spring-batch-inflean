package com.example.springbatchinflearn.batchTest;

import com.example.springbatchinflearn.common.TestBatchConfig;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBatchTest
@SpringBootTest(classes = {BatchTestSimpleJobConfiguration.class, TestBatchConfig.class})
public class BatchTestSimpleJobTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void batchTestSimpleJob_jobTest() throws Exception {

        // given
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("requestDate", "20210101")
                .addLong("date", new Date().getTime())
                .toJobParameters();

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // then
        Assert.assertEquals(jobExecution.getStatus(), BatchStatus.COMPLETED);
        Assert.assertEquals(jobExecution.getExitStatus(), ExitStatus.COMPLETED);

    }

    @Test
    public void batchTestSimpleJob_stepTest() throws Exception {

        // given
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("requestDate", "20210101")
                .addLong("date", new Date().getTime())
                .toJobParameters();

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("batchTestSimpleStep");

        // then
        StepExecution stepExecution = (StepExecution) ((List<StepExecution>) jobExecution.getStepExecutions()).get(0);
        Assert.assertEquals(stepExecution.getCommitCount(), 6); // 데이터 10개, 청크 사이즈 2, 10/2+1(마지막read)=6
        Assert.assertEquals(stepExecution.getReadCount(), 10);
        Assert.assertEquals(stepExecution.getWriteCount(), 10);
    }

    @After
    public void clear() {
        jdbcTemplate.execute("delete from customer4");
    }

}
