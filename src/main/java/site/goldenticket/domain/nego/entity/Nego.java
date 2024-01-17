package site.goldenticket.domain.nego.entity;

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

    public void setCount(Integer count) {
        this.count = count;
    }

    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }
    public void setConsent(Boolean consent) {
        this.consent = consent;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
    public Long getProductId() {
        return (product != null) ? product.getId() : null;
    }
    public Integer getCount() {
        return (count != null) ? count : 0;
    }
    public void setProduct(Product product) {
        this.product = product;
    }

    public void setUser(User user) {
        this.user = user;
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

    public void completed() {
        status = NegotiationStatus.NEGOTIATION_COMPLETED;
    }
}
