package site.goldenticket.payment.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "Orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private Long userId;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private Integer price;
    private Boolean buyerViewCheck;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    private Order(Long productId, Long userId, OrderStatus status, Integer price, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.productId = productId;
        this.userId = userId;
        this.status = status;
        this.price = price;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public enum OrderStatus {
        REQUEST_PAYMENT, WAITING_TRANSFER, COMPLETED_TRANSFER, FAIL, CANCEL
    }

    public Integer getTotalPrice() {
        return (int) (this.price*1.05);
    }
}
