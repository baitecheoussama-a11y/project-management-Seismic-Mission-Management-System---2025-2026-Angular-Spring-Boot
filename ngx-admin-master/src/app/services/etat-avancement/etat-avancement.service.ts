import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface EtatAvancement {
  id: number;
  dateLastAvancement: string;
  status: string;
  projectId: number;
  projectName?: string;
  activeId?: number;
  activeCode?: string;
  avancements: AvancementProgress[];
}

export interface AvancementProgress {
  id: number;
  titre: string;
  date: string;
  resume: string;
  etatAvancementId: number;
}

export interface AvancementRequest {
  titre: string;
  date: string;
  resume: string;
}

@Injectable({
  providedIn: 'root'
})
export class EtatAvancementService {
  private apiUrl = 'http://localhost:8080/api/etat-avancement';

  constructor(private http: HttpClient) {}

  getEtatAvancementByActive(activeId: number, missionId: number): Observable<EtatAvancement> {
    console.log('[DEBUG] getEtatAvancementByActive called:', { activeId, missionId });
    return this.http.get<EtatAvancement>(`${this.apiUrl}/active/${activeId}/mission/${missionId}`);
  }

  createEtatAvancementForActive(activeId: number, missionId: number): Observable<EtatAvancement> {
    console.log('[DEBUG] createEtatAvancementForActive called:', { activeId, missionId });
    return this.http.post<EtatAvancement>(`${this.apiUrl}/active/${activeId}/mission/${missionId}`, {});
  }

  getEtatAvancementsByProject(projectId: number): Observable<EtatAvancement[]> {
    return this.http.get<EtatAvancement[]>(`${this.apiUrl}/project/${projectId}`);
  }

  getEtatAvancementById(id: number): Observable<EtatAvancement> {
    return this.http.get<EtatAvancement>(`${this.apiUrl}/${id}`);
  }

  createEtatAvancementForProject(projectId: number): Observable<EtatAvancement> {
    return this.http.post<EtatAvancement>(`${this.apiUrl}/project/${projectId}`, {});
  }

  // ✅ FIXED: Send status as plain string, not JSON object
  updateEtatAvancementStatus(etatAvancementId: number, status: string): Observable<EtatAvancement> {
    console.log('[DEBUG] updateEtatAvancementStatus called:', { etatAvancementId, status });
    console.log('[DEBUG] Sending status as string:', status);
    console.log('[DEBUG] Request body type:', typeof status);
    
    // IMPORTANT: Send as plain string, not JSON object
    // Use text/plain content type or just send the string
    const headers = new HttpHeaders({
      'Content-Type': 'text/plain'
    });
    
    // Send the status string directly, not wrapped in an object
    return this.http.put<EtatAvancement>(
      `${this.apiUrl}/${etatAvancementId}/status`, 
      status,
      { headers }
    );
  }
  
  deleteEtatAvancement(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  addAvancement(etatAvancementId: number, request: AvancementRequest): Observable<AvancementProgress> {
    console.log('[DEBUG] addAvancement called:', { etatAvancementId, request });
    return this.http.post<AvancementProgress>(`${this.apiUrl}/${etatAvancementId}/avancements`, request);
  }

  getAvancementsByEtatAvancement(etatAvancementId: number): Observable<AvancementProgress[]> {
    return this.http.get<AvancementProgress[]>(`${this.apiUrl}/${etatAvancementId}/avancements`);
  }

  updateAvancement(avancementId: number, request: AvancementRequest): Observable<AvancementProgress> {
    return this.http.put<AvancementProgress>(`${this.apiUrl}/avancements/${avancementId}`, request);
  }

  deleteAvancement(avancementId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/avancements/${avancementId}`);
  }
}