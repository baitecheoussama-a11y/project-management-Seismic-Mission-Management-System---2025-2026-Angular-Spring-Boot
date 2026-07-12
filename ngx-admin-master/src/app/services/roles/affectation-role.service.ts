import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AffectationRole {
  id: number;
  compteId: number;
  roleId: number;
  roleName: string;
  roleType: string;
  dateDebut: string;
  dateFin: string;
  active: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class AffectationRoleService {
  private apiUrl = 'http://localhost:8080/api/affectations-roles';

  constructor(private http: HttpClient) {}

  getRolesByCompteId(compteId: number): Observable<AffectationRole[]> {
    return this.http.get<AffectationRole[]>(`${this.apiUrl}/compte/${compteId}`);
  }

  create(data: any): Observable<AffectationRole> {
    return this.http.post<AffectationRole>(this.apiUrl, data);
  }

  update(id: number, data: any): Observable<AffectationRole> {
    return this.http.put<AffectationRole>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  toggleActive(id: number, active: boolean): Observable<AffectationRole> {
    return this.http.patch<AffectationRole>(`${this.apiUrl}/${id}/toggle?active=${active}`, {});
  }
}