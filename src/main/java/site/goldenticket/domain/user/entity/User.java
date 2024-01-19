package site.goldenticket.domain.user.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SQLRestriction;
import site.goldenticket.common.entiy.BaseTimeEntity;
import site.goldenticket.common.exception.CustomException;

import java.util.Objects;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static site.goldenticket.common.response.ErrorCode.ALREADY_REGISTER_YANOLJA_ID;
import static site.goldenticket.domain.user.entity.RoleType.ROLE_USER;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(name = "users")
@SQLRestriction("deleted = false")
@ToString(exclude = {"password", "agreement"})
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String email;
    private String name;
    private String nickname;
    private String password;
    private String phoneNumber;
    private String imageUrl;
    private String bankName;
    private String accountNumber;

    @Enumerated(STRING)
    private RoleType role;

    private Long yanoljaId;
    private boolean deleted;

    @OneToOne(
            mappedBy = "user", fetch = LAZY,
            cascade = ALL, orphanRemoval = true
    )
    private Agreement agreement;

    @Builder
    private User(
            @Nullable String name,
            @Nullable String nickname,
            @Nullable String email,
            @Nullable String password,
            @Nullable String phoneNumber,
            Long yanoljaId
    ) {
        this.role = ROLE_USER;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.yanoljaId = yanoljaId;
    }

    public void registerAlertSetting(Agreement agreement) {
        this.agreement = agreement;
    }

    public void registerYanoljaId(Long yanoljaId) {
        if (!Objects.isNull(this.yanoljaId)) {
            throw new CustomException(ALREADY_REGISTER_YANOLJA_ID);
        }

        this.yanoljaId = yanoljaId;
    }

    public void registerAccount(String bankName, String accountNumber) {
        this.bankName = bankName;
        this.accountNumber = accountNumber;
    }
}
