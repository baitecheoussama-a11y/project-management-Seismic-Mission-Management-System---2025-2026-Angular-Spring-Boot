// src/app/pages/safety/shared/services/event-api.service.ts

import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Mission } from '../../services/mission/mission.service';

export interface Event {
  id: number;
  titre: string;
  description: string;
  date: string;
  heure: string;
  formattedDateTime?: string;
  missionId?: number;
  missionNom?: string;
  typeEvenementId?: number;
  typeEvenementNom?: string;
  niveauPriorite?: string;
  niveauPrioriteLabel?: string;
  niveauPrioriteColor?: string;
  isUpcoming?: boolean;
  isToday?: boolean;
  isPast?: boolean;
}

export interface EventRequest {
  titre: string;
  description: string;
  date: string;
  heure: string;
  missionId?: number;
  typeEvenementId?: number;
}

export interface EventResponse {
  id: number;
  titre: string;
  description: string;
  date: string;
  heure: string;
  formattedDateTime: string;
  missionId: number;
  missionNom: string;
  typeEvenementId: number;
  typeEvenementNom: string;
  niveauPriorite: string;
  niveauPrioriteLabel: string;
  niveauPrioriteColor: string;
  isUpcoming: boolean;
  isToday: boolean;
  isPast: boolean;
}

export interface EventType {
  id: number;
  nom: string;
  description: string;
  niveauPriorite: string;
  niveauPrioriteLabel: string;
  niveauPrioriteColor: string;
  actif: boolean;
  evenementsCount: number;
}

export interface EventTypeRequest {
  nom: string;
  description: string;
  niveauPriorite: string;
  actif: boolean;
}

export interface PriorityLevel {
  value: string;
  label: string;
  color: string;
}

@Injectable({
  providedIn: 'root'
})
export class EventApiService {
  private baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  // ========== EVENTS APIs ==========

  createEvent(event: EventRequest): Observable<EventResponse> {
    return this.http.post<EventResponse>(`${this.baseUrl}/evenements`, event);
  }

  updateEvent(id: number, event: EventRequest): Observable<EventResponse> {
    return this.http.put<EventResponse>(`${this.baseUrl}/evenements/${id}`, event);
  }

  
  deleteEvent(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/evenements/${id}`);
  }

  getAllEvents(): Observable<EventResponse[]> {
    return this.http.get<EventResponse[]>(`${this.baseUrl}/evenements`);
  }

  getEventById(id: number): Observable<EventResponse> {
    return this.http.get<EventResponse>(`${this.baseUrl}/evenements/${id}`);
  }

  getEventsByMission(missionId: number): Observable<EventResponse[]> {
    return this.http.get<EventResponse[]>(`${this.baseUrl}/evenements/mission/${missionId}`);
  }

  getEventsByType(typeId: number): Observable<EventResponse[]> {
    return this.http.get<EventResponse[]>(`${this.baseUrl}/evenements/type/${typeId}`);
  }

  getEventsByDate(date: string): Observable<EventResponse[]> {
    const params = new HttpParams().set('date', date);
    return this.http.get<EventResponse[]>(`${this.baseUrl}/evenements/date`, { params });
  }

  getEventsByDateRange(startDate: string, endDate: string): Observable<EventResponse[]> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);
    return this.http.get<EventResponse[]>(`${this.baseUrl}/evenements/date-range`, { params });
  }

  getUpcomingEvents(): Observable<EventResponse[]> {
    return this.http.get<EventResponse[]>(`${this.baseUrl}/evenements/upcoming`);
  }

  getPastEvents(): Observable<EventResponse[]> {
    return this.http.get<EventResponse[]>(`${this.baseUrl}/evenements/past`);
  }

  getTodaysEvents(): Observable<EventResponse[]> {
    return this.http.get<EventResponse[]>(`${this.baseUrl}/evenements/today`);
  }

  searchEvents(keyword: string): Observable<EventResponse[]> {
    const params = new HttpParams().set('keyword', keyword);
    return this.http.get<EventResponse[]>(`${this.baseUrl}/evenements/search`, { params });
  }

  getEventsCount(): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/evenements/count`);
  }

  // ========== EVENT TYPES APIs ==========

  createEventType(type: EventTypeRequest): Observable<EventType> {
    return this.http.post<EventType>(`${this.baseUrl}/type-evenements`, type);
  }

  updateEventType(id: number, type: EventTypeRequest): Observable<EventType> {
    return this.http.put<EventType>(`${this.baseUrl}/type-evenements/${id}`, type);
  }

  deleteEventType(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/type-evenements/${id}`);
  }

  getAllEventTypes(): Observable<EventType[]> {
    return this.http.get<EventType[]>(`${this.baseUrl}/type-evenements`);
  }

  getActiveEventTypes(): Observable<EventType[]> {
    return this.http.get<EventType[]>(`${this.baseUrl}/type-evenements/active`);
  }

  getEventTypeById(id: number): Observable<EventType> {
    return this.http.get<EventType>(`${this.baseUrl}/type-evenements/${id}`);
  }

 // In your EventApiService class
toggleEventTypeStatus(id: number): Observable<EventType> {
  return this.http.patch<EventType>(`${this.baseUrl}/type-evenements/${id}/toggle`, {});
}

  // ========== MISSIONS APIs ==========
  getAllMissions(): Observable<Mission[]> {
    return this.http.get<Mission[]>(`${this.baseUrl}/missions`);
  }

  // In your Angular service (type-event.service.ts)
toggleTypeStatus(id: number): Observable<any> {
  return this.http.patch(`${this.baseUrl}/type-evenements/${id}/toggle`, {});
}

// Alternative method in event-api.service.ts
updateEventTypeStatus(id: number, actif: boolean): Observable<EventType> {
  return this.http.put<EventType>(`${this.baseUrl}/type-evenements/${id}/status`, { actif });
}

}