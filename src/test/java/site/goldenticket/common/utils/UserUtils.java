package site.goldenticket.common.utils;

import site.goldenticket.domain.product.constants.AreaCode;
import site.goldenticket.domain.user.dto.*;
import site.goldenticket.domain.user.entity.User;
import site.goldenticket.domain.user.wish.entity.WishRegion;

public final class UserUtils {

    public static final String EMAIL = "email@gamil.com";
    public static final String PASSWORD = "password";
    public static final String NAME = "name";
    public static final String NICKNAME = "nickname";
    public static final String PHONE_NUMBER = "01000000000";
    public static final Long YANOLJA_ID = 1L;
    public static final String BANK_NAME = "bankName";
    public static final String ACCOUNT_NUMBER = "000000000000";
    public static final String JOIN_NICKNAME = "joinNickname";
    public static final String JOIN_EMAIL = "join@gmail.com";
    public static final String CHANGE_NICKNAME = "changeNickname";
    public static final String CHANGE_PASSWORD = "changePassword";
    public static final String ENCODED_PASSWORD = "encodedPassword";

    public static User createUser(String encodePassword) {
        return User.builder()
                .email(EMAIL)
                .password(encodePassword)
                .name(NAME)
                .nickname(NICKNAME)
                .phoneNumber(PHONE_NUMBER)
                .build();
    }

    public static User createUserWithAccount(String encodePassword) {
        User user = User.builder()
                .email(EMAIL)
                .password(encodePassword)
                .name(NAME)
                .nickname(NICKNAME)
                .phoneNumber(PHONE_NUMBER)
                .build();
        user.registerAccount(BANK_NAME, ACCOUNT_NUMBER);
        return user;
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
                JOIN_NICKNAME,
                JOIN_EMAIL,
                PASSWORD,
                PHONE_NUMBER,
                null,
                new AgreementRequest(true)
        );
    }

    public static ChangeProfileRequest createChangeProfileRequest() {
        return new ChangeProfileRequest(CHANGE_NICKNAME);
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
