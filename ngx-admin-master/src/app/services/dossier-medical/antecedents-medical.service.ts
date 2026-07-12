import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AntecedentsMedical } from './etat-medical.service';

@Injectable({
  providedIn: 'root'
})
export class AntecedentsMedicalService {
  private apiUrl = 'http://localhost:8080/api/antecedents-medical';

  constructor(private http: HttpClient) {}

  getAntecedentsByEtatMedicalId(etatMedicalId: number): Observable<AntecedentsMedical[]> {
    return this.http.get<AntecedentsMedical[]>(`${this.apiUrl}/etat-medical/${etatMedicalId}`);
  }

  getAntecedentById(id: number): Observable<AntecedentsMedical> {
    return this.http.get<AntecedentsMedical>(`${this.apiUrl}/${id}`);
  }

  createOrUpdateAntecedent(antecedent: Partial<AntecedentsMedical>): Observable<AntecedentsMedical> {
    return this.http.post<AntecedentsMedical>(this.apiUrl, antecedent);
  }

  deleteAntecedent(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}