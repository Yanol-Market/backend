package site.goldenticket.common.scheduler.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import site.goldenticket.domain.product.service.ProductService;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulerService {

    private final ProductService productService;

    @Scheduled(cron = "59 59 23 * * *")
    public void updateViewCountsScheduler() {
        log.info("매일 23시 59분 59초에 실행 되는 Product ViewCount Update Scheduler.");
        productService.updateViewCounts();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void updateProductStatusScheduler() {
        log.info("매일 24시 00분 00초에 실행 되는 Product Status Update Scheduler.");
        productService.updateProductStatus();
    }
}
