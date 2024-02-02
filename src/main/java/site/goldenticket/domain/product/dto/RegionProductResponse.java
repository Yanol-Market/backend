package site.goldenticket.domain.product.dto;

import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.product.repository.CustomSlice;

import java.util.List;
import java.util.stream.Collectors;

public record RegionProductResponse(
        List<WishedProductResponse> wishedProductResponseList
) {
    public static RegionProductResponse fromEntity(CustomSlice<Product> productSlice, boolean isAuthenticated) {

        List<WishedProductResponse> wishedProductResponseList = productSlice.getContent().stream()
                .map(
                        product -> WishedProductResponse.fromEntity(product, isAuthenticated)
                )
                .collect(Collectors.toList());

        return new RegionProductResponse(
                wishedProductResponseList
        );
    }
}
