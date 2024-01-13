package site.goldenticket.common.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateUtil {
    private DateUtil() {}

    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }
    public static long daysFromNow(LocalDate targetDate) {
        return ChronoUnit.DAYS.between(LocalDate.now(), targetDate);
    }
}
