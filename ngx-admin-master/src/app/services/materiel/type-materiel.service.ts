import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface TypeMateriel {
  idTypeMateriel: number;
  libelle: string;
  categorieId?: number;
  categorieNom?: string;
}

@Injectable({ providedIn: 'root' })
export class TypeMaterielService {
  private apiUrl = 'http://localhost:8080/api/types-materiel';

  constructor(private http: HttpClient) {}

  getAll(): Observable<TypeMateriel[]> {
    return this.http.get<TypeMateriel[]>(this.apiUrl);
  }

  getByCategorie(categorieId: number): Observable<TypeMateriel[]> {
    return this.http.get<TypeMateriel[]>(`${this.apiUrl}/categorie/${categorieId}`);
  }

  // ✅ التعديل هنا: نرسل categorieId مباشرة
  create(type: { libelle: string; categorieId: number }): Observable<TypeMateriel> {
    return this.http.post<TypeMateriel>(this.apiUrl, type);
  }

  update(id: number, type: TypeMateriel): Observable<TypeMateriel> {
    return this.http.put<TypeMateriel>(`${this.apiUrl}/${id}`, type);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}