package site.goldenticket.dummy.reservation.service;

import org.springframework.transaction.annotation.Transactional;
import site.goldenticket.dummy.reservation.dto.ReservationDetailsResponse;
import site.goldenticket.dummy.reservation.dto.YanoljaProductResponse;

import java.util.List;

public interface ReservationService {
    @Transactional(readOnly = true)
    List<YanoljaProductResponse> getReservations(Long yaUserId);

    @Transactional(readOnly = true)
    ReservationDetailsResponse getReservationDetails(Long reservationId);
}
