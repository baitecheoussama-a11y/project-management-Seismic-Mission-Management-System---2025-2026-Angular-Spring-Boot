import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil, debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { ReportViewerService, Report, Project } from '../../services/rapport/report-viewer.service';
import { MissionService, Mission } from '../../services/mission/mission.service';
import { AuthService } from '../../services/auth.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-reports-viewer',
  templateUrl: './reports-viewer.component.html',
  styleUrls: ['./reports-viewer.component.scss']
})
export class ReportsViewerComponent implements OnInit, OnDestroy {

  // Data
  projects: Project[] = [];
  selectedProject: Project | null = null;
  reports: Report[] = [];
  filteredReports: Report[] = [];
  selectedReport: Report | null = null;
  
  // Missions for filter
  missions: Mission[] = [];
  selectedMissionId: string = 'all';
  
  // UI State
  isLoading: boolean = false;
  showSidebar: boolean = true;
  isMobile: boolean = false;
  showReportDetail: boolean = false;
  showFilters: boolean = false;
  
  // Filters
  searchTerm: string = '';
  sortBy: string = 'date';
  sortOrder: string = 'desc';
  dateFrom: string = '';
  dateTo: string = '';
  
  // Pagination
  currentPage: number = 1;
  pageSize: number = 12;
  totalPages: number = 1;
  paginatedReports: Report[] = [];
  
  Math = Math;
  
  // Role-based permissions
  canViewAllProjects: boolean = false;
  
  private destroy$ = new Subject<void>();
  private searchSubject = new Subject<string>();

  // Status mapping
  statusMap: { [key: string]: { class: string; label: string; icon: string } } = {
    'PLANIFIER': { class: 'status-planned', label: 'Planned', icon: 'far fa-calendar-alt' },
    'ENCOURS': { class: 'status-progress', label: 'In Progress', icon: 'fas fa-play-circle' },
    'ENATTENTE': { class: 'status-pending', label: 'On Hold', icon: 'fas fa-pause-circle' },
    'ENRETARD': { class: 'status-delayed', label: 'Delayed', icon: 'fas fa-exclamation-circle' },
    'TERMINI': { class: 'status-completed', label: 'Completed', icon: 'fas fa-check-circle' },
    'ANNULE': { class: 'status-cancelled', label: 'Cancelled', icon: 'fas fa-ban' }
  };

  constructor(
    private reportViewerService: ReportViewerService,
    private missionService: MissionService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.initializePermissions();
    this.loadMissions();
    this.loadProjects();
    this.checkScreenSize();
    window.addEventListener('resize', () => this.checkScreenSize());
    
    this.searchSubject.pipe(
      debounceTime(500),
      distinctUntilChanged(),
      takeUntil(this.destroy$)
    ).subscribe(() => {
      this.applyFilters();
    });
  }

  // ========== PERMISSIONS ==========

  initializePermissions(): void {
    const userRoles = this.authService.getCurrentUser()?.roles || [];
    this.canViewAllProjects = userRoles.includes('DIRECTEUR') || 
                              userRoles.includes('ADMIN') || 
                              userRoles.includes('ADMINISTRATEUR');
  }

  // ========== LOAD DATA ==========

  loadMissions(): void {
    this.missionService.getAllMissions().subscribe({
      next: (missions) => {
        this.missions = missions;
      },
      error: (err) => {
        console.error('Error loading missions:', err);
      }
    });
  }

  loadProjects(): void {
    this.isLoading = true;
    
    if (this.canViewAllProjects && this.selectedMissionId === 'all') {
      this.reportViewerService.getAllProjects().subscribe({
        next: (projects) => {
          this.projects = this.enhanceProjectsWithStatus(projects);
          if (this.projects.length > 0) {
            this.selectedProject = this.projects[0];
            this.loadReports();
          } else {
            this.isLoading = false;
          }
        },
        error: (err) => {
          console.error('Error loading all projects:', err);
          this.isLoading = false;
          Swal.fire({ icon: 'error', title: 'Error', text: 'Failed to load projects' });
        }
      });
    } else if (this.canViewAllProjects && this.selectedMissionId !== 'all') {
      const missionId = Number(this.selectedMissionId);
      this.reportViewerService.getProjectsByMissionId(missionId).subscribe({
        next: (projects) => {
          this.projects = this.enhanceProjectsWithStatus(projects);
          if (this.projects.length > 0) {
            this.selectedProject = this.projects[0];
            this.loadReports();
          } else {
            this.isLoading = false;
          }
        },
        error: (err) => {
          console.error('Error loading projects for mission:', err);
          this.isLoading = false;
          Swal.fire({ icon: 'error', title: 'Error', text: 'Failed to load projects' });
        }
      });
    } else {
      this.reportViewerService.getProjectsByCurrentMission().subscribe({
        next: (projects) => {
          this.projects = this.enhanceProjectsWithStatus(projects);
          if (this.projects.length > 0) {
            this.selectedProject = this.projects[0];
            this.loadReports();
          } else {
            this.isLoading = false;
          }
        },
        error: (err) => {
          console.error('Error loading projects:', err);
          this.isLoading = false;
          Swal.fire({ icon: 'error', title: 'Error', text: 'Failed to load projects' });
        }
      });
    }
  }

