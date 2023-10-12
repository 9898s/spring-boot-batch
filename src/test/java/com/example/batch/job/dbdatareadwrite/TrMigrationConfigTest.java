package com.example.batch.job.dbdatareadwrite;

import com.example.batch.SpringBatchTestConfig;
import com.example.batch.core.domain.accounts.AccountsRepository;
import com.example.batch.core.domain.orders.Orders;
import com.example.batch.core.domain.orders.OrdersRepository;
import java.util.Date;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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
@SpringBootTest(classes = {SpringBatchTestConfig.class, TrMigrationConfig.class})
class TrMigrationConfigTest {

  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;

  @Autowired
  private OrdersRepository ordersRepository;

  @Autowired
  private AccountsRepository accountsRepository;

  @AfterEach
  void cleanUpEach() {
    ordersRepository.deleteAll();
    accountsRepository.deleteAll();
  }

  @Test
  void success_noData() throws Exception {
    // when
    JobExecution execution = jobLauncherTestUtils.launchJob();

    // then
    Assertions.assertEquals(ExitStatus.COMPLETED, execution.getExitStatus());
    Assertions.assertEquals(0, accountsRepository.count());
  }

  @Test
  void success_existData() throws Exception {
    // given
    Orders orders1 = new Orders(null, "kakao gift", 15000, new Date());
    Orders orders2 = new Orders(null, "naver gift", 15000, new Date());

    ordersRepository.save(orders1);
    ordersRepository.save(orders2);

    // when
    JobExecution execution = jobLauncherTestUtils.launchJob();

    // then
    Assertions.assertEquals(ExitStatus.COMPLETED, execution.getExitStatus());
    Assertions.assertEquals(2, accountsRepository.count());
  }
}