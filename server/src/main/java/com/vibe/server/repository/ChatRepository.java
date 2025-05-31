package com.vibe.server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vibe.server.model.Chat;
import com.vibe.server.model.User;

/**
 * Repository for Chat entity.
 */
@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    
    /**
     * Find all chats where the user is a participant.
     *
     * @param user the user
     * @return the list of chats
     */
    List<Chat> findByParticipantsContaining(User user);
    
    /**
     * Find all chats where the user is the owner.
     *
     * @param owner the owner
     * @return the list of chats
     */
    List<Chat> findByOwner(User owner);
    
    /**
     * Find a direct chat between two users.
     *
     * @param user1 the first user
     * @param user2 the second user
     * @return the list of chats (should be at most one)
     */
    @Query("SELECT c FROM Chat c JOIN c.participants p1 JOIN c.participants p2 " +
           "WHERE c.type = 'DIRECT' AND p1 = :user1 AND p2 = :user2")
    List<Chat> findDirectChatBetweenUsers(@Param("user1") User user1, @Param("user2") User user2);
}