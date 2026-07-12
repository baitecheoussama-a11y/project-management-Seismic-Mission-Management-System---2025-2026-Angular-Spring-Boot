import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { EquipeDetailService, EquipeDetail, EmployeSimple, AssignActivityRequest } from '../../../services/team/equipe-detail.service';
import { MissionService, EmployeDTO } from '../../../services/mission/mission.service';
import { RapportService, RapportResponse, RendementResponse, RapportRequest, RendementRequest } from '../../../services/rapport/rapport.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ChartConfiguration, ChartData } from 'chart.js';
import Swal from 'sweetalert2';
import { EtatAvancementService } from '../../../services/etat-avancement/etat-avancement.service';
 
@Component({
  selector: 'app-equipe-detail',
  templateUrl: './equipe-detail.component.html',
  styleUrls: ['./equipe-detail.component.scss']
})
export class EquipeDetailComponent implements OnInit, OnDestroy {
  
  equipeDetail: EquipeDetail | null = null;
  missionId: number = 0;
  equipeId: number = 0;
  isLoading: boolean = true;
  selectedTab: string = 'members';
  
  memberSearchTerm: string = '';

  
  // Store rapports and rendements separately
  teamRapports: RapportResponse[] = [];
  teamRendements: RendementResponse[] = [];
  
  // Modals
  showAddMemberModal: boolean = false;
  showAddActivityModal: boolean = false;
  showEditEquipeModal: boolean = false;
  showRapportDetailModal: boolean = false;
  showAddRendementModal: boolean = false;
  
  selectedRapport: RapportResponse | null = null;
  selectedRapportRendements: RendementResponse[] = [];
  editingRendementId: number | null = null;
  
  availableEmployees: EmployeDTO[] = [];
  selectedEmployees: EmployeDTO[] = [];
  
// Add these properties with your other properties
activityProgressMap: Map<number, any> = new Map();

// Status options (display only)
statusOptions = [
  { value: 'PLANIFIER', label: 'Planned', color: '#64748b', icon: 'far fa-calendar-alt' },
  { value: 'ENCOURS', label: 'In Progress', color: '#3b82f6', icon: 'fas fa-play-circle' },
  { value: 'ENATTENTE', label: 'On Hold', color: '#f59e0b', icon: 'fas fa-pause-circle' },
  { value: 'ENRETARD', label: 'Delayed', color: '#ef4444', icon: 'fas fa-exclamation-circle' },
  { value: 'TERMINI', label: 'Completed', color: '#10b981', icon: 'fas fa-check-circle' },
  { value: 'ANNULE', label: 'Cancelled', color: '#94a3b8', icon: 'fas fa-ban' }
];

  
  // Activity Form
  activityForm: FormGroup;
  availableActives: any[] = [];
  
  // Edit Equipe Form
  equipeForm: FormGroup;
  
  // Rapport Form
  rapportForm: FormGroup;
  showAddRapportModal: boolean = false;
  
  // Rendement Form
  rendementForm: FormGroup;
  selectedRapportId: number | null = null;
selectedActivityId: number | null = null;
teamActivities: any[] = []; // قائمة الأنشطة المتاحة للفريق
showAddProductivityModal: boolean = false;

// أضف مع باقي المتغيرات
selectedActivityForRendement: any = null;
showActivityDetailModal: boolean = false;

// Filters for Performance (Productivity Records)
productivitySearchTerm: string = '';
productivityTypeFilter: string = 'all';
productivitySortBy: string = 'date';
productivitySortOrder: string = 'desc';
filteredProductivityRecords: RendementResponse[] = [];

// Filters for Members
memberTypeFilter: string = 'all';
memberSortBy: string = 'name';
memberSortOrder: string = 'asc';

// Filters for Activities
activitySearchTerm: string = '';
activityStatusFilter: string = 'all';
activitySortBy: string = 'code';
activitySortOrder: string = 'asc';
filteredActivities: any[] = [];

// Filters for Reports (already exists, but ensure it filters by team productivity)
// Add pagination for filtered productivity records
productivityCurrentPage: number = 1;
productivityPageSize: number = 10;
productivityTotalPages: number = 1;
paginatedProductivityRecords: RendementResponse[] = [];


  // Chart data
  performanceChartData: ChartData<'line'> = {
    labels: [],
    datasets: [
      {
        data: [],
        label: 'Productivity %',
        borderColor: '#f59e0b',
        backgroundColor: 'rgba(245, 158, 11, 0.1)',
        fill: true,
        tension: 0.4,
        pointBackgroundColor: '#f59e0b',
        pointBorderColor: '#fff',
        pointRadius: 4,
        pointHoverRadius: 6
      }
    ]
  };
  
