// rapport-details.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface RapportDetails {
  id: string;
  rapportId: number;
  projectId: number;
  createdAt: string;
  details: any;
}

@Injectable({
  providedIn: 'root'
})
export class RapportDetailsService {
  private baseUrl = 'http://localhost:8080/api/rapport-details';

  constructor(private http: HttpClient) {}

  saveDetails(rapportId: number, details: any): Observable<RapportDetails> {
    return this.http.post<RapportDetails>(this.baseUrl, { rapportId, details });
  }

  updateDetails(rapportId: number, details: any): Observable<RapportDetails> {
    return this.http.put<RapportDetails>(`${this.baseUrl}/${rapportId}`, details);
  }

  getDetails(rapportId: number): Observable<RapportDetails> {
    return this.http.get<RapportDetails>(`${this.baseUrl}/${rapportId}`);
  }

  getByProjectId(projectId: number): Observable<RapportDetails[]> {
    return this.http.get<RapportDetails[]>(`${this.baseUrl}/project/${projectId}`);
  }

  deleteDetails(rapportId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${rapportId}`);
  }
}