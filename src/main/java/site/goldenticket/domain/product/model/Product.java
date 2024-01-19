package site.goldenticket.domain.product.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.goldenticket.domain.product.constants.AreaCode;
import site.goldenticket.domain.product.constants.ProductStatus;
import site.goldenticket.domain.product.wish.entity.WishProduct;
import site.goldenticket.dummy.reservation.constants.ReservationType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
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
    private boolean sellerViewCheck;

    private Long reservationId;
    private Long userId;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<WishProduct> wishProducts;

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
            Long reservationId,
            Long userId,
            boolean sellerViewCheck
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
        this.userId = userId;
        this.sellerViewCheck = sellerViewCheck;
    }

    public void update(Integer goldenPrice, String content) {
        this.goldenPrice = goldenPrice;
        this.content = content;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public void setProductStatus(ProductStatus productStatus) {
        this.productStatus = productStatus;
    }

    public boolean isOnSale() {
        return this.productStatus == ProductStatus.SELLING;
    }

    public boolean isNotOnSale() {
        return !isOnSale();
    }

    public void setGoldenPrice(int goldenPrice) {
        this.goldenPrice = goldenPrice;
    }
}
