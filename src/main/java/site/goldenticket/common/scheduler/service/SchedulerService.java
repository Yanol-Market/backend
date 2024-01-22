package site.goldenticket.common.scheduler.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import site.goldenticket.domain.product.service.ProductSchedulerService;
import site.goldenticket.domain.product.service.ProductService;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulerService {

    private final ProductSchedulerService productSchedulerService;

    @Scheduled(cron = "0 */10 * * * *")
    public void updateViewCountsScheduler() {
        log.info("매 10분마다 실행 되는 Product ViewCount Update Scheduler.");
        productSchedulerService.updateViewCounts();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void updateProductStatusScheduler() {
        log.info("매일 24시 00분 00초에 실행 되는 Product Status Update Scheduler.");
        productSchedulerService.updateProductStatus();
    }
}
