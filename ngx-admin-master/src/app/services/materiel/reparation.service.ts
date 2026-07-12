// reparation.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ReparationItem {
  idReparation: number;
  datePanne: string;
  dateReparation?: string;
  detailProbleme?: string;
  cout?: number;
  // ❌ REMOVED: quantity
  materielId: number;
  missionId?: number;
  type?: string;
  status?: string;
  technicien?: string;
  fournisseur?: string;
  dateSortieChantier?: string;
  dateEntreeChantier?: string;
  sourceType?: string;
  affectationId?: number;
}

export interface PanneRequest {
  materielId: number;
  datePanne: string;
  // ❌ REMOVED: quantity
  detailProbleme?: string;
  missionId?: number;      
  affectationId?: number; 
}

export interface LancementReparation {
  reparationId: number;
  type: string;
  technicien?: string;
  fournisseur?: string;
  dateSortieChantier?: string;
}

export interface FinReparation {
  reparationId: number;
  dateReparation: string;
  cout: number;
  dateEntreeChantier?: string;
}

export interface UpdatePanneRequest {
  datePanne?: string;
  // ❌ REMOVED: quantity
  detailProbleme?: string;
}

export interface UpdateInternalRepairRequest {
  technicien?: string;
  // ❌ REMOVED: quantity
  detailProbleme?: string;
}

export interface UpdateExternalRepairRequest {
  fournisseur?: string;
  // ❌ REMOVED: quantity
  detailProbleme?: string;
  dateSortieChantier?: string;
}

@Injectable({ providedIn: 'root' })
export class ReparationService {
  private apiUrl = 'http://localhost:8080/api/reparations';

  constructor(private http: HttpClient) {}

  declarePanne(request: PanneRequest): Observable<ReparationItem> {
    return this.http.post<ReparationItem>(`${this.apiUrl}/panne`, request);
  }

  updatePanne(reparationId: number, request: UpdatePanneRequest): Observable<ReparationItem> {
    return this.http.put<ReparationItem>(`${this.apiUrl}/${reparationId}/panne`, request);
  }

  updateInternalRepair(reparationId: number, request: UpdateInternalRepairRequest): Observable<ReparationItem> {
    return this.http.put<ReparationItem>(`${this.apiUrl}/${reparationId}/internal`, request);
  }

  updateExternalRepair(reparationId: number, request: UpdateExternalRepairRequest): Observable<ReparationItem> {
    return this.http.put<ReparationItem>(`${this.apiUrl}/${reparationId}/external`, request);
  }

  deleteReparation(reparationId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${reparationId}`);
  }

  launchRepair(request: LancementReparation): Observable<ReparationItem> {
    return this.http.post<ReparationItem>(`${this.apiUrl}/launch`, request);
  }

  completeRepair(request: FinReparation): Observable<ReparationItem> {
    return this.http.post<ReparationItem>(`${this.apiUrl}/complete`, request);
  }

  getAllByMateriel(materielId: number): Observable<ReparationItem[]> {
    return this.http.get<ReparationItem[]>(`${this.apiUrl}/materiel/${materielId}`);
  }

  getPendingByMateriel(materielId: number): Observable<ReparationItem[]> {
    return this.http.get<ReparationItem[]>(`${this.apiUrl}/materiel/${materielId}/pending`);
  }

  getOngoingInternal(materielId: number): Observable<ReparationItem[]> {
    return this.http.get<ReparationItem[]>(`${this.apiUrl}/materiel/${materielId}/ongoing/internal`);
  }

  getOngoingExternal(materielId: number): Observable<ReparationItem[]> {
    return this.http.get<ReparationItem[]>(`${this.apiUrl}/materiel/${materielId}/ongoing/external`);
  }

  getCompletedByMateriel(materielId: number): Observable<ReparationItem[]> {
    return this.http.get<ReparationItem[]>(`${this.apiUrl}/materiel/${materielId}/completed`);
  }

  getById(id: number): Observable<ReparationItem> {
    return this.http.get<ReparationItem>(`${this.apiUrl}/${id}`);
  }

  getByMaterielAndMission(materielId: number, missionId: number): Observable<ReparationItem[]> {
    return this.http.get<ReparationItem[]>(`${this.apiUrl}/materiel/${materielId}/mission/${missionId}`);
  }

  getPendingByMaterielAndMission(materielId: number, missionId: number): Observable<ReparationItem[]> {
    return this.http.get<ReparationItem[]>(`${this.apiUrl}/materiel/${materielId}/mission/${missionId}/pending`);
  }

  getOngoingByMaterielAndMission(materielId: number, missionId: number): Observable<ReparationItem[]> {
    return this.http.get<ReparationItem[]>(`${this.apiUrl}/materiel/${materielId}/mission/${missionId}/ongoing`);
  }

  getCompletedByMaterielAndMission(materielId: number, missionId: number): Observable<ReparationItem[]> {
    return this.http.get<ReparationItem[]>(`${this.apiUrl}/materiel/${materielId}/mission/${missionId}/completed`);
  }

  getAllByMaterielAndMission(materielId: number, missionId: number): Observable<ReparationItem[]> {
    return this.http.get<ReparationItem[]>(`${this.apiUrl}/materiel/${materielId}/mission/${missionId}/all`);
  }
}