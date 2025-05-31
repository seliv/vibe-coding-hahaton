import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { Chat } from '../../models/chat.model';
import { Message, MessageType } from '../../models/message.model';
import { User } from '../../models/user.model';
import { ChatService } from '../../services/chat.service';
import { MessageService } from '../../services/message.service';
import { AuthService } from '../../services/auth.service';
import { EventService } from '../../services/event.service';

@Component({
  selector: 'app-chat',
  template: `
    <div class="chat-container" *ngIf="chat">
      <div class="chat-header">
        <h3>{{ getChatName() }}</h3>
        <div class="chat-participants">
          {{ getParticipantsText() }}
        </div>
      </div>
      
      <div class="messages-container" #messagesContainer>
        <div *ngIf="isLoading" class="loading">Loading messages...</div>
        
        <div *ngIf="!isLoading && messages.length === 0" class="no-messages">
          No messages yet. Start the conversation!
        </div>
        
        <div *ngFor="let message of messages" class="message-wrapper">
          <app-message [message]="message" [currentUser]="currentUser"></app-message>
        </div>
      </div>
      
      <div class="message-input">
        <form [formGroup]="messageForm" (ngSubmit)="sendMessage()">
          <input 
            type="text" 
            formControlName="content" 
            placeholder="Type a message..." 
            class="message-text-input"
          />
          <button 
            type="submit" 
            [disabled]="messageForm.invalid || isSending"
            class="send-button"
          >
            {{ isSending ? 'Sending...' : 'Send' }}
          </button>
        </form>
      </div>
    </div>
    
    <div *ngIf="!chat && !isLoading" class="no-chat-selected">
      <p>Select a chat or start a new conversation</p>
    </div>
  `,
  styles: [`
    .chat-container {
      display: flex;
      flex-direction: column;
      height: 100%;
      width: 100%;
    }
    
    .chat-header {
      padding: 15px;
      border-bottom: 1px solid #ccc;
      background-color: #f5f5f5;
    }
    
    .chat-header h3 {
      margin: 0;
      margin-bottom: 5px;
    }
    
    .chat-participants {
      font-size: 0.9em;
      color: #666;
    }
    
    .messages-container {
      flex: 1;
      overflow-y: auto;
      padding: 15px;
      display: flex;
      flex-direction: column;
    }
    
    .message-wrapper {
      margin-bottom: 10px;
    }
    
    .message-input {
      padding: 15px;
      border-top: 1px solid #ccc;
    }
    
    .message-input form {
      display: flex;
    }
    
    .message-text-input {
      flex: 1;
      padding: 10px;
      border: 1px solid #ccc;
      border-radius: 4px;
      margin-right: 10px;
    }
    
    .send-button {
      padding: 10px 15px;
      background-color: #4CAF50;
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    }
    
    .send-button:disabled {
      background-color: #cccccc;
      cursor: not-allowed;
    }
    
    .loading, .no-messages, .no-chat-selected {
      padding: 20px;
      text-align: center;
      color: #666;
    }
    
    .no-chat-selected {
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100%;
      width: 100%;
      font-size: 1.2em;
    }
  `]
})
export class ChatComponent implements OnInit, OnDestroy {
  chat: Chat | null = null;
  messages: Message[] = [];
  currentUser: User | null = null;
  isLoading = false;
  isSending = false;
  messageForm: FormGroup;
  
  private chatId: number | null = null;
  private routeSubscription: Subscription | null = null;
  private userSubscription: Subscription | null = null;
  private eventSubscription: Subscription | null = null;
  
  constructor(
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private chatService: ChatService,
    private messageService: MessageService,
    private authService: AuthService,
    private eventService: EventService
  ) {
    this.messageForm = this.formBuilder.group({
      content: ['', Validators.required]
    });
  }
  
  ngOnInit(): void {
    this.userSubscription = this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
    
    this.routeSubscription = this.route.params.subscribe(params => {
      const id = params['id'];
      if (id) {
        this.chatId = +id;
        this.loadChat();
        this.loadMessages();
      }
    });
    
    this.subscribeToEvents();
  }
  
  ngOnDestroy(): void {
    if (this.routeSubscription) {
      this.routeSubscription.unsubscribe();
    }
    
    if (this.userSubscription) {
      this.userSubscription.unsubscribe();
    }
    
    if (this.eventSubscription) {
      this.eventSubscription.unsubscribe();
    }
  }
  
  loadChat(): void {
    if (!this.chatId) return;
    
    this.isLoading = true;
    this.chatService.getChat(this.chatId).subscribe({
      next: chat => {
        this.chat = chat;
        this.isLoading = false;
      },
      error: error => {
        console.error('Error loading chat:', error);
        this.isLoading = false;
      }
    });
  }
  
  loadMessages(): void {
    if (!this.chatId) return;
    
    this.isLoading = true;
    this.messageService.getChatMessages(this.chatId).subscribe({
      next: response => {
        this.messages = response.content;
        this.isLoading = false;
        this.markMessagesAsRead();
      },
      error: error => {
        console.error('Error loading messages:', error);
        this.isLoading = false;
      }
    });
  }
  
  markMessagesAsRead(): void {
    if (!this.chatId) return;
    
    this.messageService.markMessagesAsRead(this.chatId).subscribe({
      error: error => {
        console.error('Error marking messages as read:', error);
      }
    });
  }
  
  sendMessage(): void {
    if (this.messageForm.invalid || !this.chatId) return;
    
    this.isSending = true;
    const content = this.messageForm.value.content;
    
    this.messageService.sendTextMessage(this.chatId, content).subscribe({
      next: message => {
        this.messages.unshift(message);
        this.messageForm.reset();
        this.isSending = false;
      },
      error: error => {
        console.error('Error sending message:', error);
        this.isSending = false;
      }
    });
  }
  
  subscribeToEvents(): void {
    this.eventSubscription = this.eventService.getEvents().subscribe(events => {
      const newMessages = events.filter(event => 
        event.type === 'NEW_MESSAGE' && 
        event.payload.chatId === this.chatId
      );
      
      if (newMessages.length > 0) {
        this.loadMessages();
      }
    });
  }
  
  getChatName(): string {
    if (!this.chat || !this.currentUser) return '';
    
    if (this.chat.type === 'DIRECT') {
      const otherUser = this.chat.participants.find(p => p.id !== this.currentUser?.id);
      return otherUser ? otherUser.username : this.chat.name;
    }
    
    return this.chat.name;
  }
  
  getParticipantsText(): string {
    if (!this.chat) return '';
    
    if (this.chat.type === 'DIRECT') {
      return 'Direct Message';
    }
    
    const count = this.chat.participants.length;
    return `${count} participant${count !== 1 ? 's' : ''}`;
  }
}