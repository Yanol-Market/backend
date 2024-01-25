package site.goldenticket.common.utils;

import site.goldenticket.domain.chat.entity.Chat;
import site.goldenticket.domain.chat.entity.ChatRoom;
import site.goldenticket.domain.chat.entity.SenderType;
import site.goldenticket.domain.user.entity.User;

import static site.goldenticket.domain.chat.entity.SenderType.BUYER;

public class ChatUtils {
    private static final SenderType senderType = BUYER;
    public static final String content = "채팅 메시지 내용";
    private static final Boolean viewedBySeller = false;
    private static final Boolean viewedByBuyer = true;

    private ChatUtils() {}

    public static Chat createChat(ChatRoom chatRoom, User user) {
        Long chatRoomId = chatRoom.getId();
        Long userId = user.getId();

        return Chat.builder()
                .chatRoomId(chatRoomId)
                .senderType(senderType)
                .userId(userId)
                .content(content)
                .viewedBySeller(viewedBySeller)
                .viewedByBuyer(viewedByBuyer)
                .build();
    }
}