  performanceChartOptions: ChartConfiguration<'line'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'top',
        labels: { font: { size: 12 } }
      },
      tooltip: { mode: 'index', intersect: false }
    },
    scales: {
      y: {
        beginAtZero: true,
        max: 100,
        title: { display: true, text: 'Percentage (%)' }
      },
      x: {
        title: { display: true, text: 'Date' }
      }
    }
  };
  
  private destroy$ = new Subject<void>();
  

  // Members Pagination
Math = Math;
filteredMembers: EmployeSimple[] = [];
membersCurrentPage: number = 1;
membersPageSize: number = 8;
membersTotalPages: number = 1;
paginatedMembers: EmployeSimple[] = [];

// Activities Pagination
activitiesCurrentPage: number = 1;
activitiesPageSize: number = 3;
activitiesTotalPages: number = 1;
paginatedActivities: any[] = [];

// Reports Pagination
reportSearchTerm: string = '';
filteredReports: RapportResponse[] = [];
reportsCurrentPage: number = 1;
reportsPageSize: number = 8;
reportsTotalPages: number = 1;
paginatedReports: RapportResponse[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private equipeDetailService: EquipeDetailService,
    private missionService: MissionService,
    private rapportService: RapportService,
      private etatAvancementService: EtatAvancementService,
    private fb: FormBuilder
  ) {
    this.activityForm = this.fb.group({
      activeId: [null, Validators.required],
      dateDebut: [new Date().toISOString().split('T')[0], Validators.required],
      dateFin: ['']
    });
    
    this.equipeForm = this.fb.group({
      nom: ['', Validators.required],
      type: ['', Validators.required]
    });
    
    this.rapportForm = this.fb.group({
      titre: ['', Validators.required],
      date: [new Date().toISOString().split('T')[0], Validators.required],
      resume: ['', Validators.required]
    });
    
    this.rendementForm = this.fb.group({
      heureDebut: ['08:00', Validators.required],
      heureFin: ['17:00', Validators.required],
      valeurRendement: [0, [Validators.required, Validators.min(0)]],
      uniteRendement: ['m²', Validators.required],
      date: [new Date().toISOString().split('T')[0], Validators.required]
    });
  }
  
  ngOnInit(): void {
    this.route.params.pipe(takeUntil(this.destroy$)).subscribe(params => {
      this.equipeId = +params['equipeId'];
      this.route.queryParams.pipe(takeUntil(this.destroy$)).subscribe(queryParams => {
        this.missionId = +queryParams['missionId'];
        if (this.equipeId && this.missionId) {
          this.loadEquipeDetail();
          this.loadAvailableActives();
          this.loadRapports();
        }
      });
    });
  }
  loadEquipeDetail(): void {
  this.isLoading = true;
  this.equipeDetailService.getEquipeDetail(this.equipeId, this.missionId)
    .pipe(takeUntil(this.destroy$))
    .subscribe({
      next: (data: EquipeDetail) => {
        this.equipeDetail = data;
        this.filteredMembers = [...data.membres];
        this.updateMembersPagination();
        this.updateActivitiesPagination();
        // Load progress for each activity
        this.loadProgressForActivities();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading equipe detail:', err);
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'Failed to load team data'
        });
        this.isLoading = false;
      }
    });
}
  loadRapports(): void {
  // Get rapports for current project
  this.rapportService.getRapportsForCurrentProject(this.missionId)
    .pipe(takeUntil(this.destroy$))
    .subscribe({
      next: (rapports: RapportResponse[]) => {
        this.teamRapports = rapports;
        this.filteredReports = [...rapports];
        this.updateReportsPagination();
        
        // Load rendements directly by equipe (more reliable)
        this.loadRendementsByEquipe();
      },
      error: (err) => console.error('Error loading rapports:', err)
    });
}
  
  loadRendementsForRapport(rapportId: number): void {
    this.rapportService.getRendementsByRapport(rapportId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.selectedRapportRendements = data;
        },
        error: (err) => {
          console.error('Error loading rendements:', err);
          this.selectedRapportRendements = [];
        }
      });
  }
  
