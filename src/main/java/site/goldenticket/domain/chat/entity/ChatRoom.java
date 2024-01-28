package site.goldenticket.domain.chat.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.goldenticket.common.entiy.BaseTimeEntity;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private Long productId;

    private Long buyerId;

    @Builder
    public ChatRoom(Long id, Long productId, Long buyerId) {
        this.id = id;
        this.productId = productId;
        this.buyerId = buyerId;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
