package site.goldenticket.dummy.reservation.dto;

import lombok.Builder;
import lombok.Getter;
import site.goldenticket.domain.product.constants.AreaCode;
import site.goldenticket.dummy.reservation.constants.ReservationType;
import site.goldenticket.dummy.reservation.model.Reservation;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class ReservationDetailsResponse {

    private Long reservationId;
    private AreaCode areaCode;
    private String accommodationImage;
    private String accommodationName;
    private String accommodationAddress;
    private ReservationType reservationType;
    private String roomName;
    private int standardNumber;
    private int maximumNumber;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private LocalDate reservationDate;
    private int originPrice;
    private int yanoljaPrice;

    public static ReservationDetailsResponse fromEntity(Reservation reservation) {
        return ReservationDetailsResponse.builder()
                .reservationId(reservation.getId())
                .areaCode(reservation.getAreaCode())
                .accommodationImage(reservation.getAccommodationImage())
                .accommodationName(reservation.getAccommodationName())
                .accommodationAddress(reservation.getAccommodationAddress())
                .reservationType(reservation.getReservationType())
                .roomName(reservation.getRoomName())
                .standardNumber(reservation.getStandardNumber())
                .maximumNumber(reservation.getMaximumNumber())
                .checkInTime(reservation.getCheckInTime())
                .checkOutTime(reservation.getCheckOutTime())
                .checkInDate(reservation.getCheckInDate())
                .checkOutDate(reservation.getCheckOutDate())
                .reservationDate(reservation.getReservationDate())
                .originPrice(reservation.getOriginPrice())
                .yanoljaPrice(reservation.getYanoljaPrice())
                .build();
    }
}
