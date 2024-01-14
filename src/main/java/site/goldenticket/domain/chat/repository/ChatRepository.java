package site.goldenticket.domain.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.goldenticket.domain.chat.entity.Chat;

public interface ChatRepository extends JpaRepository<Chat, Long> {

}
