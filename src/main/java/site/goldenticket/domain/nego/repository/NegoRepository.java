package site.goldenticket.domain.nego.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.status.NegotiationStatus;

import java.time.LocalDateTime;
import java.util.List;


public interface NegoRepository extends JpaRepository<Nego, Long> {

    Nego findLatestNegoByProductIdAndUserIdOrderByCreatedAtDesc(Long productId, Long userId);
    List<Nego> findByStatus(NegotiationStatus status);

    List<Nego> findByStatusAndUpdatedAtBefore(NegotiationStatus negotiationStatus, LocalDateTime localDateTime);
}
