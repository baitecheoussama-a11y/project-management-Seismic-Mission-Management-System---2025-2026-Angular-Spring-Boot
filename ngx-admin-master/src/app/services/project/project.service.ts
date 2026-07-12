// services/project/project.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { map } from 'rxjs/operators';

// Add missing ProjectResponseDTO interface
export interface ProjectResponseDTO {
  id: number;
  nom: string;
  description: string;
  budget: number;
  objectifVP: number;
  objectifDebut: string;
  objectifFin: string;
  progression: number;
  missionId: number;
  missionCode: string;
  status?: string;
  statusCode?: string;
  etatAvancements?: EtatAvancementDTO[];
  budgetDepense?: number;
  vpAtteint?: number;
  annule?: boolean;
    dateStartReelle?: string | null;
  dateFinReelle?: string;
   site?: SiteInfo; 
}


export interface SiteInfo {
  id: number;
  surface: number;
  wilaya?: {
    numWilaya: number;
    nom: string;
    centerLatitude: number;
    centerLongitude: number;
  };
  coordonnees?: Array<{
    id: number;
    latitude: number;
    longitude: number;
    ordre: number;
  }>;
}


// Add missing MyProjectResponseDTO interface
export interface MyProjectResponseDTO {
  hasProject: boolean;
  projectId: number;
  projectName: string;
}

// Rest of your existing interfaces remain the same...
export interface Project {
  id: number;
  nom: string;
  description: string;
  budget: number;
  budgetDepense?: number;
  objectifVP: number;
  vpAtteint?: number;
  objectifDebut: string;
  objectifFin: string;
  progression: number;
  annule?: boolean;
    dateStartReelle?: string | null;  // ✅ NEW
  dateFinReelle?: string | null;    // ✅ Already exists
  status?: string;     
  mission?: {
    id: number;
    code: string;
    nom?: string;
  };
  wilaya?: {
    numWilaya: number;
    nom: string;
    codePostal?: string;
    region?: string;
  };
  site?: {
    id: number;
    surface: number;
    coordonnees?: Coordonnee[];
  };
  equipes?: EquipeSummary[];
  rapports?: Rapport[];
  etatAvancements?: EtatAvancement[];
}

export interface Coordonnee {
  id: number;
  latitude: number;
  longitude: number;
  ordre: number;
}

export interface EquipeSummary {
  id: number;
  nom: string;
  type: string;
  nbMembres: number;
}

export interface ActivityProgressDTO {
  activeId: number;
  codeActive: string;
  objectif: string;
  status: string;
  productivityValue: number;
  totalWorkHours: number;
  rendementCount: number;
  completionPercentage: number;
}

export interface ProjectProgressStatsDTO {
  totalActivities: number;
  completedActivities: number;
  inProgressActivities: number;
  pendingActivities: number;
  delayedActivities: number;
  totalWorkHours: number;
  averageProductivity: number;
  totalProductivityValue: number;
  totalReports: number;
  totalRendements: number;
  activitiesProgress: ActivityProgressDTO[];
}

export interface Rapport {
  id: number;
  titre: string;
  date: string;
  resume: string;
  projectId?: number;
}

export interface EtatAvancement {
  id: number;
  dateLastAvancement: string;
  status: string;
  projectId?: number;
  activeId?: number;
  activeCode?: string;
  avancements: Avancement[];
}

export interface Avancement {
  id: number;
  titre: string;
  date: string;
  resume: string;
}

export interface ProjectRequest {
  nom: string;
  description: string;
  budget: number;
  objectifVP: number;
  objectifDebut: string;
  objectifFin: string;
  missionId: number;
}

export interface WilayaDTO {
  numWilaya: number;
  nom: string;
  centerLatitude: number;
  centerLongitude: number;
}

export interface CoordonneeDTO {
  latitude: number;
  longitude: number;
  ordre: number;
}

export interface SiteRequest {
  projectId: number;
  numWilaya: number;
  surface: number;
  coordonnees: CoordonneeDTO[];
}

export interface SiteResponse {
  id: number;
  surface: number;
  wilaya: WilayaDTO;
  coordonnees: CoordonneeDTO[];
}

export interface MyProjectResponse {
  hasProject: boolean;
  projectId: number;
  projectName: string;
}

