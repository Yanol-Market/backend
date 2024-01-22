package site.goldenticket.domain.product.dto;

import site.goldenticket.dummy.reservation.constants.ReservationStatus;
import site.goldenticket.dummy.reservation.constants.ReservationType;
import site.goldenticket.dummy.reservation.dto.YanoljaProductResponse;
import site.goldenticket.dummy.reservation.model.Reservation;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationResponse(
        Long reservationId,
        String accommodationName,
        ReservationType reservationType,
        String roomName,
        int standardNumber,
        int maximumNumber,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        LocalTime checkInTime,
        LocalTime checkOutTime,
        long nights,
        LocalDate reservationDate,
        int originPrice,
        int yanoljaPrice,
        ReservationStatus reservationStatus
) {
    public static ReservationResponse from(YanoljaProductResponse yanoljaProductResponse, ReservationStatus reservationStatus) {

        return new ReservationResponse(
                yanoljaProductResponse.reservationId(),
                yanoljaProductResponse.accommodationName(),
                yanoljaProductResponse.reservationType(),
                yanoljaProductResponse.roomName(),
                yanoljaProductResponse.standardNumber(),
                yanoljaProductResponse.maximumNumber(),
                yanoljaProductResponse.checkInDate(),
                yanoljaProductResponse.checkOutDate(),
                yanoljaProductResponse.checkInTime(),
                yanoljaProductResponse.checkOutTime(),
                yanoljaProductResponse.nights(),
                yanoljaProductResponse.reservationDate(),
                yanoljaProductResponse.originPrice(),
                yanoljaProductResponse.yanoljaPrice(),
                reservationStatus
        );
    }
}

