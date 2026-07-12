import { Component, OnInit, OnDestroy } from '@angular/core';
import { LocalDataSource } from 'ng2-smart-table';
import { ProjectService, ProjectResponseDTO } from '../../../services/project/project.service';
import { MissionService, Mission } from '../../../services/mission/mission.service';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';
import { NbToastrService } from '@nebular/theme';
import { ProjectViewButtonComponent } from './project-view-button/project-view-button.component';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, takeUntil } from 'rxjs/operators';

@Component({
  selector: 'ngx-projects',
  templateUrl: './projects.component.html',
  styleUrls: ['./projects.component.scss'],
})
export class ProjectsComponent implements OnInit, OnDestroy {

  currentMissionId: number = 0;
  selectedMissionId: string = 'all';
  missions: Mission[] = [];
  allProjects: ProjectResponseDTO[] = [];
  private destroy$ = new Subject<void>();
  
  settings: any;
  source: LocalDataSource = new LocalDataSource();

  // Advanced filters
  showAdvancedFilters: boolean = false;
  filterForm: FormGroup;
  
  // Filter options
  statusOptions = [
    { value: 'PLANIFIER', title: 'Planned', icon: 'far fa-calendar-alt' },
    { value: 'ENCOURS', title: 'In Progress', icon: 'fas fa-play-circle' },
    { value: 'ENATTENTE', title: 'On Hold', icon: 'fas fa-pause-circle' },
    { value: 'ENRETARD', title: 'Delayed', icon: 'fas fa-exclamation-circle' },
    { value: 'TERMINI', title: 'Completed', icon: 'fas fa-check-circle' },
    { value: 'ANNULE', title: 'Cancelled', icon: 'fas fa-ban' }
  ];

  // Role-based permissions
  canViewAllProjects: boolean = false;
  isLoading: boolean = false;

  constructor(
    private projectService: ProjectService,
    private missionService: MissionService,
    private authService: AuthService,
    private router: Router,
    private toastrService: NbToastrService,
    private fb: FormBuilder
  ) {
    this.filterForm = this.fb.group({
      nom: [''],
      description: [''],
      budgetMin: [null],
      budgetMax: [null],
      vpMin: [null],
      vpMax: [null],
      startDateFrom: [''],
      startDateTo: [''],
      endDateFrom: [''],
      endDateTo: [''],
      actualEndDateFrom: [''],
      actualEndDateTo: [''],
      statuses: [[]]
    });
  }

  ngOnInit() {
    const user = this.authService.getCurrentUser();
    if (!user) {
      this.router.navigate(['/auth/login']);
      return;
    }

    this.initializePermissions();
    this.loadMissions();
    this.buildSettings();
    this.setupAutoFilter();
    this.loadProjects();
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  setupAutoFilter() {
    this.filterForm.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        takeUntil(this.destroy$)
      )
      .subscribe(() => {
        this.applyFilters();
      });
  }

  initializePermissions(): void {
    const userRoles = this.authService.getCurrentUser()?.roles || [];
    this.canViewAllProjects = userRoles.includes('DIRECTEUR') || 
                              userRoles.includes('ADMIN') || 
                              userRoles.includes('ADMINISTRATEUR');
    console.log('Can view all projects:', this.canViewAllProjects);
  }

  loadMissions(): void {
    this.missionService.getAllMissions().subscribe({
      next: (missions) => {
        this.missions = missions;
        console.log('Missions loaded:', this.missions);
      },
      error: (err) => {
        console.error('Error loading missions:', err);
      }
    });
  }

  loadAllProjects(): void {
    this.isLoading = true;
    console.log('Loading all projects...');
    this.projectService.getAllProjects().subscribe({
      next: (projects) => {
        console.log('All projects loaded:', projects.length);
        this.allProjects = projects;
        this.applyFilters();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading all projects:', err);
        this.toastrService.danger('Failed to load projects', 'Error');
        this.allProjects = [];
        this.source.load([]);
        this.isLoading = false;
      }
    });
  }

