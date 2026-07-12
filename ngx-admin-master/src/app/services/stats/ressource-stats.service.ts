// services/stats/ressource-stats.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ConsommationStatsDTO {
  label: string;
  value: number;
}

export interface RessourceCostStatsDTO {
  label: string;
  value: number;
  cost: number;
}

export interface MonthlyConsommationDTO {
  month: string;
  value: number;
}

export interface RessourceStatsSummaryDTO {
  totalCost: number;
  consommationByRessource: ConsommationStatsDTO[];
  consommationByMission: ConsommationStatsDTO[];
  costByRessource: RessourceCostStatsDTO[];
  consommationByMonth: MonthlyConsommationDTO[];
  consommationByType: ConsommationStatsDTO[];
  costByType: RessourceCostStatsDTO[];
  top5Ressources: RessourceCostStatsDTO[];
  criticalStock: RessourceCostStatsDTO[];
}

@Injectable({
  providedIn: 'root'
})
export class RessourceStatsService {
  private apiUrl = 'http://localhost:8080/api/stats/ressources';

  constructor(private http: HttpClient) {}

  getStatsSummary(startDate: string, endDate: string): Observable<RessourceStatsSummaryDTO> {
    return this.http.get<RessourceStatsSummaryDTO>(
      `${this.apiUrl}/summary?startDate=${startDate}&endDate=${endDate}`
    );
  }

  getConsommationByRessource(startDate: string, endDate: string): Observable<ConsommationStatsDTO[]> {
    return this.http.get<ConsommationStatsDTO[]>(
      `${this.apiUrl}/by-ressource?startDate=${startDate}&endDate=${endDate}`
    );
  }

  getConsommationByMission(startDate: string, endDate: string): Observable<ConsommationStatsDTO[]> {
    return this.http.get<ConsommationStatsDTO[]>(
      `${this.apiUrl}/by-mission?startDate=${startDate}&endDate=${endDate}`
    );
  }

  getCostByRessource(startDate: string, endDate: string): Observable<RessourceCostStatsDTO[]> {
    return this.http.get<RessourceCostStatsDTO[]>(
      `${this.apiUrl}/cost-by-ressource?startDate=${startDate}&endDate=${endDate}`
    );
  }

  getConsommationByMonth(startDate: string, endDate: string): Observable<MonthlyConsommationDTO[]> {
    return this.http.get<MonthlyConsommationDTO[]>(
      `${this.apiUrl}/by-month?startDate=${startDate}&endDate=${endDate}`
    );
  }

  getTotalCost(startDate: string, endDate: string): Observable<number> {
    return this.http.get<number>(
      `${this.apiUrl}/total-cost?startDate=${startDate}&endDate=${endDate}`
    );
  }

  getConsommationByType(startDate: string, endDate: string): Observable<ConsommationStatsDTO[]> {
    return this.http.get<ConsommationStatsDTO[]>(
      `${this.apiUrl}/by-type?startDate=${startDate}&endDate=${endDate}`
    );
  }

  getCostByType(startDate: string, endDate: string): Observable<RessourceCostStatsDTO[]> {
    return this.http.get<RessourceCostStatsDTO[]>(
      `${this.apiUrl}/cost-by-type?startDate=${startDate}&endDate=${endDate}`
    );
  }

  getTop5Ressources(startDate: string, endDate: string): Observable<RessourceCostStatsDTO[]> {
    return this.http.get<RessourceCostStatsDTO[]>(
      `${this.apiUrl}/top5?startDate=${startDate}&endDate=${endDate}`
    );
  }

  getCriticalStock(): Observable<RessourceCostStatsDTO[]> {
    return this.http.get<RessourceCostStatsDTO[]>(`${this.apiUrl}/critical-stock`);
  }
}