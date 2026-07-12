// services/notification/notification.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface NotificationDTO {
  id: number;
  title: string;
  message: string;
  type: string;
  read: boolean;
  createdAt: string;
  link: string;
  compteId: number;
  compteUsername: string;
}

export interface UnreadCountResponse {
  count: number;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  private apiUrl = 'http://localhost:8080/api/notifications';

  constructor(private http: HttpClient) {}

  getMyNotifications(): Observable<NotificationDTO[]> {
    return this.http.get<NotificationDTO[]>(`${this.apiUrl}/my`);
  }

  getUnreadNotifications(): Observable<NotificationDTO[]> {
    return this.http.get<NotificationDTO[]>(`${this.apiUrl}/my/unread`);
  }

  // ✅ FIX: Handle the response correctly
  getUnreadCount(): Observable<number> {
    return this.http.get<UnreadCountResponse>(`${this.apiUrl}/my/unread/count`)
      .pipe(
        map(response => response.count)
      );
  }

  markAsRead(notificationId: number): Observable<NotificationDTO> {
    return this.http.put<NotificationDTO>(`${this.apiUrl}/${notificationId}/read`, {});
  }

  markAllAsRead(): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/read-all`, {});
  }

  deleteNotification(notificationId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${notificationId}`);
  }

  deleteAllRead(): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/read`);
  }
}