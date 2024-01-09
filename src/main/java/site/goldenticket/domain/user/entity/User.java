package site.goldenticket.domain.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(name = "users")
@SQLRestriction("deleted = false")
public class User {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String name;
    private String nickname;
    private String email;
    private String password;
    private String phoneNumber;
    private String imageUrl;
    private String yanoljaId;

    @Enumerated(STRING)
    private RoleType role;

    private String bankName;
    private String accountNumber;
    private boolean deleted;
    private String withdrawalReason;
    private String withdrawalAt;

    @Builder
    private User(
            String name,
            String nickname,
            String email,
            String password,
            String phoneNumber,
            String imageUrl,
            String yanoljaId,
            RoleType role
    ) {
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.imageUrl = imageUrl;
        this.yanoljaId = yanoljaId;
        this.role = role;
    }
}
