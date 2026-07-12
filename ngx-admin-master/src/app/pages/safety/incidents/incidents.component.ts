// src/app/pages/safety/incidents/incidents.component.ts

import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, takeUntil } from 'rxjs/operators';
import { IncidentApiService, Incident, IncidentRequest } from '../../../services/incident/incident-api.service';
import { EmployeApiService, Employe } from '../../../services/incident/employe-api.service';
import Swal from 'sweetalert2';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-incidents',
  templateUrl: './incidents.component.html',
  styleUrls: ['./incidents.component.scss']
})
export class IncidentsComponent implements OnInit, OnDestroy {
  
  // Data
  incidents: Incident[] = [];
  filteredIncidents: Incident[] = [];
  employees: Employe[] = [];
  
  // UI State
  isLoading: boolean = false;
  showAddModal: boolean = false;
  showEditModal: boolean = false;
  showFilters: boolean = false;
  selectedIncident: Incident | null = null;
  
  // Form
  incidentForm: FormGroup;
  
  // Filters
  searchTerm: string = '';
  typeFilter: string = 'all';
  graviteFilter: string = 'all';
  sortBy: string = 'date';
  sortOrder: string = 'desc';
  
  // Pagination
  currentPage: number = 1;
  pageSize: number = 10;
  totalPages: number = 1;
  paginatedIncidents: Incident[] = [];
  
  // Statistics
  statsByType: { [key: string]: number } = {};
  statsByGravite: { [key: string]: number } = {};
  
  Math = Math;
  
  // أضف بعد المتغيرات الموجودة
canManageIncidents: boolean = false;

  // Options
  typeOptions = [
    { value: 'ACCIDENT_TRAVAIL', label: 'Accident de travail', color: '#ef4444' },
    { value: 'MALADIE_PROFESSIONNELLE', label: 'Maladie professionnelle', color: '#f97316' },
    { value: 'INCIDENT_SECURITE', label: 'Incident de sécurité', color: '#f59e0b' },
    { value: 'INCIDENT_ENVIRONNEMENTAL', label: 'Incident environnemental', color: '#10b981' },
    { value: 'AUTRE', label: 'Autre', color: '#6b7280' }
  ];
  
  graviteOptions = [
    { value: 'CRITIQUE', label: 'Critique', color: '#ef4444', level: 4 },
    { value: 'ELEVE', label: 'Élevé', color: '#f97316', level: 3 },
    { value: 'MOYEN', label: 'Moyen', color: '#f59e0b', level: 2 },
    { value: 'FAIBLE', label: 'Faible', color: '#10b981', level: 1 }
  ];
  
  private destroy$ = new Subject<void>();
  private searchSubject = new Subject<string>();

  constructor(
    private fb: FormBuilder,
    private incidentApiService: IncidentApiService,
    private employeApiService: EmployeApiService,
      private authService: AuthService
  ) {
    this.incidentForm = this.fb.group({
      type: ['', Validators.required],
      description: ['', Validators.required],
      dateIncident: [new Date().toISOString().split('T')[0], Validators.required],
      niveauGravite: ['', Validators.required],
      employeId: [null, Validators.required],
      coordonnee: this.fb.group({
        latitude: [''],
        longitude: [''],
        ordre: [1]
        // siteId تمت إزالته - ليس ضرورياً للحادث
      })
    });
  }

  ngOnInit(): void {
      this.initializePermissions();
    this.loadIncidents();
    this.loadEmployees();
    this.loadStatistics();
    
    this.searchSubject.pipe(
      debounceTime(500),
      distinctUntilChanged(),
      takeUntil(this.destroy$)
    ).subscribe(() => {
      this.applyFilters();
    });
  }

  // أضف هذه الدالة بعد ngOnInit أو داخله
initializePermissions(): void {
  const userRoles = this.authService.getCurrentUser()?.roles || [];
  const hasChefTerrainRole = userRoles.includes('CHEF_TERRAIN');
  const hasAdminRole = userRoles.includes('ADMIN');
  
  // Only CHEF_TERRAIN and ADMINISTRATEUR can manage incidents
  this.canManageIncidents = hasChefTerrainRole || hasAdminRole;
  
  console.log('[DEBUG] User roles:', userRoles);
  console.log('[DEBUG] Can manage incidents:', this.canManageIncidents);
}
  // ========== LOAD DATA ==========
  
  loadIncidents(): void {
    this.isLoading = true;
    this.incidentApiService.getAllIncidents().subscribe({
      next: (incidents) => {
        this.incidents = incidents;
        this.applyFilters();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading incidents:', err);
        this.isLoading = false;
        Swal.fire('Error', 'Failed to load incidents', 'error');
      }
    });
  }
  
