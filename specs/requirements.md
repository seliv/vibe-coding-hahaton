# Lightweight Home-brewed Skype Replacement

## Overview

In May 2025 Skype was shutdown. There are many alternatives, but Skype was very good for team chatting. 
Let’s create a privately deployable Skype-like chatting tool.

## References
- Solution technologies and infrastructure: environment.md
- Solution Architecture Decisions: architecture.md

## Core Functionality (User's Perspective)

1. User self-registration: a new user must be able to visit the application page and register.
   The user should Just enter username and password (twice) and they are registered.
   No IDP or email verification is required.
2. The user has a list of contacts, sought and added by username.
   On add contact, the counterparty should accept the connection request.
   When the user adds a person to their address book, the action is mutual: the counterparty gets the requestor in their address book as well.
   Users can remove contacts from their address book.
3. Users can chat with their contacts.
4. Chat history is persistent and searchable across all chats and groups.
5. Users can create group chats, up to 300 participants. 
   Group chat history is also persistent and searchable.
   Users can leave group chats. 
   The owner can delete group chat and edit the participant list.
6. Chat message delivery should be reliable.
   If the user sent a message, and the message reached the server, it should be delivered to all counterparties.
7. Chat messages should be persistent, should not disappear if the server (all servers) goes down.
8. UI is the typical chat UI with chat list on the left and chat messages on the right.
9. Users can send text messages with bold and italic font style.
10. Users can post images (including posting to group chats) 
