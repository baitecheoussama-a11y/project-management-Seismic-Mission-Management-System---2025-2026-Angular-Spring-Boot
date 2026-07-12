import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Compte {
  id: number;
  username: string;
  status: string;
  employeNom: string;
  employePrenom: string;
  employeId: number;
  employeEmail: string;
}

export interface CompteRequest {
  username: string;
  status: string;
  employeId: number;
}

@Injectable({
  providedIn: 'root'
})
export class CompteService {
  private apiUrl = 'http://localhost:8080/api/comptes';

  constructor(private http: HttpClient) {}

  getAllComptes(): Observable<Compte[]> {
    return this.http.get<Compte[]>(this.apiUrl);
  }

  createCompte(compte: CompteRequest): Observable<Compte> {
    return this.http.post<Compte>(this.apiUrl, compte);
  }

  updateStatus(id: number, status: string): Observable<Compte> {
    return this.http.put<Compte>(`${this.apiUrl}/${id}/status?status=${status}`, {});
  }

  updateUsername(id: number, username: string): Observable<Compte> {
    return this.http.put<Compte>(`${this.apiUrl}/${id}/username?username=${username}`, {});
  }

  resetPassword(id: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${id}/reset-password`, {});
  }

  deleteCompte(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
  getCompteByEmployeId(employeId: number): Observable<any> {
  return this.http.get<any>(`${this.apiUrl}/employe/${employeId}`);
}
}