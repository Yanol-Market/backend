package site.goldenticket.domain.chat.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import site.goldenticket.domain.chat.entity.Chat;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findAllByChatRoomId(Long chatRoomId);
}
