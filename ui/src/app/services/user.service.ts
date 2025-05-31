import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  constructor(private http: HttpClient) {}

  // Get the current user
  getCurrentUser(): Observable<User> {
    return this.http.get<User>(`${environment.apiUrl}/api/users/me`);
  }

  // Search for users by username
  searchUsers(query: string): Observable<User[]> {
    return this.http.get<User[]>(`${environment.apiUrl}/api/users/search?query=${query}`);
  }

  // Get the user's contacts
  getContacts(): Observable<User[]> {
    return this.http.get<User[]>(`${environment.apiUrl}/api/users/contacts`);
  }

  // Add a contact (send a contact request)
  addContact(userId: number): Observable<User[]> {
    return this.http.post<User[]>(`${environment.apiUrl}/api/users/contacts/${userId}`, {});
  }

  // Remove a contact
  removeContact(userId: number): Observable<User[]> {
    return this.http.delete<User[]>(`${environment.apiUrl}/api/users/contacts/${userId}`);
  }

  // Get contact requests
  getContactRequests(): Observable<User[]> {
    return this.http.get<User[]>(`${environment.apiUrl}/api/users/contact-requests`);
  }

  // Accept a contact request
  acceptContactRequest(userId: number): Observable<User[]> {
    return this.http.post<User[]>(`${environment.apiUrl}/api/users/contact-requests/${userId}/accept`, {});
  }

  // Reject a contact request
  rejectContactRequest(userId: number): Observable<User[]> {
    return this.http.post<User[]>(`${environment.apiUrl}/api/users/contact-requests/${userId}/reject`, {});
  }
}