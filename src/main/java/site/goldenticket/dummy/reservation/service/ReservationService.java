package site.goldenticket.dummy.reservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.common.response.ErrorCode;
import site.goldenticket.dummy.reservation.dto.ReservationDetailsResponse;
import site.goldenticket.dummy.reservation.dto.ReservationResponse;
import site.goldenticket.dummy.reservation.model.Reservation;
import site.goldenticket.dummy.reservation.repository.ReservationRepository;

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
        LocalDate currentDate = LocalDate.now();
        List<Reservation> reservationList = reservationRepository.findByYaUserIdAndCheckInDateAfter(yaUserId, currentDate);
        return reservationList.stream()
                .map(ReservationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReservationDetailsResponse getReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        return ReservationDetailsResponse.fromEntity(reservation);
    }
}
