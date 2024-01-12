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

    private Long id;
    private final String areaName;
    private final String keyword;
    private final LocalDate checkInDate;
    private final LocalDate checkOutDate;
    private final String priceRange;

    @Builder
    @JsonCreator
    private SearchHistory(
            @JsonProperty("keyword") String keyword,
            @JsonProperty("areaName") String areaName,
            @JsonProperty("checkInDate") @JsonFormat(pattern = "yyyy-MM-dd") LocalDate checkInDate,
            @JsonProperty("checkOutDate") @JsonFormat(pattern = "yyyy-MM-dd") LocalDate checkOutDate,
            @JsonProperty("priceRange") String priceRange) {
        this.areaName = areaName;
        this.keyword = keyword;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.priceRange = priceRange;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        SearchHistory other = (SearchHistory) obj;

        return Objects.equals(areaName, other.areaName) &&
                Objects.equals(keyword, other.keyword) &&
                Objects.equals(checkInDate, other.checkInDate) &&
                Objects.equals(checkOutDate, other.checkOutDate) &&
                Objects.equals(priceRange, other.priceRange);
    }
}
