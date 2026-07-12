// mission-overview.component.ts
import { Component, OnInit, OnDestroy , Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { MissionService, MissionOverview, MissionResourceSummary, MissionResource, ConsumptionRequest, Motif, Contexte } from '../../services/mission/mission.service';
import { ReparationService, PanneRequest, LancementReparation, FinReparation, ReparationItem } from '../../services/materiel/reparation.service';
import { AffectationMaterielService, AffectationMateriel } from '../../services/materiel/affectation-materiel.service';
import { MaterielService, Materiel } from '../../services/materiel/materiel.service'; // ✅ IMPORTANT: Add this
import Swal from 'sweetalert2';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { EmployeDTO, EquipeDTO, MissionTeamDTO, AffectationRequest, EquipeRequest,ProjectResponse,ProjectRequest } from '../../services/mission/mission.service';
import { EquipmentTypeDetail, EquipmentTypeDetailComponent } from './equipment-type-detail/equipment-type-detail.component';
import { ActiveDTO, ActiveRequest } from '../../services/mission/mission.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-mission-overview',
  templateUrl: './mission-overview.component.html',
  styleUrls: ['./mission-overview.component.scss']
})
export class MissionOverviewComponent implements OnInit, OnDestroy {
    @Input() missionIdInput: number | null = null

      @Input() fromDashboard: boolean = false;
  missionId: number = 1;
  mission: MissionOverview | null = null;
  isLoading: boolean = true;
  selectedTab: string = 'overview';
  
  equipmentList: EquipmentDetail[] = [];
  filteredEquipmentList: EquipmentDetail[] = [];
  affectations: AffectationMateriel[] = [];
  
  // Search
  equipmentSearchTerm: string = '';
  resourceSearchTerm: string = '';
  
  selectedMemberFilter: 'all' | 'assigned' | 'not-assigned' = 'all';

  // Resources data
  resourcesList: any[] = [];
  filteredResourcesList: any[] = [];
  
  // NEW: Mission Resource Summary
  missionResourceSummary: MissionResourceSummary | null = null;
  
  // NEW: Consumption Modal
  showConsumptionModal = false;
  consumptionForm: FormGroup;
  motifs: Motif[] = [];
  contexts: Contexte[] = [];
  availableResources: MissionResource[] = [];
  selectedResourceForConsumption: MissionResource | null = null;
  
  showBreakdownModal = false;
  selectedEquipment: EquipmentDetail | null = null;
  breakdownForm: FormGroup;
  
  showLaunchRepairModal = false;
  selectedReparation: ReparationItem | null = null;
  launchRepairForm: FormGroup;
  
  showCompleteRepairModal = false;
  selectedOngoingRepair: ReparationItem | null = null;
  completeRepairForm: FormGroup;
  
  // For scrollable pending repairs
  expandedRepairs: { [key: string]: boolean } = {};
  visibleRepairsCount: number = 3;

  // NEW: Expanded resource sections
  expandedResources: { [key: number]: boolean } = {};

  // Team properties
  missionTeam: MissionTeamDTO | null = null;
  showAddMemberModal = false;
  availableEmployees: EmployeDTO[] = [];
  selectedEmployees: EmployeDTO[] = [];
  selectedEquipeId: number | null = null;
  equipes: EquipeDTO[] = [];
  showTeamDetailsModal = false;
  selectedEquipeMembers: EmployeDTO[] = [];
  editingEmployeeId: number | null = null;
  tempEquipeId: number | null = null;

  unassignedMembersList: EmployeDTO[] = [];
  unassignedCurrentPage: number = 1;
unassignedPageSize: number = 12;
unassignedTotalPages: number = 1;
paginatedUnassignedMembers: EmployeDTO[] = [];

  // ✅ Add Materiel array
  materiels: Materiel[] = [];
  
  // Equipment Type Detail
  showEquipmentTypeDetail = false;
  selectedEquipmentType: any = null;
  equipmentTypeDetailData: EquipmentTypeDetail | null = null;

  // Team Management
  showTeamManagementModal = false;
  editingEquipe: EquipeDTO | null = null;
  equipeForm: FormGroup;
  equipeMembers: EmployeDTO[] = [];
  showEquipeMembersModal = false;
  selectedEquipeForMembers: EquipeDTO | null = null;
  availableEmployeesForAssignment: EmployeDTO[] = [];
  selectedEmployeesForEquipe: EmployeDTO[] = [];
  showCreateEditTeamModal = false;

  resourceSearchInput: string = '';
  showResourceDropdown: boolean = false;
  filteredAvailableResources: MissionResource[] = [];

  statsCards = [
    { icon: 'fas fa-microchip', iconClass: 'equipment-bg', label: 'Equipment Units', value: '0', delay: '0.1s' },
    { icon: 'fas fa-cubes', iconClass: 'resources-bg', label: 'Resources Available', value: '0', delay: '0.2s' },
    { icon: 'fas fa-chart-line', iconClass: 'consumed-bg', label: 'Resources Consumed', value: '0', delay: '0.3s' },
    { icon: 'fas fa-users', iconClass: 'team-bg', label: 'Team Members', value: '0', delay: '0.4s' }
  ];
  
  private destroy$ = new Subject<void>();

  

  // Add these properties inside the component class
// Active Management
activesList: ActiveDTO[] = [];
filteredActivesList: ActiveDTO[] = [];
activeSearchTerm: string = '';
showActiveModal: boolean = false;
editingActive: ActiveDTO | null = null;
activeForm: FormGroup;

// Project Management
showCreateProjectModal: boolean = false;
projectForm: FormGroup;
currentProjectData: ProjectResponse | null = null;

canCreateProject: boolean = false;

// Pagination properties
Math = Math;
currentPage: number = 1;
pageSize: number = 15;
totalPages: number = 1;

// Pagination for filtered lists
paginatedFilteredEquipmentList: EquipmentDetail[] = [];
paginatedFilteredResourcesList: any[] = [];
paginatedFilteredActivesList: ActiveDTO[] = [];

// Role-based permissions
canManageTeams: boolean = false;
canManageActivities: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private missionService: MissionService,
    private reparationService: ReparationService,
    private affectationService: AffectationMaterielService,
    private materielService: MaterielService, // ✅ Add this
    private fb: FormBuilder,
    private authService: AuthService

  ) {
    this.breakdownForm = this.fb.group({
      datePanne: [new Date().toISOString().split('T')[0], Validators.required],
      detailProbleme: ['']
    });
    
    this.launchRepairForm = this.fb.group({
      type: ['INTERNE', Validators.required],
      technicien: [''],
      fournisseur: [''],
      dateSortieChantier: ['']
    });
    
    this.completeRepairForm = this.fb.group({
      dateReparation: [new Date().toISOString().split('T')[0], Validators.required],
      cout: [0, [Validators.required, Validators.min(0)]],
      dateEntreeChantier: ['']
    });
    
    this.consumptionForm = this.fb.group({
      quantity: [null, [Validators.required, Validators.min(0.01)]],
      date: [new Date().toISOString().split('T')[0], Validators.required],
      motifCode: ['', Validators.required],
      motifDescription: [''],
      contexteTitle: ['', Validators.required],
      contexteDescription: [''],
      description: ['']
    });

    this.equipeForm = this.fb.group({
      nom: ['', Validators.required],
      type: ['', Validators.required]
    });

    this.activeForm = this.fb.group({
  codeActive: ['', Validators.required],
  objectif: ['', Validators.required],
  description: ['']
});

// Add to constructor after other form initializations:
this.projectForm = this.fb.group({
  nom: ['', Validators.required],
  description: ['', Validators.required],
  budget: [0, [Validators.required, Validators.min(0)]],
  objectifVP: [0, [Validators.required, Validators.min(0)]],
  objectifDebut: [new Date().toISOString().split('T')[0], Validators.required],
  objectifFin: ['', Validators.required]
});

  }

  // أضف هذه الدالة في ngOnInit بعد this.loadAllData()

