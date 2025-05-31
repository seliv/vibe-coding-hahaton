package com.vibe.server.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vibe.server.model.User;
import com.vibe.server.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for user operations.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Get a user by username.
     *
     * @param username the username
     * @return the user
     * @throws IllegalArgumentException if the user is not found
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    }

    /**
     * Search for users by username.
     *
     * @param query the search query
     * @return the list of users matching the query
     */
    public List<User> searchUsersByUsername(String query) {
        return userRepository.findAll().stream()
                .filter(user -> user.getUsername().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Add a contact to a user.
     * This creates a contact request that needs to be accepted by the other user.
     *
     * @param userId the ID of the user
     * @param contactId the ID of the contact to add
     * @return the updated contacts of the user
     * @throws IllegalArgumentException if the user or contact is not found
     */
    @Transactional
    public Set<User> addContact(Long userId, Long contactId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        User contact = userRepository.findById(contactId)
                .orElseThrow(() -> new IllegalArgumentException("Contact not found: " + contactId));

        // Create a contact request
        user.addContactRequest(contact);
        userRepository.save(user);

        return user.getContacts();
    }

    /**
     * Remove a contact from a user.
     *
     * @param userId the ID of the user
     * @param contactId the ID of the contact to remove
     * @return the updated contacts of the user
     * @throws IllegalArgumentException if the user or contact is not found
     */
    @Transactional
    public Set<User> removeContact(Long userId, Long contactId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        User contact = userRepository.findById(contactId)
                .orElseThrow(() -> new IllegalArgumentException("Contact not found: " + contactId));

        user.removeContact(contact);
        contact.removeContact(user);

        userRepository.save(user);
        userRepository.save(contact);

        return user.getContacts();
    }

    /**
     * Get contact requests for a user.
     *
     * @param userId the ID of the user
     * @return the list of contact requests
     * @throws IllegalArgumentException if the user is not found
     */
    public List<User> getContactRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        return user.getContactRequestsList();
    }

    /**
     * Accept a contact request.
     *
     * @param userId the ID of the user
     * @param requesterId the ID of the requester
     * @return the updated contacts of the user
     * @throws IllegalArgumentException if the user or requester is not found
     */
    @Transactional
    public Set<User> acceptContactRequest(Long userId, Long requesterId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("Requester not found: " + requesterId));

        // Remove the contact request
        user.removeContactRequest(requester);

        // Add the contact
        user.addContact(requester);
        requester.addContact(user);

        userRepository.save(user);
        userRepository.save(requester);

        return user.getContacts();
    }

    /**
     * Reject a contact request.
     *
     * @param userId the ID of the user
     * @param requesterId the ID of the requester
     * @return the updated contact requests of the user
     * @throws IllegalArgumentException if the user or requester is not found
     */
    @Transactional
    public List<User> rejectContactRequest(Long userId, Long requesterId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("Requester not found: " + requesterId));

        // Remove the contact request
        user.removeContactRequest(requester);

        userRepository.save(user);

        return user.getContactRequestsList();
    }
}
