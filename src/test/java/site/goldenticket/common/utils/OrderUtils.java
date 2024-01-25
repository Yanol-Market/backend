package site.goldenticket.common.utils;

import site.goldenticket.common.constants.OrderStatus;
import site.goldenticket.domain.nego.status.NegotiationStatus;
import site.goldenticket.domain.payment.model.Order;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.user.entity.User;

import static site.goldenticket.common.constants.OrderStatus.WAITING_TRANSFER;
import static site.goldenticket.domain.nego.status.NegotiationStatus.TRANSFER_PENDING;

public class OrderUtils {
    private static final OrderStatus status = WAITING_TRANSFER;
    private static final NegotiationStatus negoStatus = TRANSFER_PENDING;
    private static final Integer price = 50000;

    private OrderUtils() {}

    public static Order createOrder(Product product, User user){
        Long productId = product.getId();
        Long userId = user.getId();

        return Order.builder()
                .productId(productId)
                .userId(userId)
                .status(status)
                .negoStatus(negoStatus)
                .price(price)
                .build();
    }
}
