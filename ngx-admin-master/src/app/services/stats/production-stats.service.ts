// services/stats/production-stats.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ProductionByTeamDTO {
  teamName: string;
  productionValue: number;
}

export interface ProductionByActivityDTO {
  activityCode: string;
  activityName: string;
  productionValue: number;
}

export interface ProductionTrendDTO {
  month: string;
  productionValue: number;
}

export interface ProductionByMissionDTO {
  missionCode: string;
  productionValue: number;
}

export interface TopTeamDTO {
  rank: number;
  teamName: string;
  productionValue: number;
}

export interface TopActivityDTO {
  rank: number;
  activityCode: string;
  activityName: string;
  productionValue: number;
}

export interface ProductivityByTeamDTO {
  teamName: string;
  productionValue: number;
  hoursWorked: number;
  productivity: number;
}

export interface ProductivityByActivityDTO {
  activityCode: string;
  activityName: string;
  productionValue: number;
  hoursWorked: number;
  productivity: number;
}

export interface ProductionStatsDTO {
  totalProductionRecords: number;
  averageProductivity: number;
  activeTeamsCount: number;
  averageActivityDuration: number;
  productionByTeam: ProductionByTeamDTO[];
  productionByActivity: ProductionByActivityDTO[];
  productionTrend: ProductionTrendDTO[];
  productionByMission: ProductionByMissionDTO[];
  top5Teams: TopTeamDTO[];
  top5Activities: TopActivityDTO[];
  productivityByTeam: ProductivityByTeamDTO[];
  productivityByActivity: ProductivityByActivityDTO[];
}

@Injectable({
  providedIn: 'root'
})
export class ProductionStatsService {
  private apiUrl = 'http://localhost:8080/api/stats/production';

  constructor(private http: HttpClient) {}

  getProductionStats(startDate: string, endDate: string): Observable<ProductionStatsDTO> {
    return this.http.get<ProductionStatsDTO>(
      `${this.apiUrl}/summary?startDate=${startDate}&endDate=${endDate}`
    );
  }

  getProductionByTeam(startDate: string, endDate: string): Observable<ProductionByTeamDTO[]> {
    return this.http.get<ProductionByTeamDTO[]>(
      `${this.apiUrl}/by-team?startDate=${startDate}&endDate=${endDate}`
    );
  }

  getProductionByActivity(startDate: string, endDate: string): Observable<ProductionByActivityDTO[]> {
    return this.http.get<ProductionByActivityDTO[]>(
      `${this.apiUrl}/by-activity?startDate=${startDate}&endDate=${endDate}`
    );
  }

  getProductionTrend(startDate: string, endDate: string): Observable<ProductionTrendDTO[]> {
    return this.http.get<ProductionTrendDTO[]>(
      `${this.apiUrl}/trend?startDate=${startDate}&endDate=${endDate}`
    );
  }

  getProductionByMission(startDate: string, endDate: string): Observable<ProductionByMissionDTO[]> {
    return this.http.get<ProductionByMissionDTO[]>(
      `${this.apiUrl}/by-mission?startDate=${startDate}&endDate=${endDate}`
    );
  }

  getTop5Teams(): Observable<TopTeamDTO[]> {
    return this.http.get<TopTeamDTO[]>(`${this.apiUrl}/top5/teams`);
  }

  getTop5Activities(): Observable<TopActivityDTO[]> {
    return this.http.get<TopActivityDTO[]>(`${this.apiUrl}/top5/activities`);
  }

  getProductivityByTeam(): Observable<ProductivityByTeamDTO[]> {
    return this.http.get<ProductivityByTeamDTO[]>(`${this.apiUrl}/productivity/teams`);
  }

  getProductivityByActivity(): Observable<ProductivityByActivityDTO[]> {
    return this.http.get<ProductivityByActivityDTO[]>(`${this.apiUrl}/productivity/activities`);
  }
}