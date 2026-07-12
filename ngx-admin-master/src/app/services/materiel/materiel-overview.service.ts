// materiel-overview.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface MaterielImage {
  idImage: number;
  imageUrl: string;
  fileName: string;
  contentType: string;
}

export interface UsageHistory {
  idUtilisation: number;
  resume: string;
  dateDebut: string;
  dateFin: string;
  valeurUtilisation: number;
  uniteUtilisation: string;
}

export interface RepairInfo {
  idReparation: number;
  type: 'INTERNE' | 'EXTERNE';
  cout: number;
  datePanne: string;
  dateReparation: string;
  detailProbleme: string;
  technicien?: string;
  fournisseur?: string;
  dateSortieChantier?: string;
  dateEntreeChantier?: string;
}

export interface MaterielOverview {
  idMateriel: number;
  codeMateriel: string;
  marque: string;
  modele: string;
  designation: string;
  dateAchat: string;
  status: string;
  categoryName: string;
  typeName: string;
  images: MaterielImage[];
  recentUsageHistory: UsageHistory[];
  totalUsageCount: number;
  recentRepairs: RepairInfo[];
  totalRepairCount: number;
}

export interface MaterielDetails extends MaterielOverview {
  allUsageHistory: UsageHistory[];
  allRepairs: RepairInfo[];
}

@Injectable({ providedIn: 'root' })
export class MaterielOverviewService {
  private apiUrl = 'http://localhost:8080/api/materiels';

  constructor(private http: HttpClient) {}

  getOverview(id: number): Observable<MaterielOverview> {
    return this.http.get<MaterielOverview>(`${this.apiUrl}/${id}/overview`);
  }

  getDetails(id: number): Observable<MaterielDetails> {
    return this.http.get<MaterielDetails>(`${this.apiUrl}/${id}/details`);
  }
}