import { Component, OnInit, OnDestroy, ViewChild, ElementRef, AfterViewInit,Input } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CategorieMaterielService, CategorieMateriel } from '../../services/materiel/categorie-materiel.service';
import { TypeMaterielService, TypeMateriel } from '../../services/materiel/type-materiel.service';
import { MaterielService, Materiel } from '../../services/materiel/materiel.service';
import { AffectationMaterielService, AffectationMateriel } from '../../services/materiel/affectation-materiel.service';
import { AffectationMaterielToActiveService, AffectationMaterielToActiveDTO, AssignMaterielToActiveRequest } from '../../services/materiel/affectation-materiel-to-active.service';
import { NbDialogService } from '@nebular/theme';
import { MissionService, ActiveDTO } from '../../services/mission/mission.service';

import { 
  trigger, 
  transition, 
  style, 
  animate, 
  keyframes, 
  state,
  query,
  stagger,
  group
} from '@angular/animations';
import { AssignActivityModalComponent } from './assign-activity-modal/assign-activity-modal.component';

import { Subject } from 'rxjs';
import { takeUntil, debounceTime, distinctUntilChanged } from 'rxjs/operators';
import Swal from 'sweetalert2';
import { MaterielDetailModalComponent } from './materiel-detail-modal/materiel-detail-modal.component';

@Component({
  selector: 'ngx-materiel',
  templateUrl: './materiel.component.html',
  styleUrls: ['./materiel.component.scss'],
  animations: [
    // Container Animation
    trigger('containerAnimation', [
      transition(':enter', [
        style({ opacity: 0 }),
        animate('300ms ease-in', style({ opacity: 1 }))
      ])
    ]),

    // Sidebar Animation
    trigger('sidebarAnimation', [
      state('expanded', style({
        width: '320px',
        opacity: 1
      })),
      state('collapsed', style({
        width: '0px',
        opacity: 0,
        transform: 'translateX(-100%)'
      })),
      transition('expanded <=> collapsed', [
        animate('300ms cubic-bezier(0.4, 0.0, 0.2, 1)')
      ])
    ]),

    // Content Animation
    trigger('contentAnimation', [
      transition(':enter', [
        style({ opacity: 0 }),
        animate('400ms ease-out', style({ opacity: 1 }))
      ])
    ]),

    // Mobile Button Animation
    trigger('mobileButtonAnimation', [
      transition(':enter', [
        style({ opacity: 0, transform: 'scale(0.8)' }),
        animate('200ms ease-out', style({ opacity: 1, transform: 'scale(1)' }))
      ])
    ]),

    // Icon Pulse Animation
    trigger('iconPulse', [
      transition('* => *', [
        animate('500ms ease-in-out', keyframes([
          style({ transform: 'scale(1)', offset: 0 }),
          style({ transform: 'scale(1.2)', offset: 0.5 }),
          style({ transform: 'scale(1)', offset: 1 })
        ]))
      ])
    ]),

    // Search Animation
    trigger('searchAnimation', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(-10px)' }),
        animate('200ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ]),
      transition(':leave', [
        animate('200ms ease-in', style({ opacity: 0, transform: 'translateY(-10px)' }))
      ])
    ]),

    // Slide Down Animation
    trigger('slideDown', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(-20px)', height: '0px' }),
        animate('300ms cubic-bezier(0.4, 0.0, 0.2, 1)', 
          style({ opacity: 1, transform: 'translateY(0)', height: '*' }))
      ]),
      transition(':leave', [
        animate('300ms cubic-bezier(0.4, 0.0, 0.2, 1)', 
          style({ opacity: 0, transform: 'translateY(-20px)', height: '0px' }))
      ])
    ]),

    // Slide In Right Animation
    trigger('slideInRight', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateX(30px)' }),
        animate('300ms ease-out', style({ opacity: 1, transform: 'translateX(0)' }))
      ])
    ]),

    // Fade In Animation
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0 }),
        animate('200ms ease-in', style({ opacity: 1 }))
      ]),
      transition(':leave', [
        animate('200ms ease-out', style({ opacity: 0 }))
      ])
    ]),

    // Fade In Up Animation
    trigger('fadeInUp', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(20px)' }),
        animate('400ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),

    // Icon Rotate Animation
    trigger('iconRotate', [
      transition(':enter', [
        style({ transform: 'rotate(0deg)' }),
        animate('500ms ease-out', style({ transform: 'rotate(360deg)' }))
      ])
    ]),

    // Category Animation
    trigger('categoryAnimation', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateX(-15px)' }),
        animate('250ms ease-out', style({ opacity: 1, transform: 'translateX(0)' }))
      ]),
      transition(':leave', [
        animate('250ms ease-in', style({ opacity: 0, transform: 'translateX(-15px)' }))
      ])
    ]),

    // List Animation (Stagger)
    trigger('listAnimation', [
      transition(':enter', [
        query('.type-card, .materiel-card', [
          style({ opacity: 0, transform: 'translateY(20px)' }),
          stagger('50ms', [
            animate('300ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
          ])
        ], { optional: true })
      ])
    ]),

    // Grid Animation
    trigger('gridAnimation', [
      transition(':enter', [
        query('.materiel-card', [
          style({ opacity: 0, transform: 'scale(0.95)' }),
          stagger('80ms', [
            animate('350ms cubic-bezier(0.34, 1.56, 0.64, 1)', 
              style({ opacity: 1, transform: 'scale(1)' }))
          ])
        ], { optional: true })
      ])
    ]),

    // Card Animation
    trigger('cardAnimation', [
      transition(':enter', [
        style({ opacity: 0, transform: 'scale(0.9)' }),
        animate('200ms ease-out', style({ opacity: 1, transform: 'scale(1)' }))
      ])
    ]),

    // Modal Animation
    trigger('modalAnimation', [
      transition(':enter', [
        style({ opacity: 0, transform: 'scale(0.9)' }),
        animate('200ms ease-out', style({ opacity: 1, transform: 'scale(1)' }))
      ]),
      transition(':leave', [
        animate('200ms ease-in', style({ opacity: 0, transform: 'scale(0.9)' }))
      ])
    ])
  ]
})
export class MaterielComponent implements OnInit, OnDestroy, AfterViewInit {

