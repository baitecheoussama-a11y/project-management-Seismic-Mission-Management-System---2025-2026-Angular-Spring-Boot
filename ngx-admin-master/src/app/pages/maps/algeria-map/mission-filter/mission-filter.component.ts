// mission-filter.component.ts
import { Component, Output, EventEmitter, OnInit, OnDestroy, ChangeDetectorRef, Input } from '@angular/core';
import { MissionService, Mission } from '../../../../services/mission/mission.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NbToastrService } from '@nebular/theme';

@Component({
  selector: 'app-mission-filter',
  template: `
    <div class="mission-filter-wrapper">
      <!-- Mission Filter Section - Show only for ADMIN/DIRECTEUR -->
      <div *ngIf="canViewAllProjects">
        <div class="filter-header-info">
          <i class="fas fa-rocket"></i>
          <span class="filter-title">Mission Filter</span>
        </div>
        
        <div class="filter-controls">
          <label>
            <i class="fas fa-bullseye"></i>
            <span>Select Mission:</span>
          </label>
          <select 
            [(ngModel)]="selectedMissionId" 
            (change)="onMissionChange()" 
            class="mission-select"
            [disabled]="isLoading">
            <option value="all">📊 All Missions</option>
            <option *ngFor="let mission of missions" [value]="mission.id">
              🚀 {{ mission.codeMission }} - {{ mission.description | slice:0:60 }}
            </option>
          </select>
          
          <div class="loading-indicator" *ngIf="isLoading">
            <i class="fas fa-spinner fa-spin"></i>
            Loading missions...
          </div>
          
          <!-- Advanced Filters Toggle Button -->
          <button class="filter-toggle-btn" (click)="toggleAdvancedFilters()" type="button">
            <i class="fas fa-filter"></i>
            <span>Advanced Filters</span>
            <span class="filter-count" *ngIf="getActiveFiltersCount() > 0">{{ getActiveFiltersCount() }}</span>
            <i class="fas" [class.fa-chevron-down]="!showAdvancedFilters" [class.fa-chevron-up]="showAdvancedFilters"></i>
          </button>
        </div>
        
        <div class="selected-info" *ngIf="selectedMissionId !== 'all' && getSelectedMission()">
          <i class="fas fa-info-circle"></i>
          <span>Showing projects for: <strong>{{ getSelectedMission()?.codeMission }}</strong></span>
        </div>
        
        <div class="selected-info" *ngIf="selectedMissionId === 'all'">
          <i class="fas fa-globe"></i>
          <span>Showing all projects from all missions</span>
        </div>
      </div>

      <!-- For Regular Users - Simple message only -->
      <div *ngIf="!canViewAllProjects" class="regular-user-message">
        <i class="fas fa-info-circle"></i>
        <span>Showing projects from your current mission only</span>
      </div>

      <!-- Advanced Filters Panel -->
      <div class="advanced-filters" *ngIf="showAdvancedFilters">
        <div class="filter-header">
          <h5><i class="fas fa-search"></i> Advanced Filters</h5>
          <button class="close-filter" (click)="toggleAdvancedFilters()" type="button">✕</button>
        </div>
        
        <div class="filter-row">
          <div class="filter-field">
            <label><i class="fas fa-tag"></i> Project Name</label>
            <input type="text" [(ngModel)]="filterValues.nom" (ngModelChange)="onFilterChange()" placeholder="Search by name..." class="filter-input">
          </div>
          <div class="filter-field">
            <label><i class="fas fa-align-left"></i> Description</label>
            <input type="text" [(ngModel)]="filterValues.description" (ngModelChange)="onFilterChange()" placeholder="Search in description..." class="filter-input">
          </div>
        </div>
        
        <div class="filter-row">
          <div class="filter-field">
            <label><i class="fas fa-coins"></i> Budget Range (DA)</label>
            <div class="range-inputs">
              <input type="number" [(ngModel)]="filterValues.budgetMin" (ngModelChange)="onFilterChange()" placeholder="Min" class="filter-input small">
              <span>-</span>
              <input type="number" [(ngModel)]="filterValues.budgetMax" (ngModelChange)="onFilterChange()" placeholder="Max" class="filter-input small">
            </div>
          </div>
          <div class="filter-field">
            <label><i class="fas fa-flag-checkered"></i> Target VP Range</label>
            <div class="range-inputs">
              <input type="number" [(ngModel)]="filterValues.vpMin" (ngModelChange)="onFilterChange()" placeholder="Min" class="filter-input small">
              <span>-</span>
              <input type="number" [(ngModel)]="filterValues.vpMax" (ngModelChange)="onFilterChange()" placeholder="Max" class="filter-input small">
            </div>
          </div>
        </div>
        
        <div class="filter-row">
          <div class="filter-field">
            <label><i class="fas fa-calendar-alt"></i> Start Date</label>
            <div class="date-range">
              <input type="date" [(ngModel)]="filterValues.startDateFrom" (ngModelChange)="onFilterChange()" class="filter-input">
              <span>to</span>
              <input type="date" [(ngModel)]="filterValues.startDateTo" (ngModelChange)="onFilterChange()" class="filter-input">
            </div>
          </div>
          <div class="filter-field">
            <label><i class="fas fa-calendar-check"></i> End Date</label>
            <div class="date-range">
              <input type="date" [(ngModel)]="filterValues.endDateFrom" (ngModelChange)="onFilterChange()" class="filter-input">
              <span>to</span>
              <input type="date" [(ngModel)]="filterValues.endDateTo" (ngModelChange)="onFilterChange()" class="filter-input">
            </div>
          </div>
        </div>
        
        <div class="filter-row">
          <div class="filter-field">
            <label><i class="fas fa-calendar-times"></i> Actual End Date</label>
            <div class="date-range">
              <input type="date" [(ngModel)]="filterValues.actualEndDateFrom" (ngModelChange)="onFilterChange()" class="filter-input">
              <span>to</span>
              <input type="date" [(ngModel)]="filterValues.actualEndDateTo" (ngModelChange)="onFilterChange()" class="filter-input">
            </div>
          </div>
          <div class="filter-field status-filter">
            <label><i class="fas fa-chart-line"></i> Status</label>
            <div class="status-checkboxes">
              <label *ngFor="let status of statusOptions" class="status-checkbox">
                <input type="checkbox" [value]="status.value" 
                       [checked]="isStatusSelected(status.value)"
                       (change)="onStatusFilterChange($event, status.value)">
                <span class="status-dot" [style.backgroundColor]="status.color"></span>
                <span>{{ status.label }}</span>
              </label>
            </div>
          </div>
        </div>
        
        <div class="filter-actions">
          <button class="btn-reset" (click)="resetFilters()" type="button">
            <i class="fas fa-undo-alt"></i> Reset All Filters
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .mission-filter-wrapper {
      background: var(--background-basic-color-1);
      padding: 1rem 1.5rem;
      border-radius: var(--border-radius);
      box-shadow: var(--shadow);
      margin-bottom: 1rem;
      transition: all 0.3s ease;
    }
    
    .filter-header-info {
      display: flex;
      align-items: center;
      gap: 0.75rem;
      margin-bottom: 1rem;
      padding-bottom: 0.75rem;
      border-bottom: 2px solid var(--border-basic-color-3);
    }
    
    .regular-user-message {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      padding: 0.5rem 0.75rem;
      background: var(--background-basic-color-2);
      border-radius: var(--border-radius);
      margin-bottom: 0.75rem;
      font-size: 0.8125rem;
      color: var(--text-hint-color);
    }
    
    .regular-user-message i {
      color: var(--color-primary-default);
      font-size: 0.875rem;
    }
    
    .filter-header-info i {
      font-size: 1.25rem;
      color: var(--color-primary-default);
    }
    
    .filter-title {
      font-size: 1rem;
      font-weight: 600;
      color: var(--text-basic-color);
      letter-spacing: 0.5px;
    }
    
    .filter-controls {
      display: flex;
      align-items: center;
      gap: 1rem;
      flex-wrap: wrap;
      margin-bottom: 0.75rem;
    }
    
    .filter-controls label {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      font-weight: 600;
      color: var(--text-basic-color);
      font-size: 0.875rem;
    }
    
    .filter-controls label i {
      color: var(--color-primary-default);
      font-size: 1rem;
    }
    
    .mission-select {
      padding: 0.625rem 1rem;
      border: 1px solid var(--border-basic-color-3);
      border-radius: var(--border-radius);
      background: var(--background-basic-color-1);
      color: var(--text-basic-color);
      min-width: 300px;
      cursor: pointer;
      font-size: 0.875rem;
      transition: all 0.3s ease;
    }
    
    .mission-select:hover:not(:disabled) {
      border-color: var(--color-primary-default);
      background: var(--background-basic-color-2);
    }
    
    .mission-select:focus {
      outline: none;
      border-color: var(--color-primary-default);
      box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
    }
    
    .mission-select:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }
    
    .filter-toggle-btn {
      background: var(--background-basic-color-2);
      border: 1px solid var(--border-basic-color-3);
      padding: 0.625rem 1rem;
      border-radius: var(--border-radius);
      cursor: pointer;
      display: flex;
      align-items: center;
      gap: 0.5rem;
      transition: all 0.3s ease;
      color: var(--text-basic-color);
      font-size: 0.875rem;
    }
    
    .filter-toggle-btn:hover {
      background: var(--background-basic-color-3);
      border-color: var(--color-primary-default);
    }
    
    .filter-count {
      background: var(--color-primary-default);
      color: white;
      border-radius: 50%;
      padding: 0.125rem 0.5rem;
      font-size: 0.75rem;
      font-weight: bold;
      margin-left: 0.25rem;
    }
    
    .loading-indicator {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      color: var(--color-primary-default);
      font-size: 0.875rem;
    }
    
    .selected-info {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      padding: 0.5rem 0.75rem;
      background: var(--background-basic-color-2);
      border-radius: var(--border-radius);
      margin-top: 0.75rem;
      font-size: 0.8125rem;
      color: var(--text-hint-color);
    }
    
    .selected-info i {
      font-size: 0.875rem;
    }
    
    .selected-info i.fa-info-circle {
      color: var(--color-primary-default);
    }
    
    .selected-info i.fa-globe {
      color: var(--color-success-default);
    }
    
    .selected-info strong {
      color: var(--text-basic-color);
    }
    
    /* Advanced Filters Styles */
    .advanced-filters {
      margin-top: 1rem;
      padding: 1rem;
      background: var(--background-basic-color-2);
      border-radius: var(--border-radius);
      animation: slideDown 0.3s ease;
      display: block !important;
    }
    
    @keyframes slideDown {
      from {
        opacity: 0;
        transform: translateY(-10px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }
    
    .filter-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 1rem;
      padding-bottom: 0.5rem;
      border-bottom: 1px solid var(--border-basic-color-3);
    }
    
    .filter-header h5 {
      margin: 0;
      font-size: 1rem;
      font-weight: 600;
      color: var(--text-basic-color);
    }
    
    .filter-header h5 i {
      margin-right: 0.5rem;
      color: var(--color-primary-default);
    }
    
    .close-filter {
      background: none;
      border: none;
      font-size: 1.25rem;
      cursor: pointer;
      color: var(--text-hint-color);
      transition: color 0.3s ease;
    }
    
    .close-filter:hover {
      color: var(--color-danger-default);
    }
    
    .filter-row {
      display: flex;
      gap: 1rem;
      margin-bottom: 1rem;
      flex-wrap: wrap;
    }
    
    .filter-field {
      flex: 1;
      min-width: 200px;
    }
    
    .filter-field label {
      display: block;
      font-size: 0.75rem;
      font-weight: 600;
      color: var(--text-hint-color);
      margin-bottom: 0.375rem;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }
    
    .filter-field label i {
      margin-right: 0.375rem;
      font-size: 0.7rem;
    }
    
    .filter-input {
      width: 100%;
      padding: 0.5rem 0.75rem;
      border: 1px solid var(--border-basic-color-3);
      border-radius: var(--border-radius);
      background: var(--background-basic-color-1);
      color: var(--text-basic-color);
      font-size: 0.875rem;
      transition: all 0.3s ease;
    }
    
    .filter-input:focus {
      outline: none;
      border-color: var(--color-primary-default);
      box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
    }
    
    .filter-input.small {
      width: calc(50% - 0.5rem);
      display: inline-block;
    }
    
    .range-inputs {
      display: flex;
      align-items: center;
      gap: 0.5rem;
    }
    
    .date-range {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      flex-wrap: wrap;
    }
    
    .status-checkboxes {
      display: flex;
      flex-wrap: wrap;
      gap: 0.75rem;
    }
    
    .status-checkbox {
      display: flex;
      align-items: center;
      gap: 0.375rem;
      cursor: pointer;
      font-size: 0.8125rem;
      padding: 0.25rem 0.5rem;
      border-radius: var(--border-radius);
      transition: background 0.3s ease;
    }
    
    .status-checkbox:hover {
      background: var(--background-basic-color-3);
    }
    
    .status-checkbox input {
      cursor: pointer;
    }
    
    .status-dot {
      width: 10px;
      height: 10px;
      border-radius: 50%;
      display: inline-block;
    }
    
    .filter-actions {
      display: flex;
      gap: 0.75rem;
      justify-content: flex-end;
      margin-top: 1rem;
      padding-top: 1rem;
      border-top: 1px solid var(--border-basic-color-3);
    }
    
    .btn-reset {
      padding: 0.5rem 1rem;
      border: none;
      border-radius: var(--border-radius);
      cursor: pointer;
      font-size: 0.8125rem;
      font-weight: 500;
      display: flex;
      align-items: center;
      gap: 0.5rem;
      transition: all 0.3s ease;
      background: var(--color-danger-default);
      color: white;
    }
    
    .btn-reset:hover {
      background: var(--color-danger-active);
      transform: translateY(-1px);
    }
    
    @media (max-width: 768px) {
      .mission-filter-wrapper {
        padding: 0.75rem 1rem;
      }
      
      .mission-select {
        min-width: 100%;
      }
      
      .filter-controls {
        flex-direction: column;
        align-items: stretch;
      }
      
      .filter-controls label {
        margin-bottom: 0.25rem;
      }
      
      .filter-row {
        flex-direction: column;
      }
      
      .filter-field {
        min-width: 100%;
      }
      
      .status-checkboxes {
        flex-direction: column;
      }
    }
  `]
})
export class MissionFilterComponent implements OnInit, OnDestroy {
  @Input() canViewAllProjects: boolean = false;
  @Output() missionChanged = new EventEmitter<string>();
  @Output() filtersApplied = new EventEmitter<any>();

