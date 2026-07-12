import { Component, Input, OnInit } from '@angular/core';
import { NbDialogRef } from '@nebular/theme';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ReparationService, PanneRequest, LancementReparation, FinReparation, ReparationItem, UpdatePanneRequest, UpdateInternalRepairRequest, UpdateExternalRepairRequest } from '../../../services/materiel/reparation.service';
import { MaterielService, Materiel } from '../../../services/materiel/materiel.service';
import { AffectationMaterielService, AffectationMateriel } from '../../../services/materiel/affectation-materiel.service';
import { AffectationMaterielToActiveService, AffectationMaterielToActiveDTO, UpdateMaterielToActiveRequest } from '../../../services/materiel/affectation-materiel-to-active.service';
import { MissionService, ActiveDTO } from '../../../services/mission/mission.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'ngx-materiel-detail-modal',
  templateUrl: './materiel-detail-modal.component.html',
  styleUrls: ['./materiel-detail-modal.component.scss']
})
export class MaterielDetailModalComponent implements OnInit {
  @Input() materielId!: number;
  @Input() missionId: number | null = null;
  @Input() projectId: number | null = null;

  materiel: Materiel | null = null;
  isLoading = false;

  // Activities for select
  activities: ActiveDTO[] = [];
  isLoadingActivities = false;

  // Activity Affectations
  activityAffectations: AffectationMaterielToActiveDTO[] = [];
  editingActivityAffectationId: number | null = null;
  editingActivityAffectationForm: FormGroup;

  // Repair management
  showBreakdownForm = false;
  breakdownForm: FormGroup;

  // Edit modes for repairs
  editingPendingId: number | null = null;
  editingPendingForm: FormGroup;

  editingInternalId: number | null = null;
  editingInternalForm: FormGroup;

  editingExternalId: number | null = null;
  editingExternalForm: FormGroup;

  showLaunchRepairModal = false;
  selectedReparation: ReparationItem | null = null;
  launchRepairForm: FormGroup;

  showCompleteRepairModal = false;
  selectedOngoingRepair: ReparationItem | null = null;
  completeRepairForm: FormGroup;

  // Lists
  pendingBreakdowns: ReparationItem[] = [];
  ongoingInternalRepairs: ReparationItem[] = [];
  ongoingExternalRepairs: ReparationItem[] = [];
  completedRepairs: ReparationItem[] = [];

  showFullHistory = false;
  showBreakdownFromMission = false;
  selectedAffectation: AffectationMateriel | null = null;

