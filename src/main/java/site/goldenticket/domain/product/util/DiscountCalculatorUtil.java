package site.goldenticket.domain.product.util;

public class DiscountCalculatorUtil {
    private DiscountCalculatorUtil() {}

    public static int calculateDiscountPercentage(int originalPrice, int discountedPrice) {
        double discountPercentage = (double) (originalPrice - discountedPrice) / originalPrice * 100;
        return (int) discountPercentage;
    }

    public static int calculateFee(int goldenPrice) {
        return (int) Math.round(goldenPrice * 0.05);
    }
}
