package site.goldenticket.domain.product.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Slice;
import site.goldenticket.common.constants.AreaCode;
import site.goldenticket.common.constants.PriceRange;
import site.goldenticket.domain.product.model.Product;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class SearchProductResponse {

    private String areaName;
    private String keyword;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String priceRange;
    private long totalCounts;
    private List<ProductResponse> productResponseList;

    public static SearchProductResponse fromEntity(
            AreaCode areaCode, String keyword, LocalDate checkInDate, LocalDate checkOutDate, PriceRange priceRange, long totalCounts, Slice<Product> productSlice
    ) {
        List<ProductResponse> productResponseList = productSlice.getContent().stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());

        return SearchProductResponse.builder()
                .areaName(areaCode.getAreaName())
                .keyword(keyword)
                .checkInDate(checkInDate)
                .checkOutDate(checkOutDate)
                .totalCounts(totalCounts)
                .priceRange(priceRange.getLabel())
                .productResponseList(productResponseList)
                .build();
    }
}
