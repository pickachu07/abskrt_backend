package com.absk.rtrader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.absk.rtrader.core.constants.CoreConstants;


@SpringBootApplication
@EnableScheduling
@EnableAsync
public class RtraderApplication {	
	/*
	 * Configuring default Async Task Executor
	 */
	@Bean("AsyncTaskExecuter")
	public TaskExecutor getAsyncExecuter() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(CoreConstants.CORE_POOL_SIZE);
		executor.setMaxPoolSize(CoreConstants.MAX_POOL_SIZE);
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setThreadNamePrefix(CoreConstants.ASYNC_THREAD_NAME_PREFIX);
		return executor;
	}
	/*
	 * Entrypoint of the Application - Spring boot style. Part of boilerplate.
	 */
	public static void main(String[] args) {
		SpringApplication.run(RtraderApplication.class, args);
	}

}

