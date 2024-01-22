package site.goldenticket.domain.payment.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import site.goldenticket.common.constants.OrderStatus;
import site.goldenticket.domain.payment.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByProductIdAndStatus(Long productId, OrderStatus orderStatus);

    List<Order> findByStatusAndProductId(OrderStatus status, Long productId);

    Order findByProductId(Long productId);

    List<Order> findByUserId(Long userId);

    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);
}
