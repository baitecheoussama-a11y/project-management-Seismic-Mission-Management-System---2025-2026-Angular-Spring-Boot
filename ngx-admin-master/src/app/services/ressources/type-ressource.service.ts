import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface TypeRessource {
  idTypeRessource: number;
  nom: string;  // ✅ Changed from 'libelle' to 'nom' to match backend
  categorieRessourceId?: number;
  categorieRessourceNom?: string;
}

@Injectable({ providedIn: 'root' })
export class TypeRessourceService {
  private apiUrl = 'http://localhost:8080/api/ressources/types';

  constructor(private http: HttpClient) {}

  getAll(): Observable<TypeRessource[]> {
    return this.http.get<TypeRessource[]>(this.apiUrl);
  }

  getByCategorie(categorieId: number): Observable<TypeRessource[]> {
    return this.http.get<TypeRessource[]>(`${this.apiUrl}/categorie/${categorieId}`);
  }

  // ✅ Use 'nom' instead of 'libelle'
  create(type: { nom: string; categorieId: number }): Observable<TypeRessource> {
    return this.http.post<TypeRessource>(this.apiUrl, type);
  }

  update(id: number, type: TypeRessource): Observable<TypeRessource> {
    return this.http.put<TypeRessource>(`${this.apiUrl}/${id}`, type);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}