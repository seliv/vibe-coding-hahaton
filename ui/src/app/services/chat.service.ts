import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Chat, ChatType } from '../models/chat.model';
import { User } from '../models/user.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  constructor(private http: HttpClient) {}

  // Get all chats for the current user
  getUserChats(): Observable<Chat[]> {
    return this.http.get<Chat[]>(`${environment.apiUrl}/api/chats`);
  }

  // Get a specific chat by ID
  getChat(chatId: number): Observable<Chat> {
    return this.http.get<Chat>(`${environment.apiUrl}/api/chats/${chatId}`);
  }

  // Create a new direct chat with another user
  createDirectChat(otherUserId: number): Observable<Chat> {
    return this.http.post<Chat>(`${environment.apiUrl}/api/chats/direct`, { userId: otherUserId });
  }

  // Create a new group chat
  createGroupChat(name: string, participantIds: number[]): Observable<Chat> {
    return this.http.post<Chat>(`${environment.apiUrl}/api/chats/group`, { name, participantIds });
  }

  // Add a user to a group chat
  addUserToChat(chatId: number, userId: number): Observable<Chat> {
    return this.http.post<Chat>(`${environment.apiUrl}/api/chats/${chatId}/participants`, { userId });
  }

  // Remove a user from a group chat
  removeUserFromChat(chatId: number, userId: number): Observable<Chat> {
    return this.http.delete<Chat>(`${environment.apiUrl}/api/chats/${chatId}/participants/${userId}`);
  }

  // Delete a group chat (only owner can do this)
  deleteChat(chatId: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/api/chats/${chatId}`);
  }
}