package site.goldenticket.common.utils;

import site.goldenticket.domain.user.entity.User;

public final class UserUtils {

    public static String EMAIL = "email@gamil.com";
    public static String PASSWORD = "password";
    public static String NAME = "name";
    public static String NICKNAME = "nickname";
    public static String PHONE_NUMBER = "01000000000";

    public static User createUser(String encodePassword) {
        return User.builder()
                .email(EMAIL)
                .password(encodePassword)
                .name(NAME)
                .nickname(NICKNAME)
                .phoneNumber(PHONE_NUMBER)
                .build();
    }


}
