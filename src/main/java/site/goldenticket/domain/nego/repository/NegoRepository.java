package site.goldenticket.domain.nego.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.status.NegotiationStatus;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.user.entity.User;


public interface NegoRepository extends JpaRepository<Nego, Long> {


    List<Nego> findByStatus(NegotiationStatus status);

    Optional<Nego> findFirstByUser_IdAndProduct_IdOrderByCreatedAtDesc(Long userId, Long productId);

    Optional<Nego> findByUserAndProduct(User user, Product product);

    Nego findByProduct(Product product);

    Boolean existsByUserIdAndProductId(Long userId, Long productId);

    List<Nego> findAllByUserIdAndProductId(Long userId, Long productId);
}