updateStatistics(): void {
  if (this.equipeDetail && this.equipeDetail.statistiques) {
    this.equipeDetail.statistiques.totalRapports = this.teamRapports.length;
    // totalRendements is already updated in loadRendementsByEquipe
    // totalHeuresTravaillees is already updated in loadRendementsByEquipe
    // moyenneRendement is already updated in loadRendementsByEquipe
  }
}
getRapportById(rapportId: number): RapportResponse | undefined {
  return this.teamRapports.find(r => r.id === rapportId);
}

  initChart(): void {
  if (this.teamRendements.length === 0) {
    // Set empty chart data
    this.performanceChartData = {
      labels: [],
      datasets: [{
        data: [],
        label: 'Productivity %',
        borderColor: '#f59e0b',
        backgroundColor: 'rgba(245, 158, 11, 0.1)',
        fill: true,
        tension: 0.4
      }]
    };
    return;
  }
  
  // Group rendements by date
  const rendementsByDate = new Map<string, number[]>();
  
  this.teamRendements.forEach((rend: RendementResponse) => {
    const date = rend.date;
    if (!rendementsByDate.has(date)) {
      rendementsByDate.set(date, []);
    }
    rendementsByDate.get(date)!.push(rend.valeurRendement);
  });
  
  // Sort dates chronologically
  const sortedDates = Array.from(rendementsByDate.keys()).sort();
  
  // Calculate average for each date
  const avgRendements = sortedDates.map(date => {
    const values = rendementsByDate.get(date)!;
    return values.reduce((a, b) => a + b, 0) / values.length;
  });
  
  // Format labels
  this.performanceChartData.labels = sortedDates.map(date => {
    const d = new Date(date);
    return `${d.getDate()}/${d.getMonth() + 1}`;
  });
  
  this.performanceChartData.datasets[0].data = avgRendements;
}
  
  loadAvailableActives(): void {
    this.equipeDetailService.getAvailableActives(this.missionId).subscribe({
      next: (data) => {
        this.availableActives = data;
      },
      error: (err) => console.error('Error loading actives:', err)
    });
  }
  
  // ============ RAPPORT METHODS ============
  
  openAddRapportModal(): void {
    this.rapportForm.reset({
      titre: '',
      date: new Date().toISOString().split('T')[0],
      resume: ''
    });
    this.showAddRapportModal = true;
  }
  
  closeAddRapportModal(): void {
    this.showAddRapportModal = false;
  }
  
  submitAddRapport(): void {
    if (this.rapportForm.invalid) {
      Swal.fire({ icon: 'warning', title: 'Warning', text: 'Please fill all required fields' });
      return;
    }
    
    const request: RapportRequest = this.rapportForm.value;
    
    this.rapportService.addRapportToCurrentProject(this.missionId, request)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          Swal.fire({ icon: 'success', title: 'Success', text: 'Report added successfully' });
          this.closeAddRapportModal();
          this.loadRapports();
        },
        error: (err) => {
          console.error('Error adding rapport:', err);
          Swal.fire({ icon: 'error', title: 'Error', text: 'Failed to add report' });
        }
      });
  }
  
  viewRapportDetail(rapport: RapportResponse): void {
    this.selectedRapport = rapport;
    this.loadRendementsForRapport(rapport.id);
    this.showRapportDetailModal = true;
  }
  
  closeRapportDetailModal(): void {
    this.showRapportDetailModal = false;
    this.selectedRapport = null;
    this.selectedRapportRendements = [];
    this.showAddRendementModal = false;
    this.editingRendementId = null;
  }
  
  // ============ RENDEMENT METHODS ============
  
  openAddRendementModal(): void {
    this.rendementForm.reset({
      heureDebut: '08:00',
      heureFin: '17:00',
      valeurRendement: 0,
      uniteRendement: 'm²',
      date: new Date().toISOString().split('T')[0]
    });
    this.editingRendementId = null;
    this.showAddRendementModal = true;
  }
  
  openEditRendementModal(rendement: RendementResponse): void {
    this.editingRendementId = rendement.id;
    this.rendementForm.patchValue({
      heureDebut: rendement.heureDebut,
      heureFin: rendement.heureFin,
      valeurRendement: rendement.valeurRendement,
      uniteRendement: rendement.uniteRendement,
      date: rendement.date
    });
    this.showAddRendementModal = true;
  }
  
  closeAddRendementModal(): void {
    this.showAddRendementModal = false;
    this.editingRendementId = null;
    this.rendementForm.reset();
  }
  