  loadProjects(): void {
    this.isLoading = true;
    
    if (this.canViewAllProjects && this.selectedMissionId === 'all') {
      // ADMIN/DIRECTEUR - All missions
      console.log('Loading ALL projects for ADMIN/DIRECTEUR');
      this.projectService.getAllProjects().subscribe({
        next: (projects) => {
          console.log('All projects loaded:', projects.length);
          this.allProjects = projects;
          this.applyFilters();
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error loading all projects:', err);
          this.toastrService.danger('Failed to load projects', 'Error');
          this.allProjects = [];
          this.source.load([]);
          this.isLoading = false;
        }
      });
    } else if (this.canViewAllProjects && this.selectedMissionId !== 'all') {
      // ADMIN/DIRECTEUR - Specific mission
      const missionId = Number(this.selectedMissionId);
      console.log('Loading projects for mission:', missionId);
      this.projectService.getProjectsByMissionId(missionId).subscribe({
        next: (projects) => {
          console.log('Projects loaded for mission:', projects.length);
          this.allProjects = projects;
          this.applyFilters();
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error loading projects for mission:', err);
          this.toastrService.danger('Failed to load projects', 'Error');
          this.allProjects = [];
          this.source.load([]);
          this.isLoading = false;
        }
      });
    } else {
      // Regular user - Get projects from their current mission only
      console.log('Loading projects for current mission (regular user)');
      this.projectService.getProjectsByCurrentMission().subscribe({
        next: (projects) => {
          console.log('Current mission projects loaded:', projects.length);
          this.allProjects = projects;
          this.applyFilters();
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error loading projects for current mission:', err);
          this.toastrService.danger('Failed to load projects', 'Error');
          this.allProjects = [];
          this.source.load([]);
          this.isLoading = false;
        }
      });
    }
  }

  loadProjectsByMission(missionId: number): void {
    this.isLoading = true;
    console.log('Loading projects for mission ID:', missionId);
    this.projectService.getProjectsByMissionId(missionId).subscribe({
      next: (projects) => {
        console.log('Projects loaded for mission:', projects.length);
        this.allProjects = projects;
        this.applyFilters();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading projects for mission:', err);
        this.toastrService.danger('Failed to load projects', 'Error');
        this.allProjects = [];
        this.source.load([]);
        this.isLoading = false;
      }
    });
  }

  onMissionFilterChange(missionId: string): void {
    console.log('Mission changed to:', missionId);
    this.selectedMissionId = missionId;
    
    this.resetFilters();
    this.allProjects = [];
    this.source.load([]);
    
    if (!this.canViewAllProjects) {
      console.log('Regular user - loading current mission projects');
      this.projectService.getProjectsByCurrentMission().subscribe({
        next: (projects) => {
          this.allProjects = projects;
          this.applyFilters();
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error loading projects:', err);
          this.toastrService.danger('Failed to load projects', 'Error');
          this.isLoading = false;
        }
      });
    } else if (missionId === 'all') {
      this.loadAllProjects();
    } else {
      const missionIdNum = Number(missionId);
      if (!isNaN(missionIdNum) && missionIdNum > 0) {
        this.loadProjectsByMission(missionIdNum);
      } else {
        console.error('Invalid mission ID:', missionId);
        this.toastrService.danger('Invalid mission selection', 'Error');
      }
    }
  }

  // ✅ FIXED: getProjectStatus with annule priority
  getProjectStatus(project: any): string {
    // 1. ✅ FIRST: If annule is true, it's CANCELLED (highest priority)
    if (project.annule === true) {
      return 'ANNULE';
    }
    
    // 2. Use the status field directly from the API response
    if (project.status) {
      return project.status;
    }
    
    // 3. If dateFinReelle is set, it's completed (only if not annule)
    if (project.dateFinReelle) {
      return 'TERMINI';
    }
    
    // 4. Check if project is active based on dates
    const now = new Date().toISOString().split('T')[0];
    
    if (project.dateStartReelle) {
      if (project.dateStartReelle > now) {
        return 'ENATTENTE';
      }
      if (project.objectifFin && project.objectifFin < now) {
        return 'ENRETARD';
      }
      return 'ENCOURS';
    }
    
    if (project.objectifDebut && project.objectifFin) {
      if (now > project.objectifFin) {
        return 'ENRETARD';
      }
      if (now >= project.objectifDebut) {
        return 'ENCOURS';
      }
      return 'PLANIFIER';
    }
    
    return 'PLANIFIER';
  }

