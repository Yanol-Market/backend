package site.goldenticket.domain.nego.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.status.NegotiationStatus;


public interface NegoRepository extends JpaRepository<Nego, Long> {

    Page<Nego> findLatestNegoByProductIdAndUserIdOrderByCreatedAtDesc(Long productId, Long userId,
        Pageable pageable);

    List<Nego> findByStatus(NegotiationStatus status);

    Optional<Nego> findLatestNegoByUserIdOrderByCreatedAtDesc(Long userId, PageRequest of);

    Optional<Nego> findLatestNegoByUserIdAndProductIdOrderByCreatedAtDesc(Long userId,
        Long productId, PageRequest of);

    Boolean existsByUserIdAndProductId(Long userId, Long productId);
    List<Nego> findAllByUserIdAndProductId(Long userId, Long productId);
}
