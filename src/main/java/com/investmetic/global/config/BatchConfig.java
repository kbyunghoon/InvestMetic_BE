package com.investmetic.global.config;


import com.investmetic.domain.strategy.model.entity.DailyAnalysis;
import com.investmetic.domain.strategy.repository.DailyAnalysisRepository;
import com.investmetic.global.scheduler.DailyAnalysisProcessor;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {
    private final EntityManagerFactory entityManagerFactory;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DailyAnalysisRepository dailyAnalysisRepository;
    private final DailyAnalysisProcessor dailyAnalysisProcessor;

    @Bean
    public Job strategyAnalysisJob() {
        return new JobBuilder("strategyAnalysisJob", jobRepository)
                .start(strategyStep())
                .build();
    }

    @Bean
    @JobScope
    public Step strategyStep() {
        return new StepBuilder("strategyStep", jobRepository)
                .<DailyAnalysis, DailyAnalysis>chunk(10, transactionManager)
                .reader(dailyAnalysisReader())
                .processor(dailyAnalysisProcessor)
                .writer(loggingWriter()) // 로그 출력 후 저장
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<DailyAnalysis> dailyAnalysisReader() {
        LocalDateTime lastUpdatedTime = getPreviousHour();
        return new JpaPagingItemReaderBuilder<DailyAnalysis>()
                .name("dailyAnalysisReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString(
                        "SELECT d FROM DailyAnalysis d WHERE d.updatedAt >= :lastUpdatedTime ORDER BY d.dailyDate ASC")
                .parameterValues(Map.of("lastUpdatedTime", lastUpdatedTime))
                .pageSize(10)
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<DailyAnalysis, DailyAnalysis> processor() {
        return dailyAnalysisProcessor;
    }

    @Bean
    public ItemWriter<DailyAnalysis> loggingWriter() {
        return items -> {
            for (DailyAnalysis item : items) {
            }
            dailyAnalysisWriter().write(items);
        };
    }

    @Bean
    @StepScope
    public JpaItemWriter<DailyAnalysis> dailyAnalysisWriter() {
        return new JpaItemWriterBuilder<DailyAnalysis>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    /**
     * 이전 정각을 계산하는 메서드 현재 시간 기준으로 한 시간 전의 정각을 반환합니다.
     */
    private LocalDateTime getPreviousHour() {
        return LocalDateTime.now()
                .minusHours(1)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
    }
}