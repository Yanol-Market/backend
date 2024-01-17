package site.goldenticket.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.goldenticket.domain.payment.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