  applyFilters(): void {
    if (!this.allProjects || this.allProjects.length === 0) {
      console.log('No projects to filter');
      this.source.load([]);
      return;
    }

    const filters = this.filterForm.value;
    let filtered = [...this.allProjects];

    // Name filter
    if (filters.nom && filters.nom.trim()) {
      const searchTerm = filters.nom.toLowerCase().trim();
      filtered = filtered.filter(p => p.nom?.toLowerCase().includes(searchTerm));
    }

    // Description filter
    if (filters.description && filters.description.trim()) {
      const searchTerm = filters.description.toLowerCase().trim();
      filtered = filtered.filter(p => p.description?.toLowerCase().includes(searchTerm));
    }

    // Budget range
    if (filters.budgetMin !== null && filters.budgetMin !== undefined && filters.budgetMin > 0) {
      filtered = filtered.filter(p => (p.budget || 0) >= filters.budgetMin);
    }
    if (filters.budgetMax !== null && filters.budgetMax !== undefined && filters.budgetMax > 0) {
      filtered = filtered.filter(p => (p.budget || 0) <= filters.budgetMax);
    }

    // VP range
    if (filters.vpMin !== null && filters.vpMin !== undefined && filters.vpMin > 0) {
      filtered = filtered.filter(p => (p.objectifVP || 0) >= filters.vpMin);
    }
    if (filters.vpMax !== null && filters.vpMax !== undefined && filters.vpMax > 0) {
      filtered = filtered.filter(p => (p.objectifVP || 0) <= filters.vpMax);
    }

    // Start date range
    if (filters.startDateFrom) {
      filtered = filtered.filter(p => p.objectifDebut >= filters.startDateFrom);
    }
    if (filters.startDateTo) {
      filtered = filtered.filter(p => p.objectifDebut <= filters.startDateTo);
    }

    // End date range
    if (filters.endDateFrom) {
      filtered = filtered.filter(p => p.objectifFin >= filters.endDateFrom);
    }
    if (filters.endDateTo) {
      filtered = filtered.filter(p => p.objectifFin <= filters.endDateTo);
    }

    // Actual end date range
    if (filters.actualEndDateFrom) {
      filtered = filtered.filter(p => p.dateFinReelle && p.dateFinReelle >= filters.actualEndDateFrom);
    }
    if (filters.actualEndDateTo) {
      filtered = filtered.filter(p => p.dateFinReelle && p.dateFinReelle <= filters.actualEndDateTo);
    }

    // ✅ Status filter - use the fixed getProjectStatus method
    if (filters.statuses && filters.statuses.length > 0) {
      filtered = filtered.filter(p => {
        const status = this.getProjectStatus(p);
        return filters.statuses.includes(status);
      });
    }

    const enhancedData = filtered.map(project => ({
      ...project,
      status: this.getProjectStatus(project),
      missionCode: project.missionCode || 'N/A',
      dateFinReelle: project.dateFinReelle || null
    }));

    this.source.load(enhancedData);
  }

  resetFilters(): void {
    this.filterForm.patchValue({
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
    });
  }

  toggleAdvancedFilters(): void {
    this.showAdvancedFilters = !this.showAdvancedFilters;
  }

  onStatusFilterChange(event: any, statusValue: string): void {
    const currentStatuses = this.filterForm.get('statuses')?.value || [];
    if (event.target.checked) {
      this.filterForm.get('statuses')?.setValue([...currentStatuses, statusValue]);
    } else {
      this.filterForm.get('statuses')?.setValue(currentStatuses.filter((s: string) => s !== statusValue));
    }
  }

  isStatusSelected(statusValue: string): boolean {
    const statuses = this.filterForm.get('statuses')?.value || [];
    return statuses.includes(statusValue);
  }

  getActiveFiltersCount(): number {
    const filters = this.filterForm.value;
    let count = 0;
    if (filters.nom && filters.nom.trim()) count++;
    if (filters.description && filters.description.trim()) count++;
    if (filters.budgetMin && filters.budgetMin > 0) count++;
    if (filters.budgetMax && filters.budgetMax > 0) count++;
    if (filters.vpMin && filters.vpMin > 0) count++;
    if (filters.vpMax && filters.vpMax > 0) count++;
    if (filters.startDateFrom) count++;
    if (filters.startDateTo) count++;
    if (filters.endDateFrom) count++;
    if (filters.endDateTo) count++;
    if (filters.actualEndDateFrom) count++;
    if (filters.actualEndDateTo) count++;
    if (filters.statuses?.length) count++;
    return count;
  }

