// src/app/pages/safety/incidents/services/site-api.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Site {
  id: number;
  surface: number;
  wilaya?: {
    numWilaya: number;
    nom: string;
  };
}

@Injectable({
  providedIn: 'root'
})
export class SiteApiService {
  private baseUrl = 'http://localhost:8080/api/sites';

  constructor(private http: HttpClient) {}

  getAllSites(): Observable<Site[]> {
    return this.http.get<Site[]>(this.baseUrl);
  }
}