  constructor(
    protected dialogRef: NbDialogRef<MaterielDetailModalComponent>,
    private fb: FormBuilder,
    private materielService: MaterielService,
    private reparationService: ReparationService,
    private affectationService: AffectationMaterielService,
    private affectationToActiveService: AffectationMaterielToActiveService,
    private missionService: MissionService
  ) {
    // Breakdown form
    this.breakdownForm = this.fb.group({
      datePanne: [new Date().toISOString().split('T')[0], Validators.required],
      detailProbleme: ['']
    });

    // Edit pending form
    this.editingPendingForm = this.fb.group({
      datePanne: ['', Validators.required],
      detailProbleme: ['']
    });

    // Edit internal form
    this.editingInternalForm = this.fb.group({
      technicien: ['', Validators.required],
      detailProbleme: ['']
    });

    // Edit external form
    this.editingExternalForm = this.fb.group({
      fournisseur: ['', Validators.required],
      detailProbleme: [''],
      dateSortieChantier: ['', Validators.required]
    });

    // Edit activity affectation form
    this.editingActivityAffectationForm = this.fb.group({
      dateDebut: ['', Validators.required],
      dateFin: ['']
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
  }

  ngOnInit() {
    this.loadMateriel();
    this.loadReparations();
    this.loadActivityAffectations();
    this.loadActivities();
  }

  loadActivities() {
    if (!this.missionId) return;

    this.isLoadingActivities = true;
    this.missionService.getActivesByMission(this.missionId).subscribe({
      next: (data) => {
        this.activities = data;
        this.isLoadingActivities = false;
      },
      error: (err) => {
        console.error('Error loading activities:', err);
        this.isLoadingActivities = false;
      }
    });
  }

  loadMateriel() {
    this.isLoading = true;
    this.materielService.getById(this.materielId).subscribe({
      next: (data) => {
        this.materiel = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading materiel:', err);
        this.isLoading = false;
      }
    });
  }

  loadReparations() {
    this.reparationService.getPendingByMateriel(this.materielId).subscribe({
      next: (data) => this.pendingBreakdowns = data
    });

    this.reparationService.getOngoingInternal(this.materielId).subscribe({
      next: (data) => this.ongoingInternalRepairs = data
    });

    this.reparationService.getOngoingExternal(this.materielId).subscribe({
      next: (data) => this.ongoingExternalRepairs = data
    });

    this.reparationService.getCompletedByMateriel(this.materielId).subscribe({
      next: (data) => this.completedRepairs = data
    });
  }

  loadActivityAffectations() {
    this.affectationToActiveService.getByMaterielId(this.materielId).subscribe({
      next: (data) => {
        this.activityAffectations = data;
      },
      error: (err) => {
        console.error('Error loading activity affectations:', err);
      }
    });
  }

  // ==================== DECLARE BREAKDOWN ====================

  declareBreakdown() {
    if (this.breakdownForm.invalid) {
      Swal.fire({ icon: 'warning', title: 'Invalid', text: 'Please fill required fields' });
      return;
    }

    const request: PanneRequest = {
      materielId: this.materielId,
      datePanne: this.breakdownForm.value.datePanne,
      detailProbleme: this.breakdownForm.value.detailProbleme,
      missionId: this.showBreakdownFromMission ? this.selectedAffectation?.missionId : null,
      affectationId: this.showBreakdownFromMission ? this.selectedAffectation?.idAffectation : null
    };

    this.isLoading = true;
    this.reparationService.declarePanne(request).subscribe({
      next: () => {
        Swal.fire({ icon: 'success', title: 'Success!', text: 'Breakdown declared' });
        this.showBreakdownForm = false;
        this.breakdownForm.reset({ datePanne: new Date().toISOString().split('T')[0] });
        this.loadMateriel();
        this.loadReparations();
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message || 'Failed to declare breakdown' });
      }
    });
  }

  // ==================== EDIT PENDING BREAKDOWN ====================

  startEditPending(repair: ReparationItem) {
    this.editingPendingId = repair.idReparation;
    this.editingPendingForm.patchValue({
      datePanne: repair.datePanne,
      detailProbleme: repair.detailProbleme || ''
    });
  }

  cancelEditPending() {
    this.editingPendingId = null;
  }

