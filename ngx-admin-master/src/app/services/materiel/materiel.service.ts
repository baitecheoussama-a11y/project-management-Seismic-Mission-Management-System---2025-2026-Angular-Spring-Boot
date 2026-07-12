// materiel.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Materiel {
  idMateriel: number;
  codeMateriel: string;
  marque: string;
  modele: string;
  designation: string;
  dateAchat: string;
  prix?: number;
  status: 'EN_BON_ETAT' | 'EN_PANNE' | 'EN_REPARATION_INTERNE' | 'EN_REPARATION_EXTERNE';
  enUtilisation?: boolean;
  coutTotalReparations?: number;
  typeMaterielId?: number;
  typeMaterielLibelle?: string;
  categorieId?: number;
  categorieNom?: string;
  images?: any[];
  affectationCount?: number;
  reparationCount?: number;
  // ❌ REMOVED: quantityTotal, quantityAvailable
}

export interface CreateMaterielRequest {
  codeMateriel: string;
  marque: string;
  modele: string;
  designation: string;
  dateAchat: string;
  prix?: number;
  // ❌ REMOVED: quantityTotal
  status: string;
  typeMaterielId: number;
}

@Injectable({ providedIn: 'root' })
export class MaterielService {
  private apiUrl = 'http://localhost:8080/api/materiels';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Materiel[]> {
    return this.http.get<Materiel[]>(this.apiUrl);
  }

  getById(id: number): Observable<Materiel> {
    return this.http.get<Materiel>(`${this.apiUrl}/${id}`);
  }

  getByType(typeId: number): Observable<Materiel[]> {
    return this.http.get<Materiel[]>(`${this.apiUrl}/type/${typeId}`);
  }

  getByCategorie(categorieId: number): Observable<Materiel[]> {
    return this.http.get<Materiel[]>(`${this.apiUrl}/categorie/${categorieId}`);
  }

  create(materiel: CreateMaterielRequest): Observable<Materiel> {
    return this.http.post<Materiel>(this.apiUrl, materiel);
  }

  update(id: number, materiel: Partial<Materiel>): Observable<Materiel> {
    return this.http.put<Materiel>(`${this.apiUrl}/${id}`, materiel);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  updateStatus(id: number, status: string): Observable<Materiel> {
    return this.http.patch<Materiel>(`${this.apiUrl}/${id}/status?status=${status}`, {});
  }
  // Add this method to your existing MaterielService
getAffectationsByMateriel(materielId: number): Observable<any[]> {
  return this.http.get<any[]>(`http://localhost:8080/api/affectations-materiel/materiel/${materielId}`);
}

getAllAffectations(): Observable<any[]> {
  return this.http.get<any[]>('http://localhost:8080/api/affectations-materiel');
}


}