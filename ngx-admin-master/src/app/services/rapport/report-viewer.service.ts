// src/app/pages/tables/reports/report-viewer.service.ts

import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Report {
  id: number;
  titre: string;
  date: string;
  resume: string;
  projectId: number;
  projectName: string;
  missionCode: string;
  rendements?: any[];
  formattedDate?: string;
}

export interface Project {
  id: number;
  nom: string;
  description: string;
  progression: number;
  objectifDebut: string;
  objectifFin: string;
  missionId: number;
  missionCode: string;
}

@Injectable({
  providedIn: 'root'
})
export class ReportViewerService {
  private baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  // ========== PROJECTS APIs ==========

  // Get all projects for current mission (regular users)
  getProjectsByCurrentMission(): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.baseUrl}/projects/current-mission`);
  }

  // ✅ NEW: Get all projects from all missions (for ADMIN/DIRECTEUR)
  getAllProjects(): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.baseUrl}/projects/all`);
  }

  // ✅ NEW: Get projects by specific mission ID (for ADMIN/DIRECTEUR)
  getProjectsByMissionId(missionId: number): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.baseUrl}/projects/mission/${missionId}/all`);
  }

  // ========== REPORTS APIs ==========

  // Get reports by project ID
  getReportsByProject(projectId: number): Observable<Report[]> {
    return this.http.get<Report[]>(`${this.baseUrl}/rapports/project/${projectId}`);
  }

  // Get report by ID
  getReportById(id: number): Observable<Report> {
    return this.http.get<Report>(`${this.baseUrl}/rapports/${id}`);
  }

  // Search reports
  searchReports(keyword: string, projectId?: number): Observable<Report[]> {
    let params = new HttpParams().set('keyword', keyword);
    if (projectId) {
      params = params.set('projectId', projectId.toString());
    }
    return this.http.get<Report[]>(`${this.baseUrl}/rapports/search`, { params });
  }
}