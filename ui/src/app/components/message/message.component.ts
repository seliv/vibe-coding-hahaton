import { Component, Input } from '@angular/core';
import { Message, MessageType } from '../../models/message.model';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-message',
  template: `
    <div class="message" [ngClass]="{'own-message': isOwnMessage()}">
      <div class="message-sender" *ngIf="!isOwnMessage()">
        {{ message.sender.username }}
      </div>
      
      <div class="message-content" [ngClass]="{'image-message': isImageMessage()}">
        <ng-container *ngIf="isTextMessage()">
          {{ message.content }}
        </ng-container>
        
        <img *ngIf="isImageMessage()" [src]="message.attachmentUrl" alt="Image" class="message-image" />
      </div>
      
      <div class="message-time">
        {{ formatTime(message.sentAt) }}
        <span class="message-status">
          {{ message.delivered ? '✓' : '' }}
          {{ message.read ? '✓✓' : '' }}
        </span>
      </div>
    </div>
  `,
  styles: [`
    .message {
      max-width: 70%;
      padding: 10px;
      border-radius: 10px;
      margin-bottom: 10px;
      background-color: #f1f1f1;
      align-self: flex-start;
    }
    
    .own-message {
      background-color: #dcf8c6;
      align-self: flex-end;
    }
    
    .message-sender {
      font-weight: bold;
      margin-bottom: 5px;
      font-size: 0.9em;
    }
    
    .message-content {
      word-wrap: break-word;
    }
    
    .image-message {
      padding: 5px;
    }
    
    .message-image {
      max-width: 100%;
      border-radius: 5px;
    }
    
    .message-time {
      font-size: 0.8em;
      color: #888;
      text-align: right;
      margin-top: 5px;
    }
    
    .message-status {
      margin-left: 5px;
    }
  `]
})
export class MessageComponent {
  @Input() message!: Message;
  @Input() currentUser: User | null = null;
  
  isOwnMessage(): boolean {
    if (!this.currentUser) return false;
    return this.message.sender.id === this.currentUser.id;
  }
  
  isTextMessage(): boolean {
    return this.message.type === MessageType.TEXT;
  }
  
  isImageMessage(): boolean {
    return this.message.type === MessageType.IMAGE;
  }
  
  formatTime(date: Date): string {
    if (!date) return '';
    
    const messageDate = new Date(date);
    const today = new Date();
    
    // If the message is from today, just show the time
    if (messageDate.toDateString() === today.toDateString()) {
      return messageDate.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    }
    
    // Otherwise, show the date and time
    return messageDate.toLocaleString([], { 
      month: 'short', 
      day: 'numeric',
      hour: '2-digit', 
      minute: '2-digit' 
    });
  }
}