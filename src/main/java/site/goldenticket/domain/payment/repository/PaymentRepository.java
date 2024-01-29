package site.goldenticket.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.goldenticket.domain.payment.model.Payment;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(Long orderId);

    Optional<Payment> findByPgTid(String pgTid);
}