  missions: Mission[] = [];
  selectedMissionId: string = 'all';
  isLoading: boolean = false;
  showAdvancedFilters: boolean = false;
  
  filterValues: any = {
    nom: '',
    description: '',
    budgetMin: null,
    budgetMax: null,
    vpMin: null,
    vpMax: null,
    startDateFrom: '',
    startDateTo: '',
    endDateFrom: '',
    endDateTo: '',
    actualEndDateFrom: '',
    actualEndDateTo: '',
    statuses: []
  };
  
  private destroy$ = new Subject<void>();
  private debounceTimer: any;

  statusOptions = [
    { value: 'PLANIFIER', label: 'Planned', color: '#64748b' },
    { value: 'ENCOURS', label: 'In Progress', color: '#3b82f6' },
    { value: 'ENATTENTE', label: 'On Hold', color: '#f59e0b' },
    { value: 'ENRETARD', label: 'Delayed', color: '#ef4444' },
    { value: 'TERMINI', label: 'Completed', color: '#10b981' },
    { value: 'ANNULE', label: 'Cancelled', color: '#94a3b8' }
  ];

  constructor(
    private missionService: MissionService,
    private toastrService: NbToastrService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    if (this.canViewAllProjects) {
      this.loadMissions();
    }
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
    if (this.debounceTimer) {
      clearTimeout(this.debounceTimer);
    }
  }

