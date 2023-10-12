package com.example.batch.job.helloworld;

import static org.junit.jupiter.api.Assertions.*;

import com.example.batch.SpringBatchTestConfig;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBatchTest
@SpringBootTest(classes = {SpringBatchTestConfig.class, HelloWorldJobConfig.class})
class HelloWorldJobConfigTest {

  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;

  @Test
  void success() throws Exception {
    // when
    JobExecution jobExecution = jobLauncherTestUtils.launchJob();

    // then
    assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
  }
}