import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Contrat {
  id: number;
  type: string;
  dateDebut: string;
  dateFin: string;
  salaire: number;
  dureeTravail: string;
  regimeTravail: string;
  employeNom: string;
  employePrenom: string;
  employeId: number;
}

export interface ContratRequest {
  type: string;
  dateDebut: string;
  dateFin?: string;
  salaire: number;
  dureeTravail: string;
  regimeTravail: string;
  employeId: number;
}

@Injectable({
  providedIn: 'root'
})
export class ContratService {
  private apiUrl = 'http://localhost:8080/api/contrats';

  constructor(private http: HttpClient) {}

  getAllContrats(): Observable<Contrat[]> {
    return this.http.get<Contrat[]>(this.apiUrl);
  }

  getContratById(id: number): Observable<Contrat> {
    return this.http.get<Contrat>(`${this.apiUrl}/${id}`);
  }

  createContrat(contrat: ContratRequest): Observable<Contrat> {
    return this.http.post<Contrat>(this.apiUrl, contrat);
  }

  updateContrat(id: number, contrat: ContratRequest): Observable<Contrat> {
    return this.http.put<Contrat>(`${this.apiUrl}/${id}`, contrat);
  }

  deleteContrat(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
  // ✅ جلب عقود موظف محدد
getContratsByEmploye(employeId: number): Observable<Contrat[]> {
  return this.http.get<Contrat[]>(`${this.apiUrl}/employe/${employeId}`);
}
}