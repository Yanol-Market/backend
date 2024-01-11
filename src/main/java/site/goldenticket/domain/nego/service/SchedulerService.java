package site.goldenticket.domain.nego.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.repository.NegoRepository;
import site.goldenticket.domain.nego.status.NegotiationStatus;

import java.time.LocalDateTime;
import java.util.List;

import static site.goldenticket.domain.nego.status.NegotiationStatus.NEGOTIATION_APPROVED;

@Service
@RequiredArgsConstructor
public class SchedulerService {
    private final NegoRepository negoRepository;

    @Scheduled(fixedDelay = 6000) // 10초
    public void changeStatus() {
        List<Nego> negos = negoRepository.findByStatus(NegotiationStatus.PENDING);
        for (Nego nego : negos) {
            LocalDateTime updatedAt = nego.getUpdatedAt();
            if (updatedAt.isBefore(LocalDateTime.now().plusSeconds(10))) {
                nego.setStatus(NEGOTIATION_APPROVED);
                negoRepository.save(nego);
            }
        }
    }

}
