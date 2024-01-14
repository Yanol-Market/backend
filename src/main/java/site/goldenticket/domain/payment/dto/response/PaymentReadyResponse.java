package site.goldenticket.domain.payment.dto.response;

import site.goldenticket.domain.payment.model.Order;
import site.goldenticket.domain.payment.service.PaymentService;

public record PaymentReadyResponse(
        Long orderId,
        String roomName,
        Integer price,
        String email,
        String userName,
        String phoneNumber
) {
    public static PaymentReadyResponse create(PaymentService.User user, PaymentService.Product product, Order order) {

        return new PaymentReadyResponse(
                order.getId(),
                product.getRoomName(),
                order.getTotalPrice(),
                user.getEmail(),
                user.getName(),
                user.getPhoneNumber()
        );
    }
}
