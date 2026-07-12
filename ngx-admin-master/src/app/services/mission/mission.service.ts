import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';



export interface Mission {
  id?: number;
  codeMission: string;
  methodologie: 'D2' | 'D3';
  description: string;
  affectations?: any[];
  projects?: any[];
  createdAt?: Date;
}


// mission.service.ts - Update the ConsumptionRequest interface

export interface ConsumptionRequest {
  resourceId: number;
  missionId: number;
  motifCode: string;
  motifDescription: string;
  contexteTitle: string;
  contexteDescription: string;
  quantity: number;
  date: string;
  description: string;
}``

export interface ConsumptionDetail {
  id: number;
  date: string;
  quantity: number;
  description: string;
  motifCode: string;
  motifDescription: string;
  contexteTitle: string;
  totalCost: number;
}



export interface MissionResource {
  resourceId: number;
  resourceName: string;
  totalAllocated: number;
  totalConsumed: number;
  remaining: number;
  unit: string;
  costPerUnit: number;
  consumptions: ConsumptionDetail[];
}

export interface MissionResourceSummary {
  totalAllocated: number;
  totalConsumed: number;
  totalRemaining: number;
  totalCost: number;
  resources: MissionResource[];
}

export interface Motif {
  idMotif: number;
  code: string;
  description: string;
}

export interface Contexte {
  idContexte: number;
  titre: string;
  description: string;
}


export interface MissionOverview {
  id: number;
  codeMission: string;
  description: string;
  currentProject: {
    id: number;
    name: string;
    description: string;
    startDate: string;
    targetEndDate: string;
    progress: number;
        statusCode?: string;
    status: 'on-track' | 'delayed' | 'at-risk' | 'pending' | 'completed';
  };
  aggregatedEquipment: {
    total: number;
    byType: { type: string; count: number; icon: string; color: string }[];
    byStatus: { good: number; broken: number; inRepair: number };
  };
  aggregatedResources: {
    total: number;
    consumed: number;
    remaining: number;
    byCategory: { name: string; allocated: number; consumed: number; unit: string }[];
    totalCost: number;
  };
    aggregatedEmployees?: {
    total: number;
    byRole: Array<{
      role: string;
      count: number;
      icon: string;
    }>;
  };
  financial: {
    budget: number;
    spent: number;
    remaining: number;
    breakdown: { category: string; amount: number; color: string }[];
  };
  recentActivities: { icon: string; text: string; time: string; color: string }[];
}




export interface EmployeDTO {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  numTel: string;
  poste: string;
  available: boolean;
  currentMissionId?: number;
  currentMissionName?: string;
  fullName: string;
    fonctionNom?: string;  // Add this
  fonctionId?: number;    
}

export interface EquipeDTO {
  id: number;
  nom: string;
  type: string;
  memberCount: number;
  members?: EmployeDTO[];
}

export interface MissionTeamDTO {
  missionId: number;
  missionName: string;
  totalMembers: number;
  members: EmployeDTO[];
  membersByEquipe: { [key: string]: EmployeDTO[] };
  equipes: EquipeDTO[];
}

export interface AffectationRequest {
  missionId: number;
  employeIds: number[];
  equipeId?: number;
  dateDebut: string;
  dateFin?: string;
}

export interface EquipeRequest {
  nom: string;
  type: string;
}

export interface ActiveDTO {
  id: number;
  codeActive: string;
  objectif: string;
  description: string;
  equipeCount: number;
}

export interface ActiveRequest {
  codeActive: string;
  objectif: string;
  description: string;
   missionId: number;        // Required for creation
  dateDebut?: string;       // Optional start date
  dateFin?: string;   
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

export interface ProjectResponse {
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
   statusCode?: string;  // ✅ Add this
  status?: string;  
    etatAvancements?: EtatAvancementDTO[]; 
    
}

export interface EtatAvancementDTO {
  id: number;
  status: string;
  // ... other fields
}
@Injectable({
  providedIn: 'root'
})
export class MissionService {


