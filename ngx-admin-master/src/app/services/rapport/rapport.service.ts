import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface RendementRequest {
  heureDebut: string;
  heureFin: string;
  valeurRendement: number;
  uniteRendement: string;
  date: string;
  activeId?: number;  // ✅ ADD THIS - make it optional for backward compatibility
}

export interface RendementResponse {
  id: number;
  heureDebut: string;
  heureFin: string;
  valeurRendement: number;
  uniteRendement: string;
  date: string;
  dureeHeures: number;
  rapportId: number;
  affectationEquipeId: number;
  activeId?: number;  // ✅ ADD THIS
}

export interface FichierDTO {
  id: number;
  chemin: string;
  titre: string;
  type: 'VIDEO' | 'IMAGE' | 'DOCUMENT';
  dateUpload: string;
  taille: number;
  rapportId: number;
}

export interface RapportRequest {
  titre: string;
  date: string;
  resume: string;
}

export interface RapportResponse {
  id: number;
  titre: string;
  date: string;
  resume: string;
  projectId: number;
  projectName: string;
  missionCode: string;
  rendements?: RendementResponse[];
    fichiers?: FichierDTO[];  // ✅ ADD THIS
}

@Injectable({
  providedIn: 'root'
})
export class RapportService {
  private apiUrl = 'http://localhost:8080/api/rapports';
  private rendementsUrl = 'http://localhost:8080/api/rendements';

  constructor(private http: HttpClient) {}

  // ============ RAPPORT METHODS ============
  
  getRapportsByMission(missionId: number): Observable<RapportResponse[]> {
    return this.http.get<RapportResponse[]>(`${this.apiUrl}/mission/${missionId}`);
  }

  getRapportsForCurrentProject(missionId: number): Observable<RapportResponse[]> {
    return this.http.get<RapportResponse[]>(`${this.apiUrl}/mission/${missionId}/current-project`);
  }

  addRapportToCurrentProject(missionId: number, request: RapportRequest): Observable<RapportResponse> {
    return this.http.post<RapportResponse>(`${this.apiUrl}/mission/${missionId}`, request);
  }

  updateRapport(id: number, request: RapportRequest): Observable<RapportResponse> {
    return this.http.put<RapportResponse>(`${this.apiUrl}/${id}`, request);
  }

  deleteRapport(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getRapportById(id: number): Observable<RapportResponse> {
    return this.http.get<RapportResponse>(`${this.apiUrl}/${id}`);
  }

  searchRapports(missionId: number, keyword: string): Observable<RapportResponse[]> {
    return this.http.get<RapportResponse[]>(`${this.apiUrl}/mission/${missionId}/search?keyword=${keyword}`);
  }

  // ============ RENDEMENT METHODS ============
  
  // In rapport.service.ts, update the endpoint
// In rapport.service.ts

// ✅ FIXED: Remove missionId from URL - it will be derived from rapport on backend
addRendementToRapportWithEquipe(rapportId: number, equipeId: number, rendement: RendementRequest): Observable<RendementResponse> {
  // The activeId is inside rendement object
  return this.http.post<RendementResponse>(
    `${this.rendementsUrl}/rapport/${rapportId}/equipe/${equipeId}`, 
    rendement
  );
}

  getRendementsByRapport(rapportId: number): Observable<RendementResponse[]> {
    return this.http.get<RendementResponse[]>(`${this.rendementsUrl}/rapport/${rapportId}`);
  }

  addRendementToRapport(rapportId: number, request: RendementRequest): Observable<RendementResponse> {
    return this.http.post<RendementResponse>(`${this.rendementsUrl}/rapport/${rapportId}`, request);
  }

  updateRendement(id: number, request: RendementRequest): Observable<RendementResponse> {
    return this.http.put<RendementResponse>(`${this.rendementsUrl}/${id}`, request);
  }

  deleteRendement(id: number): Observable<void> {
    return this.http.delete<void>(`${this.rendementsUrl}/${id}`);
  }

  getRendementsByRapportAndEquipe(rapportId: number, equipeId: number): Observable<RendementResponse[]> {
    return this.http.get<RendementResponse[]>(`${this.rendementsUrl}/rapport/${rapportId}/equipe/${equipeId}`);
  }


  getRendementsByEquipe(equipeId: number, missionId: number): Observable<RendementResponse[]> {
    return this.http.get<RendementResponse[]>(`${this.rendementsUrl}/equipe/${equipeId}/mission/${missionId}`);
  }

  
}