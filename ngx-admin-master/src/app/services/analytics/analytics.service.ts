// src/app/services/analytics/analytics.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PivotRequest {
  rows: string[];
  columns: string[];
  measures: string[];
  filter?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {
  private apiUrl = 'http://localhost:8080/api/dashboard';

  constructor(private http: HttpClient) {}

  getKPIs(): Observable<any> {
    return this.http.get(`${this.apiUrl}/kpis`);
  }

  getCostByMission(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/cost-by-mission`);
  }

  getTrendData(missionId: number, days: number = 30): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/trend?missionId=${missionId}&days=${days}`);
  }

  getProjectStatus(): Observable<any> {
    return this.http.get(`${this.apiUrl}/project-status`);
  }

  getTopResources(limit: number = 5): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/top-resources?limit=${limit}`);
  }

  // ✅ PIVOT TABLE API
  getPivotData(request: PivotRequest): Observable<any[]> {
    return this.http.post<any[]>(`${this.apiUrl}/pivot`, request);
  }
}