    // ✅ ADD THESE INPUT PROPERTIES
  @Input() missionId: number | null = null;
  @Input() projectId: number | null = null;

  @ViewChild('categoryInput') categoryInput!: ElementRef;
  @ViewChild('typeInput') typeInput!: ElementRef;
  @ViewChild('editCategoryInput') editCategoryInput!: ElementRef;
  @ViewChild('editTypeInput') editTypeInput!: ElementRef;

  // Category data
  categories: CategorieMateriel[] = [];
  selectedCategory: CategorieMateriel | null = null;
  searchTerm: string = '';
  
  // Type data
  types: TypeMateriel[] = [];
  selectedTypeId: number | null = null;
  
  // Materiel data
  materiels: Materiel[] = [];
  filteredMateriels: Materiel[] = [];
  
  // Category UI States
  isAddingCategory: boolean = false;
  newCategoryName: string = '';
  editingCategoryId: number | null = null;
  editingCategoryName: string = '';
  
  // Type UI States
  isAddingType: boolean = false;
  newTypeName: string = '';
  editingTypeId: number | null = null;
  editingTypeName: string = '';
  
  // Materiel UI States
  isAddingMateriel: boolean = false;
  newMateriel: Partial<Materiel> = {};
  
  // State
  isLoading: boolean = false;
  isMobile: boolean = false;
  sidebarCollapsed: boolean = false;
  private destroy$ = new Subject<void>();
  public searchSubject = new Subject<string>();

  // Selection mode properties
  selectedMaterielsForAssignment: Set<number> = new Set();
  showSelectionMode = false;
  equipmentSearchTerm: string = '';

  // Counts maps
  categoryMaterielCounts: Map<number, number> = new Map();
  typeMaterielCounts: Map<number, number> = new Map();

  // Pagination
  Math = Math;
  currentPage: number = 1;
  pageSize: number = 12;
  totalPages: number = 1;

  // Advanced Filters
  showAdvancedFilters: boolean = false;
  filterStatus: string = '';
  filterBrand: string = '';
  filterDateFrom: string = '';
  filterDateTo: string = '';
  filterPriceMin: number | null = null;
  filterPriceMax: number | null = null;
  filterAvailability: string = '';
  filterActivityCode: string = '';
  sortField: string = 'codeMateriel';
  sortDirection: string = 'asc';

  // Store affectations and current activity for each materiel
  affectationsMap: Map<number, AffectationMateriel[]> = new Map();
  currentActivityMap: Map<number, string> = new Map();

  // Store activities for the modal
  availableActivities: ActiveDTO[] = [];
  currentMissionId: number | null = null;
  currentProjectId: number | null = null;

  constructor(
    private route: ActivatedRoute,
    private categorieService: CategorieMaterielService,
    private typeService: TypeMaterielService,
    private materielService: MaterielService,
    private affectationService: AffectationMaterielService,
    private affectationToActiveService: AffectationMaterielToActiveService,
    private dialogService: NbDialogService,
    private missionService: MissionService,
  ) {}

  ngOnInit() {
    this.route.params.subscribe(params => {
      const categoryId = params['id'];
      if (categoryId) {
        this.loadCategories();
        this.loadTypes(categoryId);
      } else {
        this.loadCategories();
      }
    });
    
    this.checkScreenSize();
    window.addEventListener('resize', () => this.checkScreenSize());
    
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      takeUntil(this.destroy$)
    ).subscribe(() => {
      this.applyFilter();
    });

