package site.goldenticket.domain.nego.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.goldenticket.domain.nego.status.NegotiationStatus;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.user.entity.User;

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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; //유저ID

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product; // 상품ID

    @Enumerated(EnumType.STRING)
    private NegotiationStatus status; // 네고 상태

    @Nullable
    private Boolean consent; // 승낙여부
    private LocalDateTime expirationTime; // 만료일시
    private LocalDateTime createdAt; // 생성일시
    private LocalDateTime updatedAt; // 수정일시


    public Nego(User user, Product product) {
        this.user = user;
        this.product = product;
    }

    public void setStatus(NegotiationStatus status) {
        this.status = status;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }

    public void setConsent(Boolean consent) {
        this.consent = consent;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Long getProductId() {
        return (product != null) ? product.getId() : null;
    }

    public Integer getCount() {
        return (count != null) ? count : 0;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void updatePrice(Integer price) {
        this.price = price;
    }


    public void updateNego(Integer count, Integer price, NegotiationStatus status, LocalDateTime createdAt, LocalDateTime updatedAt, Boolean consent) {
        this.count = count;
        this.price = price;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.consent = consent;
    }

    public void payNego(NegotiationStatus status, Boolean consent, LocalDateTime updatedAt) {
        this.status = status;
        this.consent = consent;
        this.updatedAt = updatedAt;
    }

    public void confirmNego(LocalDateTime updatedAt, NegotiationStatus status, LocalDateTime expirationTime, Boolean consent){
        this.updatedAt = updatedAt;
        this.status = status;
        this.expirationTime = expirationTime;
        this.consent = consent;
    }


    @Builder
    public Nego(Long id, Integer price, Integer count, Long userId, Long productId,
                NegotiationStatus status, Boolean consent, LocalDateTime expirationTime,
                LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.price = price;
        this.count = (count != null) ? count : 0;
        this.status = (status != null) ? status : NegotiationStatus.PAYMENT_PENDING;
        this.consent = consent;
        this.expirationTime = expirationTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void transferPending() {
        status = NegotiationStatus.TRANSFER_PENDING;
    }
}
