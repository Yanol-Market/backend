package site.goldenticket.domain.nego.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.goldenticket.domain.nego.entity.Nego;

@Repository
public interface NegoRepository extends JpaRepository<Nego, Long> {
}
