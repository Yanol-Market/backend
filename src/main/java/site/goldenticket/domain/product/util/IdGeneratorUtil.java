package site.goldenticket.domain.product.util;

import java.util.concurrent.atomic.AtomicLong;

public class IdGeneratorUtil {
    private static final AtomicLong idCounter = new AtomicLong(1);

    private IdGeneratorUtil() {}

    public static Long createID() {
        return idCounter.getAndIncrement();
    }
}
