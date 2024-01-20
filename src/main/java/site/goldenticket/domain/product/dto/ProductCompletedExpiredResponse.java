package site.goldenticket.domain.product.dto;

import site.goldenticket.domain.product.model.Product;

import java.time.LocalDate;
import java.time.LocalTime;

public record ProductCompletedExpiredResponse(
        Long productId,
        String accommodationImage,
        String accommodationName,
        String roomName,
        int standardNumber,
        int maximumNumber,
        LocalTime checkInTime,
        LocalTime checkOutTime,
        LocalDate checkInDate,
        LocalDate checkOutDate
) {
    public static ProductCompletedExpiredResponse fromEntity(Product product) {
        return new ProductCompletedExpiredResponse(
                product.getId(),
                product.getAccommodationImage(),
                product.getAccommodationName(),
                product.getRoomName(),
                product.getStandardNumber(),
                product.getMaximumNumber(),
                product.getCheckInTime(),
                product.getCheckOutTime(),
                product.getCheckInDate(),
                product.getCheckOutDate()
        );
    }
}
