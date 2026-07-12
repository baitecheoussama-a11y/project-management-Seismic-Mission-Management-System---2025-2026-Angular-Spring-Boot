import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DashboardStats {
  totalEmployees: number;
  totalAccounts: number;
  totalContracts: number;
  totalRoles: number;
  activeAccounts: number;
  suspendedAccounts: number;
  desactiveAccounts: number;
  activeContracts: number;
  expiringSoonContracts: number;
  contractsByType: any;
  employeesByGender: any;
  rolesDistribution: RoleDistribution[];  // ✅ Array
  employeesGrowth: EmployeeGrowth[];
  recentEmployees: RecentEmployee[];
  recentActivities: Activity[];
  alerts: Alert[];
}


export interface EmployeeGrowth {
  month: string;
  count: number;
}


export interface RoleDistribution {
  roleName: string;
  count: number;
  color?: string;
}

export interface Activity {
  id: number;
  type: 'CREATE_ACCOUNT' | 'ASSIGN_ROLE' | 'CREATE_EMPLOYEE' | 'CREATE_CONTRACT';
  message: string;
  timestamp: string;
  user: string;
  icon: string;
}

export interface Alert {
  type: 'warning' | 'danger' | 'info' | 'success';
  message: string;
  action?: string;
  actionLink?: string;
}

export interface RecentEmployee {
  id: number;
  name: string;
  nom?: string;
  prenom?: string;
  email: string;
  createdAt: string;
}

export interface RecentAccount {
  id: number;
  username: string;
  employeeName: string;
  status: string;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private apiUrl = 'http://localhost:8080/api/dashboard';

  constructor(private http: HttpClient) {}

  getStats(): Observable<DashboardStats> {
    return this.http.get<DashboardStats>(`${this.apiUrl}/stats`);
  }
}