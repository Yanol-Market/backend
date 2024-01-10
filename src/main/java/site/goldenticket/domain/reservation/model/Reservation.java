package site.goldenticket.domain.reservation.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.goldenticket.common.constants.AreaCode;
import site.goldenticket.common.constants.ReservationStatus;
import site.goldenticket.common.constants.ReservationType;

import java.time.LocalDate;
import java.time.LocalTime;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AreaCode areaCode;
    private String accommodationImage;
    private String accommodationName;
    private String accommodationAddress;
    @Enumerated(EnumType.STRING)
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
    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;

    private Long yaUserId;

    @Builder
    private Reservation(
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
            int yanoljaPrice,
            ReservationStatus reservationStatus,
            Long yaUserId
    ) {
        this.areaCode = areaCode;
        this.accommodationImage = accommodationImage;
        this.accommodationName = accommodationName;
        this.accommodationAddress = accommodationAddress;
        this.reservationType = reservationType;
        this.roomName = roomName;
        this.standardNumber = standardNumber;
        this.maximumNumber = maximumNumber;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.reservationDate = reservationDate;
        this.originPrice = originPrice;
        this.yanoljaPrice = yanoljaPrice;
        this.reservationStatus = reservationStatus;
        this.yaUserId = yaUserId;
    }

    public void setReservationStatus(ReservationStatus reservationStatus) {
        this.reservationStatus = reservationStatus;
    }
}