submitRendement(): void {
  if (this.rendementForm.invalid || !this.selectedRapport) {
    Swal.fire({ icon: 'warning', title: 'Warning', text: 'Please fill all required fields' });
    return;
  }
  
  const formValue = this.rendementForm.value;
  
  // ✅ If you have an activity selector in this modal, you need to include activeId
  const request: RendementRequest = {
    heureDebut: formValue.heureDebut,
    heureFin: formValue.heureFin,
    valeurRendement: formValue.valeurRendement,
    uniteRendement: formValue.uniteRendement,
    date: formValue.date,
    activeId: formValue.activityId || this.selectedActivityId  // Include if available
  };
  
  if (this.editingRendementId) {
    this.rapportService.updateRendement(this.editingRendementId, request)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          Swal.fire({ icon: 'success', title: 'Success', text: 'Productivity record updated successfully' });
          this.closeAddRendementModal();
          this.loadRendementsForRapport(this.selectedRapport!.id);
          this.loadRapports();
        },
        error: (err) => {
          console.error('Error updating rendement:', err);
          Swal.fire({ icon: 'error', title: 'Error', text: 'Failed to update productivity record' });
        }
      });
  } else {
    // ✅ For adding rendement to a specific rapport with equipe
    this.rapportService.addRendementToRapportWithEquipe(
      this.selectedRapport.id,
      this.equipeId,
      request
    ).pipe(takeUntil(this.destroy$))
    .subscribe({
      next: () => {
        Swal.fire({ icon: 'success', title: 'Success', text: 'Productivity record added successfully' });
        this.closeAddRendementModal();
        this.loadRendementsForRapport(this.selectedRapport!.id);
        this.loadRapports();
      },
      error: (err) => {
        console.error('Error adding rendement:', err);
        Swal.fire({ icon: 'error', title: 'Error', text: 'Failed to add productivity record' });
      }
    });
  }
}
// Add this method to your EquipeDetailComponent
viewEmployeeProfile(employeeId: number, firstName: string, lastName: string): void {
  const employeeName = `${firstName} ${lastName}`;
  this.router.navigate(['/pages/employe-account'], {
    queryParams: {
      employeId: employeeId,
      employeName: employeeName
    }
  });
}
  
  deleteRendement(rendement: RendementResponse, event: Event): void {
    event.stopPropagation();
    
    Swal.fire({
      title: 'Delete Record?',
      text: `Are you sure you want to delete this productivity record?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      confirmButtonText: 'Yes, Delete',
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        this.rapportService.deleteRendement(rendement.id)
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: () => {
              Swal.fire({ icon: 'success', title: 'Deleted', text: 'Productivity record deleted successfully' });
              this.loadRendementsForRapport(this.selectedRapport!.id);
              this.loadRapports(); // Reload to update statistics
            },
            error: (err) => {
              console.error('Error deleting rendement:', err);
              Swal.fire({ icon: 'error', title: 'Error', text: 'Failed to delete productivity record' });
            }
          });
      }
    });
  }
  
  calculateTotalProductivity(): number {
    return this.selectedRapportRendements.reduce((sum, r) => sum + (r.valeurRendement || 0), 0);
  }
  
  calculateTotalHours(): number {
    return this.selectedRapportRendements.reduce((sum, r) => sum + (r.dureeHeures || 0), 0);
  }
  
  
  
  // ============ MEMBERS MANAGEMENT ============
  
  openAddMemberModal(): void {
    this.missionService.getAllEmployeesWithStatus().subscribe({
      next: (employees) => {
        const currentMemberIds = this.equipeDetail?.membres.map(m => m.id) || [];
        this.availableEmployees = employees.filter(e => !currentMemberIds.includes(e.id));
        this.selectedEmployees = [];
        this.showAddMemberModal = true;
      },
      error: (err) => console.error('Error loading employees:', err)
    });
  }
  
  closeAddMemberModal(): void {
    this.showAddMemberModal = false;
    this.selectedEmployees = [];
  }
  
  toggleEmployeeSelection(employee: EmployeDTO): void {
    const index = this.selectedEmployees.findIndex(e => e.id === employee.id);
    if (index === -1) {
      this.selectedEmployees.push(employee);
    } else {
      this.selectedEmployees.splice(index, 1);
    }
  }
  
  isEmployeeSelected(employee: EmployeDTO): boolean {
    return this.selectedEmployees.some(e => e.id === employee.id);
  }
  
  submitAddMembers(): void {
    if (this.selectedEmployees.length === 0) {
      Swal.fire({ icon: 'warning', title: 'Warning', text: 'Please select at least one employee' });
      return;
    }
    
    const employeIds = this.selectedEmployees.map(e => e.id);
    
    this.equipeDetailService.addMembersToEquipe(this.equipeId, employeIds, this.missionId).subscribe({
      next: () => {
        Swal.fire({ icon: 'success', title: 'Success', text: 'Members added successfully' });
        this.closeAddMemberModal();
        this.loadEquipeDetail();
      },
      error: (err) => {
        console.error('Error adding members:', err);
        Swal.fire({ icon: 'error', title: 'Error', text: 'Failed to add members' });
      }
    });
  }
  
  removeMember(memberId: number, memberName: string): void {
    Swal.fire({
      title: 'Confirm Removal',
      text: `Are you sure you want to remove ${memberName} from this team?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Yes, Remove',
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        this.equipeDetailService.removeMemberFromEquipe(this.equipeId, memberId, this.missionId).subscribe({
          next: () => {
            Swal.fire('Removed', 'Member has been removed successfully', 'success');
            this.loadEquipeDetail();
          },
          error: (err) => {
            console.error('Error removing member:', err);
            Swal.fire('Error', 'Failed to remove member', 'error');
          }
        });
      }
    });
  }
  
  // ============ ACTIVITIES MANAGEMENT ============
  
  openAddActivityModal(): void {
    this.activityForm.reset({
      activeId: null,
      dateDebut: new Date().toISOString().split('T')[0],
      dateFin: ''
    });
    this.showAddActivityModal = true;
  }
  
  closeAddActivityModal(): void {
    this.showAddActivityModal = false;
  }
  
  submitAddActivity(): void {
    if (this.activityForm.invalid) {
      Swal.fire({ icon: 'warning', title: 'Warning', text: 'Please fill all required fields' });
      return;
    }
    
    const request: AssignActivityRequest = {
      equipeId: this.equipeId,
      activeId: this.activityForm.value.activeId,
      missionId: this.missionId,
      dateDebut: this.activityForm.value.dateDebut,
      dateFin: this.activityForm.value.dateFin || null
    };
    
    this.isLoading = true;
    
    this.equipeDetailService.assignActivityToEquipe(request).subscribe({
      next: () => {
        Swal.fire({ icon: 'success', title: 'Success', text: 'Activity added successfully' });
        this.closeAddActivityModal();
        this.loadEquipeDetail();
        this.loadRapports(); // Reload to update statistics
      },
      error: (err) => {
        console.error('Error adding activity:', err);
        this.isLoading = false;
        Swal.fire({ icon: 'error', title: 'Error', text: 'Failed to add activity' });
      }
    });
  }
  
  removeActivity(activityId: number, activityName: string): void {
    Swal.fire({
      title: 'Confirm Removal',
      text: `Are you sure you want to remove activity "${activityName}"?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      confirmButtonText: 'Yes, Remove',
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        this.isLoading = true;
        this.equipeDetailService.removeActivityFromEquipe(this.equipeId, activityId, this.missionId).subscribe({
          next: () => {
            Swal.fire('Removed', 'Activity has been removed successfully', 'success');
            this.loadEquipeDetail();
            this.loadRapports(); // Reload to update statistics
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
  
  // ============ EQUIPE MANAGEMENT ============
  
  openEditEquipeModal(): void {
    if (!this.equipeDetail) return;
    this.equipeForm.patchValue({
      nom: this.equipeDetail.nom,
      type: this.equipeDetail.type
    });
    this.showEditEquipeModal = true;
  }
  
  closeEditEquipeModal(): void {
    this.showEditEquipeModal = false;
    this.equipeForm.reset();
  }
  
  saveEquipeChanges(): void {
    if (this.equipeForm.invalid) return;
    
    this.equipeDetailService.updateEquipe(this.equipeId, this.equipeForm.value).subscribe({
      next: () => {
        Swal.fire({ icon: 'success', title: 'Success', text: 'Team information updated successfully' });
        this.closeEditEquipeModal();
        this.loadEquipeDetail();
      },
      error: (err) => {
        console.error('Error updating equipe:', err);
        Swal.fire({ icon: 'error', title: 'Error', text: 'Failed to update team information' });
      }
    });
  }
  
  deleteEquipe(): void {
    Swal.fire({
      title: 'Confirm Delete',
      text: `Are you sure you want to delete team "${this.equipeDetail?.nom}"? This action cannot be undone.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Yes, Delete',
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        this.equipeDetailService.deleteEquipe(this.equipeId).subscribe({
          next: () => {
            Swal.fire('Deleted', 'Team has been deleted successfully', 'success');
             this.router.navigate(['/pages/mission-dashboard']);
          },
          error: (err) => {
            console.error('Error deleting equipe:', err);
            Swal.fire('Error', 'Failed to delete team', 'error');
          }
        });
      }
    });
  }

  
  
  // ============ FILTERS & HELPERS ============
  
 // Update filterMembers method
