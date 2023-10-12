package com.example.batch.job.dbdatareadwrite;

import com.example.batch.core.domain.accounts.Accounts;
import com.example.batch.core.domain.accounts.AccountsRepository;
import com.example.batch.core.domain.orders.Orders;
import com.example.batch.core.domain.orders.OrdersRepository;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort.Direction;

@RequiredArgsConstructor
@Configuration
public class TrMigrationConfig {

  @Autowired
  private JobBuilderFactory jobBuilderFactory;

  @Autowired
  private StepBuilderFactory stepBuilderFactory;

  @Autowired
  private OrdersRepository ordersRepository;

  @Autowired
  private AccountsRepository accountsRepository;

  @Bean
  public Job trMigrationJob(Step trMigrationStep) {
    return jobBuilderFactory.get("trMigrationJob")
        .incrementer(new RunIdIncrementer())
        .start(trMigrationStep)
        .build();
  }

  @JobScope
  @Bean
  public Step trMigrationStep(
      ItemReader trOrdersReader, ItemProcessor trOrdersProcessor, ItemWriter trOrdersWriter) {
    return stepBuilderFactory.get("trMigrationStep")
        .<Orders, Accounts>chunk(5)
        .reader(trOrdersReader)
//        .writer(items -> items.forEach(System.out::println))
        .processor(trOrdersProcessor)
        .writer(trOrdersWriter)
        .build();
  }

  @StepScope
  @Bean
  public RepositoryItemReader<Orders> trOrdersReader() {
    return new RepositoryItemReaderBuilder<Orders>()
        .name("trOrdersReader")
        .repository(ordersRepository)
        .methodName("findAll")
        .pageSize(5)
        .arguments(Collections.emptyList())
        .sorts(Collections.singletonMap("id", Direction.ASC))
        .build();
  }

  @StepScope
  @Bean
  public ItemProcessor<Orders, Accounts> trOrdersProcessor() {
    return new ItemProcessor<Orders, Accounts>() {
      @Override
      public Accounts process(Orders item) throws Exception {
        return new Accounts(item);
      }
    };
  }

//  @StepScope
//  @Bean
//  public RepositoryItemWriter<Accounts> trOrdersWriter() {
//    return new RepositoryItemWriterBuilder<Accounts>()
//        .repository(accountsRepository)
//        .methodName("save")
//        .build();
//  }

  @StepScope
  @Bean
  public ItemWriter<Accounts> trOrdersWriter() {
    return new ItemWriter<Accounts>() {
      @Override
      public void write(List<? extends Accounts> items) throws Exception {
        items.forEach(item -> accountsRepository.save(item));
      }
    };
  }
}
