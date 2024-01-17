package site.goldenticket.dummy.reservation.dto;

import site.goldenticket.dummy.reservation.constants.ReservationStatus;
import site.goldenticket.dummy.reservation.constants.ReservationType;
import site.goldenticket.dummy.reservation.model.Reservation;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record ReservationResponse(
        Long reservationId,
        ReservationStatus reservationStatus,
        String accommodationName,
        ReservationType reservationType,
        String roomName,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        long nights,
        LocalDate reservationDate,
        int originPrice,
        int yanoljaPrice
) {
    public static ReservationResponse fromEntity(Reservation reservation) {
        long nights = ChronoUnit.DAYS.between(reservation.getCheckInDate(), reservation.getCheckOutDate());

        return new ReservationResponse(
                reservation.getId(),
                reservation.getReservationStatus(),
                reservation.getAccommodationName(),
                reservation.getReservationType(),
                reservation.getRoomName(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                nights,
                reservation.getReservationDate(),
                reservation.getOriginPrice(),
                reservation.getYanoljaPrice()
        );
    }
}
