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
public class SchedulerService {
    private final NegoRepository negoRepository;

    @Scheduled(fixedDelay = 1000)
    public void changeStatus() {
        LocalDateTime currentTime = LocalDateTime.now();

        List<Nego> pendingNegos = negoRepository.findByStatus(PENDING);

        for (Nego nego : pendingNegos) {
            LocalDateTime updatedAt = nego.getUpdatedAt();
            if (updatedAt != null && currentTime.isAfter(updatedAt.plusSeconds(5))) {
                nego.setStatus(NEGOTIATING);
                nego.setUpdatedAt(currentTime);
            }
        }
        negoRepository.saveAll(pendingNegos);
    }

}