  enhanceProjectsWithStatus(projects: Project[]): Project[] {
    return projects.map(project => ({
      ...project,
      statusInfo: this.getProjectStatusInfo(project)
    }));
  }

  getProjectStatusInfo(project: any): { status: string; label: string; class: string; icon: string } {
    // ✅ FIRST: If annule is true, it's CANCELLED (highest priority)
    if (project.annule === true) {
      const status = this.statusMap['ANNULE'];
      return {
        status: 'ANNULE',
        label: status.label,
        class: status.class,
        icon: status.icon
      };
    }
    
    // Check etatAvancements (for backward compatibility)
    if (project.etatAvancements && project.etatAvancements.length > 0) {
      const projectStatus = project.etatAvancements.find(
        (e: any) => !e.activeId || e.activeId === null
      );
      if (projectStatus && projectStatus.status) {
        const status = this.statusMap[projectStatus.status] || this.statusMap['PLANIFIER'];
        return {
          status: projectStatus.status,
          label: status.label,
          class: status.class,
          icon: status.icon
        };
      }
    }
    
    // If dateFinReelle is set, it's completed (only if not annule)
    if (project.dateFinReelle) {
      const status = this.statusMap['TERMINI'];
      return {
        status: 'TERMINI',
        label: status.label,
        class: status.class,
        icon: status.icon
      };
    }
    
    // If dateStartReelle is set
    const now = new Date().toISOString().split('T')[0];
    if (project.dateStartReelle) {
      if (project.dateStartReelle > now) {
        const status = this.statusMap['ENATTENTE'];
        return {
          status: 'ENATTENTE',
          label: status.label,
          class: status.class,
          icon: status.icon
        };
      }
      if (project.objectifFin && project.objectifFin < now) {
        const status = this.statusMap['ENRETARD'];
        return {
          status: 'ENRETARD',
          label: status.label,
          class: status.class,
          icon: status.icon
        };
      }
      const status = this.statusMap['ENCOURS'];
      return {
        status: 'ENCOURS',
        label: status.label,
        class: status.class,
        icon: status.icon
      };
    }
    
    // Fallback to progression-based status
    const progression = project.progression || 0;
    let statusKey = 'PLANIFIER';
    if (progression >= 100) statusKey = 'TERMINI';
    else if (progression >= 75) statusKey = 'ENCOURS';
    else if (progression >= 50) statusKey = 'ENATTENTE';
    else if (progression >= 25) statusKey = 'ENRETARD';
    
    const status = this.statusMap[statusKey];
    return {
      status: statusKey,
      label: status.label,
      class: status.class,
      icon: status.icon
    };
  }

  onMissionFilterChange(): void {
    this.selectedProject = null;
    this.reports = [];
    this.filteredReports = [];
    this.loadProjects();
  }

  loadReports(): void {
    if (!this.selectedProject) {
      this.isLoading = false;
      return;
    }
    
    this.isLoading = true;
    this.reportViewerService.getReportsByProject(this.selectedProject.id).subscribe({
      next: (reports) => {
        this.reports = reports;
        this.applyFilters();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading reports:', err);
        this.isLoading = false;
        Swal.fire({ icon: 'error', title: 'Error', text: 'Failed to load reports' });
      }
    });
  }

  // ========== PROJECT SELECTION ==========

  selectProject(project: Project): void {
    this.selectedProject = project;
    this.loadReports();
    if (this.isMobile) {
      this.toggleSidebar();
    }
  }

  // ========== FILTERS & SORTING ==========