  private apiUrl = 'http://localhost:8080/api/missions';

  constructor(private http: HttpClient) { }

  getAllMissions(): Observable<Mission[]> {
    return this.http.get<Mission[]>(this.apiUrl);
  }

  getMissionById(id: number): Observable<Mission> {
    return this.http.get<Mission>(`${this.apiUrl}/${id}`);
  }

  createMission(mission: Mission): Observable<Mission> {
    return this.http.post<Mission>(this.apiUrl, mission);
  }
  getMissionOverview(id: number): Observable<MissionOverview> {
    return this.http.get<MissionOverview>(`${this.apiUrl}/${id}/overview`);
  }
  updateMission(id: number, mission: Mission): Observable<Mission> {
    return this.http.put<Mission>(`${this.apiUrl}/${id}`, mission);
  }

  deleteMission(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  private apiUrl2 = 'http://localhost:8080/api';


  getMissionResourceSummary(missionId: number): Observable<MissionResourceSummary> {
    return this.http.get<MissionResourceSummary>(`${this.apiUrl2}/consommations/mission/${missionId}/summary`);
  }

  createConsumption(request: ConsumptionRequest): Observable<any> {
    return this.http.post(`${this.apiUrl2}/consommations`, request);
  }

  getAllMotifs(): Observable<Motif[]> {
    return this.http.get<Motif[]>(`${this.apiUrl2}/motifs`);
  }

  getAllContextes(): Observable<Contexte[]> {
    return this.http.get<Contexte[]>(`${this.apiUrl2}/contextes`);
  }

  deleteConsumption(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl2}/consommations/${id}`);
  }


  // Add to MissionService class
getAvailableEmployees(): Observable<EmployeDTO[]> {
  return this.http.get<EmployeDTO[]>(`${this.apiUrl2}/mission-team/available-employees`);
}

getAllEmployeesWithStatus(): Observable<EmployeDTO[]> {
  return this.http.get<EmployeDTO[]>(`${this.apiUrl2}/mission-team/all-employees`);
}

getMissionTeam(missionId: number): Observable<MissionTeamDTO> {
  return this.http.get<MissionTeamDTO>(`${this.apiUrl2}/mission-team/mission/${missionId}`);
}



addEmployeesToMission(request: AffectationRequest): Observable<void> {
  return this.http.post<void>(`${this.apiUrl2}/mission-team/add-members`, request);
}

updateEmployeeTeam(missionId: number, employeId: number, equipeId: number): Observable<void> {
  return this.http.put<void>(`${this.apiUrl2}/mission-team/${missionId}/employee/${employeId}/equipe/${equipeId}`, {});
}

removeEmployeeFromMission(missionId: number, employeId: number): Observable<void> {
  return this.http.delete<void>(`${this.apiUrl2}/mission-team/${missionId}/employee/${employeId}`);
}
  


// Equipe Management
getAllEquipes(): Observable<EquipeDTO[]> {
  return this.http.get<EquipeDTO[]>(`${this.apiUrl2}/equipes`);
}

getEquipeById(id: number): Observable<EquipeDTO> {
  return this.http.get<EquipeDTO>(`${this.apiUrl2}/equipes/${id}`);
}

getEquipeMembers(id: number): Observable<EmployeDTO[]> {
  return this.http.get<EmployeDTO[]>(`${this.apiUrl2}/equipes/${id}/members`);
}

createEquipe(request: EquipeRequest): Observable<EquipeDTO> {
  return this.http.post<EquipeDTO>(`${this.apiUrl2}/equipes`, request);
}

updateEquipe(id: number, request: EquipeRequest): Observable<EquipeDTO> {
  return this.http.put<EquipeDTO>(`${this.apiUrl2}/equipes/${id}`, request);
}

deleteEquipe(id: number): Observable<void> {
  return this.http.delete<void>(`${this.apiUrl2}/equipes/${id}`);
}

assignEmployeesToEquipe(equipeId: number, employeIds: number[]): Observable<void> {
  return this.http.post<void>(`${this.apiUrl2}/equipes/${equipeId}/assign`, employeIds);
}

removeEmployeeFromEquipe(equipeId: number, employeId: number): Observable<void> {
  return this.http.delete<void>(`${this.apiUrl2}/equipes/${equipeId}/employees/${employeId}`);
}



// Active Management
getAllActives(): Observable<ActiveDTO[]> {
  return this.http.get<ActiveDTO[]>(`${this.apiUrl2}/actives`);
}

getActivesByMission(missionId: number): Observable<ActiveDTO[]> {
  return this.http.get<ActiveDTO[]>(`${this.apiUrl2}/actives/mission/${missionId}`);
}

getAvailableActives(missionId: number): Observable<ActiveDTO[]> {
  return this.http.get<ActiveDTO[]>(`${this.apiUrl2}/actives/available?missionId=${missionId}`);
}

createActive(request: ActiveRequest): Observable<ActiveDTO> {
  return this.http.post<ActiveDTO>(`${this.apiUrl2}/actives`, request);
}

updateActive(id: number, request: ActiveRequest): Observable<ActiveDTO> {
  return this.http.put<ActiveDTO>(`${this.apiUrl2}/actives/${id}`, request);
}

deleteActive(id: number): Observable<void> {
  return this.http.delete<void>(`${this.apiUrl2}/actives/${id}`);
}





getCurrentProjectByMission(missionId: number): Observable<ProjectResponse | null> {
  return this.http.get<ProjectResponse | null>(`${this.apiUrl2}/projects/mission/${missionId}/current`);
}

createProject(request: ProjectRequest): Observable<ProjectResponse> {
  return this.http.post<ProjectResponse>(`${this.apiUrl2}/projects`, request);
}

updateProject(id: number, request: ProjectRequest): Observable<ProjectResponse> {
  return this.http.put<ProjectResponse>(`${this.apiUrl2}/projects/${id}`, request);
}

getProjectById(id: number): Observable<ProjectResponse> {
  return this.http.get<ProjectResponse>(`${this.apiUrl2}/projects/${id}`);
}

// Get current mission for logged-in user
getMyCurrentMission(): Observable<{ missionId: number; hasAccess: boolean; missionCode: string; dateDebut: string; dateFin: string }> {
  return this.http.get<{ missionId: number; hasAccess: boolean; missionCode: string; dateDebut: string; dateFin: string }>(`${this.apiUrl2}/missions/my-mission`);
}

// Check if user has access to a specific mission
checkMissionAccess(missionId: number): Observable<{ hasAccess: boolean }> {
  return this.http.get<{ hasAccess: boolean }>(`${this.apiUrl2}/missions/${missionId}/check-access`);
}

// services/mission/mission.service.ts - أضف هذه الدوال

// Get my team (the team of the logged-in employee)
getMyTeam(): Observable<EquipeDTO> {
  return this.http.get<EquipeDTO>(`${this.apiUrl2}/equipes/my-team`);
}

// Get available members for my mission (to add to team)
getAvailableMembersForMyTeam(): Observable<EmployeDTO[]> {
  return this.http.get<EmployeDTO[]>(`${this.apiUrl2}/equipes/my-team/available-members`);
}


// services/mission/mission.service.ts - أضف هذه الدوال

// Get all equipes with member count (for sidebar)
getAllEquipesWithMemberCount(): Observable<EquipeDTO[]> {
  return this.http.get<EquipeDTO[]>(`${this.apiUrl2}/equipes/all-with-count`);
}

// Get equipe members by equipe ID
getEquipeMembersById(equipeId: number): Observable<EmployeDTO[]> {
  return this.http.get<EmployeDTO[]>(`${this.apiUrl2}/equipes/${equipeId}/members-list`);
}

// Get current user's equipe
getCurrentUserEquipe(): Observable<{ hasEquipe: boolean; equipe?: EquipeDTO }> {
  return this.http.get<{ hasEquipe: boolean; equipe?: EquipeDTO }>(`${this.apiUrl2}/equipes/my-equipe`);
}
}