  buildSettings() { 
    this.settings = {
      actions: false,
      columns: {
        view: {
          title: 'Overview',
          type: 'custom',
          filter: false,
          sort: false,
          width: '100px',
          renderComponent: ProjectViewButtonComponent,
        },
        nom: {
          title: 'Project Name',
          type: 'string',
          filter: true,
        },
        description: {
          title: 'Description',
          type: 'string',
          filter: true,
        },
        status: {
          title: 'Status',
          type: 'html',
          filter: false,
          valuePrepareFunction: (value, row) => {
            // ✅ Use the fixed getProjectStatus method
            const status = this.getProjectStatus(row);
            const statusMap: { [key: string]: { class: string; label: string; icon: string } } = {
              'PLANIFIER': { class: 'status-planned', label: 'Planned', icon: 'far fa-calendar-alt' },
              'ENCOURS': { class: 'status-progress', label: 'In Progress', icon: 'fas fa-play-circle' },
              'ENATTENTE': { class: 'status-pending', label: 'On Hold', icon: 'fas fa-pause-circle' },
              'ENRETARD': { class: 'status-delayed', label: 'Delayed', icon: 'fas fa-exclamation-circle' },
              'TERMINI': { class: 'status-completed', label: 'Completed', icon: 'fas fa-check-circle' },
              'ANNULE': { class: 'status-cancelled', label: 'Cancelled', icon: 'fas fa-ban' }
            };
            const statusInfo = statusMap[status] || statusMap['PLANIFIER'];
            return `<span class="status-badge ${statusInfo.class}">
                      <i class="${statusInfo.icon}"></i>
                      ${statusInfo.label}
                    </span>`;
          },
        },
        objectifDebut: {
          title: 'Start Date',
          type: 'date',
          valuePrepareFunction: (date) => date ? new Date(date).toLocaleDateString() : '-',
        },
        objectifFin: {
          title: 'End Date',
          type: 'date',
          valuePrepareFunction: (date) => date ? new Date(date).toLocaleDateString() : '-',
        },
        dateFinReelle: {
          title: 'Actual End Date',
          type: 'html',
          filter: false,
          valuePrepareFunction: (date) => {
            if (!date) return '<span class="badge-not-completed">Not completed</span>';
            return `<span class="badge-completed">✅ ${new Date(date).toLocaleDateString()}</span>`;
          },
        },
        budget: {
          title: 'Budget (DA)',
          type: 'number',
          valuePrepareFunction: (value) => value ? value.toLocaleString() : '0',
        },
        objectifVP: {
          title: 'Target VP',
          type: 'number',
          valuePrepareFunction: (value) => value ? value.toLocaleString() : '0',
        },
        missionCode: {
          title: 'Mission',
          type: 'string',
          filter: true,
        },
      },
      noDataMessage: 'No projects found',
    };
  }

  onFiltersApplied(filters: any): void {
    console.log('Filters applied from component:', filters);
    this.applyFiltersFromComponent(filters);
  }

