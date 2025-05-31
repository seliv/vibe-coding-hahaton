import { Injectable, OnDestroy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, Subject, interval, takeUntil, switchMap, catchError, of } from 'rxjs';
import { environment } from '../../environments/environment';
import { Message } from '../models/message.model';
import { AuthService } from './auth.service';

export interface Event {
  type: 'NEW_MESSAGE' | 'MESSAGE_READ' | 'USER_ONLINE' | 'USER_OFFLINE' | 'CHAT_CREATED' | 'CHAT_UPDATED';
  payload: any;
}

@Injectable({
  providedIn: 'root'
})
export class EventService implements OnDestroy {
  private pollingInterval = 3000; // 3 seconds
  private destroy$ = new Subject<void>();
  private events$ = new BehaviorSubject<Event[]>([]);
  
  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  // Start polling for events
  startPolling(): void {
    if (!this.authService.isAuthenticated()) {
      return;
    }

    interval(this.pollingInterval)
      .pipe(
        takeUntil(this.destroy$),
        switchMap(() => this.pollEvents()),
        catchError(error => {
          console.error('Error polling events:', error);
          return of([]);
        })
      )
      .subscribe(events => {
        if (events.length > 0) {
          this.events$.next(events);
        }
      });
  }

  // Stop polling for events
  stopPolling(): void {
    this.destroy$.next();
  }

  // Get the events observable
  getEvents(): Observable<Event[]> {
    return this.events$.asObservable();
  }

  // Poll for new events from the server
  private pollEvents(): Observable<Event[]> {
    return this.http.get<Event[]>(`${environment.apiUrl}/api/events/poll`);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}