initializePermissions(): void {
  const userRoles = this.authService.getCurrentUser()?.roles || [];
  const hasGestionnaireRole = userRoles.includes('GESTIONNAIRE');
  const hasAdminRole = userRoles.includes('ADMIN');
  const hasChefMissionRole = userRoles.includes('CHEF_MISSION');

  
  // Only Gestionnaire and ADMINISTRATEUR can manage teams and activities
  this.canManageTeams = hasGestionnaireRole || hasAdminRole;
  this.canManageActivities = hasGestionnaireRole || hasAdminRole;

    // CHEF_MISSION can create projects (but not manage teams/activities)
  this.canCreateProject = hasChefMissionRole ||  hasAdminRole;

  console.log('[DEBUG] User roles:', userRoles);

}

  // ==================== EQUIPMENT TYPE DETAIL METHODS ====================

  openEquipmentTypeDetail(equipmentType: any) {
    // Get materiels of this type from the existing array
    const materielsOfType = this.materiels.filter(m => {
      const fullName = `${m.marque} ${m.modele}`.trim();
      return m.typeMaterielLibelle === equipmentType.type || fullName === equipmentType.type;
    });
    
    // Get affectations for these materiels
    const affectationsOfType = this.affectations.filter(a => 
      materielsOfType.some(m => m.idMateriel === a.materielId)
    );
    
    this.equipmentTypeDetailData = {
      typeName: equipmentType.type,
      icon: equipmentType.icon,
      color: equipmentType.color,
      materiels: materielsOfType,
      affectations: affectationsOfType,
      repairs: new Map()
    };
    
    this.showEquipmentTypeDetail = true;
  }

  closeEquipmentTypeDetail() {
    this.showEquipmentTypeDetail = false;
    this.equipmentTypeDetailData = null;
  }

  // ==================== FILTER METHODS ====================

  filterEquipmentList() {
    if (!this.equipmentSearchTerm.trim()) {
      this.filteredEquipmentList = [...this.equipmentList];
    } else {
      const term = this.equipmentSearchTerm.toLowerCase();
      this.filteredEquipmentList = this.equipmentList.filter(eq => 
        eq.type.toLowerCase().includes(term)
      );
    }
  }

  // ==================== LOAD METHODS ====================

  loadEquipmentDetails() {
    if (!this.mission?.aggregatedEquipment?.byType) return;
    
    this.affectationService.getByMissionId(this.missionId).subscribe({
      next: (affectations) => {
        this.affectations = affectations;
        
        // Load materiel details for each affectation
        const materielIds = [...new Set(affectations.map(a => a.materielId).filter(id => id))];
        
        if (materielIds.length > 0) {
          let loadedCount = 0;
          materielIds.forEach(materielId => {
            this.materielService.getById(materielId).subscribe({
              next: (materiel) => {
                if (!this.materiels.some(m => m.idMateriel === materiel.idMateriel)) {
                  this.materiels.push(materiel);
                }
                loadedCount++;
                if (loadedCount === materielIds.length) {
                  this.buildEquipmentList(affectations);
                }
              },
              error: () => {
                loadedCount++;
                if (loadedCount === materielIds.length) {
                  this.buildEquipmentList(affectations);
                }
              }
            });
          });
        } else {
          this.buildEquipmentList(affectations);
        }
      },
      error: (err) => {
        console.error('Error loading affectations:', err);
        this.buildEquipmentList([]);
      }
    });
  }

  private buildEquipmentList(affectations: AffectationMateriel[]) {
    this.equipmentList = this.mission!.aggregatedEquipment.byType.map(eq => {
      let affectation = null;
      
      for (const aff of affectations) {
        const materiel = this.materiels.find(m => m.idMateriel === aff.materielId);
        if (materiel) {
          const fullName = `${materiel.marque} ${materiel.modele}`.trim();
          if (materiel.typeMaterielLibelle === eq.type || fullName === eq.type) {
            affectation = aff;
            break;
          }
        }
      }
      
      return {
        id: eq.type,
        type: eq.type,
        count: eq.count,
        icon: eq.icon,
        color: eq.color,
        materielId: affectation?.materielId || null,
        affectationId: affectation?.idAffectation || null,
        goodCount: eq.count,
        brokenCount: 0,
        inRepairCount: 0,
        repairs: []
      };
    });
    
    this.filteredEquipmentList = [...this.equipmentList];
    this.updatePaginationForResources();
    this.loadRepairsForAllEquipment();
  }

  loadRepairsForAllEquipment() {
    this.equipmentList.forEach(equipment => {
      if (equipment.materielId) {
        this.reparationService.getAllByMaterielAndMission(equipment.materielId, this.missionId).subscribe({
          next: (repairs) => {
            equipment.repairs = repairs;
            equipment.brokenCount = repairs.filter(r => r.status === 'PENDING').length;
            equipment.inRepairCount = repairs.filter(r => r.status === 'IN_PROGRESS' || r.status === 'SENT').length;
            equipment.goodCount = equipment.count - equipment.brokenCount - equipment.inRepairCount;
            this.filterEquipmentList();
          },
          error: (err) => console.error('Error loading repairs:', err)
        });
      }
    });
  }

  // ==================== OTHER METHODS ====================

  filterAvailableResources() {
    if (!this.availableResources) return;
    const searchTerm = this.resourceSearchInput.toLowerCase();
    this.filteredAvailableResources = this.availableResources.filter(res => 
      res.resourceName.toLowerCase().includes(searchTerm) && res.remaining > 0
    );
  }

  selectResource(resource: MissionResource) {
    this.selectedResourceForConsumption = resource;
    this.resourceSearchInput = resource.resourceName;
    this.showResourceDropdown = false;
    this.consumptionForm.get('quantity')?.setValidators([
      Validators.required,
      Validators.min(0.01),
      Validators.max(resource.remaining)
    ]);
    this.consumptionForm.get('quantity')?.updateValueAndValidity();
  }

  closeResourceDropdown() {
    setTimeout(() => {
      this.showResourceDropdown = false;
    }, 200);
  }

  ngOnInit() {
    if (this.missionIdInput) {
      // إذا تم تمرير missionId كـ Input (من Dashboard)
      this.missionId = this.missionIdInput;
      this.loadAllData();
       this.initializePermissions();
    } else {
      // الوضع العادي (من الـ URL)
      this.route.params.pipe(takeUntil(this.destroy$)).subscribe(params => {
        this.missionId = params['id'] ? +params['id'] : 1;
        this.loadAllData();
         this.initializePermissions();
      });
    }
  }
  
  private loadAllData(): void {
    this.loadMissionData();
    this.loadActives();
    this.loadMissionTeam();
    this.loadLookupData();
  }
  
  loadLookupData() {
    this.missionService.getAllMotifs().subscribe({
      next: (data) => this.motifs = data,
      error: (err) => console.error('Error loading motifs:', err)
    });
    
    this.missionService.getAllContextes().subscribe({
      next: (data) => this.contexts = data,
      error: (err) => console.error('Error loading contexts:', err)
    });
  }
