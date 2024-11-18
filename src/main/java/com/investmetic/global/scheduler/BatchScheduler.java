package com.investmetic.global.scheduler;


import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job strategyAnalysisJob;

    /**
     * 매시간 정각(00분)에 배치 작업을 실행하는 스케줄러
     */
    @Scheduled(cron = "0 * * * * ?")
    public void runBatchJob() {
        try {
            // 현재 시간을 기준으로 Job 파라미터 생성
            jobLauncher.run(strategyAnalysisJob, new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters());
            System.out.println("Batch job executed at: " + System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}