  loadEmployees(): void {
    this.employeApiService.getAllEmployes().subscribe({
      next: (employees) => {
        this.employees = employees;
      },
      error: (err) => {
        console.error('Error loading employees:', err);
      }
    });
  }
  
  loadStatistics(): void {
    this.incidentApiService.getStatsByType().subscribe({
      next: (stats) => {
        this.statsByType = stats;
      },
      error: (err) => console.error('Error loading type stats:', err)
    });
    
    this.incidentApiService.getStatsByGravite().subscribe({
      next: (stats) => {
        this.statsByGravite = stats;
      },
      error: (err) => console.error('Error loading gravite stats:', err)
    });
  }

  // ========== FILTERS & SORTING ==========

  applyFilters(): void {
    let filtered = [...this.incidents];
    
    // Search filter
    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase().trim();
      filtered = filtered.filter(incident =>
        incident.description?.toLowerCase().includes(term) ||
        incident.employeFullName?.toLowerCase().includes(term) ||
        incident.typeLabel?.toLowerCase().includes(term)
      );
    }
    
    // Type filter
    if (this.typeFilter !== 'all') {
      filtered = filtered.filter(incident => incident.type === this.typeFilter);
    }
    
    // Severity filter
    if (this.graviteFilter !== 'all') {
      filtered = filtered.filter(incident => incident.niveauGravite === this.graviteFilter);
    }
    
    // Sorting
    filtered = this.sortIncidents(filtered);
    