loadMissionData() {
  this.isLoading = true;
  this.missionService.getMissionOverview(this.missionId).pipe(
    takeUntil(this.destroy$)
  ).subscribe({
    next: (data) => {
      this.mission = data;
      
      // Set currentProjectData ONLY if there's a valid project (id > 0)
      if (data.currentProject && data.currentProject.id > 0) {
        this.currentProjectData = {
          id: data.currentProject.id,
          nom: data.currentProject.name,
          description: data.currentProject.description,
          objectifDebut: data.currentProject.startDate,
          objectifFin: data.currentProject.targetEndDate,
          progression: data.currentProject.progress,
          status: data.currentProject.statusCode || 'PLANIFIER',
          budget: 0,
          objectifVP: 0
        } as ProjectResponse;
      } else {
        this.currentProjectData = null;  // Set to null to show "No Active Project" card
      }
      
      this.updateStatsCards();
      this.loadEquipmentDetails();
      this.loadResourcesData();
      this.loadMissionResourceSummary();
      this.isLoading = false;
    },
    error: (err) => {
      console.error('Error loading mission overview:', err);
      this.isLoading = false;
      this.loadMockData();
    }
  });
}
  
  loadMissionResourceSummary() {
    this.missionService.getMissionResourceSummary(this.missionId).subscribe({
      next: (summary) => {
        this.missionResourceSummary = summary;
        this.updateResourcesFromSummary();
      },
      error: (err) => console.error('Error loading resource summary:', err)
    });
  }
  
  updateResourcesFromSummary() {
    if (this.missionResourceSummary?.resources) {
      this.resourcesList = this.missionResourceSummary.resources.map(r => ({
        name: r.resourceName,
        allocated: r.totalAllocated,
        consumed: r.totalConsumed,
        remaining: r.remaining,
        unit: r.unit,
        costPerUnit: r.costPerUnit,
        totalCost: r.totalConsumed * r.costPerUnit,
        resourceId: r.resourceId,
        consumptions: r.consumptions
      }));
      this.filteredResourcesList = [...this.resourcesList];
      this.statsCards[1].value = this.missionResourceSummary.totalAllocated.toLocaleString();
      this.statsCards[2].value = this.missionResourceSummary.totalConsumed.toLocaleString();
    }
  }

  loadResourcesData() {
    if (this.mission?.aggregatedResources?.byCategory) {
      this.resourcesList = this.mission.aggregatedResources.byCategory;
      this.filteredResourcesList = [...this.resourcesList];
    }
  }

 filterEquipment() {
  this.filterEquipmentList();
  this.resetPagination();
}

filterResources() {
  if (!this.resourceSearchTerm.trim()) {
    this.filteredResourcesList = [...this.resourcesList];
  } else {
    const term = this.resourceSearchTerm.toLowerCase();
    this.filteredResourcesList = this.resourcesList.filter(res => 
      res.name?.toLowerCase().includes(term)
    );
  }
  this.resetPagination();
}

filterActives(): void {
  if (!this.activeSearchTerm.trim()) {
    this.filteredActivesList = [...this.activesList];
  } else {
    const term = this.activeSearchTerm.toLowerCase();
    this.filteredActivesList = this.activesList.filter(active =>
      active.codeActive.toLowerCase().includes(term) ||
      active.objectif.toLowerCase().includes(term)
    );
  }
  this.resetPagination();
}

