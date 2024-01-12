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

    String area;
    String keyword;
    LocalDate checkInDate;
    LocalDate checkOutDate;
    String price;
    long totalCounts;
    List<ProductResponse> productResponseList;

    public static SearchProductResponse fromEntity(
            AreaCode areaCode, String keyword, LocalDate checkInDate, LocalDate checkOutDate, PriceRange priceRange, long totalCounts, Slice<Product> productSlice
    ) {
        List<ProductResponse> productResponseList = productSlice.getContent().stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());

        String area = areaCode != null ? areaCode.getAreaName() : "전체";
        String price = priceRange != null ? priceRange.getLabel() : "전체";

        return SearchProductResponse.builder()
                .area(area)
                .keyword(keyword)
                .checkInDate(checkInDate)
                .checkOutDate(checkOutDate)
                .totalCounts(totalCounts)
                .price(price)
                .productResponseList(productResponseList)
                .build();
    }
}
