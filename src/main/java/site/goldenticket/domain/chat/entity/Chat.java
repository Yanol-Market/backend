package site.goldenticket.domain.chat.entity;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.goldenticket.common.entiy.BaseTimeEntity;
@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Chat extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private Long chatRoomId;

    @Enumerated(EnumType.STRING)
    private SenderType senderType;

    private Long userId;

    private Boolean viewedBySeller;

    private Boolean viewedByBuyer;

    private String content;

    @Builder
    public Chat(Long id, Long chatRoomId, SenderType senderType, Long userId, String content, Boolean viewedBySeller, Boolean viewedByBuyer) {
        this.id = id;
        this.chatRoomId = chatRoomId;
        this.senderType = senderType;
        this.userId = userId;
        this.content = content;
        this.viewedBySeller = viewedBySeller;
        this.viewedByBuyer = viewedByBuyer;
    }

    public void setViewedBySeller(Boolean viewed) {
        this.viewedBySeller = viewed;
    }
    public void setViewedByBuyer(Boolean viewed) {
        this.viewedByBuyer = viewed;
    }
}
