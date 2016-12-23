package com.example;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchJobConfig {
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;

	public BatchJobConfig(JobBuilderFactory jobBuilderFactory,
                          StepBuilderFactory stepBuilderFactory) {
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
	}

	@Bean
	Step step1() {
		return stepBuilderFactory.get("step1").tasklet(myTasklet()).build();
	}

	@Bean
	@StepScope
	Tasklet myTasklet() {
		return (contribution, chunkContext) -> {
			System.out.println("Tasklet has run");
			return RepeatStatus.FINISHED;
		};
	}

	@Bean
	Step step2() {
		return stepBuilderFactory.get("step2").<String, String> chunk(3)
				.reader(myItemReader()).processor(myItemProcessor())
				.writer(myItemWriter()).build();
	}

	@Bean
	@StepScope
	ItemReader<String> myItemReader() {
		return new ListItemReader<>(IntStream.range(0, 10).mapToObj(String::valueOf)
				.collect(Collectors.toList()));
	}

	@Bean
	@StepScope
	ItemProcessor<String, String> myItemProcessor() {
		return item -> {
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			return item;
		};
	}

	@Bean
	@StepScope
	ItemWriter<String> myItemWriter() {
		return items -> {
			for (String item : items) {
				System.out.println(">> " + item);
			}
		};
	}

	@Bean
	Job job() {
		return this.jobBuilderFactory.get("job").start(step1()).next(step2()).build();
	}
}
