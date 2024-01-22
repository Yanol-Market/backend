package site.goldenticket.domain.payment.dto.response;

import site.goldenticket.domain.payment.model.Order;
import site.goldenticket.domain.product.model.Product;

import java.time.LocalDateTime;

public record PurchaseCompletedResponse(
        Long productId,
        Long OrderId,
        String accommodationImage,
        String accommodationName,
        String roomName,
        Integer standardNumber,
        Integer maximumNumber,
        Integer price,
        LocalDateTime completedAt
) {
    public static PurchaseCompletedResponse create(Product product, Order order) {
        return new PurchaseCompletedResponse(
                product.getId(),
                order.getId(),
                product.getAccommodationImage(),
                product.getAccommodationName(),
                product.getRoomName(),
                product.getStandardNumber(),
                product.getMaximumNumber(),
                order.getPrice(),
                order.getUpdatedAt()
        );
    }
}
