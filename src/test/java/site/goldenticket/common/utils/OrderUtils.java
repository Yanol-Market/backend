package site.goldenticket.common.utils;

import site.goldenticket.domain.payment.model.Order;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.user.entity.User;

import static site.goldenticket.common.constants.OrderStatus.REQUEST_PAYMENT;

public class OrderUtils {
    private static final Integer price = 50000;

    private OrderUtils() {}

    public static Order createOrder(Product product, User user) {
        Long productId = product.getId();
        Long userId = user.getId();

        return Order.builder()
                .productId(productId)
                .userId(userId)
                .status(REQUEST_PAYMENT)
                .price(price)
                .build();
    }
}
