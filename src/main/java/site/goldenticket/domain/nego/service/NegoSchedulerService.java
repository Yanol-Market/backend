package site.goldenticket.domain.nego.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.common.response.ErrorCode;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.repository.NegoRepository;

import java.time.LocalDateTime;
import java.util.List;

import static site.goldenticket.domain.nego.status.NegotiationStatus.*;

@Service
@RequiredArgsConstructor
public class NegoSchedulerService {
    private final NegoRepository negoRepository;

    @Scheduled(fixedDelay = 1000)
    public void changeStatus() {
        LocalDateTime currentTime = LocalDateTime.now();

        List<Nego> pendingNegos = negoRepository.findByStatus(PAYMENT_PENDING);
        List<Nego> completedNegos = negoRepository.findByStatus(NEGOTIATION_COMPLETED);

        for (Nego nego : pendingNegos) {
            LocalDateTime updatedAt = nego.getUpdatedAt();
            if (updatedAt != null && currentTime.isAfter(updatedAt.plusSeconds(10))) {
                nego.setStatus(NEGOTIATION_TIMEOUT);
                nego.setUpdatedAt(currentTime);
            }
        }

        for (Nego nego : completedNegos){
            if(nego.getStatus() == NEGOTIATION_COMPLETED){
                throw new CustomException("완료된 네고로 인해 제안을 막습니다.", ErrorCode.COMMON_CANNOT_NEGOTIATE);
            }
        }


        negoRepository.saveAll(pendingNegos);
    }

}
