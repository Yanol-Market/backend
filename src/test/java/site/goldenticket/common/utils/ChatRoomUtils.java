package site.goldenticket.common.utils;

import site.goldenticket.domain.chat.entity.ChatRoom;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.user.entity.User;

public class ChatRoomUtils {

    private  ChatRoomUtils() {}

    public static ChatRoom createChatRoom(Product product, User user) {
        return ChatRoom.builder()
                .productId(product.getId())
                .buyerId(user.getId())
                .build();
    }
}
