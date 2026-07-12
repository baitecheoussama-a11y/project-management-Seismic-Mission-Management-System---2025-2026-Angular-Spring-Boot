import { Component, OnInit, OnDestroy, ViewChild, ElementRef, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil, debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MissionService } from '../../../services/mission/mission.service';
import { 
  EquipeDetailService, 
  ActiveDetail, 
  AssignActivityRequest,
  AffectationEquipeDTO,
  UpdateRealDatesRequest,
  EquipeActivities 
} from '../../../services/team/equipe-detail.service';
import { AuthService } from '../../../services/auth.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-activities',
  templateUrl: './activities.component.html',
  styleUrls: ['./activities.component.scss']
})
export class ActivitiesComponent implements OnInit, OnDestroy {
  
  @ViewChild('teamNameInput') teamNameInput!: ElementRef;
  @ViewChild('editTeamInput') editTeamInput!: ElementRef;

  equipes: EquipeActivities[] = [];
  filteredEquipes: EquipeActivities[] = [];
  selectedEquipe: EquipeActivities | null = null;
  teamActivities: ActiveDetail[] = [];
  filteredActivities: ActiveDetail[] = [];
  
  isLoading: boolean = false;
  isMobile: boolean = false;
  sidebarCollapsed: boolean = false;
  
  // Team CRUD States
  isAddingTeam: boolean = false;
  newTeamName: string = '';
  newTeamType: string = 'TOPOGRAPHIE';
  editingTeamId: number | null = null;
  editingTeamName: string = '';
  editingTeamType: string = '';
  
  // Modal States
  showAddActivityModal: boolean = false;
  
  activityForm: FormGroup;
  availableActives: any[] = [];
  
  // Search and Filters
  searchTerm: string = '';
  teamSearchTerm: string = '';
  statusFilter: string = 'all';
  sortBy: string = 'code';
  sortOrder: string = 'asc';
  
  currentMissionId: number = 0;
  currentProject: any = null;
  
  Math = Math;
  currentPage: number = 1;
  pageSize: number = 6;
  totalPages: number = 1;
  paginatedActivities: ActiveDetail[] = [];
  
  // Role-based access flags
  canManageTeams: boolean = false;
  canManageActivities: boolean = false;
  
  typeOptions = [
    { value: 'TOPOGRAPHIE', label: 'Topography', icon: 'fas fa-map' },
    { value: 'LAYONNAGE', label: 'Layout', icon: 'fas fa-draw-polygon' },
    { value: 'ENERGISREMENT', label: 'Energization', icon: 'fas fa-bolt' },
    { value: 'POSE', label: 'Installation', icon: 'fas fa-tools' },
    { value: 'RAMASSAGE', label: 'Collection', icon: 'fas fa-truck' }
  ];
  
  statusOptions = [
    { value: 'PLANIFIER', label: 'Planned', color: '#64748b', icon: 'far fa-calendar-alt' },
    { value: 'ENCOURS', label: 'In Progress', color: '#3b82f6', icon: 'fas fa-play-circle' },
    { value: 'ENATTENTE', label: 'On Hold', color: '#f59e0b', icon: 'fas fa-pause-circle' },
    { value: 'ENRETARD', label: 'Delayed', color: '#ef4444', icon: 'fas fa-exclamation-circle' },
    { value: 'TERMINI', label: 'Completed', color: '#10b981', icon: 'fas fa-check-circle' },
    { value: 'ANNULE', label: 'Cancelled', color: '#94a3b8', icon: 'fas fa-ban' }
  ];
  
  private destroy$ = new Subject<void>();
  private searchSubject = new Subject<string>();
  
  constructor(
    private router: Router,
    private missionService: MissionService,
    private equipeDetailService: EquipeDetailService,
    private fb: FormBuilder,
    private cdr: ChangeDetectorRef,
    private authService: AuthService
  ) {
    this.activityForm = this.fb.group({
      activeId: [null, Validators.required],
      dateDebut: [new Date().toISOString().split('T')[0], Validators.required],
      dateFin: [''],
      ordre: [1, [Validators.required, Validators.min(1)]]
    });
  }
  
