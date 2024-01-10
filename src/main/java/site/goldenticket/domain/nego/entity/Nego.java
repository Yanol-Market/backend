package site.goldenticket.domain.nego.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.goldenticket.domain.nego.status.NegotiationStatus;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class Nego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer price; // 네고가격
    private Integer count; // 네고횟수

    //임시
    private Long userId;
    private Long productId;


    //@ManyToOne
    //@JoinColumn(name = "user_id")
    //private User user; //유저ID

    //@ManyToOne
    //@JoinColumn(name = "product_id")
    //private Product product; // 상품ID

    @Enumerated(EnumType.STRING)
    private NegotiationStatus status; // 네고 상태

    private Boolean consent; // 승낙여부
    private LocalDateTime expirationTime; // 만료일시
    private LocalDateTime createdAt; // 생성일시
    private LocalDateTime updatedAt; // 수정일시

    public void setStatus(NegotiationStatus status) {
        this.status = status;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }
    public void setConsent(Boolean consent) {
        this.consent = consent;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    @Builder
    public Nego(Long id, Integer price, Integer count, Long userId, Long productId,
                NegotiationStatus status, Boolean consent, LocalDateTime expirationTime,
                LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.price = price;
        this.count = (count != null) ? count : 0;
        this.userId = userId;
        this.productId = productId;
        this.status = (status != null) ? status : NegotiationStatus.PENDING;
        this.consent = consent;
        this.expirationTime = expirationTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