  applyFilters(): void {
    let filtered = [...this.reports];
    
    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase().trim();
      filtered = filtered.filter(report =>
        report.titre.toLowerCase().includes(term) ||
        (report.resume && report.resume.toLowerCase().includes(term))
      );
    }
    
    if (this.dateFrom) {
      filtered = filtered.filter(report => report.date >= this.dateFrom);
    }
    if (this.dateTo) {
      filtered = filtered.filter(report => report.date <= this.dateTo);
    }
    
    filtered = this.sortReports(filtered);
    
    this.filteredReports = filtered;
    this.currentPage = 1;
    this.updatePagination();
  }

  sortReports(reports: Report[]): Report[] {
    return [...reports].sort((a, b) => {
      let comparison = 0;
      
      switch (this.sortBy) {
        case 'title':
          comparison = a.titre.localeCompare(b.titre);
          break;
        case 'date':
          comparison = new Date(a.date).getTime() - new Date(b.date).getTime();
          break;
        default:
          comparison = 0;
      }
      
      return this.sortOrder === 'asc' ? comparison : -comparison;
    });
  }

  onSearchInput(): void {
    this.searchSubject.next(this.searchTerm);
  }

  clearSearch(): void {
    this.searchTerm = '';
    this.applyFilters();
  }

  resetFilters(): void {
    this.searchTerm = '';
    this.sortBy = 'date';
    this.sortOrder = 'desc';
    this.dateFrom = '';
    this.dateTo = '';
    this.applyFilters();
  }

  isFilterApplied(): boolean {
    return this.searchTerm !== '' ||
           this.sortBy !== 'date' ||
           this.sortOrder !== 'desc' ||
           this.dateFrom !== '' ||
           this.dateTo !== '';
  }

  toggleFilters(): void {
    this.showFilters = !this.showFilters;
  }

  // ========== PAGINATION ==========

  updatePagination(): void {
    this.totalPages = Math.ceil(this.filteredReports.length / this.pageSize);
    if (this.totalPages === 0) this.totalPages = 1;
    
    if (this.currentPage > this.totalPages) {
      this.currentPage = this.totalPages;
    }
    
    const startIndex = (this.currentPage - 1) * this.pageSize;
    this.paginatedReports = this.filteredReports.slice(startIndex, startIndex + this.pageSize);
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

  // ========== REPORT DETAIL ==========

  viewReportDetail(report: Report): void {
    this.selectedReport = report;
    this.showReportDetail = true;
  }

  closeReportDetail(): void {
    this.showReportDetail = false;
    this.selectedReport = null;
  }

  // ========== SIDEBAR & HELPERS ==========

  toggleSidebar(): void {
    this.showSidebar = !this.showSidebar;
  }

  checkScreenSize(): void {
    this.isMobile = window.innerWidth < 768;
    if (this.isMobile) {
      this.showSidebar = false;
    } else {
      this.showSidebar = true;
    }
  }

  formatDate(date: string): string {
    if (!date) return 'N/A';
    return new Date(date).toLocaleDateString('en-US', {
      day: '2-digit',
      month: 'short',
      year: 'numeric'
    });
  }

  getProjectStatusClass(project: any): string {
    if (project.statusInfo) {
      return project.statusInfo.class;
    }
    if (project.annule === true) {
      return 'status-cancelled';
    }
    if (project.progression >= 100) return 'status-completed';
    if (project.progression >= 75) return 'status-progress';
    if (project.progression >= 50) return 'status-pending';
    return 'status-planned';
  }

  getProjectStatusText(project: any): string {
    if (project.statusInfo) {
      return project.statusInfo.label;
    }
    if (project.annule === true) {
      return 'Cancelled';
    }
    if (project.progression >= 100) return 'Completed';
    if (project.progression >= 75) return 'In Progress';
    if (project.progression >= 50) return 'On Hold';
    return 'Planned';
  }

  getProjectStatusIcon(project: any): string {
    if (project.statusInfo) {
      return project.statusInfo.icon;
    }
    if (project.annule === true) {
      return 'fas fa-ban';
    }
    return 'far fa-calendar-alt';
  }

  getProgressColor(progress: number): string {
    if (progress >= 80) return '#10b981';
    if (progress >= 50) return '#f59e0b';
    return '#ef4444';
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    window.removeEventListener('resize', () => this.checkScreenSize());
  }
}