    // Load current mission and project
    this.loadCurrentMissionAndProject();
  }

  // ==================== LOAD CURRENT MISSION AND PROJECT ====================

  loadCurrentMissionAndProject() {
    this.missionService.getMyCurrentMission().pipe(
      takeUntil(this.destroy$)
    ).subscribe({
      next: (response) => {
        if (response && response.missionId) {
          this.currentMissionId = response.missionId;
          this.loadActivitiesForMission();
          this.loadCurrentProject();
        }
      },
      error: (err) => {
        console.error('Error loading current mission:', err);
      }
    });
  }

 loadActivitiesForMission() {
  if (!this.currentMissionId) return;
  
  this.missionService.getActivesByMission(this.currentMissionId).pipe(
    takeUntil(this.destroy$)
  ).subscribe({
    next: (activities) => {
      console.log('[DEBUG] Activities loaded:', activities); // Add debug log
      this.availableActivities = activities;
    },
    error: (err) => {
      console.error('Error loading activities:', err);
      this.availableActivities = [];
    }
  });
}

  loadCurrentProject() {
    if (!this.currentMissionId) return;
    
    this.missionService.getCurrentProjectByMission(this.currentMissionId).pipe(
      takeUntil(this.destroy$)
    ).subscribe({
      next: (project) => {
        if (project) {
          this.currentProjectId = project.id;
        }
      },
      error: (err) => {
        console.error('Error loading current project:', err);
      }
    });
  }

  // ==================== GETTERS ====================

  get uniqueBrands(): string[] {
    const brands = new Set(this.materiels.map(m => m.marque).filter(b => b));
    return Array.from(brands).sort();
  }

  get uniqueActivityCodes(): string[] {
    const activities = new Set<string>();
    this.materiels.forEach(m => {
      const activityCode = this.getCurrentActivityCode(m.idMateriel);
      if (activityCode) {
        activities.add(activityCode);
      }
    });
    return Array.from(activities).sort();
  }

  // ==================== AVAILABILITY METHODS ====================

  getCurrentActivityCode(materielId: number): string {
    return this.currentActivityMap.get(materielId) || '';
  }

  isMaterielAvailable(materiel: Materiel): boolean {
    return !materiel.enUtilisation && materiel.status === 'EN_BON_ETAT';
  }

  getAvailabilityStatus(materiel: Materiel): { text: string, color: string, icon: string } {
    if (materiel.enUtilisation) {
      const activityCode = this.getCurrentActivityCode(materiel.idMateriel);
      return {
        text: activityCode ? `Assigned to: ${activityCode}` : 'In Use',
        color: '#f59e0b',
        icon: 'briefcase-outline'
      };
    } else if (materiel.status !== 'EN_BON_ETAT') {
      return {
        text: 'Not Available (Needs Repair)',
        color: '#ef4444',
        icon: 'alert-circle-outline'
      };
    } else {
      return {
        text: 'Available',
        color: '#10b981',
        icon: 'checkmark-circle-outline'
      };
    }
  }

  // ==================== LOADING METHODS ====================

  loadCategories() {
    this.isLoading = true;
    this.categorieService.getAll().subscribe({
      next: (data) => {
        this.categories = data;
        this.loadCategoryMaterielCounts();
        
        const id = this.route.snapshot.params['id'];
        if (id) {
          this.selectedCategory = this.categories.find(c => c.idCategorie == id) || null;
          if (this.selectedCategory) {
            this.loadTypes(this.selectedCategory.idCategorie);
          }
        }
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading categories:', err);
        this.isLoading = false;
        Swal.fire({
          icon: 'error',
          title: 'Connection Error',
          text: 'Failed to load categories. Make sure the backend is running.',
          confirmButtonText: 'OK'
        });
      }
    });
  }

  loadCategoryMaterielCounts() {
    this.categories.forEach(category => {
      this.materielService.getByCategorie(category.idCategorie).subscribe({
        next: (materiels) => {
          this.categoryMaterielCounts.set(category.idCategorie, materiels.length);
        },
        error: (err) => console.error('Error loading category count:', err)
      });
    });
  }

  loadTypes(categoryId: number) {
    this.isLoading = true;
    this.typeService.getByCategorie(categoryId).subscribe({
      next: (data) => {
        this.types = data;
        this.loadTypeMaterielCounts();
        this.loadMaterielsByCategory(categoryId);
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading types:', err);
        this.isLoading = false;
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'Failed to load types',
          toast: true,
          position: 'top-end',
          showConfirmButton: false,
          timer: 3000
        });
      }
    });
  }

  loadTypeMaterielCounts() {
    this.types.forEach(type => {
      this.materielService.getByType(type.idTypeMateriel).subscribe({
        next: (materiels) => {
          this.typeMaterielCounts.set(type.idTypeMateriel, materiels.length);
        },
        error: (err) => console.error('Error loading type count:', err)
      });
    });
  }

  loadMaterielsByCategory(categoryId: number) {
    this.isLoading = true;
    this.materielService.getByCategorie(categoryId).subscribe({
      next: (data) => {
        this.materiels = data;
        this.loadAllAffectationsForMateriels(data);
        this.applyFilter();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading materiels:', err);
        this.isLoading = false;
      }
    });
  }

  loadMaterielsByType(typeId: number) {
    this.isLoading = true;
    this.materielService.getByType(typeId).subscribe({
      next: (data) => {
        this.materiels = data;
        this.loadAllAffectationsForMateriels(data);
        this.applyFilter();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading materiels by type:', err);
        this.isLoading = false;
      }
    });
  }

  loadAllAffectationsForMateriels(materiels: Materiel[]) {
    if (materiels.length === 0) return;
    
    this.affectationService.getAll().subscribe({
      next: (allAffectations) => {
        const affectationsByMateriel = new Map<number, AffectationMateriel[]>();
        const currentActivityMap = new Map<number, string>();
        
        allAffectations.forEach(affectation => {
          const materielId = affectation.materielId;
          if (!affectationsByMateriel.has(materielId)) {
            affectationsByMateriel.set(materielId, []);
          }
          affectationsByMateriel.get(materielId)!.push(affectation);
        });
        
        const today = new Date().toISOString().split('T')[0];
        materiels.forEach(materiel => {
          const affectations = affectationsByMateriel.get(materiel.idMateriel) || [];
          const currentAffectation = affectations.find(a => 
            !a.dateFin || a.dateFin >= today
          );
          
          if (currentAffectation && currentAffectation.missionCode) {
            currentActivityMap.set(materiel.idMateriel, currentAffectation.missionCode);
          } else {
            currentActivityMap.set(materiel.idMateriel, '');
          }
        });
        
        this.currentActivityMap = currentActivityMap;
        this.applyFilter();
      },
      error: (err) => console.error('Error loading affectations:', err)
    });
  }

  // ==================== FILTER METHODS ====================

  toggleAdvancedFilters() {
    this.showAdvancedFilters = !this.showAdvancedFilters;
  }

  hasActiveFilters(): boolean {
    return !!(this.filterStatus || 
      this.filterBrand || 
      this.filterDateFrom || 
      this.filterDateTo || 
      this.filterPriceMin !== null || 
      this.filterPriceMax !== null ||
      this.filterAvailability ||
      (this.searchTerm && this.searchTerm.trim()) ||
      this.sortField !== 'codeMateriel');
  }

  setSortDirection(direction: string) {
    this.sortDirection = direction;
    this.applyFilter();
  }

  removeFilter(filterType: string) {
    switch(filterType) {
      case 'status':
        this.filterStatus = '';
        break;
      case 'brand':
        this.filterBrand = '';
        break;
      case 'date':
        this.filterDateFrom = '';
        this.filterDateTo = '';
        break;
      case 'price':
        this.filterPriceMin = null;
        this.filterPriceMax = null;
        break;
      case 'availability':
        this.filterAvailability = '';
        this.filterActivityCode = '';
        break;
      case 'activity':
        this.filterActivityCode = '';
        this.applyFilter();
        break;
    }
    this.applyFilter();
  }

  clearAllFilters() {
    this.searchTerm = '';
    this.filterStatus = '';
    this.filterBrand = '';
    this.filterDateFrom = '';
    this.filterDateTo = '';
    this.filterPriceMin = null;
    this.filterPriceMax = null;
    this.filterAvailability = '';
    this.filterActivityCode = '';
    this.sortField = 'codeMateriel';
    this.sortDirection = 'asc';
    this.applyFilter();
    this.showAdvancedFilters = false;
  }

  applyFilter() {
    let filtered = [...this.materiels];
    
    if (this.searchTerm && this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(m => 
        m.codeMateriel?.toLowerCase().includes(term) ||
        m.marque?.toLowerCase().includes(term) ||
        m.modele?.toLowerCase().includes(term) ||
        m.designation?.toLowerCase().includes(term)
      );
    }
    
    if (this.filterStatus) {
      filtered = filtered.filter(m => m.status === this.filterStatus);
    }
    
    if (this.filterBrand) {
      filtered = filtered.filter(m => m.marque === this.filterBrand);
    }
    
    if (this.filterAvailability === 'available') {
      filtered = filtered.filter(m => !m.enUtilisation && m.status === 'EN_BON_ETAT');
    } else if (this.filterAvailability === 'assigned') {
      filtered = filtered.filter(m => m.enUtilisation === true);
      
      if (this.filterActivityCode && this.filterActivityCode.trim() !== '') {
        filtered = filtered.filter(m => {
          const activityCode = this.getCurrentActivityCode(m.idMateriel);
          return activityCode === this.filterActivityCode;
        });
      }
    }
    
    if (this.filterDateFrom) {
      const fromDate = new Date(this.filterDateFrom);
      filtered = filtered.filter(m => m.dateAchat && new Date(m.dateAchat) >= fromDate);
    }
    if (this.filterDateTo) {
      const toDate = new Date(this.filterDateTo);
      toDate.setHours(23, 59, 59);
      filtered = filtered.filter(m => m.dateAchat && new Date(m.dateAchat) <= toDate);
    }
    
    if (this.filterPriceMin !== null) {
      filtered = filtered.filter(m => (m.prix || 0) >= this.filterPriceMin!);
    }
    if (this.filterPriceMax !== null) {
      filtered = filtered.filter(m => (m.prix || 0) <= this.filterPriceMax!);
    }
    
    filtered = this.sortMateriels(filtered);
    
    this.filteredMateriels = filtered;
    this.updatePagination();
    this.currentPage = 1;
  }

  sortMateriels(materiels: Materiel[]): Materiel[] {
    return [...materiels].sort((a, b) => {
      let aVal: any = a[this.sortField as keyof Materiel];
      let bVal: any = b[this.sortField as keyof Materiel];
      
      if (this.sortField === 'dateAchat') {
        aVal = aVal ? new Date(aVal).getTime() : 0;
        bVal = bVal ? new Date(bVal).getTime() : 0;
      }
      
      if (typeof aVal === 'number' && typeof bVal === 'number') {
        return this.sortDirection === 'asc' ? aVal - bVal : bVal - aVal;
      }
      
      const aStr = String(aVal || '').toLowerCase();
      const bStr = String(bVal || '').toLowerCase();
      
      if (this.sortDirection === 'asc') {
        return aStr.localeCompare(bStr);
      } else {
        return bStr.localeCompare(aStr);
      }
    });
  }

  // ==================== SELECTION MODE METHODS ====================
  
  toggleSelectionMode() {
    this.showSelectionMode = !this.showSelectionMode;
    if (!this.showSelectionMode) {
      this.clearSelection();
      this.equipmentSearchTerm = '';
    } else {
      this.searchTerm = '';
    }
    this.currentPage = 1;
    this.updatePagination();
  }

  toggleMaterielSelection(materielId: number, event?: Event) {
    if (event) {
      event.stopPropagation();
    }
    if (this.selectedMaterielsForAssignment.has(materielId)) {
      this.selectedMaterielsForAssignment.delete(materielId);
    } else {
      this.selectedMaterielsForAssignment.add(materielId);
    }
  }

  isMaterielSelected(materielId: number): boolean {
    return this.selectedMaterielsForAssignment.has(materielId);
  }

  getSelectedCount(): number {
    return this.selectedMaterielsForAssignment.size;
  }

  clearSelection() {
    this.selectedMaterielsForAssignment.clear();
  }

  selectAllVisible() {
    const visibleMateriels = this.filteredEquipmentsForAssignment;
    if (this.selectedMaterielsForAssignment.size === visibleMateriels.length) {
      this.clearSelection();
    } else {
      visibleMateriels.forEach(m => {
        this.selectedMaterielsForAssignment.add(m.idMateriel);
      });
    }
  }

  get filteredEquipmentsForAssignment() {
    if (!this.equipmentSearchTerm || !this.equipmentSearchTerm.trim()) {
      return this.filteredMateriels;
    }
    const term = this.equipmentSearchTerm.toLowerCase();
    return this.filteredMateriels.filter(m => 
      m.codeMateriel?.toLowerCase().includes(term) ||
      m.marque?.toLowerCase().includes(term) ||
      m.modele?.toLowerCase().includes(term) ||
      m.designation?.toLowerCase().includes(term)
    );
  }

  // ==================== OPEN ASSIGN TO ACTIVITY MODAL ====================

 openAssignActivityModal() {
  if (this.selectedMaterielsForAssignment.size === 0) {
    Swal.fire({
      icon: 'warning',
      title: 'No Selection',
      text: 'Please select at least one equipment to assign',
      toast: true,
      position: 'top-end'
    });
    return;
  }

  if (!this.currentProjectId) {
    Swal.fire({
      icon: 'warning',
      title: 'No Active Project',
      text: 'No active project found. Please create a project first.',
      confirmButtonText: 'OK'
    });
    return;
  }

  // ✅ FIX: Reload activities before opening modal
  if (this.currentMissionId) {
    this.isLoading = true;
    this.missionService.getActivesByMission(this.currentMissionId).pipe(
      takeUntil(this.destroy$)
    ).subscribe({
      next: (activities) => {
        this.availableActivities = activities;
        this.isLoading = false;
        
        if (this.availableActivities.length === 0) {
          Swal.fire({
            icon: 'warning',
            title: 'No Activities',
            text: 'No activities available for assignment. Please create activities first.',
            confirmButtonText: 'OK'
          });
          return;
        }
        
        this.openAssignActivityModalWithData();
      },
      error: (err) => {
        console.error('Error loading activities:', err);
        this.isLoading = false;
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'Failed to load activities. Please try again.',
          confirmButtonText: 'OK'
        });
      }
    });
  } else {
    Swal.fire({
      icon: 'warning',
      title: 'No Mission',
      text: 'No active mission found. Please join a mission first.',
      confirmButtonText: 'OK'
    });
  }
}

