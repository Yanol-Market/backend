package site.goldenticket.domain.product.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Slice;
import site.goldenticket.domain.product.model.Product;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class RegionProductResponse {

    private long totalCount;
    private List<ProductResponse> productResponseList;

    public static RegionProductResponse fromEntity(long totalCount, Slice<Product> productSlice) {
        List<ProductResponse> productResponseList = productSlice.getContent().stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());

        return RegionProductResponse.builder()
                .totalCount(totalCount)
                .productResponseList(productResponseList)
                .build();
    }
}
