package site.goldenticket.domain.product.dto;

import lombok.Getter;
import site.goldenticket.domain.product.constants.AreaCode;
import site.goldenticket.dummy.reservation.constants.ReservationType;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
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

}
