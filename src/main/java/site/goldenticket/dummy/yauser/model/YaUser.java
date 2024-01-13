package site.goldenticket.dummy.yauser.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class YaUser {
    @Id
    @Column(name = "ya_user_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;
    private String phoneNumber;

    @Builder
    private YaUser(String name, String email, String password, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }
}
