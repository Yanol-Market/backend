package site.goldenticket.domain.nego.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.goldenticket.domain.nego.entity.Nego;
import site.goldenticket.domain.nego.status.NegotiationStatus;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.user.entity.User;

import java.util.List;
import java.util.Optional;


public interface NegoRepository extends JpaRepository<Nego, Long> {


    List<Nego> findByStatus(NegotiationStatus status);

    Optional<Nego> findFirstByUser_IdAndProduct_IdOrderByCreatedAtDesc(Long userId, Long productId);

    Optional<Nego> findByUserAndProduct(User user, Product product);

    List<Nego> findNegoByUser_Id(Long userId);

    Boolean existsByUser_IdAndProduct_Id(Long userId, Long productId);

    List<Nego> findAllByUser_IdAndProduct_Id(Long userId, Long productId);

    List<Nego> findAllByProduct(Product product);

    List<Nego> findByStatusInAndProduct(List<NegotiationStatus> negotiationStatusList, Product product);

}
