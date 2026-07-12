// services/fonctions/fonction.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Fonction {
  id?: number;
  nom: string;
  description: string;
  nombreEmployes?: number;
  employes?: EmployeSummary[];
}

export interface EmployeSummary {
  id: number;
  nom: string;
  prenom: string;
  numIdentite: string;
  email: string;
}

export interface CreateFonctionDTO {
  nom: string;
  description: string;
}

export interface UpdateFonctionDTO {
  nom?: string;
  description?: string;
}

@Injectable({
  providedIn: 'root'
})
export class FonctionService {
  private apiUrl = 'http://localhost:8080/api/fonctions';


  constructor(private http: HttpClient) {}

  getAllFonctions(): Observable<Fonction[]> {
    return this.http.get<Fonction[]>(this.apiUrl);
  }

  getFonctionById(id: number): Observable<Fonction> {
    return this.http.get<Fonction>(`${this.apiUrl}/${id}`);
  }

  getFonctionWithEmployes(id: number): Observable<Fonction> {
    return this.http.get<Fonction>(`${this.apiUrl}/${id}/details`);
  }

  createFonction(fonction: CreateFonctionDTO): Observable<Fonction> {
    return this.http.post<Fonction>(this.apiUrl, fonction);
  }

  updateFonction(id: number, fonction: UpdateFonctionDTO): Observable<Fonction> {
    return this.http.put<Fonction>(`${this.apiUrl}/${id}`, fonction);
  }

  deleteFonction(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}