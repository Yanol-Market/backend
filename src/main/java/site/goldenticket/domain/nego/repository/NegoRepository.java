package site.goldenticket.domain.nego.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.goldenticket.domain.nego.entity.Nego;


public interface NegoRepository extends JpaRepository<Nego, Long> {
}