import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Message, MessageType } from '../models/message.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MessageService {
  constructor(private http: HttpClient) {}

  // Get messages for a specific chat with pagination
  getChatMessages(chatId: number, page: number = 0, size: number = 20): Observable<{ content: Message[], totalPages: number }> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<{ content: Message[], totalPages: number }>(
      `${environment.apiUrl}/api/chats/${chatId}/messages`,
      { params }
    );
  }

  // Send a text message to a chat
  sendTextMessage(chatId: number, content: string): Observable<Message> {
    return this.http.post<Message>(`${environment.apiUrl}/api/chats/${chatId}/messages`, {
      content,
      type: MessageType.TEXT
    });
  }

  // Send an image message to a chat
  sendImageMessage(chatId: number, imageFile: File): Observable<Message> {
    const formData = new FormData();
    formData.append('file', imageFile);
    formData.append('type', MessageType.IMAGE);
    
    return this.http.post<Message>(`${environment.apiUrl}/api/chats/${chatId}/messages/attachment`, formData);
  }

  // Mark messages as read
  markMessagesAsRead(chatId: number): Observable<void> {
    return this.http.post<void>(`${environment.apiUrl}/api/chats/${chatId}/messages/read`, {});
  }

  // Search messages across all chats
  searchMessages(query: string, page: number = 0, size: number = 20): Observable<{ content: Message[], totalPages: number }> {
    const params = new HttpParams()
      .set('query', query)
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<{ content: Message[], totalPages: number }>(
      `${environment.apiUrl}/api/messages/search`,
      { params }
    );
  }
}