package site.goldenticket.dummy.reservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.goldenticket.common.exception.CustomException;
import site.goldenticket.dummy.reservation.dto.ReservationDetailsResponse;
import site.goldenticket.dummy.reservation.dto.YanoljaProductResponse;
import site.goldenticket.dummy.reservation.model.Reservation;
import site.goldenticket.dummy.reservation.repository.ReservationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static site.goldenticket.common.response.ErrorCode.RESERVATION_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {
    private final ReservationRepository reservationRepository;

    @Transactional(readOnly = true)
    public List<YanoljaProductResponse> getReservations(Long yaUserId) {
        LocalDate currentDate = LocalDate.now();
        List<Reservation> reservationList = reservationRepository.findByYaUserIdAndCheckInDateAfter(yaUserId, currentDate);
        return reservationList.stream()
                .map(YanoljaProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReservationDetailsResponse getReservationDetails (Long reservationId) {
        Reservation reservation = getReservation(reservationId);
        return ReservationDetailsResponse.fromEntity(reservation);
    }

    public Reservation getReservation(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(RESERVATION_NOT_FOUND));
    }
}