// ✅ NEW: Open modal with loaded data
private openAssignActivityModalWithData() {
  const selectedMaterielsList = this.filteredMateriels.filter(m => 
    this.selectedMaterielsForAssignment.has(m.idMateriel)
  );

  this.dialogService.open(AssignActivityModalComponent, {
    context: {
      selectedMateriels: selectedMaterielsList,
      availableActivities: this.availableActivities,
      projectId: this.currentProjectId,
      missionId: this.currentMissionId
    },
    dialogClass: 'assign-activity-dialog',
    hasBackdrop: true,
    closeOnBackdropClick: true
  }).onClose.subscribe((result) => {
    if (result) {
      this.clearSelection();
      this.showSelectionMode = false;
      if (this.selectedCategory) {
        this.loadMaterielsByCategory(this.selectedCategory.idCategorie);
      }
      this.loadCurrentMissionAndProject();
    }
  });
}
  // ==================== OPEN MATERIEL DETAILS ====================

  openMaterielDetails(materiel: Materiel, event: Event) {
    if (this.showSelectionMode) {
      this.toggleMaterielSelection(materiel.idMateriel, event);
      return;
    }
    event.stopPropagation();
    this.dialogService.open(MaterielDetailModalComponent, {
      context: {
        materielId: materiel.idMateriel,
      

      },
      dialogClass: 'materiel-detail-dialog',
      hasBackdrop: true,
      closeOnBackdropClick: true,
      closeOnEsc: true,
    });
  }

  // ==================== MATERIEL CRUD ====================

  startAddMateriel() {
    this.isAddingMateriel = true;
    this.newMateriel = {
      codeMateriel: '',
      marque: '',
      modele: '',
      designation: '',
      dateAchat: new Date().toISOString().split('T')[0],
      prix: 0,
      status: 'EN_BON_ETAT'
    };
  }

  cancelAddMateriel() {
    this.isAddingMateriel = false;
    this.newMateriel = {};
  }

  createMateriel() {
    if (!this.newMateriel.codeMateriel || !this.newMateriel.marque) {
      Swal.fire({
        icon: 'warning',
        title: 'Missing Fields',
        text: 'Code and Brand are required',
        toast: true,
        position: 'top-end',
        showConfirmButton: false,
        timer: 2000
      });
      return;
    }

    const selectedType = this.selectedTypeId 
      ? this.types.find(t => t.idTypeMateriel === this.selectedTypeId)
      : this.types[0];

    if (!selectedType) {
      Swal.fire({
        icon: 'warning',
        title: 'Missing Type',
        text: 'Please select a type first',
        toast: true,
        position: 'top-end'
      });
      return;
    }

    const materielToCreate: any = {
      codeMateriel: this.newMateriel.codeMateriel,
      marque: this.newMateriel.marque,
      modele: this.newMateriel.modele || '',
      designation: this.newMateriel.designation || '',
      dateAchat: this.newMateriel.dateAchat,
      prix: this.newMateriel.prix || 0,
      status: this.newMateriel.status || 'EN_BON_ETAT',
      typeMaterielId: selectedType.idTypeMateriel
    };

    this.isLoading = true;
    this.materielService.create(materielToCreate).subscribe({
      next: (newMateriel) => {
        this.materiels = [newMateriel, ...this.materiels];
        this.loadAllAffectationsForMateriels(this.materiels);
        this.applyFilter();
        this.cancelAddMateriel();
        this.isLoading = false;
        
        const categoryId = this.selectedCategory?.idCategorie;
        if (categoryId) {
          const currentCount = this.categoryMaterielCounts.get(categoryId) || 0;
          this.categoryMaterielCounts.set(categoryId, currentCount + 1);
        }
        
        const typeId = selectedType.idTypeMateriel;
        if (typeId) {
          const currentTypeCount = this.typeMaterielCounts.get(typeId) || 0;
          this.typeMaterielCounts.set(typeId, currentTypeCount + 1);
        }
        
        Swal.fire({
          icon: 'success',
          title: 'Success!',
          text: 'Equipment added successfully',
          toast: true,
          position: 'top-end',
          showConfirmButton: false,
          timer: 2000
        });
      },
      error: (err) => {
        console.error('Error creating materiel:', err);
        this.isLoading = false;
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: err.error?.message || 'Failed to create equipment',
          confirmButtonText: 'OK'
        });
      }
    });
  }

  editMateriel(materiel: Materiel) {
    Swal.fire({
      title: 'Edit Equipment',
      html: `
        <div style="text-align: left;">
          <div class="swal2-input-group" style="margin-bottom: 15px;">
            <label style="display: block; margin-bottom: 5px; font-weight: 500;">Code *</label>
            <input id="code" class="swal2-input" placeholder="Code" value="${materiel.codeMateriel}" style="width: 100%;">
          </div>
          <div class="swal2-input-group" style="margin-bottom: 15px;">
            <label style="display: block; margin-bottom: 5px; font-weight: 500;">Brand *</label>
            <input id="marque" class="swal2-input" placeholder="Brand" value="${materiel.marque}" style="width: 100%;">
          </div>
          <div class="swal2-input-group" style="margin-bottom: 15px;">
            <label style="display: block; margin-bottom: 5px; font-weight: 500;">Model</label>
            <input id="modele" class="swal2-input" placeholder="Model" value="${materiel.modele || ''}" style="width: 100%;">
          </div>
          <div class="swal2-input-group" style="margin-bottom: 15px;">
            <label style="display: block; margin-bottom: 5px; font-weight: 500;">Designation</label>
            <input id="designation" class="swal2-input" placeholder="Designation" value="${materiel.designation || ''}" style="width: 100%;">
          </div>
          <div class="swal2-input-group" style="margin-bottom: 15px;">
            <label style="display: block; margin-bottom: 5px; font-weight: 500;">Price (DZD)</label>
            <input id="prix" type="number" class="swal2-input" placeholder="Price" value="${materiel.prix || 0}" style="width: 100%;">
          </div>
          <div class="swal2-input-group" style="margin-bottom: 15px;">
            <label style="display: block; margin-bottom: 5px; font-weight: 500;">Status</label>
            <select id="status" class="swal2-input" style="width: 100%;">
              <option value="EN_BON_ETAT" ${materiel.status === 'EN_BON_ETAT' ? 'selected' : ''}>✅ Good Condition</option>
              <option value="EN_PANNE" ${materiel.status === 'EN_PANNE' ? 'selected' : ''}>⚠️ Broken</option>
              <option value="EN_REPARATION_INTERNE" ${materiel.status === 'EN_REPARATION_INTERNE' ? 'selected' : ''}>🔧 Internal Repair</option>
              <option value="EN_REPARATION_EXTERNE" ${materiel.status === 'EN_REPARATION_EXTERNE' ? 'selected' : ''}>🔧 External Repair</option>
            </select>
          </div>
        </div>
      `,
      focusConfirm: false,
      showCancelButton: true,
      confirmButtonText: 'Update',
      cancelButtonText: 'Cancel',
      preConfirm: () => {
        const code = (document.getElementById('code') as HTMLInputElement).value;
        const marque = (document.getElementById('marque') as HTMLInputElement).value;
        const modele = (document.getElementById('modele') as HTMLInputElement).value;
        const designation = (document.getElementById('designation') as HTMLInputElement).value;
        const prix = parseFloat((document.getElementById('prix') as HTMLInputElement).value);
        const status = (document.getElementById('status') as HTMLSelectElement).value;
        
        if (!code || !marque) {
          Swal.showValidationMessage('Code and Brand are required');
          return false;
        }
        
        return { codeMateriel: code, marque, modele, designation, prix, status };
      }
    }).then((result) => {
      if (result.isConfirmed && result.value) {
        this.isLoading = true;
        this.materielService.update(materiel.idMateriel, result.value).subscribe({
          next: (updated) => {
            const index = this.materiels.findIndex(m => m.idMateriel === materiel.idMateriel);
            if (index !== -1) {
              this.materiels[index] = updated;
              this.applyFilter();
            }
            this.isLoading = false;
            Swal.fire({
              icon: 'success',
              title: 'Updated!',
              text: 'Equipment has been updated',
              toast: true,
              position: 'top-end',
              showConfirmButton: false,
              timer: 2000
            });
          },
          error: (err) => {
            console.error('Error updating materiel:', err);
            this.isLoading = false;
            Swal.fire({
              icon: 'error',
              title: 'Error',
              text: err.error?.message || 'Failed to update equipment',
              confirmButtonText: 'OK'
            });
          }
        });
      }
    });
  }

  deleteMateriel(materiel: Materiel, event: Event) {
    event.stopPropagation();
    
    Swal.fire({
      title: 'Delete Equipment?',
      text: `Are you sure you want to delete "${materiel.codeMateriel}"?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Yes, delete it!',
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        this.isLoading = true;
        this.materielService.delete(materiel.idMateriel).subscribe({
          next: () => {
            this.materiels = this.materiels.filter(m => m.idMateriel !== materiel.idMateriel);
            this.affectationsMap.delete(materiel.idMateriel);
            this.currentActivityMap.delete(materiel.idMateriel);
            this.applyFilter();
            this.isLoading = false;
            
            const categoryId = this.selectedCategory?.idCategorie;
            if (categoryId) {
              const currentCount = this.categoryMaterielCounts.get(categoryId) || 0;
              this.categoryMaterielCounts.set(categoryId, Math.max(0, currentCount - 1));
            }
            
            const typeId = materiel.typeMaterielId;
            if (typeId) {
              const currentTypeCount = this.typeMaterielCounts.get(typeId) || 0;
              this.typeMaterielCounts.set(typeId, Math.max(0, currentTypeCount - 1));
            }
            
            Swal.fire({
              icon: 'success',
              title: 'Deleted!',
              text: 'Equipment has been deleted',
              toast: true,
              position: 'top-end',
              showConfirmButton: false,
              timer: 2000
            });
          },
          error: (err) => {
            console.error('Error deleting materiel:', err);
            this.isLoading = false;
            Swal.fire({
              icon: 'error',
              title: 'Error',
              text: err.error?.message || 'Failed to delete equipment',
              confirmButtonText: 'OK'
            });
          }
        });
      }
    });
  }

  // ==================== HELPER METHODS ====================

  selectType(typeId: number | null) {
    this.selectedTypeId = typeId;
    if (typeId === null) {
      this.loadMaterielsByCategory(this.selectedCategory!.idCategorie);
    } else {
      this.loadMaterielsByType(typeId);
    }
  }

  selectCategory(category: CategorieMateriel) {
    this.selectedCategory = category;
    this.selectedTypeId = null;
    this.searchTerm = '';
    this.loadTypes(category.idCategorie);
    if (this.isMobile) {
      this.sidebarCollapsed = true;
    }
  }

  backToCategories() {
    this.selectedCategory = null;
    this.selectedTypeId = null;
    this.types = [];
    this.materiels = [];
    this.filteredMateriels = [];
    this.searchTerm = '';
    this.affectationsMap.clear();
    this.currentActivityMap.clear();
  }

  toggleSidebar() {
    this.sidebarCollapsed = !this.sidebarCollapsed;
  }

  checkScreenSize() {
    this.isMobile = window.innerWidth < 768;
    if (!this.isMobile) {
      this.sidebarCollapsed = false;
    }
  }

  get filteredCategories() {
    if (!this.searchTerm || !this.searchTerm.trim()) {
      return this.categories;
    }
    const term = this.searchTerm.toLowerCase();
    return this.categories.filter(cat => cat.nom.toLowerCase().includes(term));
  }

  get totalMaterielsCount(): number {
    return this.materiels.length;
  }

  getMaterielCountByCategory(categoryId: number): number {
    return this.categoryMaterielCounts.get(categoryId) || 0;
  }

  getMaterielCountByType(typeId: number): number {
    return this.typeMaterielCounts.get(typeId) || 0;
  }

  getStatusText(status: string): string {
    const texts: { [key: string]: string } = {
      'EN_BON_ETAT': 'Good Condition',
      'EN_PANNE': 'Broken',
      'EN_REPARATION_INTERNE': 'Internal Repair',
      'EN_REPARATION_EXTERNE': 'External Repair'
    };
    return texts[status] || status;
  }

  getUtilizationStatus(materiel: Materiel): string {
    return materiel.enUtilisation ? 'In Use' : 'Available';
  }

  getUtilizationColor(materiel: Materiel): string {
    return materiel.enUtilisation ? '#3b82f6' : '#10b981';
  }

  getStatusColor(status: string): string {
    const colors: { [key: string]: string } = {
      'EN_BON_ETAT': '#10b981',
      'EN_PANNE': '#ef4444',
      'EN_REPARATION_INTERNE': '#f59e0b',
      'EN_REPARATION_EXTERNE': '#8b5cf6'
    };
    return colors[status] || '#6b7280';
  }

  getStatusIcon(status: string): string {
    const icons: { [key: string]: string } = {
      'EN_BON_ETAT': 'checkmark-circle-outline',
      'EN_PANNE': 'alert-circle-outline',
      'EN_REPARATION_INTERNE': 'settings-outline',
      'EN_REPARATION_EXTERNE': 'settings-outline'
    };
    return icons[status] || 'info-outline';
  }

  getStatusClass(status: string): string {
    return status.toLowerCase().replace(/_/g, '-');
  }

  // ==================== CATEGORY CRUD ====================

  startAddCategory() {
    this.isAddingCategory = true;
    this.newCategoryName = '';
    setTimeout(() => {
      if (this.categoryInput) {
        this.categoryInput.nativeElement.focus();
      }
    }, 100);
  }

  cancelAddCategory() {
    this.isAddingCategory = false;
    this.newCategoryName = '';
  }

  createCategory() {
    if (!this.newCategoryName || !this.newCategoryName.trim()) {
      Swal.fire({
        icon: 'warning',
        title: 'Invalid',
        text: 'Category name cannot be empty',
        toast: true,
        position: 'top-end',
        showConfirmButton: false,
        timer: 2000
      });
      return;
    }

    this.isLoading = true;
    this.categorieService.create({ nom: this.newCategoryName.trim() }).subscribe({
      next: (newCat) => {
        this.categories = [newCat, ...this.categories];
        this.cancelAddCategory();
        this.isLoading = false;
        Swal.fire({
          icon: 'success',
          title: 'Success!',
          text: `Category "${newCat.nom}" created`,
          toast: true,
          position: 'top-end',
          showConfirmButton: false,
          timer: 2000
        });
      },
      error: (err) => {
        console.error('Error creating category:', err);
        this.isLoading = false;
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'Failed to create category',
          confirmButtonText: 'OK'
        });
      }
    });
  }

  startEditCategory(category: CategorieMateriel, event: Event) {
    event.stopPropagation();
    this.editingCategoryId = category.idCategorie;
    this.editingCategoryName = category.nom;
    setTimeout(() => {
      if (this.editCategoryInput) {
        this.editCategoryInput.nativeElement.focus();
      }
    }, 100);
  }

  cancelEditCategory() {
    this.editingCategoryId = null;
    this.editingCategoryName = '';
  }

  saveEditCategory(category: CategorieMateriel) {
    if (!this.editingCategoryName || !this.editingCategoryName.trim()) {
      this.cancelEditCategory();
      return;
    }

    this.isLoading = true;
    this.categorieService.update(category.idCategorie, {
      idCategorie: category.idCategorie,
      nom: this.editingCategoryName.trim()
    }).subscribe({
      next: () => {
        const index = this.categories.findIndex(c => c.idCategorie === category.idCategorie);
        if (index !== -1) {
          this.categories[index].nom = this.editingCategoryName.trim();
          this.categories = [...this.categories];
        }
        if (this.selectedCategory?.idCategorie === category.idCategorie) {
          this.selectedCategory = { ...this.selectedCategory, nom: this.editingCategoryName.trim() };
        }
        this.cancelEditCategory();
        this.isLoading = false;
        Swal.fire({
          icon: 'success',
          title: 'Success!',
          text: 'Category updated',
          toast: true,
          position: 'top-end',
          showConfirmButton: false,
          timer: 2000
        });
      },
      error: (err) => {
        console.error('Error updating category:', err);
        this.isLoading = false;
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'Failed to update category',
          confirmButtonText: 'OK'
        });
      }
    });
  }

  deleteCategory(category: CategorieMateriel, event: Event) {
    event.stopPropagation();
    
    Swal.fire({
      title: 'Delete Category?',
      text: `Are you sure you want to delete "${category.nom}"? This will also delete all types and equipment in this category.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Yes, delete it!',
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        this.isLoading = true;
        this.categorieService.delete(category.idCategorie).subscribe({
          next: () => {
            this.categories = this.categories.filter(c => c.idCategorie !== category.idCategorie);
            if (this.selectedCategory?.idCategorie === category.idCategorie) {
              this.backToCategories();
            }
            this.isLoading = false;
            Swal.fire({
              icon: 'success',
              title: 'Deleted!',
              text: 'Category has been deleted',
              toast: true,
              position: 'top-end',
              showConfirmButton: false,
              timer: 2000
            });
          },
          error: (err) => {
            console.error('Error deleting category:', err);
            this.isLoading = false;
            Swal.fire({
              icon: 'error',
              title: 'Error',
              text: 'Failed to delete category. It may be linked to other data.',
              confirmButtonText: 'OK'
            });
          }
        });
      }
    });
  }

  // ==================== TYPE CRUD ====================

  startAddType() {
    this.isAddingType = true;
    this.newTypeName = '';
    setTimeout(() => {
      if (this.typeInput) {
        this.typeInput.nativeElement.focus();
      }
    }, 100);
  }

  cancelAddType() {
    this.isAddingType = false;
    this.newTypeName = '';
  }

  createType() {
    if (!this.newTypeName || !this.newTypeName.trim()) {
      Swal.fire({
        icon: 'warning',
        title: 'Invalid',
        text: 'Type name cannot be empty',
        toast: true,
        position: 'top-end',
        showConfirmButton: false,
        timer: 2000
      });
      return;
    }

    this.isLoading = true;
    this.typeService.create({
      libelle: this.newTypeName.trim(),
      categorieId: this.selectedCategory!.idCategorie
    }).subscribe({
      next: (newType) => {
        this.types = [newType, ...this.types];
        this.cancelAddType();
        this.isLoading = false;
        Swal.fire({
          icon: 'success',
          title: 'Success!',
          text: `Type "${newType.libelle}" created`,
          toast: true,
          position: 'top-end',
          showConfirmButton: false,
          timer: 2000
        });
      },
      error: (err) => {
        console.error('Error creating type:', err);
        this.isLoading = false;
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'Failed to create type',
          confirmButtonText: 'OK'
        });
      }
    });
  }

  startEditType(type: TypeMateriel, event: Event) {
    event.stopPropagation();
    this.editingTypeId = type.idTypeMateriel;
    this.editingTypeName = type.libelle;
    setTimeout(() => {
      if (this.editTypeInput) {
        this.editTypeInput.nativeElement.focus();
      }
    }, 100);
  }

  cancelEditType() {
    this.editingTypeId = null;
    this.editingTypeName = '';
  }

  saveEditType(type: TypeMateriel) {
    if (!this.editingTypeName || !this.editingTypeName.trim()) {
      this.cancelEditType();
      return;
    }

    this.isLoading = true;
    const updatedType = { ...type, libelle: this.editingTypeName.trim() };
    this.typeService.update(type.idTypeMateriel, updatedType).subscribe({
      next: () => {
        const index = this.types.findIndex(t => t.idTypeMateriel === type.idTypeMateriel);
        if (index !== -1) {
          this.types[index].libelle = this.editingTypeName.trim();
          this.types = [...this.types];
        }
        this.cancelEditType();
        this.isLoading = false;
        Swal.fire({
          icon: 'success',
          title: 'Success!',
          text: 'Type updated',
          toast: true,
          position: 'top-end',
          showConfirmButton: false,
          timer: 2000
        });
      },
      error: (err) => {
        console.error('Error updating type:', err);
        this.isLoading = false;
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'Failed to update type',
          confirmButtonText: 'OK'
        });
      }
    });
  }

  deleteType(type: TypeMateriel, event: Event) {
    event.stopPropagation();
    
    Swal.fire({
      title: 'Delete Type?',
      text: `Are you sure you want to delete "${type.libelle}"? This will also delete all equipment in this type.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Yes, delete it!',
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        this.isLoading = true;
        this.typeService.delete(type.idTypeMateriel).subscribe({
          next: () => {
            this.types = this.types.filter(t => t.idTypeMateriel !== type.idTypeMateriel);
            if (this.selectedTypeId === type.idTypeMateriel) {
              this.selectType(null);
            }
            this.isLoading = false;
            Swal.fire({
              icon: 'success',
              title: 'Deleted!',
              text: 'Type has been deleted',
              toast: true,
              position: 'top-end',
              showConfirmButton: false,
              timer: 2000
            });
          },
          error: (err) => {
            console.error('Error deleting type:', err);
            this.isLoading = false;
            Swal.fire({
              icon: 'error',
              title: 'Error',
              text: 'Failed to delete type',
              confirmButtonText: 'OK'
            });
          }
        });
      }
    });
  }

  // ==================== PAGINATION METHODS ====================

  get paginatedMateriels(): Materiel[] {
    const dataSource = this.showSelectionMode ? this.filteredEquipmentsForAssignment : this.filteredMateriels;
    const startIndex = (this.currentPage - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    return dataSource.slice(startIndex, endIndex);
  }

  updatePagination() {
    const dataSource = this.showSelectionMode ? this.filteredEquipmentsForAssignment : this.filteredMateriels;
    this.totalPages = Math.ceil(dataSource.length / this.pageSize);
    if (this.currentPage > this.totalPages) {
      this.currentPage = Math.max(1, this.totalPages);
    }
    if (this.currentPage < 1) {
      this.currentPage = 1;
    }
  }

  previousPage() {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }

  nextPage() {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
    }
  }

  goToPage(page: number) {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
    }
  }

  goToFirstPage() {
    this.goToPage(1);
  }

  goToLastPage() {
    this.goToPage(this.totalPages);
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

  ngAfterViewInit() {}

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
    window.removeEventListener('resize', () => this.checkScreenSize());
  }
}