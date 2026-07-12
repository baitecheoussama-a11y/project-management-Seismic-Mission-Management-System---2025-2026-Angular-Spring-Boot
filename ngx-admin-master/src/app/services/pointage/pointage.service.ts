// services/pointage/pointage.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface StatusPointage {
  name: string;
  label: string;
  color: string;
}

export interface Pointage {
  id?: number;
  datePointage: string;
  status: string;
  motifAbsent?: string;
  remarque?: string;
  employeId: number;
  employeNom?: string;
  employePrenom?: string;
  statusLabel?: string;
  statusColor?: string;
}

export interface PointageRequest {
  employeId: number;
  datePointage: string;
  status: string;
  motifAbsent?: string;
  remarque?: string;
}

export interface PointageStats {
  totalEmployees: number;
  present: number;
  absent: number;
  late: number;
  onLeave: number;
  onMission: number;
  notRecorded: number;
}

@Injectable({
  providedIn: 'root'
})
export class PointageService {
  private apiUrl = 'http://localhost:8080/api/pointages';

  constructor(private http: HttpClient) {}

  // Get all pointages for a specific date
  getPointagesByDate(date: string): Observable<Pointage[]> {
    return this.http.get<Pointage[]>(`${this.apiUrl}/date/${date}`);
  }

  // Get pointages for a date range
  getPointagesByDateRange(startDate: string, endDate: string): Observable<Pointage[]> {
    return this.http.get<Pointage[]>(`${this.apiUrl}/range?start=${startDate}&end=${endDate}`);
  }

  // Get pointages for a specific employee
  getPointagesByEmploye(employeId: number): Observable<Pointage[]> {
    return this.http.get<Pointage[]>(`${this.apiUrl}/employe/${employeId}`);
  }

  // Get pointage for a specific employee on a specific date
  getPointageByEmployeAndDate(employeId: number, date: string): Observable<Pointage> {
    return this.http.get<Pointage>(`${this.apiUrl}/employe/${employeId}/date/${date}`);
  }

  // Create new pointage
  createPointage(request: PointageRequest): Observable<Pointage> {
    return this.http.post<Pointage>(this.apiUrl, request);
  }

  // Update pointage
  updatePointage(id: number, request: PointageRequest): Observable<Pointage> {
    return this.http.put<Pointage>(`${this.apiUrl}/${id}`, request);
  }

  // Delete pointage
  deletePointage(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Get today's pointages
  getTodaysPointages(): Observable<Pointage[]> {
    return this.http.get<Pointage[]>(`${this.apiUrl}/today`);
  }

  // Get pointage stats for today
  getTodaysStats(): Observable<PointageStats> {
    return this.http.get<PointageStats>(`${this.apiUrl}/today/stats`);
  }

  // Get pointage stats for a specific date
  getStatsByDate(date: string): Observable<PointageStats> {
    return this.http.get<PointageStats>(`${this.apiUrl}/date/${date}/stats`);
  }

  // Get all status options
  getStatusOptions(): Observable<StatusPointage[]> {
    return this.http.get<StatusPointage[]>(`${this.apiUrl}/statuses`);
  }

  // ✅ ADD THIS METHOD - Mark all employees as present
  markAllPresent(date: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/mark-all-present?date=${date}`, {});
  }
}