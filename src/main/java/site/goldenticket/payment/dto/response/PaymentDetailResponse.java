package site.goldenticket.payment.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public class PaymentDetailResponse {

    private Long productId;
    private String imageUrl;
    private String accommodationName;
    private String roomName;
    private String reservationType; //enum 으로
    private Integer standardNumber;
    private Integer maximumNumber;
    private LocalDate checkInDate;
    private LocalTime checkInTime;
    private LocalDate checkOutDate;
    private LocalTime checkOutTime;
    private String userName;
    private String phoneNumber;
    private String email;
    private Integer price;
    private Integer fee;
    private Integer totalPrice;
}
