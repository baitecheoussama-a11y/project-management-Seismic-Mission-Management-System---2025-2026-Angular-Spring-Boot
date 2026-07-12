// affectation-materiel.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AffectationMateriel {
  idAffectation?: number;
  dateDebut: string;
  dateFin?: string | null;
  // ❌ REMOVED: quantityAssigned
  materielId: number;
  missionId: number;
  materielCode?: string;
  materielMarque?: string;
  materielModele?: string;
  missionCode?: string;
  equipmentType?: string;
}

export interface BatchAffectationRequest {
  materielIds: number[];
  missionId: number;
  dateDebut: string;
  dateFin?: string;
  // ❌ REMOVED: quantityAssigned
}

export interface AvailabilityCheck {
  materielId: number;
  isAvailable: boolean;
  hasEnoughStock: boolean;
  startDate: string;
  endDate: string;
  availableQuantity?: number;
}

@Injectable({ providedIn: 'root' })
export class AffectationMaterielService {
  private apiUrl = 'http://localhost:8080/api/affectations-materiel';

  constructor(private http: HttpClient) {}

  getAll(): Observable<AffectationMateriel[]> {
    return this.http.get<AffectationMateriel[]>(this.apiUrl);
  }

  getById(id: number): Observable<AffectationMateriel> {
    return this.http.get<AffectationMateriel>(`${this.apiUrl}/${id}`);
  }

  getByMateriel(materielId: number): Observable<AffectationMateriel[]> {
    return this.http.get<AffectationMateriel[]>(`${this.apiUrl}/materiel/${materielId}`);
  }

  getByMission(missionId: number): Observable<AffectationMateriel[]> {
    return this.http.get<AffectationMateriel[]>(`${this.apiUrl}/mission/${missionId}`);
  }

  getByMissionId(missionId: number): Observable<AffectationMateriel[]> {
    return this.http.get<AffectationMateriel[]>(`${this.apiUrl}/mission/${missionId}`);
  }

  getByMissionAndType(missionId: number, equipmentType: string): Observable<AffectationMateriel[]> {
    return this.http.get<AffectationMateriel[]>(`${this.apiUrl}/mission/${missionId}/type/${equipmentType}`);
  }

  checkAvailability(materielId: number, startDate: string, endDate?: string): Observable<AvailabilityCheck> {
    let url = `${this.apiUrl}/check-availability?materielId=${materielId}&startDate=${startDate}`;
    if (endDate) {
      url += `&endDate=${endDate}`;
    }
    return this.http.get<AvailabilityCheck>(url);
  }

  create(affectation: AffectationMateriel): Observable<AffectationMateriel> {
    return this.http.post<AffectationMateriel>(this.apiUrl, affectation);
  }

  createBatch(batchRequest: BatchAffectationRequest): Observable<AffectationMateriel[]> {
    return this.http.post<AffectationMateriel[]>(`${this.apiUrl}/batch`, batchRequest);
  }

  update(id: number, affectation: AffectationMateriel): Observable<AffectationMateriel> {
    return this.http.put<AffectationMateriel>(`${this.apiUrl}/${id}`, affectation);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  deleteByMateriel(materielId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/materiel/${materielId}`);
  }

  deleteByMission(missionId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/mission/${missionId}`);
  }
}