selectTab(tab: string) {
  this.selectedTab = tab;
  this.resetPagination();
}

  toggleShowAllRepairs(equipmentId: string) {
    this.expandedRepairs[equipmentId] = !this.expandedRepairs[equipmentId];
  }
  
  toggleResourceDetails(resourceId: number) {
    this.expandedResources[resourceId] = !this.expandedResources[resourceId];
  }
  
  isResourceExpanded(resourceId: number): boolean {
    return this.expandedResources[resourceId] || false;
  }

  getVisibleRepairs(repairs: ReparationItem[], equipmentId: string): ReparationItem[] {
    if (this.expandedRepairs[equipmentId]) {
      return repairs;
    }
    return repairs.slice(0, this.visibleRepairsCount);
  }

  getPendingAndOngoingRepairs(repairs: ReparationItem[]): ReparationItem[] {
    return repairs?.filter(r => r.status !== 'COMPLETED') || [];
  }

  getCompletedRepairs(repairs: ReparationItem[]): ReparationItem[] {
    return repairs?.filter(r => r.status === 'COMPLETED') || [];
  }

  updateStatsCards() {
    if (this.mission) {
      this.statsCards[0].value = this.mission.aggregatedEquipment.total.toString();
      this.statsCards[1].value = this.mission.aggregatedResources.total.toLocaleString();
      this.statsCards[2].value = this.mission.aggregatedResources.consumed.toLocaleString();
      const teamCount = this.mission.aggregatedEmployees?.byRole?.reduce((sum, role) => sum + role.count, 0) || 12;
      this.statsCards[3].value = teamCount.toString();
    }
  }
  
  openConsumptionModal() {
    if (this.missionResourceSummary?.resources) {
      this.availableResources = this.missionResourceSummary.resources.filter(r => r.remaining > 0);
      this.filteredAvailableResources = [...this.availableResources];
      
      if (this.availableResources.length === 0) {
        Swal.fire({
          icon: 'warning',
          title: 'No Resources Available',
          text: 'All resources have been fully consumed.'
        });
        return;
      }
    }


    
    this.consumptionForm.reset({
      quantity: null,
      date: new Date().toISOString().split('T')[0],
      motifCode: '',
      motifDescription: '',
      contexteTitle: '',
      contexteDescription: '',
      description: ''
    });
    this.selectedResourceForConsumption = null;
    this.resourceSearchInput = '';
    this.showResourceDropdown = false;
    this.showConsumptionModal = true;
  }

  isConsumptionFormValid(): boolean {
    return !!(
      this.selectedResourceForConsumption &&
      this.consumptionForm.get('quantity')?.valid &&
      this.consumptionForm.get('date')?.valid &&
      this.consumptionForm.get('motifCode')?.value &&
      this.consumptionForm.get('contexteTitle')?.value
    );
  }

  closeConsumptionModal() {
    this.showConsumptionModal = false;
    this.selectedResourceForConsumption = null;
  }
  
  onResourceSelect() {
    const resourceId = this.consumptionForm.get('resourceId')?.value;
    this.selectedResourceForConsumption = this.availableResources.find(r => r.resourceId === resourceId) || null;
    
    if (this.selectedResourceForConsumption) {
      this.consumptionForm.get('quantity')?.setValidators([
        Validators.required,
        Validators.min(0.01),
        Validators.max(this.selectedResourceForConsumption.remaining)
      ]);
      this.consumptionForm.get('quantity')?.updateValueAndValidity();
    }
  }

  submitConsumption() {
    if (!this.isConsumptionFormValid()) {
      Swal.fire({ icon: 'error', title: 'Invalid Form', text: 'Please fill all required fields correctly.' });
      return;
    }
    
    const request: ConsumptionRequest = {
      resourceId: this.selectedResourceForConsumption!.resourceId,
      missionId: this.missionId,
      motifCode: this.consumptionForm.get('motifCode')?.value,
      motifDescription: this.consumptionForm.get('motifDescription')?.value,
      contexteTitle: this.consumptionForm.get('contexteTitle')?.value,
      contexteDescription: this.consumptionForm.get('contexteDescription')?.value,
      quantity: this.consumptionForm.get('quantity')?.value,
      date: this.consumptionForm.get('date')?.value,
      description: this.consumptionForm.get('description')?.value
    };
   
    this.missionService.createConsumption(request).subscribe({
      next: () => {
        Swal.fire({
          icon: 'success',
          title: 'Consumption Recorded',
          text: `Successfully recorded consumption of ${request.quantity} units.`
        });
        this.closeConsumptionModal();
        this.loadMissionResourceSummary();
      },
      error: (err) => {
        console.error('Error recording consumption:', err);
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: err.error?.message || 'Failed to record consumption.'
        });
      }
    });
  }
  
  deleteConsumption(consumptionId: number, resourceName: string) {
    Swal.fire({
      title: 'Delete Consumption?',
      text: `Are you sure you want to delete this consumption record for ${resourceName}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Yes, delete it!'
    }).then((result) => {
      if (result.isConfirmed) {
        this.missionService.deleteConsumption(consumptionId).subscribe({
          next: () => {
            Swal.fire('Deleted!', 'Consumption record has been deleted.', 'success');
            this.loadMissionResourceSummary();
          },
          error: (err) => {
            console.error('Error deleting consumption:', err);
            Swal.fire('Error!', 'Failed to delete consumption record.', 'error');
          }
        });
      }
    });
  }

  openBreakdownModal(equipment: EquipmentDetail) {
    this.selectedEquipment = equipment;
    this.breakdownForm.reset({
      datePanne: new Date().toISOString().split('T')[0],
      detailProbleme: ''
    });
    this.showBreakdownModal = true;
  }
  
  closeBreakdownModal() {
    this.showBreakdownModal = false;
    this.selectedEquipment = null;
  }
  
  declareBreakdown() {
    if (this.breakdownForm.invalid || !this.selectedEquipment) return;
    
    let materielId = this.selectedEquipment.materielId;
    
    if (!materielId && this.affectations.length > 0) {
      materielId = this.affectations[0].materielId;
    }
    
    if (!materielId) {
      Swal.fire({ 
        icon: 'error', 
        title: 'Configuration Error', 
        text: `No physical equipment linked to "${this.selectedEquipment.type}".` 
      });
      return;
    }
    
    const request: PanneRequest = {
      materielId: materielId,
      datePanne: this.breakdownForm.value.datePanne,
      detailProbleme: this.breakdownForm.value.detailProbleme,
      missionId: this.missionId,
      affectationId: this.selectedEquipment.affectationId || this.affectations[0]?.idAffectation
    };
    
    this.reparationService.declarePanne(request).subscribe({
      next: () => {
        Swal.fire({ 
          icon: 'success', 
          title: 'Success!', 
          text: `Breakdown declared for ${this.selectedEquipment?.type}` 
        });
        this.closeBreakdownModal();
        this.loadMissionData();
      },
      error: (err) => {
        console.error('Error:', err);
        Swal.fire({ 
          icon: 'error', 
          title: 'Error', 
          text: err.error?.message || 'Failed to declare breakdown.' 
        });
      }
    });
  }

  openLaunchRepairModal(reparation: ReparationItem) {
    this.selectedReparation = reparation;
    this.launchRepairForm.reset({ type: 'INTERNE' });
    this.showLaunchRepairModal = true;
  }
  
  closeLaunchRepairModal() {
    this.showLaunchRepairModal = false;
    this.selectedReparation = null;
  }
  
  launchRepair() {
    if (this.launchRepairForm.invalid || !this.selectedReparation) return;
    
    const request: LancementReparation = {
      reparationId: this.selectedReparation.idReparation,
      type: this.launchRepairForm.value.type,
      technicien: this.launchRepairForm.value.technicien,
      fournisseur: this.launchRepairForm.value.fournisseur,
      dateSortieChantier: this.launchRepairForm.value.dateSortieChantier
    };
    
    this.reparationService.launchRepair(request).subscribe({
      next: () => {
        Swal.fire({ icon: 'success', title: 'Repair Launched!' });
        this.closeLaunchRepairModal();
        this.loadMissionData();
      },
      error: (err) => {
        Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message || 'Failed to launch repair' });
      }
    });
  }
  
  openCompleteRepairModal(reparation: ReparationItem) {
    this.selectedOngoingRepair = reparation;
    this.completeRepairForm.reset({
      dateReparation: new Date().toISOString().split('T')[0],
      cout: 0,
      dateEntreeChantier: ''
    });
    this.showCompleteRepairModal = true;
  }
  
  closeCompleteRepairModal() {
    this.showCompleteRepairModal = false;
    this.selectedOngoingRepair = null;
  }
  
  completeRepair() {
    if (this.completeRepairForm.invalid || !this.selectedOngoingRepair) return;
    
    const request: FinReparation = {
      reparationId: this.selectedOngoingRepair.idReparation,
      dateReparation: this.completeRepairForm.value.dateReparation,
      cout: this.completeRepairForm.value.cout,
      dateEntreeChantier: this.completeRepairForm.value.dateEntreeChantier
    };
    
    this.reparationService.completeRepair(request).subscribe({
      next: () => {
        Swal.fire({ icon: 'success', title: 'Repair Completed!' });
        this.closeCompleteRepairModal();
        this.loadMissionData();
      },
      error: (err) => {
        Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message || 'Failed to complete repair' });
      }
    });
  }
 // Replace the existing getProjectStatusColor and getProjectStatusText methods
// These methods are already correct for status codes
getProjectStatusColor(statusCode: string): string {
  switch(statusCode) {
    case 'PLANIFIER': return '#64748b';
    case 'ENCOURS': return '#3b82f6';
    case 'ENATTENTE': return '#f59e0b';
    case 'ENRETARD': return '#ef4444';
    case 'TERMINI': return '#10b981';
    case 'ANNULE': return '#94a3b8';
    default: return '#6b7280';
  }
}


getProjectStatusText(statusCode: string): string {
  switch(statusCode) {
    case 'PLANIFIER': return 'Planned';
    case 'ENCOURS': return 'In Progress';
    case 'ENATTENTE': return 'On Hold';
    case 'ENRETARD': return 'Delayed';
    case 'TERMINI': return 'Completed';
    case 'ANNULE': return 'Cancelled';
    default: return statusCode || 'Pending';
  }
}

  getProgressColor(rate: number): string {
    if (rate >= 80) return '#10b981';
    if (rate >= 50) return '#f59e0b';
    return '#ef4444';
  }



getProjectStatusTextForDisplay(status: string): string {
  switch(status) {
    case 'completed': return 'Completed';
    case 'on-track': return 'On Track';
    case 'at-risk': return 'At Risk';
    case 'delayed': return 'Delayed';
    case 'pending': return 'Pending';
    default: return status || 'Pending';
  }
}

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('tn-TN', { 
      style: 'currency', 
      currency: 'DZD', 
      minimumFractionDigits: 0 
    }).format(value);
  }

 

  goBack() {
    this.router.navigate(['/pages/tables/missions']);
  }

  loadMockData() {
    setTimeout(() => {
      this.mission = this.getMockMissionData(this.missionId);
      this.updateStatsCards();
      this.loadEquipmentDetails();
      this.loadResourcesData();
      this.isLoading = false;
    }, 500);
  }

  getMockMissionData(id: number): MissionOverview {
    return {
      id: id,
      codeMission: 'MISSION-ALPHA-001',
      description: 'Digital transformation project for enterprise resource planning',
      currentProject: {
        id: 101,
        name: 'ERP Implementation & Infrastructure Upgrade',
        description: 'Complete ERP system implementation including hardware and software upgrades',
        startDate: '2024-01-15',
        targetEndDate: '2024-06-30',
        progress: 65,
        status: 'on-track'
      },
      aggregatedEquipment: {
        total: 156,
        byType: [
          { type: 'Servers', count: 24, icon: 'fas fa-server', color: '#3b82f6' },
          { type: 'Workstations', count: 85, icon: 'fas fa-laptop', color: '#10b981' },
          { type: 'Network Devices', count: 32, icon: 'fas fa-wifi', color: '#f59e0b' },
          { type: 'Peripherals', count: 15, icon: 'fas fa-print', color: '#8b5cf6' }
        ],
        byStatus: { good: 128, broken: 12, inRepair: 16 }
      },
      aggregatedResources: {
        total: 2340,
        consumed: 1335,
        remaining: 1005,
        byCategory: [
          { name: 'Electricity', allocated: 1000, consumed: 600, unit: 'kWh' },
          { name: 'Water', allocated: 500, consumed: 350, unit: 'm³' },
          { name: 'Raw Materials', allocated: 840, consumed: 385, unit: 'kg' }
        ],
        totalCost: 28500
      },
      aggregatedEmployees: {
        total: 12,
        byRole: [
          { role: 'Project Manager', count: 1, icon: 'fas fa-user-tie' },
          { role: 'Engineers', count: 5, icon: 'fas fa-user-graduate' },
          { role: 'Technicians', count: 4, icon: 'fas fa-user-cog' },
          { role: 'Support Staff', count: 2, icon: 'fas fa-user-friends' }
        ]
      },
      financial: {
        budget: 450000,
        spent: 292500,
        remaining: 157500,
        breakdown: [
          { category: 'Equipment', amount: 180000, color: '#3b82f6' },
          { category: 'Labor', amount: 85000, color: '#10b981' },
          { category: 'Materials', amount: 27500, color: '#f59e0b' }
        ]
      },
      recentActivities: [
        { text: 'Server maintenance completed', time: '2 hours ago', icon: 'fas fa-check', color: '#10b981' },
        { text: 'New equipment shipment arrived', time: 'Yesterday', icon: 'fas fa-truck', color: '#3b82f6' },
        { text: 'Budget review meeting scheduled', time: '2 days ago', icon: 'fas fa-calendar', color: '#8b5cf6' }
      ]
    };
  }

  // ==================== TEAM METHODS ====================

loadMissionTeam() {
  this.missionService.getMissionTeam(this.missionId).subscribe({
    next: (data) => {
      this.missionTeam = data;
      
      // 🔧 FIX: Update equipes with correct member counts from membersByEquipe
      this.equipes = data.equipes.map(equipe => ({
        ...equipe,
        memberCount: data.membersByEquipe?.[equipe.nom]?.length || 0
      }));
      
      this.updateTeamStats();
      this.loadUnassignedMembers();
    },
    error: (err) => console.error('Error loading mission team:', err)
  });
}
 updateTeamStats() {
  if (this.missionTeam) {
    this.statsCards[3].value = this.missionTeam.totalMembers.toString();
  }
}

  openAddMemberModal() {
    this.missionService.getAllEmployeesWithStatus().subscribe({
      next: (employees) => {
        this.availableEmployees = employees;
        this.selectedEmployees = [];
        this.selectedEquipeId = null;
        this.showAddMemberModal = true;
      },
      error: (err) => console.error('Error loading employees:', err)
    });
    
    this.missionService.getAllEquipes().subscribe({
      next: (equipes) => this.equipes = equipes,
      error: (err) => console.error('Error loading equipes:', err)
    });
  }

  closeAddMemberModal() {
    this.showAddMemberModal = false;
    this.selectedEmployees = [];
  }

  toggleEmployeeSelection(employee: EmployeDTO) {
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

  submitAddMembers() {
    if (this.selectedEmployees.length === 0) {
      Swal.fire({ icon: 'warning', title: 'No Selection', text: 'Please select at least one employee.' });
      return;
    }

    const request: AffectationRequest = {
      missionId: this.missionId,
      employeIds: this.selectedEmployees.map(e => e.id),
      equipeId: this.selectedEquipeId || undefined,
      dateDebut: new Date().toISOString().split('T')[0]
    };

    this.missionService.addEmployeesToMission(request).subscribe({
      next: () => {
        Swal.fire({ icon: 'success', title: 'Success!', text: `${this.selectedEmployees.length} employee(s) added to mission.` });
        this.closeAddMemberModal();
        this.loadMissionTeam();
        this.loadMissionData();
      },
      error: (err) => {
        console.error('Error adding employees:', err);
        Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message || 'Failed to add employees.' });
      }
    });
  }

  closeTeamDetailsModal() {
    this.showTeamDetailsModal = false;
    this.selectedEquipeMembers = [];
  }

  startEditEmployeeTeam(employeeId: number, currentEquipeId: number | null) {
    this.editingEmployeeId = employeeId;
    this.tempEquipeId = currentEquipeId;
  }

  saveEmployeeTeam(employeeId: number) {
    if (this.tempEquipeId) {
      this.missionService.updateEmployeeTeam(this.missionId, employeeId, this.tempEquipeId).subscribe({
        next: () => {
          Swal.fire({ icon: 'success', title: 'Updated!', text: 'Team assignment updated.' });
          this.loadMissionTeam();
          this.editingEmployeeId = null;
          this.tempEquipeId = null;
        },
        error: (err) => {
          console.error('Error updating team:', err);
          Swal.fire({ icon: 'error', title: 'Error', text: 'Failed to update team assignment.' });
        }
      });
    }
  }

  cancelEditEmployeeTeam() {
    this.editingEmployeeId = null;
    this.tempEquipeId = null;
  }

  removeEmployee(employeeId: number, employeeName: string) {
    Swal.fire({
      title: 'Remove Employee?',
      text: `Are you sure you want to remove ${employeeName} from this mission?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      confirmButtonText: 'Yes, remove'
    }).then((result) => {
      if (result.isConfirmed) {
        this.missionService.removeEmployeeFromMission(this.missionId, employeeId).subscribe({
          next: () => {
            Swal.fire('Removed!', `${employeeName} has been removed from the mission.`, 'success');
            this.loadMissionTeam();
            this.loadMissionData();
          },
          error: (err) => {
            console.error('Error removing employee:', err);
            Swal.fire('Error!', 'Failed to remove employee.', 'error');
          }
        });
      }
    });
  }

  objectKeys(obj: any): string[] {
    return obj ? Object.keys(obj) : [];
  }

  getEquipeIcon(type: string): string {
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

  getAvatarColor(id: number): string {
    const colors = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#ec489a'];
    return colors[id % colors.length];
  }

  getEquipeIdByName(equipeName: string): number | null {
    const equipe = this.missionTeam?.equipes.find(e => e.nom === equipeName);
    return equipe?.id || null;
  }

openTeamManagementModal() {
  // 🔧 FIX: Load mission-specific equipes with correct counts
  this.missionService.getMissionTeam(this.missionId).subscribe({
    next: (data) => {
      this.equipes = data.equipes.map(equipe => ({
        ...equipe,
        memberCount: data.membersByEquipe?.[equipe.nom]?.length || 0
      }));
      this.showTeamManagementModal = true;
    },
    error: (err) => console.error('Error loading equipes:', err)
  });
}

  closeTeamManagementModal() {
    this.showTeamManagementModal = false;
    this.editingEquipe = null;
    this.equipeForm.reset();
  }

loadAllEquipes() {
  // 🔧 FIX: Use mission-specific data instead of global getAllEquipes
  this.missionService.getMissionTeam(this.missionId).subscribe({
    next: (data) => {
      this.equipes = data.equipes.map(equipe => ({
        ...equipe,
        memberCount: data.membersByEquipe?.[equipe.nom]?.length || 0
      }));
    },
    error: (err) => console.error('Error loading equipes:', err)
  });
}

  openCreateEquipeModal() {
    this.editingEquipe = null;
    this.equipeForm.reset({ type: 'TOPOGRAPHIE' });
  }

  deleteEquipe(equipe: EquipeDTO) {
    Swal.fire({
      title: 'Delete Team?',
      text: `Are you sure you want to delete "${equipe.nom}"? This action cannot be undone.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      confirmButtonText: 'Yes, delete'
    }).then((result) => {
      if (result.isConfirmed) {
        this.missionService.deleteEquipe(equipe.id).subscribe({
          next: () => {
            Swal.fire('Deleted!', 'Team has been deleted.', 'success');
            this.loadAllEquipes();
            this.loadMissionTeam();
          },
          error: (err) => {
            console.error('Error deleting equipe:', err);
            Swal.fire('Error!', err.error?.message || 'Failed to delete team.', 'error');
          }
        });
      }
    });
  }

openManageEquipeMembers(equipe: EquipeDTO) {
  this.selectedEquipeForMembers = equipe;
  this.selectedMemberFilter = 'all'; // Reset filter when opening
  
  // Load ONLY mission members (not all employees)
  this.missionService.getMissionTeam(this.missionId).subscribe({
    next: (missionTeam) => {
      // Get all unique members from the mission
      const allMissionMembers: EmployeDTO[] = [];
      const memberIds = new Set<number>();
      
      // Add from members array
      if (missionTeam.members) {
        missionTeam.members.forEach(m => {
          if (!memberIds.has(m.id)) {
            memberIds.add(m.id);
            allMissionMembers.push(m);
          }
        });
      }
      
      // Add from membersByEquipe
      if (missionTeam.membersByEquipe) {
        Object.values(missionTeam.membersByEquipe).forEach((members: EmployeDTO[]) => {
          members.forEach(m => {
            if (!memberIds.has(m.id)) {
              memberIds.add(m.id);
              allMissionMembers.push(m);
            }
          });
        });
      }
      
      this.availableEmployeesForAssignment = allMissionMembers;
      
      // Then get current members of this equipe
      this.missionService.getEquipeMembers(equipe.id).subscribe({
        next: (members) => {
          this.equipeMembers = members;
          // Pre-select current members
          this.selectedEmployeesForEquipe = [...members];
          this.showEquipeMembersModal = true;
        },
        error: (err) => {
          console.error('Error loading equipe members:', err);
          this.selectedEmployeesForEquipe = [];
          this.showEquipeMembersModal = true;
        }
      });
    },
    error: (err) => {
      console.error('Error loading mission members:', err);
      Swal.fire({ icon: 'error', title: 'Error', text: 'Failed to load mission members' });
    }
  });
}

  closeEquipeMembersModal() {
    this.showEquipeMembersModal = false;
    this.selectedEquipeForMembers = null;
    this.selectedEmployeesForEquipe = [];
  }

  toggleEmployeeForEquipe(employee: EmployeDTO) {
    const index = this.selectedEmployeesForEquipe.findIndex(e => e.id === employee.id);
    if (index === -1) {
      this.selectedEmployeesForEquipe.push(employee);
    } else {
      this.selectedEmployeesForEquipe.splice(index, 1);
    }
  }

  isEmployeeSelectedForEquipe(employee: EmployeDTO): boolean {
    return this.selectedEmployeesForEquipe.some(e => e.id === employee.id);
  }

saveEquipeMembers() {
  if (!this.selectedEquipeForMembers) return;
  
  const selectedIds = this.selectedEmployeesForEquipe.map(e => e.id);
  
  this.missionService.assignEmployeesToEquipe(this.selectedEquipeForMembers.id, selectedIds).subscribe({
    next: () => {
      Swal.fire('Success!', 'Team members updated successfully.', 'success');
      this.closeEquipeMembersModal();
      this.loadMissionTeam(); // Reload mission team to update UI
      this.loadAllEquipes();
    },
    error: (err) => {
      console.error('Error assigning members:', err);
      Swal.fire('Error!', err.error?.message || 'Failed to assign members.', 'error');
    }
  });
}

// Get team name for an employee
getEmployeeTeamName(employeeId: number): string | null {
  if (!this.missionTeam?.membersByEquipe) return null;
  
  for (const [teamName, members] of Object.entries(this.missionTeam.membersByEquipe)) {
    // Skip the "Non assigné" key when checking for assigned teams
    if (teamName === "Non assigné") continue;
    
    if (members && Array.isArray(members) && members.some(m => m && m.id === employeeId)) {
      return teamName;
    }
  }
  return null;
}

// Check if employee is currently in the selected team
isEmployeeInCurrentTeam(employeeId: number): boolean {
  if (!this.selectedEquipeForMembers) return false;
  const currentTeamName = this.selectedEquipeForMembers.nom;
  const employeeTeam = this.getEmployeeTeamName(employeeId);
  return employeeTeam === currentTeamName;
}


  getTypeIcon(type: string): string {
    const icons: { [key: string]: string } = {
      'TOPOGRAPHIE': 'fas fa-map',
      'LAYONNAGE': 'fas fa-draw-polygon',
      'ENERGISREMENT': 'fas fa-bolt',
      'POSE': 'fas fa-tools',
      'RAMASSAGE': 'fas fa-truck'
    };
    return icons[type] || 'fas fa-users';
  }

  getTypeColor(type: string): string {
    const colors: { [key: string]: string } = {
      'TOPOGRAPHIE': '#3b82f6',
      'LAYONNAGE': '#10b981',
      'ENERGISREMENT': '#f59e0b',
      'POSE': '#8b5cf6',
      'RAMASSAGE': '#14b8a6'
    };
    return colors[type] || '#6b7280';
  }

  openCreateTeamModal() {
    this.editingEquipe = null;
    this.equipeForm.reset({ nom: '', type: 'TOPOGRAPHIE' });
    this.showCreateEditTeamModal = true;
  }

  openEditEquipeModal(equipe: EquipeDTO) {
    this.editingEquipe = equipe;
    this.equipeForm.patchValue({
      nom: equipe.nom,
      type: equipe.type
    });
    this.showCreateEditTeamModal = true;
  }

  closeCreateEditTeamModal() {
    this.showCreateEditTeamModal = false;
    this.editingEquipe = null;
    this.equipeForm.reset();
  }

  saveEquipe() {
    if (this.equipeForm.invalid) return;
    
    const request: EquipeRequest = this.equipeForm.value;
    
    if (this.editingEquipe) {
      this.missionService.updateEquipe(this.editingEquipe.id, request).subscribe({
        next: () => {
          Swal.fire('Success!', 'Team updated successfully.', 'success');
          this.closeCreateEditTeamModal();
          this.loadAllEquipes();
          this.loadMissionTeam();
        },
        error: (err) => {
          console.error('Error updating equipe:', err);
          Swal.fire('Error!', err.error?.message || 'Failed to update team.', 'error');
        }
      });
    } else {
      this.missionService.createEquipe(request).subscribe({
        next: (newEquipe) => {
          Swal.fire('Success!', 'Team created successfully.', 'success');
          this.closeCreateEditTeamModal();
          this.loadAllEquipes();
          this.loadMissionTeam();
        },
        error: (err) => {
          console.error('Error creating equipe:', err);
          Swal.fire('Error!', err.error?.message || 'Failed to create team.', 'error');
        }
      });
    }
  }

  showTeamMembers(equipeName: string) {
    const equipe = this.missionTeam?.equipes.find(e => e.nom === equipeName);
    if (equipe && this.missionTeam?.membersByEquipe[equipeName]) {
      this.selectedEquipeForMembers = equipe;
      this.selectedEquipeMembers = this.missionTeam.membersByEquipe[equipeName];
      this.showTeamDetailsModal = true;
    }
  }
// mission-overview.component.ts - أضف هذه الدالة

  viewEquipeDetail(equipeId: number): void {
    this.router.navigate(['/pages/equipe-detail', equipeId], {
      queryParams: { missionId: this.missionId }
    });
  }





  // ==================== ACTIVE MANAGEMENT METHODS ====================

loadActives(): void {
  this.missionService.getAllActives().subscribe({
    next: (data) => {
      this.activesList = data;
      this.filteredActivesList = [...data];
      this.updatePaginationForActives();
    },
    error: (err) => console.error('Error loading actives:', err)
  });
}


openCreateActiveModal(): void {
  this.editingActive = null;
  this.activeForm.reset({
    codeActive: '',
    objectif: '',
    description: ''
  });
  this.showActiveModal = true;
}

openEditActiveModal(active: ActiveDTO): void {
  this.editingActive = active;
  this.activeForm.patchValue({
    codeActive: active.codeActive,
    objectif: active.objectif,
    description: active.description
  });
  this.showActiveModal = true;
}

closeActiveModal(): void {
  this.showActiveModal = false;
  this.editingActive = null;
  this.activeForm.reset();
}

submitActive(): void {
  if (this.activeForm.invalid) {
    Swal.fire({ icon: 'warning', title: 'Warning', text: 'Please fill all required fields' });
    return;
  }

  const request: ActiveRequest = {
    ...this.activeForm.value,
    missionId: this.missionId  // ✅ ADD THIS - Pass the current mission ID
  };

  if (this.editingActive) {
    this.missionService.updateActive(this.editingActive.id, request).subscribe({
      next: () => {
        Swal.fire({ icon: 'success', title: 'Success', text: 'Activity updated successfully' });
        this.closeActiveModal();
        this.loadActives();
        this.loadMissionData();
      },
      error: (err) => {
        console.error('Error updating active:', err);
        Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message || 'Failed to update activity' });
      }
    });
  } else {
    this.missionService.createActive(request).subscribe({
      next: () => {
        Swal.fire({ icon: 'success', title: 'Success', text: 'Activity created successfully' });
        this.closeActiveModal();
        this.loadActives();
        this.loadMissionData();
      },
      error: (err) => {
        console.error('Error creating active:', err);
        Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message || 'Failed to create activity' });
      }
    });
  }
}
// Add this method to the MissionOverviewComponent class
viewEmployeeProfile(employeeId: number, firstName: string, lastName: string): void {
  const employeeName = `${firstName} ${lastName}`;
  this.router.navigate(['/pages/employe-account'], {
    queryParams: {
      employeId: employeeId,
      employeName: employeeName
    }
  });
}
deleteActive(active: ActiveDTO, event: Event): void {
  event.stopPropagation();
  
  Swal.fire({
    title: 'Confirm Delete',
    text: `Are you sure you want to delete activity "${active.codeActive}"?`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#d33',
    cancelButtonColor: '#3085d6',
    confirmButtonText: 'Yes, Delete',
    cancelButtonText: 'Cancel'
  }).then((result) => {
    if (result.isConfirmed) {
      this.missionService.deleteActive(active.id).subscribe({
        next: () => {
          Swal.fire('Deleted', 'Activity has been deleted successfully', 'success');
          this.loadActives();
          this.loadMissionData();
        },
        error: (err) => {
          console.error('Error deleting active:', err);
          Swal.fire('Error', err.error?.message || 'Failed to delete activity', 'error');
        }
      });
    }
  });
}




// ==================== PROJECT MANAGEMENT METHODS ====================
// Replace the existing loadCurrentProject method

loadCurrentProject(): void {
  this.missionService.getCurrentProjectByMission(this.missionId).subscribe({
    next: (project) => {
      console.log('[DEBUG] Raw project data:', project);
      
      // Get status from etatAvancements (project status has activeId = null)
      let statusCode = 'PLANIFIER'; // Default
      
      if (project?.etatAvancements && project.etatAvancements.length > 0) {
        // Find the project status - in ProjectResponse, etatAvancements are EtatAvancementDTO
        // which don't have activeId property. So we need to check if it's the first one
        // or if the backend returns it differently
        const projectStatus = project.etatAvancements[0]; // First one is usually project status
        if (projectStatus && projectStatus.status) {
          statusCode = projectStatus.status;
        }
      } else {
        // Fallback to progression-based
        statusCode = this.getStatusCodeFromProgression(project?.progression || 0);
      }
      
      // Store the project with the status code
      this.currentProjectData = {
        ...project,
        status: statusCode
      } as ProjectResponse;
      
      console.log('[DEBUG] Project status code:', this.currentProjectData.status);
      
      // Update the mission.currentProject if it exists
      if (this.mission && project) {
        // Convert status code to the expected union type for mission.currentProject
        const displayStatus = this.convertStatusToDisplayFormat(statusCode);
        
        this.mission.currentProject = {
          id: project.id,
          name: project.nom,
          description: project.description,
          startDate: project.objectifDebut,
          targetEndDate: project.objectifFin,
          progress: project.progression,
          status: displayStatus  // Use converted status for mission display
        };
        this.updateStatsCards();
      }
    },
    error: (err) => console.error('Error loading current project:', err)
  });
}

loadCurrentProjectFromAPI(): void {
  this.missionService.getMissionOverview(this.missionId).subscribe({
    next: (data) => {
      // Only set currentProjectData if there's a valid project (id > 0)
      if (data.currentProject && data.currentProject.id > 0) {
        this.currentProjectData = {
          id: data.currentProject.id,
          nom: data.currentProject.name,
          description: data.currentProject.description,
          objectifDebut: data.currentProject.startDate,
          objectifFin: data.currentProject.targetEndDate,
          progression: data.currentProject.progress,
          status: data.currentProject.statusCode || 'PLANIFIER',
          budget: 0,
          objectifVP: 0
        } as ProjectResponse;
        
        console.log('[DEBUG] Project status code:', this.currentProjectData.status);
      } else {
        this.currentProjectData = null;  // No active project
      }
    },
    error: (err) => console.error('Error loading current project:', err)
  });
}

// Helper method to get status from progression
getStatusCodeFromProgression(progression: number): string {
  if (progression >= 100) return 'TERMINI';
  if (progression >= 75) return 'ENCOURS';
  if (progression >= 50) return 'ENATTENTE';
  if (progression >= 25) return 'ENRETARD';
  return 'PLANIFIER';
}

// ✅ Add this helper method to convert status codes
convertStatusToDisplayFormat(statusCode: string): 'on-track' | 'delayed' | 'at-risk' | 'pending' | 'completed' {
  switch(statusCode) {
    case 'TERMINI':
      return 'completed';
    case 'ENCOURS':
      return 'on-track';
    case 'ENRETARD':
      return 'delayed';
    case 'ENATTENTE':
      return 'at-risk';
    case 'PLANIFIER':
    case 'ANNULE':
    default:
      return 'pending';
  }
}


getProjectStatusFromProgression(progression: number): 'on-track' | 'delayed' | 'at-risk' | 'pending' | 'completed' {
  if (progression >= 100) return 'completed';
  if (progression >= 75) return 'on-track';
  if (progression >= 50) return 'at-risk';
  if (progression >= 25) return 'delayed';
  return 'pending';
}

// ✅ Add these methods for display
getProjectStatusColorForDisplay(status: string): string {
  switch(status) {
    case 'completed': return '#10b981';
    case 'on-track': return '#3b82f6';
    case 'at-risk': return '#f59e0b';
    case 'delayed': return '#ef4444';
    case 'pending': return '#94a3b8';
    default: return '#6b7280';
  }
}


openCreateProjectModal(): void {
  this.projectForm.reset({
    nom: '',
    description: '',
    budget: 0,
    objectifVP: 0,
    objectifDebut: new Date().toISOString().split('T')[0],
    objectifFin: ''
  });
  this.showCreateProjectModal = true;
}

closeCreateProjectModal(): void {
  this.showCreateProjectModal = false;
  this.projectForm.reset();
}

createProject(): void {
  if (this.projectForm.invalid) {
    Swal.fire({ icon: 'warning', title: 'Warning', text: 'Please fill all required fields' });
    return;
  }

  const request: ProjectRequest = {
    ...this.projectForm.value,
    missionId: this.missionId
  };

  this.missionService.createProject(request).subscribe({
    next: (project) => {
      Swal.fire({ icon: 'success', title: 'Success', text: 'Project created successfully' });
      this.closeCreateProjectModal();
      this.loadCurrentProject();
      this.loadMissionData();
    },
    error: (err) => {
      console.error('Error creating project:', err);
      Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message || 'Failed to create project' });
    }
  });
}

viewProjectDetail(): void {
  if (this.currentProjectData) {
    this.router.navigate(['/pages/project-overview', this.currentProjectData.id]);
  }
}

// ==================== PAGINATION METHODS ====================

updatePaginationForEquipment() {
  this.totalPages = Math.ceil(this.filteredEquipmentList.length / this.pageSize);
  if (this.currentPage > this.totalPages) {
    this.currentPage = Math.max(1, this.totalPages);
  }
  const startIndex = (this.currentPage - 1) * this.pageSize;
  this.paginatedFilteredEquipmentList = this.filteredEquipmentList.slice(startIndex, startIndex + this.pageSize);
}

updatePaginationForResources() {
  this.totalPages = Math.ceil(this.filteredResourcesList.length / this.pageSize);
  if (this.currentPage > this.totalPages) {
    this.currentPage = Math.max(1, this.totalPages);
  }
  const startIndex = (this.currentPage - 1) * this.pageSize;
  this.paginatedFilteredResourcesList = this.filteredResourcesList.slice(startIndex, startIndex + this.pageSize);
}

updatePaginationForActives() {
  this.totalPages = Math.ceil(this.filteredActivesList.length / this.pageSize);
  if (this.currentPage > this.totalPages) {
    this.currentPage = Math.max(1, this.totalPages);
  }
  const startIndex = (this.currentPage - 1) * this.pageSize;
  this.paginatedFilteredActivesList = this.filteredActivesList.slice(startIndex, startIndex + this.pageSize);
}

previousPage() {
  if (this.currentPage > 1) {
    this.currentPage--;
    this.updatePaginationForCurrentTab();
  }
}

nextPage() {
  if (this.currentPage < this.totalPages) {
    this.currentPage++;
    this.updatePaginationForCurrentTab();
  }
}

goToPage(page: number) {
  if (page >= 1 && page <= this.totalPages) {
    this.currentPage = page;
    this.updatePaginationForCurrentTab();
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

updatePaginationForCurrentTab() {
  switch (this.selectedTab) {
    case 'equipment':
      this.updatePaginationForEquipment();
      break;
    case 'resources':
      this.updatePaginationForResources();
      break;
    case 'actives':
      this.updatePaginationForActives();
      break;
  }
}

resetPagination() {
  this.currentPage = 1;
  this.updatePaginationForCurrentTab();
}

// Get filtered members based on selected filter
getFilteredMembersForAssignment(): EmployeDTO[] {
  if (!this.availableEmployeesForAssignment) return [];
  
  switch (this.selectedMemberFilter) {
    case 'assigned':
      // Members assigned to ANY team (excluding "Non assigné")
      return this.availableEmployeesForAssignment.filter(emp => {
        const teamName = this.getEmployeeTeamName(emp.id);
        return teamName !== null;
      });
    case 'not-assigned':
      // Members NOT assigned to any team
      return this.availableEmployeesForAssignment.filter(emp => {
        const teamName = this.getEmployeeTeamName(emp.id);
        return teamName === null;
      });
    default:
      return this.availableEmployeesForAssignment;
  }
}

// Get count of assigned members
getAssignedMembersCount(): number {
  if (!this.availableEmployeesForAssignment) return 0;
  return this.availableEmployeesForAssignment.filter(emp => this.getEmployeeTeamName(emp.id) !== null).length;
}

// Get count of not assigned members
getNotAssignedMembersCount(): number {
  if (!this.availableEmployeesForAssignment) return 0;
  return this.availableEmployeesForAssignment.filter(emp => this.getEmployeeTeamName(emp.id) === null).length;
}

// Add this method to your MissionOverviewComponent class
getTeamNamesWithoutNonAssigned(): string[] {
  if (!this.missionTeam?.membersByEquipe) return [];
  return Object.keys(this.missionTeam.membersByEquipe)
    .filter(name => name !== 'Non assigné')
    .sort(); // Sort alphabetically
}

// Update getEquipeIdForEmployee method (replace the existing getEquipeIdByName)
getEquipeIdForEmployee(employeeId: number): number | null {
  if (!this.missionTeam?.equipes) return null;
  
  for (const equipe of this.missionTeam.equipes) {
    const members = this.missionTeam.membersByEquipe[equipe.nom];
    if (members && members.some(m => m.id === employeeId)) {
      return equipe.id;
    }
  }
  return null;
}


// Add to loadMissionTeam method or create a separate method
loadUnassignedMembers() {
  if (this.missionTeam?.membersByEquipe?.['Non assigné']) {
    this.unassignedMembersList = this.missionTeam.membersByEquipe['Non assigné'];
    this.updateUnassignedPagination();
  } else {
    this.unassignedMembersList = [];
  }
}



// Pagination methods for unassigned members
updateUnassignedPagination() {
  this.unassignedTotalPages = Math.ceil(this.unassignedMembersList.length / this.unassignedPageSize);
  if (this.unassignedCurrentPage > this.unassignedTotalPages) {
    this.unassignedCurrentPage = Math.max(1, this.unassignedTotalPages);
  }
  const startIndex = (this.unassignedCurrentPage - 1) * this.unassignedPageSize;
  this.paginatedUnassignedMembers = this.unassignedMembersList.slice(startIndex, startIndex + this.unassignedPageSize);
}

previousUnassignedPage() {
  if (this.unassignedCurrentPage > 1) {
    this.unassignedCurrentPage--;
    this.updateUnassignedPagination();
  }
}

nextUnassignedPage() {
  if (this.unassignedCurrentPage < this.unassignedTotalPages) {
    this.unassignedCurrentPage++;
    this.updateUnassignedPagination();
  }
}

goToUnassignedPage(page: number) {
  if (page >= 1 && page <= this.unassignedTotalPages) {
    this.unassignedCurrentPage = page;
    this.updateUnassignedPagination();
  }
}

goToFirstUnassignedPage() {
  this.goToUnassignedPage(1);
}

goToLastUnassignedPage() {
  this.goToUnassignedPage(this.unassignedTotalPages);
}

getUnassignedPageNumbers(): number[] {
  const pages: number[] = [];
  const maxVisible = 5;
  let startPage = Math.max(1, this.unassignedCurrentPage - Math.floor(maxVisible / 2));
  let endPage = Math.min(this.unassignedTotalPages, startPage + maxVisible - 1);
  
  if (endPage - startPage + 1 < maxVisible) {
    startPage = Math.max(1, endPage - maxVisible + 1);
  }
  
  for (let i = startPage; i <= endPage; i++) {
    pages.push(i);
  }
  return pages;
}

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}

interface EquipmentDetail {
  id: string;
  type: string;
  count: number;
  icon: string;
  color: string;
  materielId: number | null;
  affectationId: number | null;
  goodCount: number;
  brokenCount: number;
  inRepairCount: number;
  repairs: ReparationItem[];
}



