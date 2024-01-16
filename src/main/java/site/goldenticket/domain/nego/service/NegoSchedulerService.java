package site.goldenticket.domain.nego.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.repository.NegoRepository;

import java.time.LocalDateTime;
import java.util.List;

import static site.goldenticket.domain.nego.status.NegotiationStatus.*;

@Service
@RequiredArgsConstructor
public class NegoSchedulerService {
    private final NegoRepository negoRepository;

    @Scheduled(fixedDelay = 100000)
    public void changeStatus() {
        LocalDateTime currentTime = LocalDateTime.now();

        List<Nego> pendingNegos = negoRepository.findByStatus(PAYMENT_PENDING);

        for (Nego nego : pendingNegos) {
            LocalDateTime updatedAt = nego.getUpdatedAt();
            if (updatedAt != null && currentTime.isAfter(updatedAt.plusSeconds(10))) {
                nego.setStatus(NEGOTIATION_TIMEOUT);
                nego.setUpdatedAt(currentTime);
            }
        }
        negoRepository.saveAll(pendingNegos);
    }

}
