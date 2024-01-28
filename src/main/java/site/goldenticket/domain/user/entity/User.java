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
import site.goldenticket.domain.user.wish.entity.WishRegion;

import java.util.*;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static site.goldenticket.common.response.ErrorCode.*;
import static site.goldenticket.domain.user.entity.RoleType.ROLE_USER;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(name = "users")
@SQLRestriction("deleted = false")
@ToString(exclude = {"password", "agreements", "wishRegions", "deleteReasons"})
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

    @OneToMany(
            mappedBy = "user", fetch = LAZY,
            cascade = ALL, orphanRemoval = true
    )
    private final List<Agreement> agreements = new ArrayList<>();

    @OneToMany(
            mappedBy = "user", fetch = LAZY,
            cascade = ALL, orphanRemoval = true
    )
    private final Set<WishRegion> wishRegions = new HashSet<>();

    private boolean deleted;

    @OneToMany(
            mappedBy = "user", fetch = LAZY,
            cascade = ALL, orphanRemoval = true
    )
    private final List<DeleteReason> deleteReasons = new ArrayList<>();

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

    public void registerAgreement(Agreement agreement) {
        this.agreements.add(agreement);
    }

    public void updateProfile(String nickname) {
        this.nickname = nickname;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void registerYanoljaId(Long yanoljaId) {
        if (!Objects.isNull(this.yanoljaId)) {
            throw new CustomException(ALREADY_REGISTER_YANOLJA_ID);
        }

        this.yanoljaId = yanoljaId;
    }

    public void registerAccount(String bankName, String accountNumber) {
        if (!Objects.isNull(this.bankName) || !Objects.isNull(this.accountNumber)) {
            throw new CustomException(ALREADY_REGISTER_ACCOUNT);
        }

        this.bankName = bankName;
        this.accountNumber = accountNumber;
    }

    public void removeAccount() {
        this.bankName = null;
        this.accountNumber = null;
    }

    public void registerWishRegions(List<WishRegion> wishRegions) {
        this.wishRegions.removeIf(wishRegion -> !wishRegions.contains(wishRegion));
        wishRegions.forEach(this::addWishRegion);
    }

    public boolean isValidRegionSize(int maxSize) {
        return wishRegions.size() > maxSize;
    }

    public void deleted(DeleteReason deleteReason) {
        if (this.deleted) {
            throw new CustomException(ALREADY_DELETE_USER);
        }

        this.deleted = true;
        deleteReasons.add(deleteReason);
        deleteReason.registerUser(this);
    }

    private void addWishRegion(WishRegion wishRegion) {
        this.wishRegions.add(wishRegion);
        wishRegion.registerUser(this);
    }

    public void setId(Long id){
        this.id = id;
    }
}
