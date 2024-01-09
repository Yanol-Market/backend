package site.goldenticket.domain.reservation.dto;

import lombok.Builder;
import lombok.Getter;
import site.goldenticket.common.constants.ReservationStatus;
import site.goldenticket.common.constants.ReservationType;
import site.goldenticket.domain.reservation.model.Reservation;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Getter
@Builder
public class ReservationResponse {

    private Long reservationId;
    private ReservationStatus reservationStatus;
    private String accommodationName;
    private ReservationType reservationType;
    private String roomName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private long nights;
    private LocalDate reservationDate;
    private int originPrice;
    private int yanoljaPrice;

    public static ReservationResponse fromEntity(Reservation reservation) {

        long nights = ChronoUnit.DAYS.between(product.getCheckInDate(), product.getCheckOutDate());

        return ReservationResponse.builder()
                .reservationId(reservation.getId())
                .reservationStatus(reservation.getReservationStatus())
                .accommodationName(reservation.getAccommodationName())
                .reservationType(reservation.getReservationType())
                .roomName(reservation.getRoomName())
                .checkInDate(reservation.getCheckInDate())
                .checkOutDate(reservation.getCheckOutDate())
                .nights(nights)
                .reservationDate(reservation.getReservationDate())
                .originPrice(reservation.getOriginPrice())
                .yanoljaPrice(reservation.getYanoljaPrice())
                .build();
    }
}
