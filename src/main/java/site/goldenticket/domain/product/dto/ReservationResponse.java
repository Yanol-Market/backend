package site.goldenticket.domain.product.dto;

import lombok.Builder;
import lombok.Getter;
import site.goldenticket.dummy.reservation.constants.ReservationStatus;
import site.goldenticket.dummy.reservation.constants.ReservationType;

import java.time.LocalDate;

@Getter
public class ReservationResponse {

    private Long reservationId;
    private ReservationStatus reservationStatus;
    private String accommodationName;
    private ReservationType reservationType;
    private String roomName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private long nights;
    private LocalDate reservationDate;
    private int originPrice;
    private int yanoljaPrice;

    @Builder(toBuilder = true)
    public ReservationResponse(Long reservationId, ReservationStatus reservationStatus,
                                      String accommodationName, ReservationType reservationType,
                                      String roomName, LocalDate checkInDate, LocalDate checkOutDate,
                                      long nights, LocalDate reservationDate, int originPrice, int yanoljaPrice) {
        this.reservationId = reservationId;
        this.reservationStatus = reservationStatus;
        this.accommodationName = accommodationName;
        this.reservationType = reservationType;
        this.roomName = roomName;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.nights = nights;
        this.reservationDate = reservationDate;
        this.originPrice = originPrice;
        this.yanoljaPrice = yanoljaPrice;
    }
}
