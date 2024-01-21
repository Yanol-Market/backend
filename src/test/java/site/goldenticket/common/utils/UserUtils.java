package site.goldenticket.common.utils;

import site.goldenticket.domain.product.constants.AreaCode;
import site.goldenticket.domain.user.entity.User;
import site.goldenticket.domain.user.wish.entity.WishRegion;

public final class UserUtils {

    public static String EMAIL = "email@gamil.com";
    public static String PASSWORD = "password";
    public static String NAME = "name";
    public static String NICKNAME = "nickname";
    public static String PHONE_NUMBER = "01000000000";
    public static Long YANOLJA_ID = 1L;
    public static String BANK_NAME = "bankName";
    public static String ACCOUNT_NUMBER = "000000000000";
    public static final String CHANGE_PASSWORD = "changePassword";

    public static User createUser(String encodePassword) {
        return User.builder()
                .email(EMAIL)
                .password(encodePassword)
                .name(NAME)
                .nickname(NICKNAME)
                .phoneNumber(PHONE_NUMBER)
                .yanoljaId(YANOLJA_ID)
                .build();
    }

    public static WishRegion createWishRegion(AreaCode areaCode) {
        return WishRegion.builder()
                .areaCode(areaCode)
                .build();
    }
}
