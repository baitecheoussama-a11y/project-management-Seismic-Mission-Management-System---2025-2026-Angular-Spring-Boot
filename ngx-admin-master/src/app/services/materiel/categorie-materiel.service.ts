import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CategorieMateriel {
  idCategorie: number;
  nom: string;
}

export interface CreateCategorieMateriel {
  nom: string;
}

@Injectable({ providedIn: 'root' })
export class CategorieMaterielService {
  private apiUrl = 'http://localhost:8080/api/categories-materiel';

  constructor(private http: HttpClient) {}

  getAll(): Observable<CategorieMateriel[]> {
    return this.http.get<CategorieMateriel[]>(this.apiUrl);
  }

  create(categorie: CreateCategorieMateriel): Observable<CategorieMateriel> {
    return this.http.post<CategorieMateriel>(this.apiUrl, categorie);
  }

  update(id: number, categorie: CategorieMateriel): Observable<CategorieMateriel> {
    return this.http.put<CategorieMateriel>(`${this.apiUrl}/${id}`, categorie);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}