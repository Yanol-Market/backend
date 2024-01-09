package site.goldenticket.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.goldenticket.payment.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
