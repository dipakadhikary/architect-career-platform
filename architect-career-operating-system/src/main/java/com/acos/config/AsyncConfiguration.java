package com.acos.config;

import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/** Enables Spring {@code @Async} execution for after-commit AI integration listeners. */
@Configuration
@EnableAsync
public class AsyncConfiguration implements AsyncConfigurer {

  private static final Logger LOG = LoggerFactory.getLogger(AsyncConfiguration.class);

  private final ThreadPoolTaskExecutor aiIntegrationExecutor;

  /** Creates async configuration and initializes the AI task executor. */
  public AsyncConfiguration() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setThreadNamePrefix("ai-integration-");
    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(8);
    executor.setQueueCapacity(500);
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setAwaitTerminationSeconds(30);
    executor.initialize();
    this.aiIntegrationExecutor = executor;
  }

  /**
   * Task executor used for AI integration asynchronous work.
   *
   * @return AI task executor
   */
  @Bean(name = "aiTaskExecutor")
  public TaskExecutor aiTaskExecutor() {
    return aiIntegrationExecutor;
  }

  @Override
  public Executor getAsyncExecutor() {
    return aiIntegrationExecutor;
  }

  @Override
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return (ex, method, params) ->
        LOG.error(
            "Unhandled async exception in {}.{}: {}",
            method.getDeclaringClass().getSimpleName(),
            method.getName(),
            ex.getMessage(),
            ex);
  }
}
