// src/app/pages/safety/incidents/services/employe-api.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Employe {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  numTel: string;
  fullName: string;
}

@Injectable({
  providedIn: 'root'
})
export class EmployeApiService {
  private baseUrl = 'http://localhost:8080/api/employes';

  constructor(private http: HttpClient) {}

  getAllEmployes(): Observable<Employe[]> {
    return this.http.get<Employe[]>(this.baseUrl);
  }

  getEmployeById(id: number): Observable<Employe> {
    return this.http.get<Employe>(`${this.baseUrl}/${id}`);
  }
}