filterMembers(): void {
  if (!this.equipeDetail) return;
  
  if (!this.memberSearchTerm.trim()) {
    this.filteredMembers = [...this.equipeDetail.membres];
  } else {
    const term = this.memberSearchTerm.toLowerCase();
    this.filteredMembers = this.equipeDetail.membres.filter(member =>
      member.nom.toLowerCase().includes(term) ||
      member.prenom.toLowerCase().includes(term)
    );
  }
  this.membersCurrentPage = 1;
  this.updateMembersPagination();
}
  
  selectTab(tab: string): void {
    this.selectedTab = tab;
  }
  
  goBack(): void {
    this.router.navigate(['/pages/mission-dashboard']);
  }
  
  getTypeIcon(): string {
    const icons: { [key: string]: string } = {
      'TOPOGRAPHIE': 'fas fa-map',
      'LAYONNAGE': 'fas fa-draw-polygon',
      'ENERGISREMENT': 'fas fa-bolt',
      'POSE': 'fas fa-tools',
      'RAMASSAGE': 'fas fa-truck'
    };
    return icons[this.equipeDetail?.type || ''] || 'fas fa-users';
  }
  
  getTypeText(): string {
    const texts: { [key: string]: string } = {
      'TOPOGRAPHIE': 'Topography Team',
      'LAYONNAGE': 'Layout Team',
      'ENERGISREMENT': 'Energization Team',
      'POSE': 'Installation Team',
      'RAMASSAGE': 'Collection Team'
    };
    return texts[this.equipeDetail?.type || ''] || this.equipeDetail?.type || '';
  }
  
  getTypeColor(): string {
    const colors: { [key: string]: string } = {
      'TOPOGRAPHIE': '#3b82f6',
      'LAYONNAGE': '#10b981',
      'ENERGISREMENT': '#f59e0b',
      'POSE': '#8b5cf6',
      'RAMASSAGE': '#14b8a6'
    };
    return colors[this.equipeDetail?.type || ''] || '#64748b';
  }
  
  getAvatarColor(id: number): string {
    const colors = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#ec489a'];
    return colors[id % colors.length];
  }
  
  getProgressColor(progress: number): string {
    if (progress >= 80) return '#10b981';
    if (progress >= 50) return '#f59e0b';
    return '#ef4444';
  }
  
  getRendementColor(value: number): string {
    if (value >= 80) return '#10b981';
    if (value >= 60) return '#f59e0b';
    return '#ef4444';
  }
  
  formatDate(date: string): string {
    if (!date) return 'N/A';
    return new Date(date).toLocaleDateString('en-US', { 
      day: '2-digit', 
      month: 'short', 
      year: 'numeric' 
    });
  }
  
  formatTime(time: string): string {
    if (!time) return '--:--';
    return time.substring(0, 5);
  }
  
  truncateText(text: string, maxLength: number = 100): string {
    if (!text) return '';
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength) + '...';
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  // Add after loadEquipeDetail method
updateMembersPagination(): void {
  this.membersTotalPages = Math.ceil(this.filteredMembers.length / this.membersPageSize);
  if (this.membersTotalPages === 0) this.membersTotalPages = 1;
  
  if (this.membersCurrentPage > this.membersTotalPages) {
    this.membersCurrentPage = Math.max(1, this.membersTotalPages);
  }
  
  const startIndex = (this.membersCurrentPage - 1) * this.membersPageSize;
  this.paginatedMembers = this.filteredMembers.slice(startIndex, startIndex + this.membersPageSize);
}

