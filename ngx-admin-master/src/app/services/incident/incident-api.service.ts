// src/app/pages/safety/incidents/services/incident-api.service.ts

import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Incident {
  id: number;
  type: string;
  typeLabel: string;
  typeColor: string;
  description: string;
  dateIncident: string;
  niveauGravite: string;
  niveauGraviteLabel: string;
  niveauGraviteColor: string;
  employeId: number;
  employeFullName: string;
  employeEmail: string;
  etatMedicalId?: number;
  groupeSanguin?: string;
  coordonneeId?: number;
  latitude?: number;
  longitude?: number;
  ordre?: number;
  siteId?: number;
  siteSurface?: number;
  wilayaNum?: number;
  wilayaNom?: string;
  formattedDate?: string;
  isRecent?: boolean;
}

export interface IncidentRequest {
  type: string;
  description: string;
  dateIncident: string;
  niveauGravite: string;
  employeId: number;
  coordonnee?: {
    latitude: number;
    longitude: number;
    ordre: number;
    siteId?: number;  // اختياري - قد لا يكون موجود
  };
}

@Injectable({
  providedIn: 'root'
})
export class IncidentApiService {
  private baseUrl = 'http://localhost:8080/api/incidents';

  constructor(private http: HttpClient) {}

  // ========== CRUD OPERATIONS ==========

  createIncident(incident: IncidentRequest): Observable<Incident> {
    return this.http.post<Incident>(this.baseUrl, incident);
  }

  updateIncident(id: number, incident: IncidentRequest): Observable<Incident> {
    return this.http.put<Incident>(`${this.baseUrl}/${id}`, incident);
  }

  deleteIncident(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getAllIncidents(): Observable<Incident[]> {
    return this.http.get<Incident[]>(this.baseUrl);
  }

  getIncidentById(id: number): Observable<Incident> {
    return this.http.get<Incident>(`${this.baseUrl}/${id}`);
  }

  getIncidentsByEmploye(employeId: number): Observable<Incident[]> {
    return this.http.get<Incident[]>(`${this.baseUrl}/employe/${employeId}`);
  }

  getIncidentsByType(type: string): Observable<Incident[]> {
    return this.http.get<Incident[]>(`${this.baseUrl}/type/${type}`);
  }

  getIncidentsByGravite(niveauGravite: string): Observable<Incident[]> {
    return this.http.get<Incident[]>(`${this.baseUrl}/gravite/${niveauGravite}`);
  }

  getIncidentsByDate(date: string): Observable<Incident[]> {
    const params = new HttpParams().set('date', date);
    return this.http.get<Incident[]>(`${this.baseUrl}/date`, { params });
  }

  getIncidentsByDateRange(startDate: string, endDate: string): Observable<Incident[]> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);
    return this.http.get<Incident[]>(`${this.baseUrl}/date-range`, { params });
  }

  getRecentIncidents(): Observable<Incident[]> {
    return this.http.get<Incident[]>(`${this.baseUrl}/recent`);
  }

  searchIncidents(keyword: string, page: number = 0, size: number = 10): Observable<any> {
    const params = new HttpParams()
      .set('keyword', keyword)
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<any>(`${this.baseUrl}/search`, { params });
  }

  getIncidentsCount(): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/count`);
  }

  getStatsByType(): Observable<{ [key: string]: number }> {
    return this.http.get<{ [key: string]: number }>(`${this.baseUrl}/stats/by-type`);
  }

  getStatsByGravite(): Observable<{ [key: string]: number }> {
    return this.http.get<{ [key: string]: number }>(`${this.baseUrl}/stats/by-gravite`);
  }
  // incident-api.service.ts - أضف هذه الدوال

getIncidentsByMission(missionId: number): Observable<Incident[]> {
  return this.http.get<Incident[]>(`${this.baseUrl}/mission/${missionId}`);
}

getIncidentsByMissionPaged(missionId: number, page: number, size: number, sortBy: string, direction: string): Observable<any> {
  return this.http.get(`${this.baseUrl}/mission/${missionId}/paged`, {
    params: {
      page: page.toString(),
      size: size.toString(),
      sortBy: sortBy,
      direction: direction
    }
  });
}

searchIncidentsByMission(missionId: number, keyword: string, page: number, size: number): Observable<any> {
  return this.http.get(`${this.baseUrl}/mission/${missionId}/search`, {
    params: {
      keyword: keyword,
      page: page.toString(),
      size: size.toString()
    }
  });
}

getIncidentsStatsByMission(missionId: number): Observable<{ [key: string]: number }> {
  return this.http.get<{ [key: string]: number }>(`${this.baseUrl}/mission/${missionId}/stats`);
}



}