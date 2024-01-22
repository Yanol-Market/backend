package site.goldenticket.domain.payment.dto.response;

import site.goldenticket.domain.payment.model.Order;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.user.entity.User;

public record PaymentReadyResponse(
        Long orderId,
        String roomName,
        Integer price,
        Integer savingPrice,
        String email,
        String userName,
        String phoneNumber
) {
    public static PaymentReadyResponse create(User user, Product product, Order order) {

        return new PaymentReadyResponse(
                order.getId(),
                product.getRoomName(),
                order.getTotalPrice(),
                calculateSavingPrice(product.getYanoljaPrice(), order.getTotalPrice()),
                user.getEmail(),
                user.getName(),
                user.getPhoneNumber()
        );
    }

    public static Integer calculateSavingPrice(Integer yanoljaPrice, Integer orderPrice) {
        return yanoljaPrice - orderPrice;
    }
}
