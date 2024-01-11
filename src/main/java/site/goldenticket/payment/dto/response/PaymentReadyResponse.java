package site.goldenticket.payment.dto.response;

import site.goldenticket.payment.model.Order;
import site.goldenticket.payment.service.PaymentService;

public record PaymentReadyResponse(
        Long id,
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
