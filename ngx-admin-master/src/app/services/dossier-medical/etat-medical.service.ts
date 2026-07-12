import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface EtatMedical {
  id: number;
  groupeSanguin: string;
  allergies: string;
  vaccinations: string;
  medicationsActuelles: string;
  medecinTraitant: string;
  derniereVisiteMedicale: string;
  employeId: number;
  employeNom: string;
  employePrenom: string;
  antecedentsMedicaux?: AntecedentsMedical[];
  incidentCount?: number;
}

export interface AntecedentsMedical {
  id: number;
  nom: string;
  description: string;
  dateDiagnostic: string;
  etatMedicalId: number;
}

@Injectable({
  providedIn: 'root'
})
export class EtatMedicalService {

  private apiUrl = 'http://localhost:8080/api/etat-medical';

  constructor(private http: HttpClient) {}

  getAllEtatMedicals(): Observable<EtatMedical[]> {
    return this.http.get<EtatMedical[]>(this.apiUrl);
  }

  getEtatMedicalById(id: number): Observable<EtatMedical> {
    return this.http.get<EtatMedical>(`${this.apiUrl}/${id}`);
  }

  getEtatMedicalByEmployeId(employeId: number): Observable<EtatMedical> {
    return this.http.get<EtatMedical>(`${this.apiUrl}/employe/${employeId}`);
  }

  createOrUpdateEtatMedical(etatMedical: Partial<EtatMedical>): Observable<EtatMedical> {
    return this.http.post<EtatMedical>(this.apiUrl, etatMedical);
  }

  deleteEtatMedical(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  existsByEmployeId(employeId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/employe/${employeId}/exists`);
  }
}