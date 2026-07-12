import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CategorieRessource {
  idCategorieRessource: number;
  nom: string;
}

@Injectable({ providedIn: 'root' })
export class CategorieRessourceService {
  private apiUrl = 'http://localhost:8080/api/ressources/categories';

  constructor(private http: HttpClient) {}

  getAll(): Observable<CategorieRessource[]> {
    return this.http.get<CategorieRessource[]>(this.apiUrl);
  }

  getById(id: number): Observable<CategorieRessource> {
    return this.http.get<CategorieRessource>(`${this.apiUrl}/${id}`);
  }

  // ✅ Fix: Accept an object with just nom, not full CategorieRessource
  create(category: { nom: string }): Observable<CategorieRessource> {
    return this.http.post<CategorieRessource>(this.apiUrl, category);
  }

  update(id: number, category: CategorieRessource): Observable<CategorieRessource> {
    return this.http.put<CategorieRessource>(`${this.apiUrl}/${id}`, category);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}