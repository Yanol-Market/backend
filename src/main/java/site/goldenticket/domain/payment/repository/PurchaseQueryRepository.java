//package site.goldenticket.domain.payment.repository;
//
//import com.querydsl.core.types.Projections;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;
//import site.goldenticket.domain.payment.dto.response.PurchaseProgressResponse;
//
//import java.util.List;
//
//import static site.goldenticket.domain.chat.entity.QChat.chat;
//import static site.goldenticket.domain.chat.entity.QChatRoom.chatRoom;
//import static site.goldenticket.domain.payment.model.QOrder.order;
//import static site.goldenticket.domain.product.model.QProduct.product;
//import static site.goldenticket.domain.user.entity.QUser.user;
//
//@RequiredArgsConstructor
//@Repository
//@Transactional(readOnly = true)
//public class PurchaseQueryRepository {
//
//    private final JPAQueryFactory query;
//
//    public List<PurchaseProgressResponse> getPurchaseProgresses(Long userId) {
//        /*
//        Long productId,
//        String accommodationImage,
//        String accommodationName,
//        ReservationType reservationType,
//        String roomName,
//        Integer standardNumber,
//        Integer maximumNumber,
//        LocalTime checkInTime,
//        LocalTime checkOutTime,
//        LocalDate checkInDate,
//        LocalDate checkOutDate,
//        Integer goldenPrice,
//        String status,
//        Long chatRoomId,
//        String receiverNickname,
//        String receiverProfileImage,
//        Integer price,
//        LocalDateTime lastUpdatedAt
//         */
////        유저 id를 통한 주문 내역
////        주문내역을 이용해서 상품
////        상품을 통해 판매자 정보 즉, 유저
////        구매자아이디와 상품아이디로 채팅방
////        채팅방으로 채팅리스트 리스트중 마지막 채팅시간
//        return query
//                .select(
//                        Projections.fields(PurchaseProgressResponse.class,
//                                product.id,
//                                product.accommodationImage,
//                                product.accommodationName,
//                                product.reservationType,
//                                product.roomName,
//                                product.standardNumber,
//                                product.maximumNumber,
//                                product.checkInTime,
//                                product.checkOutTime,
//                                product.checkInDate,
//                                product.checkOutDate,
//                                product.goldenPrice,
//                                product.productStatus.as("status"),
//
//                        )
//                )
//                .from(order)
//                .leftJoin(product).on(order.productId.eq(product.id))
//                .leftJoin(user).on(product.userId.eq(user.id))
//                .leftJoin(chatRoom).on(chatRoom.productId.eq(product.id))
//                .leftJoin(chat).on(chat.chatRoomId.eq(chatRoom.id))
//                .where(order.userId.eq(userId))
//                .fetch();
//    }
//}
