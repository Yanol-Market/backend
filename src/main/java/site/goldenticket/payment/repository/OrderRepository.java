package site.goldenticket.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.goldenticket.payment.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
