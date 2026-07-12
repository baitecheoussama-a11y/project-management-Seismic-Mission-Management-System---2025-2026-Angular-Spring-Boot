// mission-dashboard.component.ts
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { MissionService } from '../../services/mission/mission.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-mission-dashboard',
  template: `
  <div *ngIf="isLoading" class="dashboard-loading">
    <div class="loading-container">
      <div class="spinner"></div>
      <p>Loading your mission...</p>
    </div>
  </div>
    
     <div *ngIf="!isLoading && hasAccess && missionId" class="dashboard-container">
    <!-- ✅ أضف fromDashboard="true" -->
    <app-mission-overview [missionIdInput]="missionId" [fromDashboard]="true"></app-mission-overview>
  </div>
    
    <div *ngIf="!isLoading && !hasAccess && !missionId" class="no-access-container">
      <div class="no-access-card">
        <i class="fas fa-ban"></i>
        <h2>Access Denied</h2>
        <p>{{ errorMessage || 'You are not currently assigned to any active mission.' }}</p>
        <button class="btn-home" (click)="goToHome()">
          <i class="fas fa-home"></i>
          Go to Home
        </button>
      </div>
    </div>
    
    <div *ngIf="!isLoading && hasAccess && !missionId" class="no-mission-container">
      <div class="no-mission-card">
        <i class="fas fa-tasks"></i>
        <h2>No Active Mission</h2>
        <p>{{ errorMessage || 'You are not currently assigned to any active mission.' }}</p>
        <button class="btn-home" (click)="goToHome()">
          <i class="fas fa-home"></i>
          Go to Home
        </button>
      </div>
    </div>
  `,
  styles: [`
    .loading-container, .no-access-container, .no-mission-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 100vh;
      background: linear-gradient(135deg, #f5f7fa 0%, #eef2f6 100%);
    }
    
    .spinner {
      width: 50px;
      height: 50px;
      border: 4px solid #e2e8f0;
      border-top-color: #3b82f6;
      border-radius: 50%;
      animation: spin 1s linear infinite;
      margin: 0 auto;
    }
    
    @keyframes spin {
      to { transform: rotate(360deg); }
    }
    
    .no-access-card, .no-mission-card {
      background: white;
      border-radius: 24px;
      padding: 48px;
      text-align: center;
      max-width: 500px;
      margin: 20px;
      box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
    }
    
    .no-access-card i, .no-mission-card i {
      font-size: 64px;
      color: #ef4444;
      margin-bottom: 24px;
    }
    
    .no-mission-card i {
      color: #f59e0b;
    }
    
    .no-access-card h2, .no-mission-card h2 {
      margin: 0 0 16px 0;
      font-size: 28px;
      font-weight: 700;
      color: #1e293b;
    }
    
    .no-access-card p, .no-mission-card p {
      color: #64748b;
      margin-bottom: 32px;
      font-size: 16px;
    }
    
    .btn-home {
      display: inline-flex;
      align-items: center;
      gap: 10px;
      padding: 12px 32px;
      background: linear-gradient(135deg, #3b82f6, #8b5cf6);
      border: none;
      border-radius: 50px;
      color: white;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;
    }
    
    .btn-home:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 20px rgba(59, 130, 246, 0.4);
    }
    
    .dashboard-container {
      min-height: 100vh;
    }
  `]
})
export class MissionDashboardComponent implements OnInit, OnDestroy {
  
  missionId: number | null = null;
  hasAccess: boolean = false;
  isLoading: boolean = true;
  errorMessage: string = '';
  private destroy$ = new Subject<void>();
  
  constructor(
    private missionService: MissionService,
    private router: Router
  ) {}
  
  ngOnInit(): void {
    this.loadMyMission();
  }
  
  loadMyMission(): void {
    this.isLoading = true;
    this.missionService.getMyCurrentMission().subscribe({
      next: (response) => {
        if (response && response.hasAccess && response.missionId) {
          this.missionId = response.missionId;
          this.hasAccess = true;
        } else {
          this.hasAccess = false;
          this.errorMessage = 'You are not currently assigned to any active mission.';
        }
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading mission:', err);
        this.hasAccess = false;
        this.errorMessage = err.error?.error || 'You are not currently assigned to any active mission. Please contact your administrator.';
        this.isLoading = false;
      }
    });
  }
  
  goToHome(): void {
    this.router.navigate(['/pages/dashboard']);
  }
  
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}