    this.filteredIncidents = filtered;
    this.currentPage = 1;
    this.updatePagination();
  }

  sortIncidents(incidents: Incident[]): Incident[] {
    return incidents.sort((a, b) => {
      let comparison = 0;
      
      switch (this.sortBy) {
        case 'date':
          comparison = new Date(a.dateIncident).getTime() - new Date(b.dateIncident).getTime();
          break;
        case 'type':
          comparison = (a.typeLabel || '').localeCompare(b.typeLabel || '');
          break;
        case 'severity':
          const orderA = this.graviteOptions.find(g => g.value === a.niveauGravite)?.level || 0;
          const orderB = this.graviteOptions.find(g => g.value === b.niveauGravite)?.level || 0;
          comparison = orderA - orderB;
          break;
        case 'employee':
          comparison = (a.employeFullName || '').localeCompare(b.employeFullName || '');
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
    this.typeFilter = 'all';
    this.graviteFilter = 'all';
    this.sortBy = 'date';
    this.sortOrder = 'desc';
    this.applyFilters();
  }

  isFilterApplied(): boolean {
    return this.searchTerm !== '' ||
           this.typeFilter !== 'all' ||
           this.graviteFilter !== 'all' ||
           this.sortBy !== 'date' ||
           this.sortOrder !== 'desc';
  }

  toggleFilters(): void {
    this.showFilters = !this.showFilters;
  }

  // ========== PAGINATION ==========

  updatePagination(): void {
    this.totalPages = Math.ceil(this.filteredIncidents.length / this.pageSize);
    if (this.totalPages === 0) this.totalPages = 1;
    
    if (this.currentPage > this.totalPages) {
      this.currentPage = this.totalPages;
    }
    
    const startIndex = (this.currentPage - 1) * this.pageSize;
    this.paginatedIncidents = this.filteredIncidents.slice(startIndex, startIndex + this.pageSize);
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

  // ========== MODAL CONTROLS ==========

openAddModal(): void {
  if (!this.canManageIncidents) {
    Swal.fire({ icon: 'warning', title: 'Access Denied', text: 'You do not have permission to report incidents' });
    return;
  }
  this.incidentForm.reset({
    type: '',
    description: '',
    dateIncident: new Date().toISOString().split('T')[0],
    niveauGravite: '',
    employeId: null,
    coordonnee: {
      latitude: '',
      longitude: '',
      ordre: 1
    }
  });
  this.showAddModal = true;
}

  closeAddModal(): void {
    this.showAddModal = false;
  }

openEditModal(incident: Incident): void {
  if (!this.canManageIncidents) {
    Swal.fire({ icon: 'warning', title: 'Access Denied', text: 'You do not have permission to edit incidents' });
    return;
  }
  this.selectedIncident = incident;
  this.incidentForm.patchValue({
    type: incident.type,
    description: incident.description,
    dateIncident: incident.dateIncident,
    niveauGravite: incident.niveauGravite,
    employeId: incident.employeId,
    coordonnee: {
      latitude: incident.latitude || '',
      longitude: incident.longitude || '',
      ordre: incident.ordre || 1
    }
  });
  this.showEditModal = true;
}

  closeEditModal(): void {
    this.showEditModal = false;
    this.selectedIncident = null;
  }

  // ========== CRUD OPERATIONS ==========

  createIncident(): void {
  
  if (!this.canManageIncidents) {
    Swal.fire({ icon: 'warning', title: 'Access Denied', text: 'You do not have permission to report incidents' });
    return;
  }
 
    if (this.incidentForm.invalid) {
      Swal.fire('Warning', 'Please fill all required fields', 'warning');
      return;
    }
    
    const incidentRequest: IncidentRequest = this.incidentForm.value;
    
    this.isLoading = true;
    this.incidentApiService.createIncident(incidentRequest).subscribe({
      next: () => {
        Swal.fire('Success', 'Incident reported successfully', 'success');
        this.closeAddModal();
        this.loadIncidents();
        this.loadStatistics();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error creating incident:', err);
        this.isLoading = false;
        Swal.fire('Error', 'Failed to report incident', 'error');
      }
    });
  }

  updateIncident(): void {
      if (!this.canManageIncidents) {
    Swal.fire({ icon: 'warning', title: 'Access Denied', text: 'You do not have permission to update incidents' });
    return;
  }

    if (this.incidentForm.invalid || !this.selectedIncident) {
      Swal.fire('Warning', 'Please fill all required fields', 'warning');
      return;
    }
    
    const incidentRequest: IncidentRequest = this.incidentForm.value;
    
    this.isLoading = true;
    this.incidentApiService.updateIncident(this.selectedIncident.id, incidentRequest).subscribe({
      next: () => {
        Swal.fire('Success', 'Incident updated successfully', 'success');
        this.closeEditModal();
        this.loadIncidents();
        this.loadStatistics();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error updating incident:', err);
        this.isLoading = false;
        Swal.fire('Error', 'Failed to update incident', 'error');
      }
    });
  }

deleteIncident(incident: Incident): void {
  if (!this.canManageIncidents) {
    Swal.fire({ icon: 'warning', title: 'Access Denied', text: 'You do not have permission to delete incidents' });
    return;
  }
  Swal.fire({
    title: 'Delete Incident?',
    text: `Are you sure you want to delete this incident?`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#d33',
    confirmButtonText: 'Yes, Delete',
    cancelButtonText: 'Cancel'
  }).then((result) => {
      if (result.isConfirmed) {
        this.isLoading = true;
        this.incidentApiService.deleteIncident(incident.id).subscribe({
          next: () => {
            Swal.fire('Deleted', 'Incident deleted successfully', 'success');
            this.loadIncidents();
            this.loadStatistics();
            this.isLoading = false;
          },
          error: (err) => {
            console.error('Error deleting incident:', err);
            this.isLoading = false;
            Swal.fire('Error', 'Failed to delete incident', 'error');
          }
        });
      }
    });
  }

  // ========== HELPER METHODS ==========

  getTypeLabel(type: string): string {
    const option = this.typeOptions.find(t => t.value === type);
    return option?.label || type;
  }

  getTypeColor(type: string): string {
    const option = this.typeOptions.find(t => t.value === type);
    return option?.color || '#6b7280';
  }

  getGraviteLabel(gravite: string): string {
    const option = this.graviteOptions.find(g => g.value === gravite);
    return option?.label || gravite;
  }

  getGraviteColor(gravite: string): string {
    const option = this.graviteOptions.find(g => g.value === gravite);
    return option?.color || '#6b7280';
  }

  formatDate(date: string): string {
    if (!date) return 'N/A';
    return new Date(date).toLocaleDateString('en-US', {
      day: '2-digit',
      month: 'long',
      year: 'numeric'
    });
  }

  // Add this method to your IncidentsComponent class

getEmployeeName(employeeId: number): string {
  const employee = this.employees.find(e => e.id === employeeId);
  return employee ? `${employee.prenom} ${employee.nom}` : `Employee #${employeeId}`;
}

getMin(a: number, b: number): number {
  return Math.min(a, b);
}

closeModal(): void {
  if (this.showAddModal) {
    this.closeAddModal();
  } else if (this.showEditModal) {
    this.closeEditModal();
  }
}

submitForm(): void {
  if (this.showAddModal) {
    this.createIncident();
  } else if (this.showEditModal) {
    this.updateIncident();
  }
}

isRecentIncident(dateString: string): boolean {
  if (!dateString) return false;
  const incidentDate = new Date(dateString);
  const thirtyDaysAgo = new Date();
  thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30);
  return incidentDate >= thirtyDaysAgo;
}

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}