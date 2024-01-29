package site.goldenticket.dummy.reservation.dto;

import site.goldenticket.domain.product.constants.AreaCode;
import site.goldenticket.dummy.reservation.constants.ReservationType;
import site.goldenticket.dummy.reservation.model.Reservation;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationDetailsResponse(
        Long reservationId,
        AreaCode areaCode,
        String accommodationImage,
        String accommodationName,
        String accommodationAddress,
        ReservationType reservationType,
        String roomName,
        int standardNumber,
        int maximumNumber,
        LocalTime checkInTime,
        LocalTime checkOutTime,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        LocalDate reservationDate,
        int originPrice,
        int yanoljaPrice
) {
    public static ReservationDetailsResponse fromEntity(Reservation reservation) {

        return new ReservationDetailsResponse(
                reservation.getId(),
                reservation.getAreaCode(),
                reservation.getAccommodationImage(),
                reservation.getAccommodationName(),
                reservation.getAccommodationAddress(),
                reservation.getReservationType(),
                reservation.getRoomName(),
                reservation.getStandardNumber(),
                reservation.getMaximumNumber(),
                reservation.getCheckInTime(),
                reservation.getCheckOutTime(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                reservation.getReservationDate(),
                reservation.getOriginPrice(),
                reservation.getYanoljaPrice()
        );
    }
}
