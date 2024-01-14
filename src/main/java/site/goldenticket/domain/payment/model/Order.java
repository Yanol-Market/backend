package site.goldenticket.domain.payment.model;

import jakarta.persistence.*;
import lombok.*;
import site.goldenticket.common.constants.OrderStatus;
import site.goldenticket.common.entiy.BaseTimeEntity;

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

    private Order(Long productId, Long userId, OrderStatus status, Integer price) {
        this.productId = productId;
        this.userId = userId;
        this.status = status;
        this.price = price;
    }

    public static Order of(Long productId, Long userId, Integer price) {
        return new Order(productId, userId, OrderStatus.REQUEST_PAYMENT, price);
    }

    public Integer getTotalPrice() {
        return (int) (this.price*1.05);
    }

    public void waitTransfer() {
        status = OrderStatus.WAITING_TRANSFER;
    }
}
