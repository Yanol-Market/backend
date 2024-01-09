package site.goldenticket.domain.reservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.common.response.ErrorCode;
import site.goldenticket.domain.reservation.dto.ReservationResponse;
import site.goldenticket.domain.reservation.model.Reservation;
import site.goldenticket.domain.reservation.repository.ReservationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {
    private final ReservationRepository reservationRepository;

    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservations(Long yaUserId) {
        List<Reservation> reservationList = findByYaUserIdAndCheckInDateAfter(yaUserId, LocalDate.now());

        return reservationList.stream()
                .map(ReservationResponse::fromEntity)
                .collect(Collectors.toList());
    }


    private List<Reservation> findByYaUserIdAndCheckInDateAfter(Long yaUserId, LocalDate currentDate) {
        return reservationRepository.findByYaUserIdAndCheckInDateAfter(yaUserId, currentDate);
    }

    public Reservation getReservation(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
    }

    public void saveReservation(Reservation reservation) {
        reservationRepository.save(reservation);
    }
}
