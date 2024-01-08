package site.goldenticket.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.goldenticket.reservation.model.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}