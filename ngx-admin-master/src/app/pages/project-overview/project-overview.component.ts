import { Component, OnInit, OnDestroy, Input, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject, forkJoin } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProjectService, Project, Rapport, WilayaDTO, SiteResponse, 
  CoordonneeDTO, SiteRequest, RapportRequest, ProjectProgressStatsDTO 
} from '../../services/project/project.service';
import Swal from 'sweetalert2';
import { ChartConfiguration, ChartData } from 'chart.js';
import { Location } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { AlgeriaGeoService } from '../../services/geo/algeria-geo.service';

@Component({
  selector: 'app-project-overview',
  templateUrl: './project-overview.component.html',
  styleUrls: ['./project-overview.component.scss']
})
export class ProjectOverviewComponent implements OnInit, OnDestroy {
  @Input() projectIdInput: number | null = null;
  @Input() fromDashboard: boolean = false;
  
  project: Project | null = null;
  projectId: number = 0;
  isLoading: boolean = true;
  
  // Progress Stats
  projectProgressStats: ProjectProgressStatsDTO | null = null;
  
  // Modals
  showEditModal: boolean = false;
  showAddRapportModal: boolean = false;
  showSiteModal: boolean = false;
  showEditDatesModal: boolean = false; // ✅ NEW
  
  // Forms
  editForm: FormGroup;
  rapportForm: FormGroup;
  siteForm: FormGroup;
  
  editingRapportId: number | null = null;

  // ✅ NEW: Edit Dates properties
  editDateStartReelle: string | null = null;
  editDateFinReelle: string | null = null;

  // Chart
  performanceChartData: ChartData<'line'> = {
    labels: [],
    datasets: [
      {
        data: [],
        label: 'Progress %',
        borderColor: '#3b82f6',
        backgroundColor: 'rgba(59, 130, 246, 0.1)',
        fill: true,
        tension: 0.4,
        pointBackgroundColor: '#3b82f6',
        pointBorderColor: '#fff',
        pointRadius: 5,
        pointHoverRadius: 7
      }
    ]
  };
  
  performanceChartOptions: ChartConfiguration<'line'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { position: 'top', labels: { font: { size: 12 } } },
      tooltip: { mode: 'index', intersect: false }
    },
    scales: {
      y: { beginAtZero: true, max: 100, title: { display: true, text: 'Progress (%)' } },
      x: { title: { display: true, text: 'Date' } }
    }
  };

  // Site properties
  wilayas: WilayaDTO[] = [];
  currentSite: SiteResponse | null = null;
  coordinatesList: CoordonneeDTO[] = [];
  mapCenter: { lat: number; lng: number } = { lat: 36.7538, lng: 3.0588 };
  mapZoom: number = 6;
  
  private destroy$ = new Subject<void>();
  
  canManageProject: boolean = false;
  canManageReports: boolean = false;
  private geoDataLoaded: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private location: Location,
    private projectService: ProjectService,
    private fb: FormBuilder,
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
    private algeriaGeoService: AlgeriaGeoService  
  ) {
    this.editForm = this.fb.group({
      nom: ['', Validators.required],
      description: ['', Validators.required],
      budget: [0, [Validators.required, Validators.min(0)]],
      objectifVP: [0, [Validators.required, Validators.min(0)]],
      objectifDebut: ['', Validators.required],
      objectifFin: ['', Validators.required]
    });
    
    this.rapportForm = this.fb.group({
      titre: ['', Validators.required],
      date: [new Date().toISOString().split('T')[0], Validators.required],
      resume: ['', Validators.required]
    });
    
    this.siteForm = this.fb.group({
      numWilaya: [null, Validators.required],
      surface: [0, [Validators.required, Validators.min(0)]],
      coordinates: ['']
    });
  }
  
  ngOnInit(): void {
    this.initializePermissions();
    
    this.algeriaGeoService.loadGeoData().then(() => {
      this.geoDataLoaded = true;
      console.log('GeoJSON data loaded successfully');
    }).catch(error => {
      console.error('Failed to load GeoJSON data:', error);
    });
    
    if (this.projectIdInput) {
      this.projectId = this.projectIdInput;
      this.loadProject();
      this.loadWilayas();
      this.loadSite();
    } else {
      this.route.params.pipe(takeUntil(this.destroy$)).subscribe(params => {
        this.projectId = +params['id'];
        if (this.projectId) {
          this.loadProject();
          this.loadWilayas();
          this.loadSite();
        }
      });
    }
  }

  // ==================== LOAD METHODS ====================

  loadProject(): void {
    this.isLoading = true;
    
    forkJoin({
      project: this.projectService.getProjectById(this.projectId),
      progressStats: this.projectService.getProjectProgressStats(this.projectId)
    }).pipe(takeUntil(this.destroy$)).subscribe({
      next: ({ project, progressStats }) => {
        this.project = project;
        this.projectProgressStats = progressStats;
        
        if (this.project) {
          this.project.budgetDepense = this.project.budgetDepense || 0;
          this.project.vpAtteint = this.project.vpAtteint || 0;
          this.project.progression = this.project.progression || 0;
        }
        this.isLoading = false;
        this.cdr?.detectChanges();
      },
      error: (err) => {
        console.error('Error loading project:', err);
        Swal.fire({ icon: 'error', title: 'Error', text: 'Failed to load project data' });
        this.isLoading = false;
      }
    });
  }


  // Check if project is cancelled
