package site.goldenticket.common.utils;

import site.goldenticket.domain.product.constants.AreaCode;
import site.goldenticket.domain.user.dto.*;
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
                .build();
    }

    public static User createUserWithYanolja(String encodePassword) {
        return User.builder()
                .email(EMAIL)
                .password(encodePassword)
                .name(NAME)
                .nickname(NICKNAME)
                .phoneNumber(PHONE_NUMBER)
                .yanoljaId(YANOLJA_ID)
                .build();
    }

    public static JoinRequest createJoinRequest() {
        return new JoinRequest(
                NAME,
                "NICKNAME",
                "join@gmail.com",
                PASSWORD,
                PHONE_NUMBER,
                null,
                new AgreementRequest(true)
        );
    }

    public static ChangeProfileRequest createChangeProfileRequest(String changeNickname) {
        return new ChangeProfileRequest(changeNickname);
    }

    public static ChangePasswordRequest createChangePasswordRequest() {
        return new ChangePasswordRequest(PASSWORD, CHANGE_PASSWORD);
    }

    public static RegisterAccountRequest createRegisterAccountRequest() {
        return new RegisterAccountRequest(
                BANK_NAME,
                ACCOUNT_NUMBER
        );
    }

    public static RemoveUserRequest createRemoveUserRequest() {
        return new RemoveUserRequest("delete reason");
    }

    public static WishRegion createWishRegion(AreaCode areaCode) {
        return WishRegion.builder()
                .areaCode(areaCode)
                .build();
    }
}
