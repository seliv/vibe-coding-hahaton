package com.vibe.server.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vibe.server.model.Chat;
import com.vibe.server.model.Message;
import com.vibe.server.model.User;

/**
 * Repository for Message entity.
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Find all messages in a chat, ordered by sent time.
     *
     * @param chat the chat
     * @param pageable the pagination information
     * @return the page of messages
     */
    Page<Message> findByChatOrderBySentAtDesc(Chat chat, Pageable pageable);

    /**
     * Find all undelivered messages for a user.
     *
     * @param user the user
     * @return the list of messages
     */
    @Query("SELECT m FROM Message m JOIN m.chat c JOIN c.participants p " +
           "WHERE p = :user AND m.delivered = false AND m.sender != :user")
    List<Message> findUndeliveredMessagesForUser(@Param("user") User user);

    /**
     * Find all messages containing the given text.
     *
     * @param text the text to search for
     * @param user the user who is searching
     * @param pageable the pagination information
     * @return the page of messages
     */
    @Query("SELECT m FROM Message m JOIN m.chat c JOIN c.participants p " +
           "WHERE p = :user AND lower(cast(m.content as string)) LIKE lower(concat('%', cast(:text as string), '%'))")
    Page<Message> searchMessages(@Param("text") String text, @Param("user") User user, Pageable pageable);
}
