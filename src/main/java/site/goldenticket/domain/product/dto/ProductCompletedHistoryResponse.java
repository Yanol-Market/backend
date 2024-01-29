package site.goldenticket.domain.product.dto;

import site.goldenticket.domain.product.constants.ProductStatus;
import site.goldenticket.domain.product.model.Product;

public record ProductCompletedHistoryResponse(
        Long productId,
        String accommodationImage,
        String accommodationName,
        String roomName,
        int standardNumber,
        int maximumNumber,
        int goldenPrice,
        ProductStatus productStatus
) {

    public static ProductCompletedHistoryResponse fromEntity(Product product, ProductStatus productStatus) {

        return new ProductCompletedHistoryResponse(
                product.getId(),
                product.getAccommodationImage(),
                product.getAccommodationName(),
                product.getRoomName(),
                product.getStandardNumber(),
                product.getMaximumNumber(),
                product.getGoldenPrice(),
                productStatus
        );
    }
}
