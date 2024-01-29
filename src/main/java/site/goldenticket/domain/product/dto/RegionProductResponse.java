package site.goldenticket.domain.product.dto;

import org.springframework.data.domain.Slice;
import site.goldenticket.domain.product.model.Product;

import java.util.List;
import java.util.stream.Collectors;

public record RegionProductResponse(
        long totalCount,
        List<WishedProductResponse> wishedProductResponseList
) {
    public static RegionProductResponse fromEntity(long totalCount, Slice<Product> productSlice, boolean isAuthenticated) {

        List<WishedProductResponse> wishedProductResponseList = productSlice.getContent().stream()
                .map(
                        product -> WishedProductResponse.fromEntity(product, isAuthenticated)
                )
                .collect(Collectors.toList());

        return new RegionProductResponse(
                totalCount,
                wishedProductResponseList
        );
    }
}
