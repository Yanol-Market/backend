package site.goldenticket.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.goldenticket.common.constants.OrderStatus;
import site.goldenticket.domain.payment.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByProductIdAndStatus(Long productId, OrderStatus orderStatus);

    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);

    List<Order> findByStatus(OrderStatus orderStatus);

    Boolean existsByProductIdAndStatus(Long productId, OrderStatus orderStatus);
}
