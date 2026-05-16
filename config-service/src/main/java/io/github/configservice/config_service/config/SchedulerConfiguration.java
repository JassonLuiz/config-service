package io.github.configservice.config_service.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
@EnableScheduling
public class SchedulerConfiguration implements SchedulingConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerConfiguration.class);

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        logger.info("Configuring scheduled task executor");

        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);
        scheduler.setThreadNamePrefix("schedule-dlq-");
        scheduler.setErrorHandler(throwable -> {
            logger.error("Uncaught exception in scheduled task: {}",
                    throwable.getMessage(), throwable);
        });
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(120);
        scheduler.initialize();

        logger.info("Scheduled task executor configured: poolSize={}, threadNamePrefix={}",
                scheduler.getPoolSize(), scheduler.getThreadNamePrefix());

        taskRegistrar.setTaskScheduler(scheduler);
    }
}
