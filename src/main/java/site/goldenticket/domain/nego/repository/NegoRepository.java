package site.goldenticket.domain.nego.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.status.NegotiationStatus;

import java.util.List;


public interface NegoRepository extends JpaRepository<Nego, Long> {

    Page<Nego> findLatestNegoByProductIdAndUserIdOrderByCreatedAtDesc(Long productId, Long userId, Pageable pageable);


    List<Nego> findByStatus(NegotiationStatus status);
}
