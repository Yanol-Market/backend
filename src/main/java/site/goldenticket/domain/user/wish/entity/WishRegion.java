package site.goldenticket.domain.user.wish.entity;

import jakarta.persistence.*;
import lombok.*;
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
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WishRegion that = (WishRegion) o;
        return region == that.region;
    }

    @Override
    public int hashCode() {
        return Objects.hash(region);
    }
}
