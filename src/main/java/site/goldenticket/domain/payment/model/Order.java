package site.goldenticket.domain.payment.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.goldenticket.common.constants.OrderStatus;
import site.goldenticket.common.entiy.BaseTimeEntity;
import site.goldenticket.domain.nego.status.NegotiationStatus;

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
    @Enumerated(EnumType.STRING)
    private NegotiationStatus negoStatus;
    private Integer price;
    private boolean customerViewCheck;

    private Order(Long productId, Long userId, OrderStatus status, NegotiationStatus negoStatus, Integer price) {
        this.productId = productId;
        this.userId = userId;
        this.status = status;
        this.negoStatus = negoStatus;
        this.price = price;
    }

    public static Order of(Long productId, Long userId, NegotiationStatus negoStatus, Integer price) {
        return new Order(productId, userId, OrderStatus.REQUEST_ORDER, negoStatus, price);
    }

    public Integer getTotalPrice() {
        return (int) (this.price * 1.05);
    }

    public void requestPayment() {
        status = OrderStatus.REQUEST_PAYMENT;
    }

    public void waitTransfer() {
        status = OrderStatus.WAITING_TRANSFER;
    }

    public void paymentFailed() {
        status = OrderStatus.PAYMENT_FAILED;
    }
}
