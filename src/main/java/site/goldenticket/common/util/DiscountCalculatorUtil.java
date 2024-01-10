package site.goldenticket.common.util;

public class DiscountCalculatorUtil {
    private DiscountCalculatorUtil() {}

    public static int calculateDiscountPercentage(int originalPrice, int discountedPrice) {
        double discountPercentage = (double) (originalPrice - discountedPrice) / originalPrice * 100;
        return (int) discountPercentage;
    }
}
