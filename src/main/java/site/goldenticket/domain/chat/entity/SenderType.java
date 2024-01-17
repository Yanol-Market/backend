package site.goldenticket.domain.chat.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SenderType {
    SYSTEM("시스템"),
    SELLER("판매자"),
    BUYER("구매자");

    private final String description;
}
