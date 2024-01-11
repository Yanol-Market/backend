package site.goldenticket.domain.product.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Slice;
import site.goldenticket.domain.product.model.Product;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class SearchProductResponse {

    String searchAccommodationName;
    LocalDate searchCheckInDate;
    LocalDate searchCheckOutDate;
    long totalCounts;
    List<ProductResponse> productResponseList;

    public static SearchProductResponse fromEntity(
            String searchAccommodationName, LocalDate searchCheckInDate, LocalDate searchCheckOutDate, long totalCounts, Slice<Product> productSlice
    ) {
        List<ProductResponse> productResponseList = productSlice.getContent().stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());

        return SearchProductResponse.builder()
                .searchAccommodationName(searchAccommodationName)
                .searchCheckInDate(searchCheckInDate)
                .searchCheckOutDate(searchCheckOutDate)
                .totalCounts(totalCounts)
                .productResponseList(productResponseList)
                .build();
    }
}