  applyFiltersFromComponent(filters: any): void {
    if (!this.allProjects || this.allProjects.length === 0) {
      this.source.load([]);
      return;
    }

    let filtered = [...this.allProjects];

    // 1. Name filter
    if (filters.nom && filters.nom.trim()) {
      const searchTerm = filters.nom.toLowerCase().trim();
      filtered = filtered.filter(p => p.nom?.toLowerCase().includes(searchTerm));
    }

    // 2. Description filter
    if (filters.description && filters.description.trim()) {
      const searchTerm = filters.description.toLowerCase().trim();
      filtered = filtered.filter(p => p.description?.toLowerCase().includes(searchTerm));
    }

    // 3. Budget range filter
    if (filters.budgetMin !== null && filters.budgetMin !== undefined && filters.budgetMin > 0) {
      filtered = filtered.filter(p => (p.budget || 0) >= filters.budgetMin);
    }
    if (filters.budgetMax !== null && filters.budgetMax !== undefined && filters.budgetMax > 0) {
      filtered = filtered.filter(p => (p.budget || 0) <= filters.budgetMax);
    }

    // 4. VP range filter
    if (filters.vpMin !== null && filters.vpMin !== undefined && filters.vpMin > 0) {
      filtered = filtered.filter(p => (p.objectifVP || 0) >= filters.vpMin);
    }
    if (filters.vpMax !== null && filters.vpMax !== undefined && filters.vpMax > 0) {
      filtered = filtered.filter(p => (p.objectifVP || 0) <= filters.vpMax);
    }

    // 5. Start date range filter
    if (filters.startDateFrom) {
      filtered = filtered.filter(p => p.objectifDebut >= filters.startDateFrom);
    }
    if (filters.startDateTo) {
      filtered = filtered.filter(p => p.objectifDebut <= filters.startDateTo);
    }

    // 6. End date range filter
    if (filters.endDateFrom) {
      filtered = filtered.filter(p => p.objectifFin >= filters.endDateFrom);
    }
    if (filters.endDateTo) {
      filtered = filtered.filter(p => p.objectifFin <= filters.endDateTo);
    }

    // 7. Actual end date range filter
    if (filters.actualEndDateFrom) {
      filtered = filtered.filter(p => p.dateFinReelle && p.dateFinReelle >= filters.actualEndDateFrom);
    }
    if (filters.actualEndDateTo) {
      filtered = filtered.filter(p => p.dateFinReelle && p.dateFinReelle <= filters.actualEndDateTo);
    }

    // 8. ✅ Status filter - use the fixed getProjectStatus method
    if (filters.statuses && filters.statuses.length > 0) {
      filtered = filtered.filter(p => {
        const status = this.getProjectStatus(p);
        return filters.statuses.includes(status);
      });
    }

    // 9. Mission filter (if present)
    if (filters.missionId && filters.missionId !== 'all') {
      const missionId = Number(filters.missionId);
      if (!isNaN(missionId) && missionId > 0) {
        filtered = filtered.filter(p => p.missionId === missionId);
      }
    }

    // 10. Annule/Cancelled filter
    if (filters.includeCancelled !== undefined) {
      if (filters.includeCancelled === false) {
        filtered = filtered.filter(p => p.annule !== true);
      }
    }

    // Enhance the data with computed fields
    const enhancedData = filtered.map(project => ({
      ...project,
      status: this.getProjectStatus(project),
      missionCode: project.missionCode || 'N/A',
      dateFinReelle: project.dateFinReelle || null,
      budgetDisplay: project.budget ? project.budget.toLocaleString() : '0',
      vpDisplay: project.objectifVP ? project.objectifVP.toLocaleString() : '0',
      progressionDisplay: project.progression || 0,
      isOverdue: project.objectifFin && !project.dateFinReelle && 
                 new Date(project.objectifFin) < new Date() && 
                 project.status !== 'TERMINI' && 
                 project.status !== 'ANNULE'
    }));

    // Sort by date (newest first) or by name
    if (filters.sortBy) {
      switch (filters.sortBy) {
        case 'nom':
          enhancedData.sort((a, b) => a.nom?.localeCompare(b.nom || '') || 0);
          break;
        case 'budget':
          enhancedData.sort((a, b) => (b.budget || 0) - (a.budget || 0));
          break;
        case 'vp':
          enhancedData.sort((a, b) => (b.objectifVP || 0) - (a.objectifVP || 0));
          break;
        case 'startDate':
          enhancedData.sort((a, b) => (a.objectifDebut > b.objectifDebut ? -1 : 1));
          break;
        case 'endDate':
          enhancedData.sort((a, b) => (a.objectifFin > b.objectifFin ? -1 : 1));
          break;
        case 'status':
          enhancedData.sort((a, b) => (a.status || '').localeCompare(b.status || ''));
          break;
        default:
          enhancedData.sort((a, b) => (a.objectifDebut > b.objectifDebut ? -1 : 1));
          break;
      }
    } else {
      enhancedData.sort((a, b) => (a.objectifDebut > b.objectifDebut ? -1 : 1));
    }

    this.source.load(enhancedData);
    console.log(`Filtered ${this.allProjects.length} projects to ${enhancedData.length} projects`);
  }
}