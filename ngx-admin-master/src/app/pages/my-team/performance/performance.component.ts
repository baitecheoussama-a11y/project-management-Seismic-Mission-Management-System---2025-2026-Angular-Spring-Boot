// pages/teams/performance/performance.component.ts
import { Component, OnInit, OnDestroy, AfterViewInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { Subject, forkJoin } from 'rxjs';
import { takeUntil, debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ChartConfiguration, ChartData } from 'chart.js';
import Swal from 'sweetalert2';

// Services
import { EquipeDetailService, EquipeDetail, ActiveDetail } from '../../../services/team/equipe-detail.service';
import { MissionService, EquipeDTO, EmployeDTO, MissionTeamDTO } from '../../../services/mission/mission.service';
import { RapportService, RapportResponse, RendementResponse, RendementRequest } from '../../../services/rapport/rapport.service';
import { AuthService } from '../../../services/auth.service';

// Interface for productivity record with additional info
interface ProductivityRecord extends RendementResponse {
  reportTitle?: string;
  activityCode?: string;
  activityId?: number;
}

@Component({
  selector: 'app-performance',
  templateUrl: './performance.component.html',
  styleUrls: ['./performance.component.scss']
})
export class PerformanceComponent implements OnInit, OnDestroy, AfterViewInit {
  
  // Team data
  equipes: EquipeDTO[] = [];
  filteredEquipes: EquipeDTO[] = [];
  selectedEquipe: EquipeDTO | null = null;
  
  // Mission data
  missionId: number = 0;
  missionTeam: MissionTeamDTO | null = null;
  
  // Performance data
  teamRendements: ProductivityRecord[] = [];
  filteredProductivityRecords: ProductivityRecord[] = [];
  teamReports: RapportResponse[] = [];
  teamActivities: ActiveDetail[] = [];
  
  // Statistics
  totalWorkingHours: number = 0;
  totalActivities: number = 0;
  totalProductivityRecords: number = 0;
  
  // UI States
  isLoading: boolean = false;
  isMobile: boolean = false;
  sidebarCollapsed: boolean = false;
  
  // Modals
  showRecordModal: boolean = false;
  showReportModal: boolean = false;
  editingRecordId: number | null = null;
  selectedReport: RapportResponse | null = null;
  
  // Search and Filters
  searchTerm: string = '';
  teamSearchTerm: string = '';
  activityFilter: string = 'all';
  sortBy: string = 'date';
  sortOrder: string = 'desc';
  
  // Pagination
  currentPage: number = 1;
  itemsPerPage: number = 10;
  totalPages: number = 1;
  paginatedRecords: ProductivityRecord[] = [];
  
  // Role-based access
  canManagePerformance: boolean = false;
  
  // Forms
  recordForm: FormGroup;
  
  // Chart Data
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
        title: { display: true, text: 'Productivity (%)' }
      },
      x: {
        title: { display: true, text: 'Date' }
      }
    }
  };
  
  activityChartData: ChartData<'doughnut'> = {
    labels: [],
    datasets: [{
      data: [],
      backgroundColor: ['#3b82f6', '#10b981', '#f59e0b', '#8b5cf6', '#14b8a6', '#ef4444'],
      borderWidth: 0
    }]
  };
  
  activityChartOptions: any = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'bottom',
        labels: { font: { size: 11 } }
      }
    }
  };
  
  // Type options
  typeOptions = [
    { value: 'TOPOGRAPHIE', label: 'Topography', icon: 'fas fa-map' },
    { value: 'LAYONNAGE', label: 'Layout', icon: 'fas fa-draw-polygon' },
    { value: 'ENERGISREMENT', label: 'Energization', icon: 'fas fa-bolt' },
    { value: 'POSE', label: 'Installation', icon: 'fas fa-tools' },
    { value: 'RAMASSAGE', label: 'Collection', icon: 'fas fa-truck' }
  ];
  
  private destroy$ = new Subject<void>();
  private searchSubject = new Subject<string>();
  
  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private equipeDetailService: EquipeDetailService,
    private missionService: MissionService,
    private rapportService: RapportService,
    private authService: AuthService,
    private fb: FormBuilder
  ) {
    this.recordForm = this.fb.group({
      rapportId: [null, Validators.required],
      activityId: [null],
      heureDebut: ['08:00', Validators.required],
      heureFin: ['17:00', Validators.required],
      date: [new Date().toISOString().split('T')[0], Validators.required],
      valeurRendement: [0, [Validators.required, Validators.min(0)]],
      uniteRendement: ['m²', Validators.required]
    });
  }
  
  ngOnInit(): void {
    // Get missionId from query params or load from current mission
    this.route.queryParams.pipe(takeUntil(this.destroy$)).subscribe(params => {
      if (params['missionId']) {
        this.missionId = +params['missionId'];
        this.loadMissionAndTeams();
      } else {
        this.loadCurrentMission();
      }
    });
    
    this.checkScreenSize();
    window.addEventListener('resize', this.handleResize.bind(this));
    
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      takeUntil(this.destroy$)
    ).subscribe(() => {
      this.applyFilters();
    });
    
    this.initializePermissions();
  }
  
  ngAfterViewInit(): void {}
  
  // ==================== MISSION LOADING ====================
  
  loadCurrentMission(): void {
    this.missionService.getMyCurrentMission()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (missionResponse) => {
          if (missionResponse && missionResponse.missionId) {
            this.missionId = missionResponse.missionId;
            this.loadMissionAndTeams();
          } else {
            console.warn('No current mission found');
            this.isLoading = false;
          }
        },
        error: (err) => {
          console.error('Error loading current mission:', err);
          this.isLoading = false;
        }
      });
  }
  
  loadMissionAndTeams(): void {
    if (!this.missionId) return;
    
    this.isLoading = true;
    this.missionService.getMissionTeam(this.missionId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (missionTeam) => {
          this.missionTeam = missionTeam;
          // Build equipes list with member counts from mission data
          this.equipes = missionTeam.equipes.map(equipe => ({
            ...equipe,
            memberCount: missionTeam.membersByEquipe?.[equipe.nom]?.length || 0
          }));
          this.filteredEquipes = [...this.equipes];
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error loading mission teams:', err);
          this.isLoading = false;
          Swal.fire({ icon: 'error', title: 'Error', text: 'Failed to load teams' });
        }
      });
  }
  
  // ==================== PERMISSIONS ====================
  
  initializePermissions(): void {
    if (this.authService.hasRole('CHEF_TERRAIN') || 
        this.authService.hasRole('ADMIN') ||
        this.authService.hasRole('GESTIONNAIRE')) {
      this.canManagePerformance = true;
    } else {
      this.canManagePerformance = false;
    }
    
    console.log('[DEBUG] Permissions - Manage Performance:', this.canManagePerformance);
  }
  
  // ==================== LOAD METHODS ====================
  
  loadPerformanceData(equipeId: number): void {
    if (!this.missionId) {
      console.warn('No mission ID available');
      return;
    }
    
    this.isLoading = true;
    
    // Load equipe detail with activities
    forkJoin({
      equipeDetail: this.equipeDetailService.getEquipeDetail(equipeId, this.missionId),
      rendements: this.rapportService.getRendementsByEquipe(equipeId, this.missionId),
      rapports: this.rapportService.getRapportsForCurrentProject(this.missionId)
    }).pipe(takeUntil(this.destroy$))
      .subscribe({
        next: ({ equipeDetail, rendements, rapports }) => {
          this.teamActivities = equipeDetail?.activites || [];
          this.teamReports = rapports || [];
          this.totalActivities = this.teamActivities.length;
          
          // Enhance rendements with additional info
          this.teamRendements = this.enhanceRendements(rendements || []);
          this.filteredProductivityRecords = [...this.teamRendements];
          this.totalProductivityRecords = this.teamRendements.length;
          
          // Calculate statistics
          this.calculateStatistics();
          
          // Update pagination
          this.currentPage = 1;
          this.updatePagination();
          
          // Initialize charts
          this.initProductivityChart();
          this.initActivityChart();
          
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error loading performance data:', err);
          this.isLoading = false;
          Swal.fire({ icon: 'error', title: 'Error', text: 'Failed to load performance data' });
        }
      });
  }
  
  enhanceRendements(rendements: RendementResponse[]): ProductivityRecord[] {
    return rendements.map(rend => ({
      ...rend,
      reportTitle: this.getReportTitle(rend.rapportId),
      activityCode: this.getActivityCodeFromRendement(rend),
      activityId: this.getActivityIdFromRendement(rend)
    }));
  }
  
  getActivityCodeFromRendement(rend: RendementResponse): string {
    // If rendement has an activity code directly
    if ((rend as any).activityCode) {
      return (rend as any).activityCode;
    }
    // Otherwise try to find by report or return default
    const report = this.teamReports.find(r => r.id === rend.rapportId);
    if (report && (report as any).activityCode) {
      return (report as any).activityCode;
    }
    return 'General';
  }
  
  getActivityIdFromRendement(rend: RendementResponse): number {
    // If rendement has an activity id directly
    if ((rend as any).activityId) {
      return (rend as any).activityId;
    }
    // Otherwise return 0
    return 0;
  }
  
  calculateStatistics(): void {
    // Calculate total working hours
    this.totalWorkingHours = this.teamRendements.reduce((sum, r) => sum + (r.dureeHeures || 0), 0);
    
    // Update selected equipe stats
    if (this.selectedEquipe) {
      const avgProductivity = this.teamRendements.length > 0
        ? this.teamRendements.reduce((sum, r) => sum + (r.valeurRendement || 0), 0) / this.teamRendements.length
        : 0;
      (this.selectedEquipe as any).averageProductivity = avgProductivity;
    }
  }
  
  // ==================== TEAM SELECTION ====================
  
  selectTeam(equipe: EquipeDTO): void {
    if (!equipe || !equipe.id) return;
    this.selectedEquipe = equipe;
    this.loadPerformanceData(equipe.id);
    this.resetFilters();
    
    if (this.isMobile) {
      this.sidebarCollapsed = true;
    }
  }
  
  clearSelection(): void {
    this.selectedEquipe = null;
    this.teamRendements = [];
    this.filteredProductivityRecords = [];
    this.teamActivities = [];
    this.teamReports = [];
    this.totalWorkingHours = 0;
    this.totalActivities = 0;
    this.totalProductivityRecords = 0;
  }
  
  // ==================== FILTERS & SEARCH ====================
  
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
  
  onSearchInput(): void {
    this.searchSubject.next(this.searchTerm);
  }
  
  clearSearch(): void {
    this.searchTerm = '';
    this.applyFilters();
  }
  
  applyFilters(): void {
    let filtered = [...this.teamRendements];
    
    // Search filter
    if (this.searchTerm?.trim()) {
      const term = this.searchTerm.toLowerCase().trim();
      filtered = filtered.filter(record =>
        (record.reportTitle?.toLowerCase().includes(term) ||
         record.activityCode?.toLowerCase().includes(term) ||
         record.date?.includes(term))
      );
    }
    
    // Activity filter
    if (this.activityFilter !== 'all') {
      filtered = filtered.filter(record => record.activityId === Number(this.activityFilter));
    }
    
    // Sorting
    filtered.sort((a, b) => {
      let comparison = 0;
      switch (this.sortBy) {
        case 'date':
          comparison = new Date(a.date).getTime() - new Date(b.date).getTime();
          break;
        case 'productivity':
          comparison = (a.valeurRendement || 0) - (b.valeurRendement || 0);
          break;
        case 'duration':
          comparison = (a.dureeHeures || 0) - (b.dureeHeures || 0);
          break;
        default:
          comparison = 0;
      }
      return this.sortOrder === 'asc' ? comparison : -comparison;
    });
    
    this.filteredProductivityRecords = filtered;
    this.currentPage = 1;
    this.updatePagination();
  }
  
  resetFilters(): void {
    this.searchTerm = '';
    this.activityFilter = 'all';
    this.sortBy = 'date';
    this.sortOrder = 'desc';
    this.applyFilters();
  }
  
  // ==================== PAGINATION ====================
  
  updatePagination(): void {
    this.totalPages = Math.max(1, Math.ceil(this.filteredProductivityRecords.length / this.itemsPerPage));
    
    if (this.currentPage > this.totalPages) {
      this.currentPage = this.totalPages;
    }
    
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    this.paginatedRecords = this.filteredProductivityRecords.slice(startIndex, startIndex + this.itemsPerPage);
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
  
  getCurrentPageEnd(): number {
    return Math.min(this.currentPage * this.itemsPerPage, this.filteredProductivityRecords.length);
  }
  
  // ==================== CRUD OPERATIONS ====================
  
  openAddRecordModal(): void {
    if (!this.canManagePerformance) {
      Swal.fire({ icon: 'warning', title: 'Access Denied', text: 'You don\'t have permission to add records' });
      return;
    }
    
    this.editingRecordId = null;
    this.recordForm.reset({
      rapportId: null,
      activityId: null,
      heureDebut: '08:00',
      heureFin: '17:00',
      date: new Date().toISOString().split('T')[0],
      valeurRendement: 0,
      uniteRendement: 'm²'
    });
    this.showRecordModal = true;
  }
  
  openEditRecordModal(record: ProductivityRecord): void {
    if (!this.canManagePerformance) {
      Swal.fire({ icon: 'warning', title: 'Access Denied', text: 'You don\'t have permission to edit records' });
      return;
    }
    
    this.editingRecordId = record.id;
    this.recordForm.patchValue({
      rapportId: record.rapportId,
      activityId: record.activityId,
      heureDebut: record.heureDebut,
      heureFin: record.heureFin,
      date: record.date,
      valeurRendement: record.valeurRendement,
      uniteRendement: record.uniteRendement
    });
    this.showRecordModal = true;
  }
  
  closeRecordModal(): void {
    this.showRecordModal = false;
    this.editingRecordId = null;
  }
 submitRecord(): void {
    if (this.recordForm.invalid || !this.selectedEquipe) {
        Swal.fire({ icon: 'warning', title: 'Warning', text: 'Please fill all required fields' });
        return;
    }
    
    const formValue = this.recordForm.value;
    
    // Validate required fields
    if (!formValue.rapportId) {
        Swal.fire({ icon: 'warning', title: 'Warning', text: 'Please select a report' });
        return;
    }
    
    if (!formValue.activityId) {
        Swal.fire({ icon: 'warning', title: 'Warning', text: 'Please select an activity' });
        return;
    }
    
    const rendementRequest: RendementRequest = {
        heureDebut: formValue.heureDebut,
        heureFin: formValue.heureFin,
        valeurRendement: formValue.valeurRendement,
        uniteRendement: formValue.uniteRendement,
        date: formValue.date,
        activeId: formValue.activityId  // ✅ activeId is in the body
    };
    
    this.isLoading = true;
    
    if (this.editingRecordId) {
        // Update existing record
        this.rapportService.updateRendement(this.editingRecordId, rendementRequest)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: () => {
                    Swal.fire({ icon: 'success', title: 'Success', text: 'Record updated successfully' });
                    this.closeRecordModal();
                    this.loadPerformanceData(this.selectedEquipe!.id);
                },
                error: (err) => {
                    console.error('Error updating record:', err);
                    this.isLoading = false;
                    Swal.fire({ icon: 'error', title: 'Error', text: 'Failed to update record' });
                }
            });
    } else {
        // ✅ FIXED: Remove missionId parameter - only pass rapportId, equipeId, and rendement
        this.rapportService.addRendementToRapportWithEquipe(
            formValue.rapportId,
            this.selectedEquipe.id,
            rendementRequest  // activeId is inside this object
        ).pipe(takeUntil(this.destroy$))
        .subscribe({
            next: () => {
                Swal.fire({ icon: 'success', title: 'Success', text: 'Record added successfully' });
                this.closeRecordModal();
                this.loadPerformanceData(this.selectedEquipe!.id);
            },
            error: (err) => {
                console.error('Error adding record:', err);
                this.isLoading = false;
                Swal.fire({ icon: 'error', title: 'Error', text: 'Failed to add record: ' + (err.error?.message || err.message) });
            }
        });
    }
}
  
  deleteRecord(record: ProductivityRecord): void {
    if (!this.canManagePerformance) {
      Swal.fire({ icon: 'warning', title: 'Access Denied', text: 'You don\'t have permission to delete records' });
      return;
    }
    
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
        this.isLoading = true;
        this.rapportService.deleteRendement(record.id)
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: () => {
              Swal.fire({ icon: 'success', title: 'Deleted', text: 'Record deleted successfully' });
              this.loadPerformanceData(this.selectedEquipe!.id);
            },
            error: (err) => {
              console.error('Error deleting record:', err);
              this.isLoading = false;
              Swal.fire({ icon: 'error', title: 'Error', text: 'Failed to delete record' });
            }
          });
      }
    });
  }
  
  // ==================== REPORT VIEW ====================
  
  viewReportDetail(record: ProductivityRecord): void {
    const report = this.teamReports.find(r => r.id === record.rapportId);
    if (report) {
      this.selectedReport = report;
      this.showReportModal = true;
    }
  }
  
  closeReportModal(): void {
    this.showReportModal = false;
    this.selectedReport = null;
  }
  
  // ==================== CHARTS ====================
  
  initProductivityChart(): void {
    if (this.teamRendements.length === 0) {
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
    
    // Group by date and calculate average
    const rendementsByDate = new Map<string, number[]>();
    
    this.teamRendements.forEach(rend => {
      const date = rend.date;
      if (!rendementsByDate.has(date)) {
        rendementsByDate.set(date, []);
      }
      rendementsByDate.get(date)!.push(rend.valeurRendement);
    });
    
    const sortedDates = Array.from(rendementsByDate.keys()).sort();
    const avgRendements = sortedDates.map(date => {
      const values = rendementsByDate.get(date)!;
      return values.reduce((a, b) => a + b, 0) / values.length;
    });
    
    this.performanceChartData.labels = sortedDates.map(date => {
      const d = new Date(date);
      return `${d.getDate()}/${d.getMonth() + 1}`;
    });
    
    this.performanceChartData.datasets[0].data = avgRendements;
  }
  
  initActivityChart(): void {
    if (this.teamActivities.length === 0) {
      this.activityChartData = {
        labels: [],
        datasets: [{ data: [], backgroundColor: [] }]
      };
      return;
    }
    
    // Count records per activity
    const activityCount = new Map<number, number>();
    this.teamRendements.forEach(rend => {
      if (rend.activityId && rend.activityId > 0) {
        activityCount.set(rend.activityId, (activityCount.get(rend.activityId) || 0) + 1);
      }
    });
    
    const labels: string[] = [];
    const data: number[] = [];
    
    this.teamActivities.forEach(activity => {
      const count = activityCount.get(activity.id) || 0;
      if (count > 0 || this.teamRendements.length === 0) {
        labels.push(activity.codeActive);
        data.push(count > 0 ? count : 0);
      }
    });
    
    if (labels.length === 0) {
      labels.push('No Data');
      data.push(1);
    }
    
    this.activityChartData.labels = labels;
    this.activityChartData.datasets[0].data = data;
  }
  
  refreshChartData(): void {
    if (this.selectedEquipe) {
      this.initProductivityChart();
      this.initActivityChart();
    }
  }
  
  // ==================== HELPER METHODS ====================
  
  getTotalActivities(): number {
    return this.equipes.reduce((sum, t) => sum + ((t as any).activitiesCount || 0), 0);
  }
  
  getReportTitle(rapportId: number): string {
    const report = this.teamReports.find(r => r.id === rapportId);
    return report?.titre || 'Unknown Report';
  }
  
  getActivityCode(activityId: number): string {
    const activity = this.teamActivities.find(a => a.id === activityId);
    return activity?.codeActive || 'General';
  }
  
  getActivityColor(activityId: number): string {
    const colors = ['#3b82f6', '#10b981', '#f59e0b', '#8b5cf6', '#14b8a6'];
    return colors[Math.abs(activityId) % colors.length];
  }
  
  getProductivityColor(value: number): string {
    if (value >= 80) return '#10b981';
    if (value >= 60) return '#f59e0b';
    if (value >= 40) return '#f97316';
    return '#ef4444';
  }
  
  getProductivityPercentage(value: number): number {
    return Math.min(100, (value / 100) * 100);
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
  
  // ==================== UI HELPERS ====================
  
  toggleSidebar(): void {
    this.sidebarCollapsed = !this.sidebarCollapsed;
  }
  
  checkScreenSize(): void {
    this.isMobile = window.innerWidth < 768;
    if (!this.isMobile) {
      this.sidebarCollapsed = false;
    }
  }
  
  handleResize(): void {
    this.checkScreenSize();
  }
  
  goBack(): void {
    this.router.navigate(['/pages/teams']);
  }
  
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    window.removeEventListener('resize', this.handleResize.bind(this));
  }
  truncateText(text: string, limit: number = 30): string {
  if (!text) return '';
  return text.length > limit ? text.substring(0, limit) + '...' : text;
}

}