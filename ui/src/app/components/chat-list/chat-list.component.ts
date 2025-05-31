import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { Chat, ChatType } from '../../models/chat.model';
import { User } from '../../models/user.model';
import { Message } from '../../models/message.model';
import { ChatService } from '../../services/chat.service';
import { AuthService } from '../../services/auth.service';
import { EventService } from '../../services/event.service';
import { UserService } from '../../services/user.service';
import { MessageService } from '../../services/message.service';

@Component({
  selector: 'app-chat-list',
  template: `
    <div class="chat-list-container">
      <div class="header">
        <h2>Vibe Chat</h2>
        <div class="user-info">
          <span>{{ currentUser?.username }}</span>
          <button class="logout-btn" (click)="logout()">Logout</button>
        </div>
      </div>

      <div class="search-bar">
        <input type="text" placeholder="Search chats..." [(ngModel)]="searchTerm" />
      </div>

      <div class="chat-list">
        <div *ngIf="isLoading" class="loading">Loading chats...</div>

        <div *ngIf="!isLoading && chats.length === 0" class="no-chats">
          No chats found. Start a new conversation!
        </div>

        <div *ngFor="let chat of filteredChats" 
             class="chat-item" 
             [class.active]="selectedChatId === chat.id"
             (click)="selectChat(chat)">
          <div class="chat-avatar">
            {{ chat.type === ChatType.DIRECT ? getOtherUserInitial(chat) : 'G' }}
          </div>
          <div class="chat-info">
            <div class="chat-name">{{ getChatName(chat) }}</div>
            <div class="chat-last-message">{{ getLastMessagePreview(chat) }}</div>
          </div>
        </div>
      </div>

      <div class="actions">
        <button class="new-chat-btn" (click)="showNewChatDialog()">New Chat</button>
      </div>

      <div class="chat-content">
        <router-outlet></router-outlet>
      </div>
    </div>
  `,
  styles: [`
    .chat-list-container {
      display: flex;
      flex-direction: column;
      height: 100vh;
    }

    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 10px 15px;
      background-color: #4CAF50;
      color: white;
    }

    .user-info {
      display: flex;
      align-items: center;
    }

    .logout-btn {
      margin-left: 10px;
      padding: 5px 10px;
      background-color: transparent;
      border: 1px solid white;
      color: white;
      cursor: pointer;
    }

    .search-bar {
      padding: 10px;
      border-bottom: 1px solid #ccc;
    }

    .search-bar input {
      width: 100%;
      padding: 8px;
      border: 1px solid #ccc;
      border-radius: 4px;
    }

    .chat-list {
      flex: 1;
      overflow-y: auto;
      padding: 10px;
    }

    .chat-item {
      display: flex;
      align-items: center;
      padding: 10px;
      border-radius: 4px;
      cursor: pointer;
      margin-bottom: 5px;
    }

    .chat-item:hover {
      background-color: #f5f5f5;
    }

    .chat-item.active {
      background-color: #e0e0e0;
    }

    .chat-avatar {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      background-color: #4CAF50;
      color: white;
      display: flex;
      justify-content: center;
      align-items: center;
      margin-right: 10px;
    }

    .chat-info {
      flex: 1;
    }

    .chat-name {
      font-weight: bold;
    }

    .chat-last-message {
      font-size: 0.9em;
      color: #666;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .actions {
      padding: 10px;
      border-top: 1px solid #ccc;
    }

    .new-chat-btn {
      width: 100%;
      padding: 10px;
      background-color: #4CAF50;
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    }

    .loading, .no-chats {
      padding: 20px;
      text-align: center;
      color: #666;
    }

    .chat-content {
      display: flex;
      flex: 2;
      border-left: 1px solid #ccc;
    }
  `]
})
export class ChatListComponent implements OnInit, OnDestroy {
  chats: Chat[] = [];
  filteredChats: Chat[] = [];
  searchTerm = '';
  isLoading = true;
  selectedChatId: number | null = null;
  currentUser: User | null = null;
  ChatType = ChatType; // Make enum available in template
  lastMessages: Map<number, Message> = new Map(); // Store last message for each chat

