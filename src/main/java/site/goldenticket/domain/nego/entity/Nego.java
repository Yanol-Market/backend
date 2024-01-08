package site.goldenticket.domain.nego.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.goldenticket.domain.nego.status.NegotiationStatus;

import java.time.LocalDateTime;

@Entity
@Table(name="nego")
@NoArgsConstructor
@Getter
public class Nego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer price; // 네고가격
    private Integer count; // 네고횟수



    //@ManyToOne
    //@JoinColumn(name = "user_id")
    //private User user; //유저ID

    //@ManyToOne
    //@JoinColumn(name = "product_id")
    //private Product product; // 상품ID



    private NegotiationStatus status; // 네고 상태
    private Boolean consent; // 승낙여부
    private LocalDateTime expirationTime; // 만료일시
    private LocalDateTime createdAt; // 생성일시
    private LocalDateTime updatedAt; // 수정일시

    public void setStatus(NegotiationStatus status) {
        this.status = status;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Builder
    public Nego(Long id, Integer price, Integer count, NegotiationStatus status, Boolean consent,
                LocalDateTime expirationTime, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.price = price;
        this.count = (count != null) ? count : 0;
        this.status = status;
        this.consent = consent;
        this.expirationTime = expirationTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
