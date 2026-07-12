// services/team/equipe-detail.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { EquipeDTO ,EmployeDTO,EquipeRequest} from '../mission/mission.service';
import { RapportResponse,RapportRequest } from '../rapport/rapport.service'; 

export interface EmployeSimple {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  numTel: string;
  poste: string;
}

export interface ActiveDTO {
  id: number;
  codeActive: string;
  objectif: string;
  description: string;
  dateDebut: string;
  dateFin: string;
  progression: number;
  dateStartReelle: string | null;
  dateFinReelle: string | null;
  projectId: number;
  projectNom: string;
  status: string; 
    ordre?: number;  // ✅ NEW: Status calculated from dates
}




export type ActiveDetail = ActiveDTO;

export interface RapportDetail {
  id: number;
  date: string;
  titre: string;
  resume: string;
}

export interface RendementDetail {
  id: number;
  heureDebut: string;
  heureFin: string;
  valeurRendement: number;
  uniteRendement: string;
  date: string;
  dureeHeures: number;
}

export interface StatActivites {
  totalActivites: number;
  totalRapports: number;
  totalRendements: number;
  moyenneRendement: number;
  totalHeuresTravaillees: number;
}


export interface RapportDetail {
  id: number;
  titre: string;
  date: string;
  resume: string;
  projectId: number;
  projectName?: string;
  rendements?: RendementDetail[];
}

export interface RendementDetail {
  id: number;
  heureDebut: string;
  heureFin: string;
  valeurRendement: number;
  uniteRendement: string;
  date: string;
  dureeHeures: number;
  rapportId?: number;
}
export interface EquipeReports {
  id: number;
  nom: string;
  type: string;
  memberCount: number;
  reportCount: number;
}

export interface EquipeDetail {
  id: number;
  nom: string;
  type: string;
  membres: EmployeSimple[];
  activites: ActiveDetail[];
  rapports: RapportDetail[];
  rendements: RendementDetail[];
  statistiques: StatActivites;
}

export interface EquipeDetail {
  id: number;
  nom: string;
  type: string;
  membres: EmployeSimple[];
  activites: ActiveDetail[];
  rapports: RapportDetail[];
  rendements: RendementDetail[];
  statistiques: StatActivites;
}

export interface AssignActivityRequest {
  equipeId: number;
  activeId: number;
  missionId: number;
    projectId?: number; 
  dateDebut: string;
  dateFin: string | null;
    dateStartReelle?: string | null;  // ✅ NEW
  dateFinReelle?: string | null; 
    ordre?: number;   // ✅ NEW
}

export interface AffectationEquipeDTO {
  id: number;
  dateDebut: string;
  dateFin: string;
  dateStartReelle: string | null;
  dateFinReelle: string | null;
  equipeId: number;
  equipeNom: string;
  activeId: number;
  activeCode: string;
  activeObjectif: string;
  projectId: number;
  projectNom: string;
  missionId: number;
}

export interface UpdateRealDatesRequest {
  dateStartReelle?: string | null;
  dateFinReelle?: string | null;
}


export interface EquipeActivities {
  id: number;
  nom: string;
  type: string;
  memberCount: number;
  activitiesCount: number;
}

@Injectable({
  providedIn: 'root'
})
export class EquipeDetailService {
  private apiUrl = "http://localhost:8080/api/equipe-detail";
  private apiUrl2 = "http://localhost:8080/api";

  constructor(private http: HttpClient) {}

  // Get equipe detail with members, activities, rapports, rendements
  getEquipeDetail(equipeId: number, missionId: number): Observable<EquipeDetail> {
    return this.http.get<EquipeDetail>(`${this.apiUrl}/${equipeId}?missionId=${missionId}`);
  }

  // ============ MEMBERS MANAGEMENT ============
  // Using existing API: POST /api/equipes/{equipeId}/assign
  addMembersToEquipe(equipeId: number, employeIds: number[], missionId: number): Observable<void> {
    // The existing API accepts just the employeIds array
    return this.http.post<void>(`${this.apiUrl2}/equipes/${equipeId}/assign`, employeIds);
  }

  // Using existing API: DELETE /api/equipes/{equipeId}/employees/{employeId}
  removeMemberFromEquipe(equipeId: number, employeId: number, missionId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl2}/equipes/${equipeId}/employees/${employeId}`);
  }

  // ============ ACTIVITIES MANAGEMENT ============
  // Get available activities for a mission (you'll need to create this backend endpoint)
  getAvailableActives(missionId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl2}/actives/available?missionId=${missionId}`);
  }

    assignActivityToEquipe(request: AssignActivityRequest): Observable<void> {
    return this.http.post<void>(`${this.apiUrl2}/equipes/assign-activity`, request);
  }


  // Remove activity from equipe (you'll need to create this backend endpoint)
  removeActivityFromEquipe(equipeId: number, activeId: number, missionId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl2}/equipes/${equipeId}/activities/${activeId}?missionId=${missionId}`);
  }

  // ============ EQUIPE CRUD ============
  // Using existing API: PUT /api/equipes/{id}
  updateEquipe(equipeId: number, equipeData: { nom: string; type: string }): Observable<EquipeDTO> {
    return this.http.put<EquipeDTO>(`${this.apiUrl2}/equipes/${equipeId}`, equipeData);
  }

  // Using existing API: DELETE /api/equipes/{id}
  deleteEquipe(equipeId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl2}/equipes/${equipeId}`);
  }

  // Get rapports for team's current project
