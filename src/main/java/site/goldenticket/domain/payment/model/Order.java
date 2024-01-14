package site.goldenticket.domain.payment.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import site.goldenticket.common.constants.OrderStatus;
import site.goldenticket.common.entiy.BaseTimeEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "Orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseTimeEntity {

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

    public Integer getTotalPrice() {
        return (int) (this.price*1.05);
    }

    public void updateStatus(OrderStatus status) {
        this.status = status;
    }
}
