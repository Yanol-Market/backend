package site.goldenticket.common.utils;

import site.goldenticket.domain.user.entity.User;

import static site.goldenticket.domain.user.entity.RoleType.ROLE_USER;

public final class UserUtils {

    public static String EMAIL = "email@gamil.com";
    public static String PASSWORD = "password";
    public static String NAME = "name";
    public static String NICKNAME = "nickname";
    public static String PHONENUMBER = "010-0000-0000";

    public static User createUser(String encodePassword) {
        return User.builder()
                .email(EMAIL)
                .password(encodePassword)
                .name(NAME)
                .nickname(NICKNAME)
                .phoneNumber(PHONENUMBER)
                .role(ROLE_USER)
                .build();
    }
}