previousMembersPage(): void {
  if (this.membersCurrentPage > 1) {
    this.membersCurrentPage--;
    this.updateMembersPagination();
  }
}

nextMembersPage(): void {
  if (this.membersCurrentPage < this.membersTotalPages) {
    this.membersCurrentPage++;
    this.updateMembersPagination();
  }
}

goToMembersPage(page: number): void {
  if (page >= 1 && page <= this.membersTotalPages) {
    this.membersCurrentPage = page;
    this.updateMembersPagination();
  }
}

goToFirstMembersPage(): void {
  this.goToMembersPage(1);
}

goToLastMembersPage(): void {
  this.goToMembersPage(this.membersTotalPages);
}

getMembersPageNumbers(): number[] {
  const pages: number[] = [];
  const maxVisible = 5;
  let startPage = Math.max(1, this.membersCurrentPage - Math.floor(maxVisible / 2));
  let endPage = Math.min(this.membersTotalPages, startPage + maxVisible - 1);
  
  if (endPage - startPage + 1 < maxVisible) {
    startPage = Math.max(1, endPage - maxVisible + 1);
  }
  
  for (let i = startPage; i <= endPage; i++) {
    pages.push(i);
  }
  return pages;
}

// Activities Pagination Methods
updateActivitiesPagination(): void {
  if (!this.equipeDetail) return;
  this.activitiesTotalPages = Math.ceil(this.equipeDetail.activites.length / this.activitiesPageSize);
  if (this.activitiesTotalPages === 0) this.activitiesTotalPages = 1;
  
  if (this.activitiesCurrentPage > this.activitiesTotalPages) {
    this.activitiesCurrentPage = Math.max(1, this.activitiesTotalPages);
  }
  
  const startIndex = (this.activitiesCurrentPage - 1) * this.activitiesPageSize;
  this.paginatedActivities = this.equipeDetail.activites.slice(startIndex, startIndex + this.activitiesPageSize);
}

