import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Incident {
  id: number;
  type: 'ACCIDENT_TRAVAIL' | 'MALADIE_PROFESSIONNELLE' | 'INCIDENT_SECURITE' | 'INCIDENT_ENVIRONNEMENTAL' | 'AUTRE';
  description: string;
  dateIncident: string;
  niveauGravite: 'FAIBLE' | 'MOYEN' | 'ELEVE' | 'CRITIQUE';
  employeId: number;
  employeNomComplet?: string;
  etatMedicalId?: number;
  coordonnee?: {
    id: number;
    latitude: number;
    longitude: number;
    siteName?: string;
  };
}

export interface IncidentDTO {
  id?: number;
  type: string;
  description: string;
  dateIncident: string;
  niveauGravite: string;
  employeId: number;
  etatMedicalId?: number;
  coordonneeId?: number;
}

@Injectable({
  providedIn: 'root'
})
export class IncidentService {
  private apiUrl = 'http://localhost:8080/api/incidents';


  constructor(private http: HttpClient) {}

  getAllIncidents(): Observable<Incident[]> {
    return this.http.get<Incident[]>(this.apiUrl);
  }

  getIncidentById(id: number): Observable<Incident> {
    return this.http.get<Incident>(`${this.apiUrl}/${id}`);
  }

  getIncidentsByEmployeId(employeId: number): Observable<Incident[]> {
    return this.http.get<Incident[]>(`${this.apiUrl}/employe/${employeId}`);
  }

  getIncidentsByType(type: string): Observable<Incident[]> {
    return this.http.get<Incident[]>(`${this.apiUrl}/type/${type}`);
  }

  getIncidentsByGravite(niveauGravite: string): Observable<Incident[]> {
    return this.http.get<Incident[]>(`${this.apiUrl}/gravite/${niveauGravite}`);
  }

  getIncidentsByDateRange(startDate: string, endDate: string): Observable<Incident[]> {
    return this.http.get<Incident[]>(`${this.apiUrl}/date-range?startDate=${startDate}&endDate=${endDate}`);
  }

  createIncident(incident: IncidentDTO): Observable<Incident> {
    return this.http.post<Incident>(this.apiUrl, incident);
  }

  updateIncident(id: number, incident: IncidentDTO): Observable<Incident> {
    return this.http.put<Incident>(`${this.apiUrl}/${id}`, incident);
  }

  deleteIncident(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}