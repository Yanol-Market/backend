package site.goldenticket.domain.alert.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import site.goldenticket.domain.alert.entity.Alert;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    Boolean existsByUserIdAndViewed(Long userId, Boolean viewed);
    List<Alert> findAllByUserId(Long userId);
}
