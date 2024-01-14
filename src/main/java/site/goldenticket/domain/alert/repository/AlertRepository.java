package site.goldenticket.domain.alert.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.goldenticket.domain.alert.entity.Alert;

public interface AlertRepository extends JpaRepository<Alert, Long> {

}
