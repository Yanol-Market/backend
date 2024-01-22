package site.goldenticket.domain.chat.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import site.goldenticket.domain.chat.entity.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findAllByBuyerId(Long buyerId);

    List<ChatRoom> findAllByProductId(Long productId);

    Optional<ChatRoom> findByBuyerIdAndProductId(Long buyerId, Long productId);

    Boolean existsByBuyerIdAndProductId(Long buyerId, Long productId);
}