previousActivitiesPage(): void {
  if (this.activitiesCurrentPage > 1) {
    this.activitiesCurrentPage--;
    this.updateActivitiesPagination();
  }
}

nextActivitiesPage(): void {
  if (this.activitiesCurrentPage < this.activitiesTotalPages) {
    this.activitiesCurrentPage++;
    this.updateActivitiesPagination();
  }
}

goToActivitiesPage(page: number): void {
  if (page >= 1 && page <= this.activitiesTotalPages) {
    this.activitiesCurrentPage = page;
    this.updateActivitiesPagination();
  }
}

goToFirstActivitiesPage(): void {
  this.goToActivitiesPage(1);
}

goToLastActivitiesPage(): void {
  this.goToActivitiesPage(this.activitiesTotalPages);
}

getActivitiesPageNumbers(): number[] {
  const pages: number[] = [];
  const maxVisible = 5;
  let startPage = Math.max(1, this.activitiesCurrentPage - Math.floor(maxVisible / 2));
  let endPage = Math.min(this.activitiesTotalPages, startPage + maxVisible - 1);
  
  if (endPage - startPage + 1 < maxVisible) {
    startPage = Math.max(1, endPage - maxVisible + 1);
  }
  
  for (let i = startPage; i <= endPage; i++) {
    pages.push(i);
  }
  return pages;
}

// Reports Pagination Methods
filterReports(): void {
  if (!this.reportSearchTerm.trim()) {
    this.filteredReports = [...this.teamRapports];
  } else {
    const term = this.reportSearchTerm.toLowerCase();
    this.filteredReports = this.teamRapports.filter(report =>
      report.titre.toLowerCase().includes(term) ||
      (report.resume && report.resume.toLowerCase().includes(term))
    );
  }
  this.reportsCurrentPage = 1;
  this.updateReportsPagination();
}

updateReportsPagination(): void {
  this.reportsTotalPages = Math.ceil(this.filteredReports.length / this.reportsPageSize);
  if (this.reportsTotalPages === 0) this.reportsTotalPages = 1;
  
  if (this.reportsCurrentPage > this.reportsTotalPages) {
    this.reportsCurrentPage = Math.max(1, this.reportsTotalPages);
  }
  
  const startIndex = (this.reportsCurrentPage - 1) * this.reportsPageSize;
  this.paginatedReports = this.filteredReports.slice(startIndex, startIndex + this.reportsPageSize);
}

previousReportsPage(): void {
  if (this.reportsCurrentPage > 1) {
    this.reportsCurrentPage--;
    this.updateReportsPagination();
  }
}

nextReportsPage(): void {
  if (this.reportsCurrentPage < this.reportsTotalPages) {
    this.reportsCurrentPage++;
    this.updateReportsPagination();
  }
}

goToReportsPage(page: number): void {
  if (page >= 1 && page <= this.reportsTotalPages) {
    this.reportsCurrentPage = page;
    this.updateReportsPagination();
  }
}

goToFirstReportsPage(): void {
  this.goToReportsPage(1);
}

goToLastReportsPage(): void {
  this.goToReportsPage(this.reportsTotalPages);
}

getReportsPageNumbers(): number[] {
  const pages: number[] = [];
  const maxVisible = 5;
  let startPage = Math.max(1, this.reportsCurrentPage - Math.floor(maxVisible / 2));
  let endPage = Math.min(this.reportsTotalPages, startPage + maxVisible - 1);
  
  if (endPage - startPage + 1 < maxVisible) {
    startPage = Math.max(1, endPage - maxVisible + 1);
  }
  
  for (let i = startPage; i <= endPage; i++) {
    pages.push(i);
  }
  return pages;
}
// Add these methods for status display
getStatusLabel(status: string): string {
  const option = this.statusOptions.find(s => s.value === status);
  return option?.label || status;
}

getStatusColor(status: string): string {
  const option = this.statusOptions.find(s => s.value === status);
  return option?.color || '#64748b';
}

getStatusIcon(status: string): string {
  const option = this.statusOptions.find(s => s.value === status);
  return option?.icon || 'fas fa-circle';
}

getProgressPercentage(activity: any): number {
  const progress = this.activityProgressMap.get(activity.id);
  if (!progress) return 0;
  
  // Calculate based on status
  switch (progress.status) {
    case 'TERMINI': return 100;
    case 'ENCOURS': return 50;
    case 'ENATTENTE': return 25;
    case 'ENRETARD': return 40;
    default: return 0;
  }
}


