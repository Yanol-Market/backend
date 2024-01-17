package site.goldenticket.domain.product.dto;

import org.springframework.data.domain.Slice;
import site.goldenticket.domain.product.model.Product;

import java.util.List;
import java.util.stream.Collectors;

public record RegionProductResponse(
        long totalCount,
        List<ProductResponse> productResponseList
) {

    public static RegionProductResponse fromEntity(long totalCount, Slice<Product> productSlice) {
        List<ProductResponse> productResponseList = productSlice.getContent().stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());

        return new RegionProductResponse(
                totalCount,
                productResponseList
        );
    }
}
