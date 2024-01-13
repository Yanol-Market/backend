package site.goldenticket.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import site.goldenticket.common.constants.PaymentStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Converter {
    public static LocalDateTime convertUnixToLocalDateTime(long unixTimestamp) {
        // Instant를 사용하여 유닉스 타임스탬프를 LocalDateTime으로 변환
        Instant instant = Instant.ofEpochSecond(unixTimestamp);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static LocalDate convertDatetoLocalDate(Date date) {
        // java.util.Date를 java.time.Instant로 변환
        Instant instant = date.toInstant();

        // java.time.Instant를 java.time.LocalDate로 변환
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static PaymentStatus convertStatus(String status) {
        return PaymentStatus.valueOf(status.toUpperCase());
    }
}
