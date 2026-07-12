// services/materiel/affectation-materiel-to-active.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface AffectationMaterielToActiveDTO {
  idAffectation: number;
  dateDebut: string;
  dateFin: string | null;
  materielId: number;
  materielCode: string;
  materielDesignation: string;
  activeId: number;
  activeCode: string;
  activeObjectif: string;
  isActive: boolean;
}

export interface AssignMaterielToActiveRequest {
  materielId: number;
  activeId: number;
  projectId: number;
  dateDebut: string;
  dateFin: string | null;
}

export interface UpdateMaterielToActiveRequest {
  dateDebut: string;
  dateFin: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class AffectationMaterielToActiveService {
  private apiUrl = 'http://localhost:8080/api/materiel-affectations-to-active';

  constructor(private http: HttpClient) {}

  assignMaterielToActive(request: AssignMaterielToActiveRequest): Observable<AffectationMaterielToActiveDTO> {
    return this.http.post<AffectationMaterielToActiveDTO>(this.apiUrl, request);
  }

  getByMaterielId(materielId: number): Observable<AffectationMaterielToActiveDTO[]> {
    return this.http.get<AffectationMaterielToActiveDTO[]>(`${this.apiUrl}/materiel/${materielId}`);
  }

  getByActiveId(activeId: number): Observable<AffectationMaterielToActiveDTO[]> {
    return this.http.get<AffectationMaterielToActiveDTO[]>(`${this.apiUrl}/active/${activeId}`);
  }

  getById(id: number): Observable<AffectationMaterielToActiveDTO> {
    return this.http.get<AffectationMaterielToActiveDTO>(`${this.apiUrl}/${id}`);
  }

  getActiveByMaterielId(materielId: number): Observable<AffectationMaterielToActiveDTO[]> {
    return this.http.get<AffectationMaterielToActiveDTO[]>(`${this.apiUrl}/materiel/${materielId}/active`);
  }

  update(id: number, request: UpdateMaterielToActiveRequest): Observable<AffectationMaterielToActiveDTO> {
    return this.http.put<AffectationMaterielToActiveDTO>(`${this.apiUrl}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  deleteByMaterielAndActive(materielId: number, activeId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/materiel/${materielId}/active/${activeId}`);
  }
}