import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Ressource {
  idRessource: number;
  titre: string;
  description: string;
  quantite: number;
  unite: string;
  cout: number;
  dateAchat: string;
  typeRessourceId?: number;
  typeRessourceNom?: string;
  categorieId?: number;
  categorieNom?: string;
}

@Injectable({ providedIn: 'root' })
export class RessourceService {
  private apiUrl = 'http://localhost:8080/api/ressources';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Ressource[]> {
    return this.http.get<Ressource[]>(this.apiUrl);
  }

  getByType(typeId: number): Observable<Ressource[]> {
    return this.http.get<Ressource[]>(`${this.apiUrl}/type/${typeId}`);
  }

  getByCategorie(categorieId: number): Observable<Ressource[]> {
    return this.http.get<Ressource[]>(`${this.apiUrl}/categorie/${categorieId}`);
  }

  getById(id: number): Observable<Ressource> {
    return this.http.get<Ressource>(`${this.apiUrl}/${id}`);
  }

  create(ressource: any): Observable<Ressource> {
    return this.http.post<Ressource>(this.apiUrl, ressource);
  }

  update(id: number, ressource: any): Observable<Ressource> {
    return this.http.put<Ressource>(`${this.apiUrl}/${id}`, ressource);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}