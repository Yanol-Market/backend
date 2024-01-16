package site.goldenticket.dummy.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.goldenticket.dummy.reservation.model.Reservation;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByYaUserIdAndCheckInDateAfter(Long yaUserId, LocalDate currentDate);
}
