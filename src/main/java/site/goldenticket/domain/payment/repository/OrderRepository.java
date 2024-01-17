package site.goldenticket.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.goldenticket.domain.payment.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
