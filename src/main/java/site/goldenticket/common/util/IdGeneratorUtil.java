package site.goldenticket.common.util;

import java.util.concurrent.atomic.AtomicLong;

public class IdGeneratorUtil {
    private static final AtomicLong idCounter = new AtomicLong(0);

    private IdGeneratorUtil() {}

    public static Long createID() {
        return idCounter.getAndIncrement();
    }
}
