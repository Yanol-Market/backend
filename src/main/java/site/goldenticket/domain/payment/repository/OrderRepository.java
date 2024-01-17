package site.goldenticket.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.goldenticket.domain.payment.model.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);
}