isProjectCancelled(): boolean {
  if (!this.project) return false;
  // Check both annule flag and status
  return this.project.annule === true || this.getProjectStatus() === 'ANNULE';
}
// Cancel project
cancelProject(): void {
  if (!this.canManageProject) {
    Swal.fire({ icon: 'warning', title: 'Access Denied', text: 'You do not have permission to cancel this project' });
    return;
  }
  
  if (!this.project) return;
  
  // Check if already cancelled
  if (this.project.annule === true) {
    Swal.fire({ icon: 'info', title: 'Already Cancelled', text: 'This project is already cancelled.' });
    return;
  }
  
  // Check if completed
  if (this.getProjectStatus() === 'TERMINI') {
    Swal.fire({ icon: 'warning', title: 'Cannot Cancel', text: 'Cannot cancel a completed project.' });
    return;
  }
  
  Swal.fire({
    title: 'Cancel Project?',
    text: `Are you sure you want to cancel "${this.project.nom}"? This action cannot be undone.`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#d33',
    cancelButtonColor: '#3085d6',
    confirmButtonText: 'Yes, Cancel Project',
    cancelButtonText: 'No, Keep It'
  }).then((result) => {
    if (result.isConfirmed) {
      this.isLoading = true;
      
      this.projectService.cancelProject(this.projectId).subscribe({
        next: () => {
          Swal.fire({
            icon: 'success',
            title: 'Cancelled!',
            text: 'Project has been cancelled successfully',
            toast: true,
            position: 'top-end',
            showConfirmButton: false,
            timer: 3000
          });
          this.loadProject();
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error cancelling project:', err);
          this.isLoading = false;
          Swal.fire({
            icon: 'error',
            title: 'Error',
            text: err.error?.message || 'Failed to cancel project'
          });
        }
      });
    }
  });
}
  loadWilayas(): void {
    this.projectService.getAllWilayas().subscribe({
      next: (data) => {
        const enhancedWilayas = data.map(wilaya => {
          const geoWilaya = this.algeriaGeoService.getWilayaByName(wilaya.nom);
          return {
            ...wilaya,
            bounds: geoWilaya?.bounds || null
          };
        });
        this.wilayas = enhancedWilayas;
      },
      error: (err) => console.error('Error loading wilayas:', err)
    });
  }

  loadSite(): void {
    this.projectService.getSiteByProjectId(this.projectId).subscribe({
      next: (site) => {
        this.currentSite = site;
        if (site) {
          this.coordinatesList = site.coordonnees || [];
          if (site.wilaya) {
            this.mapCenter = {
              lat: site.wilaya.centerLatitude || 36.7538,
              lng: site.wilaya.centerLongitude || 3.0588
            };
          }
        }
      },
      error: (err) => console.error('Error loading site:', err)
    });
  }

  // ==================== PROJECT STATUS METHODS ====================

