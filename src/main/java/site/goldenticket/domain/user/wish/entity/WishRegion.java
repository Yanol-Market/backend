package site.goldenticket.domain.user.wish.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.goldenticket.common.entiy.BaseTimeEntity;
import site.goldenticket.domain.product.constants.AreaCode;
import site.goldenticket.domain.user.entity.User;

import java.util.Objects;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WishRegion extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private AreaCode region;

    @Builder
    private WishRegion(AreaCode areaCode) {
        this.region = areaCode;
    }

    public void registerUser(User user) {
        if (!Objects.isNull(this.user)) {
            this.user.getWishRegions().remove(this);
        }

        this.user = user;
        user.getWishRegions().add(this);
    }
}
