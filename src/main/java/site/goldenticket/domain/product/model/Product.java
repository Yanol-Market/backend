package site.goldenticket.domain.product.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.goldenticket.common.constants.AreaCode;
import site.goldenticket.common.constants.ProductStatus;
import site.goldenticket.common.constants.ReservationType;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor
public class Product {
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
    private int goldenPrice;
    private String content;
    private int viewCount;
    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;

    private Long reservationId;

    @Builder
    private Product(
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
            int goldenPrice,
            String content,
            int viewCount,
            ProductStatus productStatus,
            Long reservationId
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
        this.goldenPrice = goldenPrice;
        this.content = content;
        this.viewCount = viewCount;
        this.productStatus = productStatus;
        this.reservationId = reservationId;
    }

    public void update(Integer goldenPrice, String content) {
        this.goldenPrice = goldenPrice;
        this.content = content;
    }
}
