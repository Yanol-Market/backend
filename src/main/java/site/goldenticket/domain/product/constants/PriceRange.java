package site.goldenticket.domain.product.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PriceRange {
    UNDER_10("10만원 미만", 0, 100000),
    BETWEEN_10_AND_20("10만원대", 100000, 200000),
    BETWEEN_20_AND_30("20만원대", 200000, 300000),
    BETWEEN_30_AND_40("30-40만원대", 300000, 400000),
    ABOVE_50("50만원 이상", 500000, null),
    FULL_RANGE("전체", null, null);

    private final String label;
    private final Integer minPrice;
    private final Integer maxPrice;
}
