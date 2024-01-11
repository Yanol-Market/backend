package site.goldenticket.domain.search.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Objects;

@Getter
public class SearchHistory {

    String keyword;
    String area;
    LocalDate checkInDate;
    LocalDate checkOutDate;
    String priceRange;

    @Builder
    @JsonCreator
    public SearchHistory(
            @JsonProperty("keyword") String keyword,
            @JsonProperty("area") String area,
            @JsonProperty("checkInDate") @JsonFormat(pattern = "yyyy-MM-dd") LocalDate checkInDate,
            @JsonProperty("checkOutDate") @JsonFormat(pattern = "yyyy-MM-dd") LocalDate checkOutDate,
            @JsonProperty("priceRange") String priceRange) {
        this.keyword = keyword;
        this.area = area;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.priceRange = priceRange;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        SearchHistory other = (SearchHistory) obj;

        return Objects.equals(keyword, other.keyword) &&
                Objects.equals(area, other.area) &&
                Objects.equals(checkInDate, other.checkInDate) &&
                Objects.equals(checkOutDate, other.checkOutDate) &&
                Objects.equals(priceRange, other.priceRange);
    }
}