export interface RapportRequest {
  titre: string;
  date: string;
  resume: string;
}

export interface RapportResponse {
  id: number;
  titre: string;
  date: string;
  resume: string;
  projectId: number;
}

export interface ProjectWithMissionDTO {
  id: number;
  nom: string;
  description: string;
  budget: number;
  objectifVP: number;
  objectifDebut: string;
  objectifFin: string;
  progression: number;
  annule?: boolean;
  missionId: number;
  missionCode: string;
  missionName: string;
}

export interface EtatAvancementDTO {
  id: number;
  dateLastAvancement: string;
  status: string;
  projectId: number;
  projectName: string;
  activeId?: number;
  activeCode?: string;
  avancements?: AvancementDTO[];
}

export interface AvancementDTO {
  id: number;
  titre: string;
  date: string;
  resume: string;
  etatAvancementId: number;
}

export interface AvancementRequestDTO {
  titre: string;
  date: string;
  resume: string;
}

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  private apiUrl = 'http://localhost:8080/api/projects';

  constructor(private http: HttpClient) {}

  // Get project by ID
  getProjectById(id: number): Observable<Project> {
    return this.http.get<Project>(`${this.apiUrl}/${id}`);
  }

  // In project.service.ts
getAllProjects(): Observable<ProjectResponseDTO[]> {
  return this.http.get<ProjectResponseDTO[]>(`${this.apiUrl}/all`);
}