getTeamRapports(equipeId: number, missionId: number): Observable<RapportDetail[]> {
  return this.http.get<RapportDetail[]>(`${this.apiUrl2}/rapports/mission/${missionId}/current-project`);
}

// Get rendements for team
getTeamRendements(equipeId: number, missionId: number): Observable<RendementDetail[]> {
  return this.http.get<RendementDetail[]>(`${this.apiUrl2}/rendements/equipe/${equipeId}?missionId=${missionId}`);
}



// services/team/equipe-detail.service.ts - أضف هذه الدوال

// Get all equipes with member count
getAllEquipesWithMemberCount(): Observable<EquipeDTO[]> {
  return this.http.get<EquipeDTO[]>(`${this.apiUrl2}/equipes/all-with-count`);
}

// Get equipe members by equipe ID
getEquipeMembersById(equipeId: number): Observable<EmployeDTO[]> {
  return this.http.get<EmployeDTO[]>(`${this.apiUrl2}/equipes/${equipeId}/members-list`);
}

// Create equipe (existing)
createEquipe(request: EquipeRequest): Observable<EquipeDTO> {
  return this.http.post<EquipeDTO>(`${this.apiUrl2}/equipes`, request);
}


// Get all equipes with activities
getAllEquipesWithActivities(): Observable<EquipeActivities[]> {
  return this.http.get<EquipeActivities[]>(`${this.apiUrl2}/equipes/all-with-activities`);
}

// Get activities by equipe ID
getActivitiesByEquipeId(equipeId: number, missionId: number): Observable<ActiveDetail[]> {
  return this.http.get<ActiveDetail[]>(`${this.apiUrl2}/equipes/${equipeId}/activities/mission/${missionId}`);
}






  // ✅ NEW: Get assignments for an equipe in a mission
  getAssignmentsByEquipeAndMission(equipeId: number, missionId: number): Observable<AffectationEquipeDTO[]> {
    return this.http.get<AffectationEquipeDTO[]>(`${this.apiUrl2}/equipes/${equipeId}/assignments/mission/${missionId}`);
  }

  // ✅ NEW: Get a specific assignment by ID
  getAssignmentById(id: number): Observable<AffectationEquipeDTO> {
    return this.http.get<AffectationEquipeDTO>(`${this.apiUrl2}/equipes/assignments/${id}`);
  }

  // ✅ NEW: Update real dates for an assignment
  updateRealDates(id: number, request: UpdateRealDatesRequest): Observable<AffectationEquipeDTO> {
    return this.http.put<AffectationEquipeDTO>(`${this.apiUrl2}/equipes/assignments/${id}/real-dates`, request);
  }

  // ✅ NEW: Update dates from status
  updateDatesFromStatus(id: number, status: string): Observable<AffectationEquipeDTO> {
    return this.http.put<AffectationEquipeDTO>(`${this.apiUrl2}/equipes/assignments/${id}/update-dates?status=${status}`, {});
  }

// equipe-detail.service.ts

// Add these new methods

// ✅ NEW: Update real dates and auto-determine status
updateRealDatesAndStatus(activeId: number, missionId: number, dateStartReelle: string | null, dateFinReelle: string | null): Observable<AffectationEquipeDTO> {
  const request = {
    dateStartReelle: dateStartReelle,
    dateFinReelle: dateFinReelle
  };
  return this.http.put<AffectationEquipeDTO>(`${this.apiUrl2}/equipes/assignments/update-real-dates`, {
    activeId,
    missionId,
    ...request
  });
}




// Get all equipes with report counts
getAllEquipesWithReportCounts(): Observable<EquipeReports[]> {
  return this.http.get<EquipeReports[]>(`${this.apiUrl2}/equipes/all-with-reports`);
}

// Get reports by equipe ID
getReportsByEquipeId(equipeId: number, missionId: number): Observable<RapportResponse[]> {
  return this.http.get<RapportResponse[]>(`${this.apiUrl2}/equipes/${equipeId}/reports/mission/${missionId}`);
}
addRapportToCurrentProject(missionId: number, equipeId: number, request: RapportRequest): Observable<RapportResponse> {
  return this.http.post<RapportResponse>(`${this.apiUrl}/mission/${missionId}/equipe/${equipeId}`, request);
}
}


