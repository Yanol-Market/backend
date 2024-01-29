package site.goldenticket.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.goldenticket.domain.payment.model.PaymentCancelDetail;

public interface PaymentCancelDetailRepository extends JpaRepository<PaymentCancelDetail, Long> {
}
