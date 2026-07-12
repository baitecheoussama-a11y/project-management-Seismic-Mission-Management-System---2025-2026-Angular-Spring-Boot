// src/app/services/analytics/dashboard.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface KPIDashboardDTO {
  totalCost: number;
  totalProjects: number;
  avgProgression: number;
  avgBudgetUsage: number;
  activeProjects: number;
  completedProjects: number;
  delayedProjects: number;
  cancelledProjects: number;
  costChangePercent: number;
  progressionChangePercent: number;
  avgCostPerProject: number;
}

export interface CostByMissionDTO {
  missionCode: string;
  methodologie: string;
  projectCount: number;
  totalCost: number;
  totalBudget: number;
  budgetUsagePercent: number;
  avgProgression: number;
}

export interface TrendDataDTO {
  date: string;  // ISO date string from backend
  avgProgression: number;
  totalCost: number;
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private apiUrl = 'http://localhost:8080/api/analytics';


  constructor(private http: HttpClient) {}

  getKPIs(): Observable<KPIDashboardDTO> {
    return this.http.get<KPIDashboardDTO>(`${this.apiUrl}/kpis`);
  }

  getCostByMission(): Observable<CostByMissionDTO[]> {
    return this.http.get<CostByMissionDTO[]>(`${this.apiUrl}/cost-by-mission`);
  }

  getTrendData(missionId: number = 1, days: number = 30): Observable<TrendDataDTO[]> {
    return this.http.get<TrendDataDTO[]>(`${this.apiUrl}/trends`, {
      params: {
        missionId: missionId.toString(),
        days: days.toString()
      }
    });
  }
}