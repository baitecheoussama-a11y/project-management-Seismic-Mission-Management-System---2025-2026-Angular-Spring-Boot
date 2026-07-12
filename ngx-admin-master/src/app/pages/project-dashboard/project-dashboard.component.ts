// project-dashboard.component.ts
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { ProjectService } from '../../services/project/project.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-project-dashboard',
  template: `
   <div *ngIf="isLoading" class="dashboard-loading">
    <div class="loading-container">
      <div class="spinner"></div>
      <p>Loading your project...</p>
    </div>
  </div>
    
     <div *ngIf="!isLoading && hasProject && projectId" class="dashboard-container">
    <!-- ✅ أضف fromDashboard="true" -->
    <app-project-overview [projectIdInput]="projectId" [fromDashboard]="true"></app-project-overview>
  </div>
    
    <div *ngIf="!isLoading && !hasProject" class="no-project-container">
      <div class="no-project-card">
        <div class="no-project-icon">
          <i class="fas fa-project-diagram"></i>
        </div>
        <div class="no-project-content">
          <h2>No Active Project</h2>
          <p>{{ errorMessage || 'You are not currently assigned to any active project.' }}</p>
          <p class="info-text">Only one active project is allowed per mission.</p>
          <button class="btn-home" (click)="goToMissionDashboard()">
            <i class="fas fa-chalkboard-user"></i>
            Go to Mission Dashboard
          </button>
          <button class="btn-home secondary" (click)="goToHome()">
            <i class="fas fa-home"></i>
            Go to Home
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .dashboard-loading, .no-project-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 100vh;
      background: linear-gradient(135deg, #f5f7fa 0%, #eef2f6 100%);
    }
    
    .loading-container {
      text-align: center;
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
    
    .loading-container p {
      margin-top: 16px;
      color: #64748b;
    }
    
    .no-project-card {
      background: white;
      border-radius: 28px;
      padding: 48px;
      text-align: center;
      max-width: 500px;
      margin: 20px;
      box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
      animation: fadeInUp 0.5s ease-out;
    }
    
    @keyframes fadeInUp {
      from {
        opacity: 0;
        transform: translateY(30px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }
    
    .no-project-icon {
      width: 100px;
      height: 100px;
      background: linear-gradient(135deg, #f59e0b20, #ef444420);
      border-radius: 50px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 24px;
    }
    
    .no-project-icon i {
      font-size: 48px;
      color: #f59e0b;
    }
    
    .no-project-content h2 {
      margin: 0 0 12px 0;
      font-size: 28px;
      font-weight: 700;
      color: #1e293b;
    }
    
    .no-project-content p {
      color: #64748b;
      margin-bottom: 16px;
      font-size: 16px;
      line-height: 1.5;
    }
    
    .no-project-content .info-text {
      font-size: 13px;
      color: #94a3b8;
      margin-bottom: 32px;
    }
    
    .btn-home {
      display: inline-flex;
      align-items: center;
      gap: 10px;
      padding: 12px 28px;
      background: linear-gradient(135deg, #3b82f6, #8b5cf6);
      border: none;
      border-radius: 50px;
      color: white;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;
      margin: 0 8px;
    }
    
    .btn-home.secondary {
      background: #f1f5f9;
      color: #475569;
    }
    
    .btn-home:hover {
      transform: translateY(-2px);
      box-shadow: 0 10px 25px -5px rgba(59, 130, 246, 0.4);
    }
    
    .btn-home.secondary:hover {
      background: #e2e8f0;
      box-shadow: none;
    }
    
    .dashboard-container {
      min-height: 100vh;
    }
  `]
})
export class ProjectDashboardComponent implements OnInit, OnDestroy {
  
  projectId: number | null = null;
  hasProject: boolean = false;
  isLoading: boolean = true;
  errorMessage: string = '';
  private destroy$ = new Subject<void>();
  
  constructor(
    private projectService: ProjectService,
    private router: Router
  ) {}
  
  ngOnInit(): void {
    this.loadMyProject();
  }
  
  loadMyProject(): void {
    this.isLoading = true;
    this.projectService.getMyActiveProject().subscribe({
      next: (response: any) => {
        if (response && response.hasProject && response.projectId) {
          this.projectId = response.projectId;
          this.hasProject = true;
        } else {
          this.hasProject = false;
          this.errorMessage = 'No active project found for your current mission.';
        }
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading project:', err);
        this.hasProject = false;
        this.errorMessage = err.error?.error || 'No active project found for your current mission.';
        this.isLoading = false;
      }
    });
  }
  
  goToMissionDashboard(): void {
    this.router.navigate(['/pages/mission-dashboard']);
  }
  
  goToHome(): void {
    this.router.navigate(['/pages/dashboard']);
  }
  
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}

