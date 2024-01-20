package site.goldenticket.domain.payment.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import site.goldenticket.common.constants.OrderStatus;
import site.goldenticket.domain.payment.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByProductIdAndStatus(Long productId, OrderStatus orderStatus);
}