  loadMissions() {
    this.isLoading = true;
    this.missionService.getAllMissions()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (missions) => {
          this.missions = missions;
          console.log('Missions loaded in filter:', this.missions.length);
          this.isLoading = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Error loading missions:', err);
          this.toastrService.danger('Failed to load missions', 'Error');
          this.isLoading = false;
          this.cdr.detectChanges();
        }
      });
  }

  onMissionChange() {
    console.log('Mission changed to:', this.selectedMissionId);
    this.missionChanged.emit(this.selectedMissionId);
  }

  onFilterChange() {
    // Debounce filter changes to avoid too many events
    if (this.debounceTimer) {
      clearTimeout(this.debounceTimer);
    }
    this.debounceTimer = setTimeout(() => {
      this.applyFilters();
    }, 500);
  }

  getSelectedMission(): Mission | undefined {
    if (this.selectedMissionId === 'all') {
      return undefined;
    }
    return this.missions.find(m => m.id?.toString() === this.selectedMissionId);
  }

  toggleAdvancedFilters() {
    console.log('Toggle button clicked - current state:', this.showAdvancedFilters);
    this.showAdvancedFilters = !this.showAdvancedFilters;
    console.log('New state:', this.showAdvancedFilters);
    this.cdr.detectChanges();
  }

  onStatusFilterChange(event: any, statusValue: string): void {
    if (event.target.checked) {
      this.filterValues.statuses.push(statusValue);
    } else {
      const index = this.filterValues.statuses.indexOf(statusValue);
      if (index > -1) {
        this.filterValues.statuses.splice(index, 1);
      }
    }
    this.applyFilters();
  }

  isStatusSelected(statusValue: string): boolean {
    return this.filterValues.statuses.includes(statusValue);
  }

  applyFilters() {
    console.log('Applying filters:', this.filterValues);
    this.filtersApplied.emit({ ...this.filterValues });
  }

  resetFilters() {
    this.filterValues = {
      nom: '',
      description: '',
      budgetMin: null,
      budgetMax: null,
      vpMin: null,
      vpMax: null,
      startDateFrom: '',
      startDateTo: '',
      endDateFrom: '',
      endDateTo: '',
      actualEndDateFrom: '',
      actualEndDateTo: '',
      statuses: []
    };
    this.applyFilters();
  }

  getActiveFiltersCount(): number {
    let count = 0;
    if (this.filterValues.nom && this.filterValues.nom.trim()) count++;
    if (this.filterValues.description && this.filterValues.description.trim()) count++;
    if (this.filterValues.budgetMin && this.filterValues.budgetMin > 0) count++;
    if (this.filterValues.budgetMax && this.filterValues.budgetMax > 0) count++;
    if (this.filterValues.vpMin && this.filterValues.vpMin > 0) count++;
    if (this.filterValues.vpMax && this.filterValues.vpMax > 0) count++;
    if (this.filterValues.startDateFrom) count++;
    if (this.filterValues.startDateTo) count++;
    if (this.filterValues.endDateFrom) count++;
    if (this.filterValues.endDateTo) count++;
    if (this.filterValues.actualEndDateFrom) count++;
    if (this.filterValues.actualEndDateTo) count++;
    if (this.filterValues.statuses?.length) count++;
    return count;
  }
}