// Load progress for all activities
loadProgressForActivities(): void {
  if (!this.equipeDetail || !this.equipeDetail.activites.length) return;
  
  this.equipeDetail.activites.forEach(activity => {
    this.etatAvancementService?.getEtatAvancementByActive(activity.id, this.missionId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (progress) => {
          this.activityProgressMap.set(activity.id, progress);
        },
        error: () => {
          // No progress exists, set default
          this.activityProgressMap.set(activity.id, { status: 'PLANIFIER', avancements: [] });
        }
      });
  });
}
loadRendementsByEquipe(): void {
  // Use the new endpoint to get rendements directly by equipe
  this.rapportService.getRendementsByEquipe(this.equipeId, this.missionId)
    .pipe(takeUntil(this.destroy$))
    .subscribe({
      next: (rendements: RendementResponse[]) => {
        this.teamRendements = rendements;
        
        // Update statistics
        if (this.equipeDetail && this.equipeDetail.statistiques) {
          this.equipeDetail.statistiques.totalRendements = rendements.length;
          
          // Calculate total working hours
          let totalHeures = 0;
          rendements.forEach(rend => {
            totalHeures += rend.dureeHeures || 0;
          });
          this.equipeDetail.statistiques.totalHeuresTravaillees = totalHeures;
          
          // Calculate average productivity
          let totalProductivity = 0;
          rendements.forEach(rend => {
            totalProductivity += rend.valeurRendement || 0;
          });
          this.equipeDetail.statistiques.moyenneRendement = rendements.length > 0 
            ? totalProductivity / rendements.length 
            : 0;
        }
        
        this.initChart();
      },
      error: (err) => {
        console.error('Error loading rendements by equipe:', err);
        this.teamRendements = [];
      }
    });
}


loadTeamActivities(): void {
  if (!this.equipeDetail?.activites) return;
  this.teamActivities = this.equipeDetail.activites;
}

// أضف هذه الدالة لفتح مودال إضافة الإنتاجية
// أضف هذه الدالة لفتح مودال إضافة الإنتاجية
openAddProductivityModal(): void {
  this.selectedRapportId = null;
  this.selectedActivityId = null;
  
  // Load available activities for this team
  if (this.equipeDetail && this.equipeDetail.activites) {
    this.teamActivities = this.equipeDetail.activites;
  }
  
  this.rendementForm.reset({
    heureDebut: '08:00',
    heureFin: '17:00',
    valeurRendement: 0,
    uniteRendement: 'm²',
    date: new Date().toISOString().split('T')[0]
  });
  this.showAddProductivityModal = true;
}

// أضف هذه الدالة لإغلاق المودال
closeAddProductivityModal(): void {
  this.showAddProductivityModal = false;
  this.selectedRapportId = null;
  this.selectedActivityId = null;
}

// أضف هذه الدالة لإرسال الإنتاجية الجديدة
// أضف هذه الدالة لإرسال الإنتاجية الجديدة - UPDATED VERSION
submitNewRendement(): void {
  if (!this.selectedRapportId) {
    Swal.fire({ icon: 'warning', title: 'Warning', text: 'Please select a report' });
    return;
  }
  
  if (!this.selectedActivityId) {
    Swal.fire({ icon: 'warning', title: 'Warning', text: 'Please select an activity' });
    return;
  }
  
  if (this.rendementForm.invalid) {
    Swal.fire({ icon: 'warning', title: 'Warning', text: 'Please fill all required fields' });
    return;
  }
  
  const formValue = this.rendementForm.value;
  
  // ✅ IMPORTANT: Create request with activeId
  const request: RendementRequest = {
    heureDebut: formValue.heureDebut,
    heureFin: formValue.heureFin,
    valeurRendement: formValue.valeurRendement,
    uniteRendement: formValue.uniteRendement,
    date: formValue.date,
    activeId: this.selectedActivityId  // ✅ activeId is in the body
  };
  
  this.isLoading = true;
  
  // ✅ FIXED: Remove missionId from the call - only pass rapportId, equipeId, and request
  this.rapportService.addRendementToRapportWithEquipe(
    this.selectedRapportId, 
    this.equipeId, 
    request  // missionId is no longer needed - backend gets it from rapport
  ).pipe(takeUntil(this.destroy$))
  .subscribe({
    next: () => {
      Swal.fire({ icon: 'success', title: 'Success', text: 'Productivity record added successfully' });
      this.closeAddProductivityModal();
      this.loadRendementsByEquipe(); // Reload rendements
      this.loadRapports(); // Reload reports to update statistics
      this.isLoading = false;
    },
    error: (err) => {
      console.error('Error adding productivity record:', err);
      this.isLoading = false;
      Swal.fire({ icon: 'error', title: 'Error', text: 'Failed to add productivity record: ' + (err.error?.message || err.message) });
    }
  });
}


}