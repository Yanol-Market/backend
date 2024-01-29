package site.goldenticket.dummy.reservation.dto;

import site.goldenticket.dummy.reservation.constants.ReservationType;
import site.goldenticket.dummy.reservation.model.Reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public record YanoljaProductResponse(
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
        int yanoljaPrice
) {
    public static YanoljaProductResponse fromEntity(Reservation reservation) {
        long nights = ChronoUnit.DAYS.between(reservation.getCheckInDate(), reservation.getCheckOutDate());
        
        return new YanoljaProductResponse(
                reservation.getId(),
                reservation.getAccommodationName(),
                reservation.getReservationType(),
                reservation.getRoomName(),
                reservation.getStandardNumber(),
                reservation.getMaximumNumber(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                reservation.getCheckInTime(),
                reservation.getCheckOutTime(),
                nights,
                reservation.getReservationDate(),
                reservation.getOriginPrice(),
                reservation.getYanoljaPrice()
        );
    }
}