  private eventSubscription: Subscription | null = null;
  private userSubscription: Subscription | null = null;

  constructor(
    private chatService: ChatService,
    private authService: AuthService,
    private eventService: EventService,
    private userService: UserService,
    private messageService: MessageService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.userSubscription = this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });

    this.loadChats();
    this.startEventPolling();
  }

  ngOnDestroy(): void {
    if (this.eventSubscription) {
      this.eventSubscription.unsubscribe();
    }

    if (this.userSubscription) {
      this.userSubscription.unsubscribe();
    }

    this.eventService.stopPolling();
  }

  loadChats(): void {
    this.isLoading = true;
    this.chatService.getUserChats().subscribe({
      next: chats => {
        this.chats = chats;
        this.applyFilter();
        this.isLoading = false;

        // Load last message for each chat
        this.chats.forEach(chat => {
          this.messageService.getChatMessages(chat.id, 0, 1).subscribe({
            next: response => {
              if (response.content.length > 0) {
                this.lastMessages.set(chat.id, response.content[0]);
              }
            },
            error: error => {
              console.error(`Error loading messages for chat ${chat.id}:`, error);
            }
          });
        });
      },
      error: error => {
        console.error('Error loading chats:', error);
        this.isLoading = false;
      }
    });
  }

  startEventPolling(): void {
    this.eventService.startPolling();
    this.eventSubscription = this.eventService.getEvents().subscribe(events => {
      // Handle events like new messages, chat updates, etc.
      const needsRefresh = events.some(event => 
        event.type === 'NEW_MESSAGE' || 
        event.type === 'CHAT_CREATED' || 
        event.type === 'CHAT_UPDATED'
      );

      if (needsRefresh) {
        this.loadChats();
      }
    });
  }

  selectChat(chat: Chat): void {
    this.selectedChatId = chat.id;
    this.router.navigate(['/chat', chat.id]);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  showNewChatDialog(): void {
    const username = prompt('Enter username to start a chat with:');
    if (!username) return;

    this.userService.searchUsers(username).subscribe({
      next: (users) => {
        if (users.length === 0) {
          alert('No users found with that username');
          return;
        }

        const user = users[0]; // Take the first matching user
        this.chatService.createDirectChat(user.id).subscribe({
          next: (chat) => {
            this.selectChat(chat);
          },
          error: (error) => {
            console.error('Error creating chat:', error);
            alert('Failed to create chat. Please try again.');
          }
        });
      },
      error: (error) => {
        console.error('Error searching users:', error);
        alert('Failed to search users. Please try again.');
      }
    });
  }

  getChatName(chat: Chat): string {
    if (chat.type === ChatType.DIRECT && this.currentUser) {
      // For direct chats, show the other user's name
      const otherUser = chat.participants.find(p => p.id !== this.currentUser?.id);
      return otherUser ? otherUser.username : chat.name;
    }
    return chat.name;
  }

  getOtherUserInitial(chat: Chat): string {
    if (chat.type === ChatType.DIRECT && this.currentUser) {
      const otherUser = chat.participants.find(p => p.id !== this.currentUser?.id);
      return otherUser ? otherUser.username.charAt(0).toUpperCase() : '?';
    }
    return '?';
  }

  getLastMessagePreview(chat: Chat): string {
    const lastMessage = this.lastMessages.get(chat.id);
    if (!lastMessage) {
      return 'No messages yet';
    }

    if (lastMessage.type === 'IMAGE') {
      return '📷 Image';
    }

    // Truncate long messages
    const maxLength = 30;
    if (lastMessage.content.length > maxLength) {
      return lastMessage.content.substring(0, maxLength) + '...';
    }

    return lastMessage.content;
  }

  applyFilter(): void {
    if (!this.searchTerm) {
      this.filteredChats = this.chats;
      return;
    }

    const term = this.searchTerm.toLowerCase();
    this.filteredChats = this.chats.filter(chat => {
      const chatName = this.getChatName(chat).toLowerCase();
      return chatName.includes(term);
    });
  }
}
