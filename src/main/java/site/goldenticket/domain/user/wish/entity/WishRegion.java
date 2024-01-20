package site.goldenticket.domain.user.wish.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.goldenticket.common.entiy.BaseTimeEntity;
import site.goldenticket.domain.product.constants.AreaCode;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WishRegion extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private AreaCode region;

    @Builder
    private WishRegion(Long userId, AreaCode areaCode) {
        this.userId = userId;
        this.region = areaCode;
    }
}