  saveEditPending(repair: ReparationItem) {
    if (this.editingPendingForm.invalid) {
      Swal.fire({ icon: 'warning', title: 'Invalid', text: 'Please fill required fields' });
      return;
    }

    const request: UpdatePanneRequest = {
      datePanne: this.editingPendingForm.value.datePanne,
      detailProbleme: this.editingPendingForm.value.detailProbleme
    };

    this.isLoading = true;
    this.reparationService.updatePanne(repair.idReparation, request).subscribe({
      next: () => {
        Swal.fire({ icon: 'success', title: 'Updated!', text: 'Breakdown updated successfully' });
        this.editingPendingId = null;
        this.loadMateriel();
        this.loadReparations();
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message || 'Failed to update' });
      }
    });
  }

  // ==================== EDIT INTERNAL REPAIR ====================

  startEditInternal(repair: ReparationItem) {
    this.editingInternalId = repair.idReparation;
    this.editingInternalForm.patchValue({
      technicien: repair.technicien,
      detailProbleme: repair.detailProbleme || ''
    });
  }

  cancelEditInternal() {
    this.editingInternalId = null;
  }

  saveEditInternal(repair: ReparationItem) {
    if (this.editingInternalForm.invalid) {
      Swal.fire({ icon: 'warning', title: 'Invalid', text: 'Please fill required fields' });
      return;
    }

    const request: UpdateInternalRepairRequest = {
      technicien: this.editingInternalForm.value.technicien,
      detailProbleme: this.editingInternalForm.value.detailProbleme
    };

    this.isLoading = true;
    this.reparationService.updateInternalRepair(repair.idReparation, request).subscribe({
      next: () => {
        Swal.fire({ icon: 'success', title: 'Updated!', text: 'Internal repair updated successfully' });
        this.editingInternalId = null;
        this.loadMateriel();
        this.loadReparations();
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message || 'Failed to update' });
      }
    });
  }

  // ==================== EDIT EXTERNAL REPAIR ====================

  startEditExternal(repair: ReparationItem) {
    this.editingExternalId = repair.idReparation;
    this.editingExternalForm.patchValue({
      fournisseur: repair.fournisseur,
      detailProbleme: repair.detailProbleme || '',
      dateSortieChantier: repair.dateSortieChantier
    });
  }

  cancelEditExternal() {
    this.editingExternalId = null;
  }

  saveEditExternal(repair: ReparationItem) {
    if (this.editingExternalForm.invalid) {
      Swal.fire({ icon: 'warning', title: 'Invalid', text: 'Please fill required fields' });
      return;
    }

    const request: UpdateExternalRepairRequest = {
      fournisseur: this.editingExternalForm.value.fournisseur,
      detailProbleme: this.editingExternalForm.value.detailProbleme,
      dateSortieChantier: this.editingExternalForm.value.dateSortieChantier
    };

    this.isLoading = true;
    this.reparationService.updateExternalRepair(repair.idReparation, request).subscribe({
      next: () => {
        Swal.fire({ icon: 'success', title: 'Updated!', text: 'External repair updated successfully' });
        this.editingExternalId = null;
        this.loadMateriel();
        this.loadReparations();
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message || 'Failed to update' });
      }
    });
  }

  // ==================== EDIT ACTIVITY AFFECTATION ====================

  startEditActivityAffectation(affectation: AffectationMaterielToActiveDTO) {
    this.editingActivityAffectationId = affectation.idAffectation;
    this.editingActivityAffectationForm.patchValue({
      dateDebut: affectation.dateDebut,
      dateFin: affectation.dateFin || ''
    });
  }

  cancelEditActivityAffectation() {
    this.editingActivityAffectationId = null;
  }

  saveEditActivityAffectation(affectation: AffectationMaterielToActiveDTO) {
    if (this.editingActivityAffectationForm.invalid) {
      Swal.fire({ icon: 'warning', title: 'Invalid', text: 'Please fill required fields' });
      return;
    }

    const request: UpdateMaterielToActiveRequest = {
      dateDebut: this.editingActivityAffectationForm.value.dateDebut,
      dateFin: this.editingActivityAffectationForm.value.dateFin || null
    };

    this.isLoading = true;
    this.affectationToActiveService.update(affectation.idAffectation, request).subscribe({
      next: () => {
        Swal.fire({ icon: 'success', title: 'Updated!', text: 'Assignment updated successfully' });
        this.editingActivityAffectationId = null;
        this.loadActivityAffectations();
        this.loadMateriel();
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message || 'Failed to update' });
      }
    });
  }

  // ==================== DELETE ACTIVITY AFFECTATION ====================

  deleteActivityAffectation(affectation: AffectationMaterielToActiveDTO, event: Event) {
    event.stopPropagation();

    Swal.fire({
      title: 'Delete Assignment?',
      text: `Are you sure you want to delete this assignment to "${affectation.activeCode}"?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Yes, delete it!',
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        this.isLoading = true;
        this.affectationToActiveService.delete(affectation.idAffectation).subscribe({
          next: () => {
            Swal.fire({ icon: 'success', title: 'Deleted!', text: 'Assignment deleted' });
            this.loadActivityAffectations();
            this.loadMateriel();
            this.isLoading = false;
          },
          error: (err) => {
            this.isLoading = false;
            Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message || 'Failed to delete' });
          }
        });
      }
    });
  }

  // ==================== DELETE ANY REPAIR ====================

  deleteReparation(repair: ReparationItem, event: Event) {
    event.stopPropagation();

    Swal.fire({
      title: 'Delete Repair Record?',
      text: `Are you sure you want to delete this ${repair.type === 'INTERNE' ? 'internal' : repair.type === 'EXTERNE' ? 'external' : 'breakdown'} repair record?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Yes, delete it!',
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        this.isLoading = true;
        this.reparationService.deleteReparation(repair.idReparation).subscribe({
          next: () => {
            Swal.fire({ icon: 'success', title: 'Deleted!', text: 'Repair record deleted' });
            this.loadMateriel();
            this.loadReparations();
            this.isLoading = false;
          },
          error: (err) => {
            this.isLoading = false;
            Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message || 'Failed to delete' });
          }
        });
      }
    });
  }

  // ==================== LAUNCH REPAIR ====================

  openLaunchRepair(repair: ReparationItem) {
    this.selectedReparation = repair;
    this.launchRepairForm.reset({ type: 'INTERNE' });
    this.showLaunchRepairModal = true;
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

    this.isLoading = true;
    this.reparationService.launchRepair(request).subscribe({
      next: () => {
        Swal.fire({ icon: 'success', title: 'Repair Launched!' });
        this.showLaunchRepairModal = false;
        this.loadMateriel();
        this.loadReparations();
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message });
      }
    });
  }

  // ==================== COMPLETE REPAIR ====================

  openCompleteRepair(repair: ReparationItem) {
    this.selectedOngoingRepair = repair;
    this.completeRepairForm.reset({
      dateReparation: new Date().toISOString().split('T')[0],
      cout: 0
    });
    this.showCompleteRepairModal = true;
  }

  completeRepair() {
    if (this.completeRepairForm.invalid || !this.selectedOngoingRepair) return;

    const request: FinReparation = {
      reparationId: this.selectedOngoingRepair.idReparation,
      dateReparation: this.completeRepairForm.value.dateReparation,
      cout: this.completeRepairForm.value.cout,
      dateEntreeChantier: this.completeRepairForm.value.dateEntreeChantier
    };

    this.isLoading = true;
    this.reparationService.completeRepair(request).subscribe({
      next: () => {
        Swal.fire({ icon: 'success', title: 'Repair Completed!' });
        this.showCompleteRepairModal = false;
        this.loadMateriel();
        this.loadReparations();
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message });
      }
    });
  }

  close() {
    this.dialogRef.close();
  }

  getStatusText(status: string): string {
    const texts: any = {
      'EN_BON_ETAT': 'Good Condition',
      'EN_PANNE': 'Broken',
      'EN_REPARATION_INTERNE': 'Internal Repair',
      'EN_REPARATION_EXTERNE': 'External Repair'
    };
    return texts[status] || status;
  }

  getStatusColor(status: string): string {
    const colors: any = {
      'EN_BON_ETAT': '#10b981',
      'EN_PANNE': '#ef4444',
      'EN_REPARATION_INTERNE': '#f59e0b',
      'EN_REPARATION_EXTERNE': '#8b5cf6'
    };
    return colors[status] || '#6b7280';
  }

  getStatusIcon(status: string): string {
    const icons: any = {
      'EN_BON_ETAT': 'checkmark-circle-outline',
      'EN_PANNE': 'alert-circle-outline',
      'EN_REPARATION_INTERNE': 'settings-outline',
      'EN_REPARATION_EXTERNE': 'settings-outline'
    };
    return icons[status] || 'info-outline';
  }

  // Helper methods (each repair = 1 unit)
  getAvailableStockQuantity(): number {
    if (!this.materiel) return 0;
    return 1 - this.getBrokenStockQuantity() - this.getInRepairStockQuantity();
  }

  getTotalStockQuantity(): number {
    return 1;
  }

  getTotalBrokenQuantity(): number {
    let total = this.getBrokenStockQuantity();
    if (this.pendingBreakdowns) {
      total += this.pendingBreakdowns.filter(r => r.sourceType === 'MISSION').length;
    }
    return total;
  }

  getBrokenStockQuantity(): number {
    if (!this.pendingBreakdowns) return 0;
    return this.pendingBreakdowns.filter(r => r.sourceType === 'STOCK').length;
  }

  getInRepairStockQuantity(): number {
    let internalRepair = this.ongoingInternalRepairs.filter(r => r.sourceType === 'STOCK').length;
    let externalRepair = this.ongoingExternalRepairs.filter(r => r.sourceType === 'STOCK').length;
    return internalRepair + externalRepair;
  }

  getMaxBreakdownQuantity(): number {
    if (this.showBreakdownFromMission && this.selectedAffectation) {
      let brokenFromThisMission = this.pendingBreakdowns.filter(r => r.affectationId === this.selectedAffectation?.idAffectation).length;
      let inRepairFromThisMission = this.ongoingInternalRepairs.filter(r => r.affectationId === this.selectedAffectation?.idAffectation).length;
      inRepairFromThisMission += this.ongoingExternalRepairs.filter(r => r.affectationId === this.selectedAffectation?.idAffectation).length;
      return 1 - brokenFromThisMission - inRepairFromThisMission;
    } else {
      return this.getAvailableStockQuantity();
    }
  }
}