getProjectStatus(): string {
  if (!this.project) return 'PLANIFIER';
  
  // 1. ✅ FIRST: If annule is true, it's CANCELLED (highest priority)
  if (this.project.annule === true) {
    return 'ANNULE';
  }
  
  // 2. Use the status from the DTO (which is calculated from dates)
  if (this.project.status) {
    return this.project.status;
  }
  
  // 3. Fallback: calculate from dates
  const now = new Date().toISOString().split('T')[0];
  
  // If dateFinReelle is set, it's completed
  if (this.project.dateFinReelle) {
    return 'TERMINI';
  }
  
  // If dateStartReelle is set
  if (this.project.dateStartReelle) {
    if (this.project.dateStartReelle > now) {
      return 'ENATTENTE';
    }
    if (this.project.objectifFin && this.project.objectifFin < now) {
      return 'ENRETARD';
    }
    return 'ENCOURS';
  }
  
  // Check planned dates
  if (this.project.objectifDebut && this.project.objectifFin) {
    if (now > this.project.objectifFin) {
      return 'ENRETARD';
    }
    if (now >= this.project.objectifDebut) {
      return 'ENCOURS';
    }
    return 'PLANIFIER';
  }
  
  return 'PLANIFIER';
}

  getStatusLabel(status: string): string {
    const labels: { [key: string]: string } = {
      'PLANIFIER': 'Planned',
      'ENCOURS': 'In Progress',
      'ENATTENTE': 'On Hold',
      'ENRETARD': 'Delayed',
      'TERMINI': 'Completed',
      'ANNULE': 'Cancelled'
    };
    return labels[status] || status;
  }

  getStatusColor(status: string): string {
    const colors: { [key: string]: string } = {
      'PLANIFIER': '#64748b',
      'ENCOURS': '#3b82f6',
      'ENATTENTE': '#f59e0b',
      'ENRETARD': '#ef4444',
      'TERMINI': '#10b981',
      'ANNULE': '#94a3b8'
    };
    return colors[status] || '#64748b';
  }

  getStatusIcon(status: string): string {
    const icons: { [key: string]: string } = {
      'PLANIFIER': 'far fa-calendar-alt',
      'ENCOURS': 'fas fa-play-circle',
      'ENATTENTE': 'fas fa-pause-circle',
      'ENRETARD': 'fas fa-exclamation-circle',
      'TERMINI': 'fas fa-check-circle',
      'ANNULE': 'fas fa-ban'
    };
    return icons[status] || 'fas fa-circle';
  }

  getStatusBadgeClass(status: string): string {
    const classes: { [key: string]: string } = {
      'PLANIFIER': 'status-planned',
      'ENCOURS': 'status-progress',
      'ENATTENTE': 'status-pending',
      'ENRETARD': 'status-delayed',
      'TERMINI': 'status-completed',
      'ANNULE': 'status-cancelled'
    };
    return classes[status] || 'status-default';
  }

  // ==================== EDIT DATES MODAL ====================

  openEditDatesModal(): void {
    if (!this.canManageProject) {
      Swal.fire({ icon: 'warning', title: 'Access Denied', text: 'You do not have permission to edit dates' });
      return;
    }
    
    if (!this.isProjectEditable()) {
      Swal.fire({ 
        icon: 'warning', 
        title: 'Read Only', 
        text: 'This project is completed or cancelled. Dates cannot be edited.' 
      });
      return;
    }
    
    this.editDateStartReelle = this.project?.dateStartReelle || null;
    this.editDateFinReelle = this.project?.dateFinReelle || null;
    this.showEditDatesModal = true;
  }

  closeEditDatesModal(): void {
    this.showEditDatesModal = false;
    this.editDateStartReelle = null;
    this.editDateFinReelle = null;
  }

  saveRealDates(): void {
    this.isLoading = true;
    
    // Convert string dates to LocalDate format
    const startDate = this.editDateStartReelle ? this.editDateStartReelle : null;
    const endDate = this.editDateFinReelle ? this.editDateFinReelle : null;
    
    this.projectService.updateProjectRealDates(
      this.projectId,
      startDate,
      endDate
    ).subscribe({
      next: () => {
        Swal.fire({ 
          icon: 'success', 
          title: 'Success', 
          text: 'Actual dates updated successfully',
          toast: true,
          position: 'top-end',
          showConfirmButton: false,
          timer: 3000
        });
        this.closeEditDatesModal();
        this.loadProject();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error updating dates:', err);
        this.isLoading = false;
        Swal.fire({ 
          icon: 'error', 
          title: 'Error', 
          text: err.error?.message || 'Failed to update dates' 
        });
      }
    });
  }

  // ==================== PROJECT CRUD ====================

  openEditModal(): void {
    if (!this.canManageProject) {
      Swal.fire({ icon: 'warning', title: 'Access Denied', text: 'You do not have permission to edit this project' });
      return;
    }
    if (!this.project) return;
    this.editForm.patchValue({
      nom: this.project.nom,
      description: this.project.description,
      budget: this.project.budget,
      objectifVP: this.project.objectifVP,
      objectifDebut: this.project.objectifDebut,
      objectifFin: this.project.objectifFin
    });
    this.showEditModal = true;
  }
  
  closeEditModal(): void {
    this.showEditModal = false;
    this.editForm.reset();
  }
  
  saveProject(): void {
    if (this.editForm.invalid) return;
    
    this.projectService.updateProject(this.projectId, this.editForm.value).subscribe({
      next: (updated: Project) => {
        Swal.fire({ icon: 'success', title: 'Success', text: 'Project updated successfully' });
        this.closeEditModal();
        this.loadProject();
      },
      error: (err) => {
        console.error('Error updating project:', err);
        Swal.fire({ icon: 'error', title: 'Error', text: 'Failed to update project' });
      }
    });
  }

  // ==================== RAPPORT MANAGEMENT ====================

  openAddRapportModal(): void {
    if (!this.canManageReports) {
      Swal.fire({ icon: 'warning', title: 'Access Denied', text: 'You do not have permission to add reports' });
      return;
    }
    this.rapportForm.reset({
      titre: '',
      date: new Date().toISOString().split('T')[0],
      resume: ''
    });
    this.showAddRapportModal = true;
  }

  closeAddRapportModal(): void {
    this.showAddRapportModal = false;
    this.rapportForm.reset();
    this.editingRapportId = null;
  }

  submitRapport(): void {
    if (this.rapportForm.invalid) {
      Swal.fire({ icon: 'warning', title: 'Warning', text: 'Please fill all required fields' });
      return;
    }

    const request: RapportRequest = {
      titre: this.rapportForm.value.titre,
      date: this.rapportForm.value.date,
      resume: this.rapportForm.value.resume
    };

    if (this.editingRapportId) {
      this.projectService.updateRapport(this.editingRapportId, request).subscribe({
        next: () => {
          Swal.fire({ icon: 'success', title: 'Success', text: 'Report updated successfully' });
          this.closeAddRapportModal();
          this.editingRapportId = null;
          this.loadProject();
        },
        error: (err) => {
          console.error('Error updating rapport:', err);
          Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message || 'Failed to update report' });
        }
      });
    } else {
      this.projectService.addRapport(this.projectId, request).subscribe({
        next: () => {
          Swal.fire({ icon: 'success', title: 'Success', text: 'Report added successfully' });
          this.closeAddRapportModal();
          this.loadProject();
        },
        error: (err) => {
          console.error('Error adding rapport:', err);
          Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message || 'Failed to add report' });
        }
      });
    }
  }

  openEditRapportModal(rapport: Rapport): void {
    if (!this.canManageReports) {
      Swal.fire({ icon: 'warning', title: 'Access Denied', text: 'You do not have permission to edit reports' });
      return;
    }
    this.rapportForm.patchValue({
      titre: rapport.titre,
      date: rapport.date,
      resume: rapport.resume
    });
    this.editingRapportId = rapport.id;
    this.showAddRapportModal = true;
  }

  deleteRapport(rapport: Rapport, event: Event): void {
    if (!this.canManageReports) {
      Swal.fire({ icon: 'warning', title: 'Access Denied', text: 'You do not have permission to delete reports' });
      return;
    }
    event.stopPropagation();
    
    Swal.fire({
      title: 'Delete Report?',
      text: `Are you sure you want to delete "${rapport.titre}"?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Yes, delete it!',
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        this.projectService.deleteRapport(rapport.id).subscribe({
          next: () => {
            Swal.fire({ icon: 'success', title: 'Deleted!', text: 'Report deleted successfully' });
            this.loadProject();
          },
          error: (err) => {
            console.error('Error deleting rapport:', err);
            Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message || 'Failed to delete report' });
          }
        });
      }
    });
  }

  // ==================== SITE MANAGEMENT ====================

  openSiteModal(): void {
    if (!this.canManageProject) {
      Swal.fire({ icon: 'warning', title: 'Access Denied', text: 'You do not have permission to edit site information' });
      return;
    }
    if (this.currentSite) {
      this.siteForm.patchValue({
        numWilaya: this.currentSite.wilaya?.numWilaya,
        surface: this.currentSite.surface
      });
      this.coordinatesList = [...(this.currentSite.coordonnees || [])];
    } else {
      this.siteForm.reset({ surface: 0 });
      this.coordinatesList = [];
    }
    this.showSiteModal = true;
  }

  closeSiteModal(): void {
    this.showSiteModal = false;
    this.siteForm.reset();
  }

  saveSite(): void {
    if (this.siteForm.invalid) {
      Swal.fire({ icon: 'warning', title: 'Warning', text: 'Please select a wilaya' });
      return;
    }

    const selectedWilayaNum = this.siteForm.get('numWilaya')?.value;
    const selectedWilaya = this.wilayas.find(w => w.numWilaya === selectedWilayaNum);
    
    if (!selectedWilaya) {
      Swal.fire({ icon: 'error', title: 'Error', text: 'Wilaya not found' });
      return;
    }

    // Validate all coordinates before saving
    for (const coord of this.coordinatesList) {
      const isValid = this.algeriaGeoService.isPointInWilaya(coord.latitude, coord.longitude, selectedWilaya.nom);
      if (!isValid) {
        const actualWilaya = this.algeriaGeoService.findWilayaByPoint(coord.latitude, coord.longitude);
        Swal.fire({
          icon: 'error',
          title: 'Invalid Coordinates',
          html: `Point ${coord.ordre} (${coord.latitude.toFixed(6)}, ${coord.longitude.toFixed(6)})<br/>
                 is not within <strong>${selectedWilaya.nom}</strong>.<br/>
                 ${actualWilaya ? `This point belongs to <strong>${actualWilaya.name}</strong>.` : ''}<br/><br/>
                 Please correct the coordinates or change the wilaya.`
        });
        return;
      }
    }

    const request: SiteRequest = {
      projectId: this.projectId,
      numWilaya: this.siteForm.value.numWilaya,
      surface: this.siteForm.value.surface,
      coordonnees: this.coordinatesList
    };

    this.projectService.createOrUpdateSite(request).subscribe({
      next: (site) => {
        Swal.fire({ icon: 'success', title: 'Success', text: 'Site information saved successfully' });
        this.closeSiteModal();
        this.loadSite();
        this.loadProject();
      },
      error: (err) => {
        console.error('Error saving site:', err);
        Swal.fire({ icon: 'error', title: 'Error', text: 'Failed to save site information' });
      }
    });
  }

  addCoordinate(): void {
    // ... (keep existing addCoordinate method)
  }

  removeCoordinate(index: number): void {
    // ... (keep existing removeCoordinate method)
  }

  // ==================== ACTIVITY STATS HELPERS ====================

  getActivityCompletionPercentage(): number {
    if (!this.projectProgressStats || this.projectProgressStats.totalActivities === 0) return 0;
    return Math.round((this.projectProgressStats.completedActivities / this.projectProgressStats.totalActivities) * 100);
  }
  
  getActivityCompletionRate(): number {
    if (!this.projectProgressStats || this.projectProgressStats.totalActivities === 0) return 0;
    return (this.projectProgressStats.completedActivities / this.projectProgressStats.totalActivities) * 100;
  }
  
  getActivityStatusClass(status: string): string {
    switch (status) {
      case 'TERMINI': return 'status-completed';
      case 'ENCOURS': return 'status-progress';
      case 'ENRETARD': return 'status-delayed';
      case 'ENATTENTE': return 'status-pending';
      case 'ANNULE': return 'status-cancelled';
      default: return 'status-pending';
    }
  }
  
  getActivityStatusLabel(status: string): string {
    switch (status) {
      case 'TERMINI': return 'Completed';
      case 'ENCOURS': return 'In Progress';
      case 'ENRETARD': return 'Delayed';
      case 'ENATTENTE': return 'On Hold';
      case 'ANNULE': return 'Cancelled';
      default: return 'Planned';
    }
  }

  // ==================== PERMISSIONS ====================

  initializePermissions(): void {
    const userRoles = this.authService.getCurrentUser()?.roles || [];
    const hasChefMissionRole = userRoles.includes('CHEF_MISSION');
    const hasAdminRole = userRoles.includes('ADMIN');
    
    this.canManageProject = hasChefMissionRole || hasAdminRole;
    this.canManageReports = hasChefMissionRole || hasAdminRole;
    
    console.log('[DEBUG] User roles:', userRoles);
    console.log('[DEBUG] Can manage project:', this.canManageProject);
    console.log('[DEBUG] Can manage reports:', this.canManageReports);
  }

  // Check if project is editable (not completed or cancelled)
isProjectEditable(): boolean {
  if (!this.project) return false;
  
  // If cancelled, not editable
  if (this.project.annule === true) {
    return false;
  }
  
  const status = this.getProjectStatus();
  // Not editable if completed or cancelled
  return status !== 'TERMINI' && status !== 'ANNULE';
}

  isSiteEditable(): boolean {
    return this.isProjectEditable() && this.canManageProject;
  }

  isReportsEditable(): boolean {
    return this.isProjectEditable() && this.canManageReports;
  }

  // ==================== HELPER METHODS ====================

  goBack(): void {
    this.location.back();
  }

  formatDate(date: string): string {
    if (!date) return 'N/A';
    try {
      return new Date(date).toLocaleDateString('en-US', { 
        day: '2-digit', 
        month: 'short', 
        year: 'numeric' 
      });
    } catch {
      return 'N/A';
    }
  }

  formatDateShort(date: string): string {
    if (!date) return '';
    try {
      return new Date(date).toLocaleDateString('en-US', { 
        day: 'numeric', 
        month: 'short' 
      });
    } catch {
      return '';
    }
  }

  formatCurrency(value: number): string {
    if (!value || isNaN(value)) return '0';
    return new Intl.NumberFormat('en-US', { 
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    }).format(value);
  }

  getProgressColor(progress: number): string {
    if (progress >= 80) return '#10b981';
    if (progress >= 50) return '#f59e0b';
    if (progress >= 25) return '#3b82f6';
    return '#ef4444';
  }

  getRemainingDays(): number {
    if (!this.project?.objectifFin) return 0;
    try {
      const endDate = new Date(this.project.objectifFin);
      const today = new Date();
      const diffTime = endDate.getTime() - today.getTime();
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      return diffDays > 0 ? diffDays : 0;
    } catch {
      return 0;
    }
  }

  getRemainingPercent(): number {
    if (!this.project?.objectifDebut || !this.project?.objectifFin) return 0;
    try {
      const startDate = new Date(this.project.objectifDebut);
      const endDate = new Date(this.project.objectifFin);
      const today = new Date();
      
      const totalDays = (endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24);
      const remainingDays = this.getRemainingDays();
      const passedDays = totalDays - remainingDays;
      
      if (totalDays <= 0 || isNaN(totalDays)) return 0;
      return Math.min(100, Math.max(0, (passedDays / totalDays) * 100));
    } catch {
      return 0;
    }
  }

  // ==================== MAP HELPERS ====================

  private getBounds(coordinates: CoordonneeDTO[]): { minLat: number; maxLat: number; minLng: number; maxLng: number } {
    // ... (keep existing method)
    if (!coordinates || coordinates.length === 0) {
      return { minLat: 0, maxLat: 0, minLng: 0, maxLng: 0 };
    }
    
    let minLat = Infinity, maxLat = -Infinity, minLng = Infinity, maxLng = -Infinity;
    coordinates.forEach(coord => {
      minLat = Math.min(minLat, coord.latitude);
      maxLat = Math.max(maxLat, coord.latitude);
      minLng = Math.min(minLng, coord.longitude);
      maxLng = Math.max(maxLng, coord.longitude);
    });
    return { minLat, maxLat, minLng, maxLng };
  }

  getPolygonPoints(): string {
    // ... (keep existing method)
    if (!this.currentSite?.coordonnees || this.currentSite.coordonnees.length === 0) {
      return '';
    }
    
    const width = 400, height = 300;
    const bounds = this.getBounds(this.currentSite.coordonnees);
    
    if (bounds.minLat === Infinity || bounds.minLng === Infinity) {
      return '';
    }
    
    const latRange = bounds.maxLat - bounds.minLat;
    const lngRange = bounds.maxLng - bounds.minLng;
    const latScale = latRange === 0 ? 1 : latRange;
    const lngScale = lngRange === 0 ? 1 : lngRange;
    
    const points = this.currentSite.coordonnees.map(coord => {
      const x = ((coord.longitude - bounds.minLng) / lngScale) * (width - 40) + 20;
      const y = height - (((coord.latitude - bounds.minLat) / latScale) * (height - 40) + 20);
      return `${x},${y}`;
    });
    
    return points.join(' ');
  }

  getMapX(lng: number): number {
    // ... (keep existing method)
    if (!this.currentSite?.coordonnees || this.currentSite.coordonnees.length === 0) {
      return 0;
    }
    const bounds = this.getBounds(this.currentSite.coordonnees);
    if (bounds.minLng === Infinity) return 0;
    const width = 400;
    const lngRange = bounds.maxLng - bounds.minLng;
    const lngScale = lngRange === 0 ? 1 : lngRange;
    return ((lng - bounds.minLng) / lngScale) * (width - 40) + 20;
  }

  getMapY(lat: number): number {
    // ... (keep existing method)
    if (!this.currentSite?.coordonnees || this.currentSite.coordonnees.length === 0) {
      return 0;
    }
    const bounds = this.getBounds(this.currentSite.coordonnees);
    if (bounds.minLat === Infinity) return 0;
    const height = 300;
    const latRange = bounds.maxLat - bounds.minLat;
    const latScale = latRange === 0 ? 1 : latRange;
    return height - (((lat - bounds.minLat) / latScale) * (height - 40) + 20);
  }

  onWilayaChange(): void {
    // ... (keep existing method)
    const selectedWilaya = this.wilayas.find(w => w.numWilaya === this.siteForm.value.numWilaya);
    if (selectedWilaya) {
      this.mapCenter = {
        lat: selectedWilaya.centerLatitude || 36.7538,
        lng: selectedWilaya.centerLongitude || 3.0588
      };
      this.mapZoom = 8;
    }
  }

  getEquipeIcon(type: string): string {
    // ... (keep existing method)
    const icons: { [key: string]: string } = {
      'TOPOGRAPHIE': 'fas fa-map',
      'LAYONNAGE': 'fas fa-draw-polygon',
      'ENERGISREMENT': 'fas fa-bolt',
      'POSE': 'fas fa-tools',
      'RAMASSAGE': 'fas fa-truck'
    };
    return icons[type] || 'fas fa-users';
  }

  getEquipeIconClass(type: string): string {
    return type?.toLowerCase() || '';
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}