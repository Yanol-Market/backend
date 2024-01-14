package site.goldenticket.domain.alert.entity;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.goldenticket.common.entiy.BaseTimeEntity;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class Alert extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private Long userId;

    private String content;

    private Boolean viewed;

    @Builder
    public Alert(Long id, Long userId, String content, Boolean viewed) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.viewed = viewed;
    }
}
