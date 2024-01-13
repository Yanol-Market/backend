package site.goldenticket.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import site.goldenticket.common.entiy.BaseTimeEntity;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"user"})
public class Agreement extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Boolean marketing;

    @Builder
    private Agreement(Boolean marketing) {
        this.marketing = marketing;
    }

    public void registerUser(User user) {
        this.user = user;
        user.registerAlertSetting(this);
    }
}