  ngOnInit(): void {
    this.loadCurrentMission();
    this.checkScreenSize();
    window.addEventListener('resize', () => this.checkScreenSize());
    this.initializePermissions();
    
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      takeUntil(this.destroy$)
    ).subscribe(() => {
      this.applyFilters();
    });
  }
  
  // ==================== PERMISSIONS ====================
  
  initializePermissions(): void {
    const hasGestionnaireRole = this.authService.hasRole('GESTIONNAIRE');
    const hasAdminRole = this.authService.hasRole('ADMIN');
    
    this.canManageTeams = hasGestionnaireRole || hasAdminRole;
    this.canManageActivities = hasGestionnaireRole || hasAdminRole;
  }
  
  // ==================== LOAD METHODS ====================
  
  loadCurrentMission(): void {
    this.missionService.getMyCurrentMission().pipe(takeUntil(this.destroy$)).subscribe({
      next: (missionResponse) => {
        if (missionResponse && missionResponse.missionId) {
          this.currentMissionId = missionResponse.missionId;
          this.loadEquipes();
          this.loadCurrentProject();
        }
      },
      error: (err) => console.error('Error loading current mission:', err)
    });
  }
  
  loadCurrentProject(): void {
    if (!this.currentMissionId) return;
    
    this.missionService.getCurrentProjectByMission(this.currentMissionId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (project) => {
          if (project) {
            this.currentProject = project;
          }
        },
        error: (err) => console.error('Error loading current project:', err)
      });
  }
  
  loadEquipes(): void {
    this.isLoading = true;
    
    this.missionService.getMissionTeam(this.currentMissionId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (missionTeam) => {
          this.equipes = missionTeam.equipes.map(equipe => ({
            id: equipe.id,
            nom: equipe.nom,
            type: equipe.type,
            memberCount: missionTeam.membersByEquipe?.[equipe.nom]?.length || 0,
            activitiesCount: 0
          }));
          
          this.filteredEquipes = [...this.equipes];
          this.loadActivityCountsForEquipes();
          this.isLoading = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Error loading equipes:', err);
          this.isLoading = false;
          Swal.fire({ icon: 'error', title: 'Error', text: 'Failed to load teams' });
        }
      });
  }
  
  loadActivityCountsForEquipes(): void {
    if (!this.currentMissionId) return;
    
    this.equipes.forEach((equipe, index) => {
      this.equipeDetailService.getActivitiesByEquipeId(equipe.id, this.currentMissionId)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (activities) => {
            this.equipes[index].activitiesCount = activities.length;
            this.filteredEquipes = [...this.equipes];
            this.cdr.detectChanges();
          },
          error: (err) => {
            console.error(`Error loading activities for equipe ${equipe.id}:`, err);
            this.equipes[index].activitiesCount = 0;
          }
        });
    });
  }
  
  loadTeamActivities(equipeId: number): void {
    if (!this.currentMissionId) return;
    
    this.isLoading = true;
    this.equipeDetailService.getActivitiesByEquipeId(equipeId, this.currentMissionId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (activities) => {
          this.teamActivities = activities;
          this.resetFilters();
          this.isLoading = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Error loading team activities:', err);
          this.isLoading = false;
          Swal.fire({ icon: 'error', title: 'Error', text: 'Failed to load team activities' });
        }
      });
  }
  
  // ==================== FILTERS & SORTING ====================
  
  applyFilters(): void {
    let filtered = [...this.teamActivities];
    
    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(activity =>
        activity.codeActive.toLowerCase().includes(term) ||
        activity.objectif.toLowerCase().includes(term) ||
        (activity.description && activity.description.toLowerCase().includes(term))
      );
    }
    
    if (this.statusFilter !== 'all') {
      filtered = filtered.filter(activity => activity.status === this.statusFilter);
    }
    
    filtered = this.sortActivities(filtered);
    
    this.filteredActivities = filtered;
    this.currentPage = 1;
    this.updatePagination();
    this.cdr.detectChanges();
  }
  
  sortActivities(activities: ActiveDetail[]): ActiveDetail[] {
    return activities.sort((a, b) => {
      let comparison = 0;
      
      switch (this.sortBy) {
        case 'code':
          comparison = a.codeActive.localeCompare(b.codeActive);
          break;
        case 'status':
          comparison = (a.status || 'PLANIFIER').localeCompare(b.status || 'PLANIFIER');
          break;
        case 'startDate':
          comparison = new Date(a.dateDebut).getTime() - new Date(b.dateDebut).getTime();
          break;
        case 'progress':
          comparison = (a.progression || 0) - (b.progression || 0);
          break;
        case 'ordre':
          comparison = (a.ordre || 999) - (b.ordre || 999);
          break;
        default:
          comparison = 0;
      }
      
      return this.sortOrder === 'asc' ? comparison : -comparison;
    });
  }
  
  resetFilters(): void {
    this.searchTerm = '';
    this.statusFilter = 'all';
    this.sortBy = 'code';
    this.sortOrder = 'asc';
    this.applyFilters();
  }
  
  isFilterApplied(): boolean {
    return this.searchTerm !== '' || 
           this.statusFilter !== 'all' || 
           this.sortBy !== 'code' || 
           this.sortOrder !== 'asc';
  }
  
  filterTeams(): void {
    if (!this.teamSearchTerm?.trim()) {
      this.filteredEquipes = [...this.equipes];
    } else {
      const term = this.teamSearchTerm.toLowerCase().trim();
      this.filteredEquipes = this.equipes.filter(team =>
        team.nom?.toLowerCase().includes(term) ||
        this.getTypeLabel(team.type)?.toLowerCase().includes(term)
      );
    }
  }
  
  clearTeamSearch(): void {
    this.teamSearchTerm = '';
    this.filterTeams();
  }
  
  clearSearch(): void {
    this.searchTerm = '';
    this.applyFilters();
  }
  
  onSearchInput(): void {
    this.searchSubject.next(this.searchTerm);
  }
  
  // ==================== HELPER METHODS ====================
  
  getStatusLabel(status: string): string {
    if (!status) return 'Planned';
    const option = this.statusOptions.find(s => s.value === status);
    return option?.label || status;
  }
  
  getStatusColor(status: string): string {
    if (!status) return '#64748b';
    const option = this.statusOptions.find(s => s.value === status);
    return option?.color || '#64748b';
  }
  
  getStatusIcon(status: string): string {
    if (!status) return 'far fa-calendar-alt';
    const option = this.statusOptions.find(s => s.value === status);
    return option?.icon || 'fas fa-circle';
  }
  
  getProgressPercentage(activity: ActiveDetail): number {
    return activity.progression || 0;
  }
  
  getProgressColor(progress: number): string {
    if (progress >= 80) return '#10b981';
    if (progress >= 50) return '#f59e0b';
    return '#ef4444';
  }
  
  getTypeIcon(type: string): string {
    const option = this.typeOptions.find(t => t.value === type);
    return option?.icon || 'fas fa-users';
  }
  
  getTypeLabel(type: string): string {
    const option = this.typeOptions.find(t => t.value === type);
    return option?.label || type;
  }
  
  getTypeColor(type: string): string {
    const colors: { [key: string]: string } = {
      'TOPOGRAPHIE': '#3b82f6',
      'LAYONNAGE': '#10b981',
      'ENERGISREMENT': '#f59e0b',
      'POSE': '#8b5cf6',
      'RAMASSAGE': '#14b8a6'
    };
    return colors[type] || '#64748b';
  }
  
  formatDate(date: string): string {
    if (!date) return 'N/A';
    return new Date(date).toLocaleDateString('en-US', { 
      day: '2-digit', 
      month: 'short', 
      year: 'numeric' 
    });
  }
  
  getTotalActivities(): number {
    return this.equipes.reduce((sum, t) => sum + t.activitiesCount, 0);
  }
  
  getStartIndex(): number {
    return (this.currentPage - 1) * this.pageSize + 1;
  }
  
  getEndIndex(): number {
    return Math.min(this.currentPage * this.pageSize, this.filteredActivities.length);
  }
  
  updatePagination(): void {
    this.totalPages = Math.ceil(this.filteredActivities.length / this.pageSize);
    if (this.totalPages === 0) this.totalPages = 1;
    
    if (this.currentPage > this.totalPages) {
      this.currentPage = Math.max(1, this.totalPages);
    }
    
    const startIndex = (this.currentPage - 1) * this.pageSize;
    this.paginatedActivities = this.filteredActivities.slice(startIndex, startIndex + this.pageSize);
  }
  
  previousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.updatePagination();
    }
  }
  
  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.updatePagination();
    }
  }
  
  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.updatePagination();
    }
  }
  
  getPageNumbers(): number[] {
    const pages: number[] = [];
    const maxVisible = 5;
    let startPage = Math.max(1, this.currentPage - Math.floor(maxVisible / 2));
    let endPage = Math.min(this.totalPages, startPage + maxVisible - 1);
    
    if (endPage - startPage + 1 < maxVisible) {
      startPage = Math.max(1, endPage - maxVisible + 1);
    }
    
    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }
    return pages;
  }
  
  selectTeam(equipe: EquipeActivities): void {
    this.selectedEquipe = equipe;
    this.loadTeamActivities(equipe.id);
    if (this.isMobile) {
      this.sidebarCollapsed = true;
    }
  }
  
  clearSelection(): void {
    this.selectedEquipe = null;
    this.teamActivities = [];
    this.filteredActivities = [];
    this.resetFilters();
    this.cdr.detectChanges();
  }
  
  toggleSidebar(): void {
    this.sidebarCollapsed = !this.sidebarCollapsed;
    this.cdr.detectChanges();
  }
  
  checkScreenSize(): void {
    this.isMobile = window.innerWidth < 768;
    if (!this.isMobile) this.sidebarCollapsed = false;
    this.cdr.detectChanges();
  }
  
  goBack(): void {
    this.router.navigate(['/pages/my-team']);
  }
  
  // ==================== TEAM CRUD ====================
  
  startAddTeam(): void {
    if (!this.canManageTeams) {
      Swal.fire({ icon: 'warning', title: 'Access Denied', text: 'You don\'t have permission to add teams' });
      return;
    }
    this.isAddingTeam = true;
    this.newTeamName = '';
    this.newTeamType = 'TOPOGRAPHIE';
    setTimeout(() => {
      if (this.teamNameInput?.nativeElement) {
        this.teamNameInput.nativeElement.focus();
      }
    }, 100);
  }
  
  cancelAddTeam(): void {
    this.isAddingTeam = false;
    this.newTeamName = '';
  }
  
  createTeam(): void {
    if (!this.canManageTeams) {
      Swal.fire({ icon: 'warning', title: 'Access Denied', text: 'You don\'t have permission to create teams' });
      return;
    }
    
    if (!this.newTeamName?.trim()) {
      Swal.fire({ icon: 'warning', title: 'Invalid', text: 'Team name cannot be empty', toast: true });
      return;
    }
    
    const request = { nom: this.newTeamName.trim(), type: this.newTeamType };
    this.isLoading = true;
    this.equipeDetailService.createEquipe(request).subscribe({
      next: (newEquipe) => {
        this.equipes = [{
          id: newEquipe.id,
          nom: newEquipe.nom,
          type: newEquipe.type,
          memberCount: 0,
          activitiesCount: 0
        }, ...this.equipes];
        this.filterTeams();
        this.cancelAddTeam();
        this.isLoading = false;
        Swal.fire({ icon: 'success', title: 'Success!', text: 'Team created successfully', toast: true });
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.isLoading = false;
        Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message || 'Failed to create team' });
      }
    });
  }
  
  startEditTeam(equipe: EquipeActivities, event: Event): void {
    if (!this.canManageTeams) {
      Swal.fire({ icon: 'warning', title: 'Access Denied', text: 'You don\'t have permission to edit teams' });
      return;
    }
    event.stopPropagation();
    this.editingTeamId = equipe.id;
    this.editingTeamName = equipe.nom;
    this.editingTeamType = equipe.type;
    setTimeout(() => {
      if (this.editTeamInput?.nativeElement) {
        this.editTeamInput.nativeElement.focus();
      }
    }, 100);
  }
  
  cancelEditTeam(): void {
    this.editingTeamId = null;
    this.editingTeamName = '';
  }
  
  saveEditTeam(equipe: EquipeActivities): void {
    if (!this.canManageTeams) {
      Swal.fire({ icon: 'warning', title: 'Access Denied', text: 'You don\'t have permission to edit teams' });
      return;
    }
    
    if (!this.editingTeamName?.trim()) {
      this.cancelEditTeam();
      return;
    }
    
    this.isLoading = true;
    this.equipeDetailService.updateEquipe(equipe.id, {
      nom: this.editingTeamName.trim(),
      type: this.editingTeamType
    }).subscribe({
      next: () => {
        const index = this.equipes.findIndex(e => e.id === equipe.id);
        if (index !== -1) {
          this.equipes[index].nom = this.editingTeamName.trim();
          this.equipes[index].type = this.editingTeamType;
          this.equipes = [...this.equipes];
        }
        if (this.selectedEquipe?.id === equipe.id) {
          this.selectedEquipe = { ...this.selectedEquipe, nom: this.editingTeamName.trim(), type: this.editingTeamType };
        }
        this.filterTeams();
        this.cancelEditTeam();
        this.isLoading = false;
        Swal.fire({ icon: 'success', title: 'Updated!', toast: true });
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.isLoading = false;
        Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message || 'Failed to update team' });
      }
    });
  }
  
  deleteTeam(equipe: EquipeActivities, event: Event): void {
    if (!this.canManageTeams) {
      Swal.fire({ icon: 'warning', title: 'Access Denied', text: 'You don\'t have permission to delete teams' });
      return;
    }
    
    event.stopPropagation();
    Swal.fire({
      title: 'Delete Team?',
      text: `Are you sure you want to delete "${equipe.nom}"?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      confirmButtonText: 'Yes, Delete',
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        this.isLoading = true;
        this.equipeDetailService.deleteEquipe(equipe.id).subscribe({
          next: () => {
            this.equipes = this.equipes.filter(e => e.id !== equipe.id);
            this.filterTeams();
            if (this.selectedEquipe?.id === equipe.id) this.clearSelection();
            this.isLoading = false;
            Swal.fire({ icon: 'success', title: 'Deleted!', toast: true });
            this.cdr.detectChanges();
          },
          error: (err) => {
            this.isLoading = false;
            Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message || 'Failed to delete team' });
          }
        });
      }
    });
  }
  
  // ==================== ACTIVITY MANAGEMENT ====================
  
  openAddActivityModal(): void {
    if (!this.canManageActivities) {
      Swal.fire({ icon: 'warning', title: 'Access Denied', text: 'You don\'t have permission to add activities' });
      return;
    }
    this.loadAvailableActives();
    this.activityForm.reset({
      activeId: null,
      dateDebut: new Date().toISOString().split('T')[0],
      dateFin: '',
      ordre: 1
    });
    this.showAddActivityModal = true;
    this.cdr.detectChanges();
  }
  
  closeAddActivityModal(): void {
    this.showAddActivityModal = false;
  }
  
  submitAddActivity(): void {
    if (!this.canManageActivities) {
      Swal.fire({ icon: 'warning', title: 'Access Denied', text: 'You don\'t have permission to add activities' });
      return;
    }
    
    if (this.activityForm.invalid || !this.selectedEquipe) {
      Swal.fire({ icon: 'warning', title: 'Warning', text: 'Please fill all required fields' });
      return;
    }
    
    if (!this.currentProject) {
      Swal.fire({
        icon: 'warning',
        title: 'No Active Project',
        text: 'No active project found for this mission. Please create a project first.',
        confirmButtonText: 'OK'
      });
      return;
    }
    
    const request: AssignActivityRequest = {
      equipeId: this.selectedEquipe.id,
      activeId: this.activityForm.value.activeId,
      missionId: this.currentMissionId,
      projectId: this.currentProject.id,
      dateDebut: this.activityForm.value.dateDebut,
      dateFin: this.activityForm.value.dateFin || null,
      dateStartReelle: null,
      dateFinReelle: null,
      ordre: this.activityForm.value.ordre || 1
    };
    
    this.isLoading = true;
    this.equipeDetailService.assignActivityToEquipe(request).subscribe({
      next: () => {
        Swal.fire({ icon: 'success', title: 'Success', text: 'Activity added successfully' });
        this.closeAddActivityModal();
        this.loadTeamActivities(this.selectedEquipe!.id);
        this.loadEquipes();
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error adding activity:', err);
        this.isLoading = false;
        Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message || 'Failed to add activity' });
      }
    });
  }
  
  removeActivity(activity: ActiveDetail): void {
    if (!this.canManageActivities) {
      Swal.fire({ icon: 'warning', title: 'Access Denied', text: 'You don\'t have permission to remove activities' });
      return;
    }
    
    if (!this.selectedEquipe) return;
    Swal.fire({
      title: 'Confirm Removal',
      text: `Are you sure you want to remove activity "${activity.codeActive}"?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      confirmButtonText: 'Yes, Remove',
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        this.isLoading = true;
        this.equipeDetailService.removeActivityFromEquipe(this.selectedEquipe!.id, activity.id, this.currentMissionId).subscribe({
          next: () => {
            Swal.fire('Removed', 'Activity removed successfully', 'success');
            this.loadTeamActivities(this.selectedEquipe!.id);
            this.loadEquipes();
            this.isLoading = false;
            this.cdr.detectChanges();
          },
          error: (err) => {
            console.error('Error removing activity:', err);
            this.isLoading = false;
            Swal.fire('Error', 'Failed to remove activity', 'error');
          }
        });
      }
    });
  }
  
  loadAvailableActives(): void {
    if (this.currentMissionId) {
      this.equipeDetailService.getAvailableActives(this.currentMissionId).subscribe({
        next: (data) => {
          this.availableActives = data;
          this.cdr.detectChanges();
        },
        error: (err) => console.error('Error loading actives:', err)
      });
    }
  }
  
  // ==================== EDIT REAL DATES ====================
  
  openEditRealDatesModal(activity: ActiveDetail): void {
    if (!this.canManageActivities) {
      Swal.fire({ icon: 'warning', title: 'Access Denied', text: 'You don\'t have permission to update dates' });
      return;
    }
    
    Swal.fire({
      title: 'Edit Actual Dates',
      html: `
        <div style="text-align: left;">
          <div style="margin-bottom: 15px;">
            <label style="display: block; font-weight: 600; margin-bottom: 5px; color: #1e293b;">Actual Start Date</label>
            <input type="date" id="dateStartReelle" class="swal2-input" value="${activity.dateStartReelle || ''}" style="width: 100%; padding: 10px 14px; border: 2px solid #e2e8f0; border-radius: 10px; font-size: 0.9rem;">
          </div>
          <div style="margin-bottom: 15px;">
            <label style="display: block; font-weight: 600; margin-bottom: 5px; color: #1e293b;">Actual End Date</label>
            <input type="date" id="dateFinReelle" class="swal2-input" value="${activity.dateFinReelle || ''}" style="width: 100%; padding: 10px 14px; border: 2px solid #e2e8f0; border-radius: 10px; font-size: 0.9rem;">
          </div>
          <div style="padding: 12px; background: #eff6ff; border-radius: 8px; border-left: 3px solid #3b82f6; margin-top: 8px;">
            <p style="margin: 4px 0; font-size: 0.75rem; color: #475569; display: flex; align-items: center; gap: 6px;">
              <span style="color: #3b82f6;">•</span> Setting start date → Status becomes "In Progress" or "Delayed"
            </p>
            <p style="margin: 4px 0; font-size: 0.75rem; color: #475569; display: flex; align-items: center; gap: 6px;">
              <span style="color: #3b82f6;">•</span> Setting end date → Status becomes "Completed"
            </p>
          </div>
        </div>
      `,
      showCancelButton: true,
      confirmButtonText: 'Save Dates',
      cancelButtonText: 'Cancel',
      confirmButtonColor: '#3b82f6',
      preConfirm: () => {
        const dateStartReelle = (document.getElementById('dateStartReelle') as HTMLInputElement).value;
        const dateFinReelle = (document.getElementById('dateFinReelle') as HTMLInputElement).value;
        
        return {
          dateStartReelle: dateStartReelle || null,
          dateFinReelle: dateFinReelle || null
        };
      }
    }).then((result) => {
      if (result.isConfirmed && result.value) {
        this.updateRealDatesAndStatus(
          activity.id,
          result.value.dateStartReelle,
          result.value.dateFinReelle
        );
      }
    });
  }
  
  updateRealDatesAndStatus(activeId: number, dateStartReelle: string | null, dateFinReelle: string | null): void {
    if (!this.canManageActivities) {
      Swal.fire({ icon: 'warning', title: 'Access Denied', text: 'You don\'t have permission to update dates' });
      return;
    }

    this.isLoading = true;
    
    this.equipeDetailService.updateRealDatesAndStatus(activeId, this.currentMissionId, dateStartReelle, dateFinReelle)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.isLoading = false;
          this.loadTeamActivities(this.selectedEquipe!.id);
          
          Swal.fire({
            icon: 'success',
            title: 'Updated!',
            text: 'Real dates updated and status auto-adjusted',
            toast: true,
            position: 'top-end',
            showConfirmButton: false,
            timer: 3000
          });
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Error updating real dates:', err);
          this.isLoading = false;
          Swal.fire({
            icon: 'error',
            title: 'Error',
            text: err.error?.message || 'Failed to update real dates'
          });
        }
      });
  }
  
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    window.removeEventListener('resize', () => this.checkScreenSize());
  }
}