// In project.service.ts
getProjectsByMissionId(missionId: number): Observable<ProjectResponseDTO[]> {
  console.log('Calling API for mission:', missionId);
  return this.http.get<ProjectResponseDTO[]>(`${this.apiUrl}/mission/${missionId}/all`);
}
  // Get current project by mission ID
  getCurrentProjectByMission(missionId: number): Observable<Project | null> {
    return this.http.get<Project | null>(`${this.apiUrl}/mission/${missionId}/current`);
  }

  // Create new project
  createProject(request: ProjectRequest): Observable<Project> {
    return this.http.post<Project>(`${this.apiUrl}`, request);
  }

  // Update project
  updateProject(id: number, data: any): Observable<Project> {
    return this.http.put<Project>(`${this.apiUrl}/${id}`, data);
  }

  // Get only project status (not activity statuses)
  getProjectStatusOnly(projectId: number): Observable<EtatAvancementDTO> {
    return this.getProjectById(projectId).pipe(
      map(project => {
        const projectStatus = project.etatAvancements?.find(
          etat => !etat.activeId || etat.activeId === null
        );
        if (!projectStatus) {
          throw new Error('No project status found');
        }
        return projectStatus as unknown as EtatAvancementDTO;
      })
    );
  }

  // Get all projects for current user's mission - FIXED return type
  getProjectsByCurrentMission(): Observable<ProjectResponseDTO[]> {
    return this.http.get<ProjectResponseDTO[]>(`${this.apiUrl}/current-mission`);
  }

  // Delete project
  deleteProject(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // project.service.ts

updateProjectRealDates(projectId: number, dateStartReelle: string | null, dateFinReelle: string | null): Observable<Project> {
  return this.http.put<Project>(`${this.apiUrl}/${projectId}/real-dates`, {
    dateStartReelle: dateStartReelle,
    dateFinReelle: dateFinReelle
  });
}

  getSiteByProjectId(projectId: number): Observable<SiteResponse | null> {
    return this.http.get<SiteResponse | null>(`${this.apiUrl.replace('/projects', '/sites')}/project/${projectId}`);
  }

  createOrUpdateSite(request: SiteRequest): Observable<SiteResponse> {
    return this.http.post<SiteResponse>(`${this.apiUrl.replace('/projects', '/sites')}`, request);
  }

  deleteSite(projectId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl.replace('/projects', '/sites')}/project/${projectId}`);
  }

  getAllWilayas(): Observable<WilayaDTO[]> {
    return this.http.get<WilayaDTO[]>(`${this.apiUrl.replace('/projects', '/sites')}/wilayas`);
  }

  // Get active project for current user's mission
  getMyActiveProject(): Observable<MyProjectResponse> {
    return this.http.get<MyProjectResponse>(`${this.apiUrl}/my-project`);
  }

  // Get active project by mission ID
  getActiveProjectByMission(missionId: number): Observable<Project | null> {
    return this.http.get<Project | null>(`${this.apiUrl}/mission/${missionId}/active`);
  }

  // Add rapport to project
  addRapport(projectId: number, request: RapportRequest): Observable<RapportResponse> {
    return this.http.post<RapportResponse>(`${this.apiUrl}/${projectId}/rapports`, request);
  }

  // Get all rapports by project
  getRapportsByProjectId(projectId: number): Observable<RapportResponse[]> {
    return this.http.get<RapportResponse[]>(`${this.apiUrl}/${projectId}/rapports`);
  }

  // Get rapport by ID
  getRapportById(id: number): Observable<RapportResponse> {
    return this.http.get<RapportResponse>(`${this.apiUrl}/rapports/${id}`);
  }

  // Update rapport
  updateRapport(id: number, request: RapportRequest): Observable<RapportResponse> {
    return this.http.put<RapportResponse>(`${this.apiUrl}/rapports/${id}`, request);
  }

  // Delete rapport
  deleteRapport(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/rapports/${id}`);
  }

  // Get project progress statistics
  getProjectProgressStats(projectId: number): Observable<ProjectProgressStatsDTO> {
    return this.http.get<ProjectProgressStatsDTO>(`${this.apiUrl}/${projectId}/progress-stats`);
  }

  // Get activities progress for project
  getProjectActivitiesProgress(projectId: number): Observable<ActivityProgressDTO[]> {
    return this.http.get<ActivityProgressDTO[]>(`${this.apiUrl}/${projectId}/activities-progress`);
  }

  // Get project with mission
  getProjectWithMission(id: number): Observable<ProjectWithMissionDTO> {
    return this.http.get<ProjectWithMissionDTO>(`${this.apiUrl}/${id}/with-mission`);
  }

  // ============ ETAT AVANCEMENT (Project Status) METHODS ============

  // Get all etat avancements for a project
  getEtatAvancementsByProject(projectId: number): Observable<EtatAvancementDTO[]> {
    return this.http.get<EtatAvancementDTO[]>(`http://localhost:8080/api/etat-avancement/project/${projectId}`);
  }

  // Get etat avancement by ID
  getEtatAvancementById(id: number): Observable<EtatAvancementDTO> {
    return this.http.get<EtatAvancementDTO>(`http://localhost:8080/api/etat-avancement/${id}`);
  }

  // Create etat avancement for project (PROJECT STATUS)
  createEtatAvancementForProject(projectId: number): Observable<EtatAvancementDTO> {
    return this.http.post<EtatAvancementDTO>(`http://localhost:8080/api/etat-avancement/project/${projectId}`, {});
  }

  // Update etat avancement status
  updateEtatAvancementStatus(id: number, status: string): Observable<EtatAvancementDTO> {
    return this.http.put<EtatAvancementDTO>(`http://localhost:8080/api/etat-avancement/${id}/status`, status);
  }

  // Delete etat avancement
  deleteEtatAvancement(id: number): Observable<void> {
    return this.http.delete<void>(`http://localhost:8080/api/etat-avancement/${id}`);
  }

  // ============ AVANCEMENT (Progress Updates) METHODS ============

  // Add avancement to etat avancement
  addAvancement(etatAvancementId: number, request: AvancementRequestDTO): Observable<AvancementDTO> {
    return this.http.post<AvancementDTO>(`http://localhost:8080/api/etat-avancement/${etatAvancementId}/avancements`, request);
  }

  // Get avancements by etat avancement
  getAvancementsByEtatAvancement(etatAvancementId: number): Observable<AvancementDTO[]> {
    return this.http.get<AvancementDTO[]>(`http://localhost:8080/api/etat-avancement/${etatAvancementId}/avancements`);
  }

  // Update avancement
  updateAvancement(id: number, request: AvancementRequestDTO): Observable<AvancementDTO> {
    return this.http.put<AvancementDTO>(`http://localhost:8080/api/etat-avancement/avancements/${id}`, request);
  }

  // Delete avancement
  deleteAvancement(id: number): Observable<void> {
    return this.http.delete<void>(`http://localhost:8080/api/etat-avancement/avancements/${id}`);
  }




  // In project.service.ts

cancelProject(projectId: number): Observable<Project> {
  return this.http.put<Project>(`${this.apiUrl}/${projectId}/cancel`, {});
}
}