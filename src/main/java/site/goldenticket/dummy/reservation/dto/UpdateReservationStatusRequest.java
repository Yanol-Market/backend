package site.goldenticket.dummy.reservation.dto;

import site.goldenticket.dummy.reservation.constants.ReservationStatus;

public record UpdateReservationStatusRequest(
        ReservationStatus reservationStatus
) {
}
