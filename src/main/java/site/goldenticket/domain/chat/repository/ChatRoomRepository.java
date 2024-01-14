package site.goldenticket.domain.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.goldenticket.domain.chat.entity.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

}
