package com.vibe.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.vibe.server.model.User;
import com.vibe.server.service.UserService;

import java.util.List;
import java.util.Set;

/**
 * Controller for user operations.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get the current user.
     *
     * @param userDetails the authenticated user details
     * @return the current user
     */
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        return ResponseEntity.ok(user);
    }

    /**
     * Search for users by username.
     *
     * @param query the search query
     * @return the list of users matching the query
     */
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String query) {
        List<User> users = userService.searchUsersByUsername(query);
        return ResponseEntity.ok(users);
    }

    /**
     * Get the contacts of the current user.
     *
     * @param userDetails the authenticated user details
     * @return the contacts of the current user
     */
    @GetMapping("/contacts")
    public ResponseEntity<Set<User>> getContacts(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        return ResponseEntity.ok(user.getContacts());
    }

    /**
     * Add a contact to the current user.
     *
     * @param userDetails the authenticated user details
     * @param contactId the ID of the contact to add
     * @return the updated contacts of the current user
     */
    @PostMapping("/contacts/{contactId}")
    public ResponseEntity<Set<User>> addContact(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long contactId) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        Set<User> contacts = userService.addContact(user.getId(), contactId);
        return ResponseEntity.ok(contacts);
    }

    /**
     * Remove a contact from the current user.
     *
     * @param userDetails the authenticated user details
     * @param contactId the ID of the contact to remove
     * @return the updated contacts of the current user
     */
    @DeleteMapping("/contacts/{contactId}")
    public ResponseEntity<Set<User>> removeContact(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long contactId) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        Set<User> contacts = userService.removeContact(user.getId(), contactId);
        return ResponseEntity.ok(contacts);
    }

    /**
     * Get contact requests for the current user.
     *
     * @param userDetails the authenticated user details
     * @return the list of contact requests
     */
    @GetMapping("/contact-requests")
    public ResponseEntity<List<User>> getContactRequests(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        List<User> contactRequests = userService.getContactRequests(user.getId());
        return ResponseEntity.ok(contactRequests);
    }

    /**
     * Accept a contact request.
     *
     * @param userDetails the authenticated user details
     * @param requesterId the ID of the requester
     * @return the updated contacts of the current user
     */
    @PostMapping("/contact-requests/{requesterId}/accept")
    public ResponseEntity<Set<User>> acceptContactRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long requesterId) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        Set<User> contacts = userService.acceptContactRequest(user.getId(), requesterId);
        return ResponseEntity.ok(contacts);
    }

    /**
     * Reject a contact request.
     *
     * @param userDetails the authenticated user details
     * @param requesterId the ID of the requester
     * @return the updated contact requests of the current user
     */
    @PostMapping("/contact-requests/{requesterId}/reject")
    public ResponseEntity<List<User>> rejectContactRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long requesterId) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        List<User> contactRequests = userService.rejectContactRequest(user.getId(), requesterId);
        return ResponseEntity.ok(contactRequests);
    }
}