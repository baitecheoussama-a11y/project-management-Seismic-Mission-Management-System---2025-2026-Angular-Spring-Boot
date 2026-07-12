import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { NbDialogRef, NbDialogService } from '@nebular/theme';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { ReparationService, PanneRequest, LancementReparation, FinReparation, ReparationItem } from '../../../services/materiel/reparation.service';
import { AffectationMaterielService, AffectationMateriel } from '../../../services/materiel/affectation-materiel.service';
import { MaterielService, Materiel } from '../../../services/materiel/materiel.service';
import { MissionService } from '../../../services/mission/mission.service';
import Swal from 'sweetalert2';
import { MaterielDetailModalComponent } from '../../materiel/materiel-detail-modal/materiel-detail-modal.component';

export interface EquipmentTypeDetail {
  typeName: string;
  icon: string;
  color: string;
  materiels: Materiel[];
  affectations: AffectationMateriel[];
  repairs: Map<number, ReparationItem[]>;
}

@Component({
  selector: 'ngx-equipment-type-detail',
  templateUrl: './equipment-type-detail.component.html',
  styleUrls: ['./equipment-type-detail.component.scss']
})
export class EquipmentTypeDetailComponent implements OnInit, OnDestroy {
  @Input() typeDetail!: EquipmentTypeDetail;
  @Input() missionId!: number;
  
  isLoading = false;
  searchTerm = '';
  expandedMaterielId: number | null = null;
  
  // Breakdown modal
  showBreakdownModal = false;
  selectedMateriel: Materiel | null = null;
  breakdownForm: FormGroup;
  
  // Launch repair modal
  showLaunchRepairModal = false;
  selectedReparation: ReparationItem | null = null;
  launchRepairForm: FormGroup;
  
  // Complete repair modal
  showCompleteRepairModal = false;
  selectedOngoingRepair: ReparationItem | null = null;
  completeRepairForm: FormGroup;
  
  private destroy$ = new Subject<void>();
  
  constructor(
    private fb: FormBuilder,
    private dialogService: NbDialogService,
    private reparationService: ReparationService,
    private affectationService: AffectationMaterielService,
    private materielService: MaterielService,
    private missionService: MissionService
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
  }
  
  ngOnInit() {
    this.loadRepairsForAllMateriels();
  }
  
  get filteredMateriels(): Materiel[] {
    if (!this.searchTerm.trim()) {
      return this.typeDetail.materiels;
    }
    const term = this.searchTerm.toLowerCase();
    return this.typeDetail.materiels.filter(m => 
      m.codeMateriel?.toLowerCase().includes(term) ||
      m.marque?.toLowerCase().includes(term) ||
      m.modele?.toLowerCase().includes(term) ||
      m.designation?.toLowerCase().includes(term)
    );
  }
  
  loadRepairsForAllMateriels() {
    this.typeDetail.materiels.forEach(materiel => {
      if (materiel.idMateriel) {
        this.reparationService.getAllByMaterielAndMission(materiel.idMateriel, this.missionId)
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: (repairs) => {
              this.typeDetail.repairs.set(materiel.idMateriel, repairs);
            },
            error: (err) => console.error('Error loading repairs:', err)
          });
      }
    });
  }
  
  getMaterielRepairs(materielId: number): ReparationItem[] {
    return this.typeDetail.repairs.get(materielId) || [];
  }
  
  getPendingBreakdowns(materielId: number): ReparationItem[] {
    return this.getMaterielRepairs(materielId).filter(r => r.status === 'PENDING');
  }
  
  getOngoingRepairs(materielId: number): ReparationItem[] {
    return this.getMaterielRepairs(materielId).filter(r => r.status === 'IN_PROGRESS' || r.status === 'SENT');
  }
  
  getCompletedRepairs(materielId: number): ReparationItem[] {
    return this.getMaterielRepairs(materielId).filter(r => r.status === 'COMPLETED');
  }
  
  // ==================== AFFECTATIONS METHODS ====================
  
  getAllMaterielAffectations(materielId: number): AffectationMateriel[] {
    return this.typeDetail.affectations.filter(a => a.materielId === materielId);
  }
  
  getSortedAffectations(materielId: number): AffectationMateriel[] {
    const affectations = this.getAllMaterielAffectations(materielId);
    return [...affectations].sort((a, b) => 
      new Date(b.dateDebut).getTime() - new Date(a.dateDebut).getTime()
    );
  }
  
  getCurrentActiveAffectation(materielId: number): AffectationMateriel | undefined {
    const affectations = this.getAllMaterielAffectations(materielId);
    if (affectations.length === 0) return undefined;
    
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    const active = affectations.find(a => {
      const startDate = new Date(a.dateDebut);
      startDate.setHours(0, 0, 0, 0);
      
      if (startDate > today) return false;
      
      if (a.dateFin) {
        const endDate = new Date(a.dateFin);
        endDate.setHours(0, 0, 0, 0);
        return endDate >= today;
      }
      
      return true;
    });
    
    return active;
  }
  
  hasAnyExpiredAffectation(materielId: number): boolean {
    const affectations = this.getAllMaterielAffectations(materielId);
    if (affectations.length === 0) return false;
    
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    return affectations.some(a => {
      if (!a.dateFin) return false;
      const endDate = new Date(a.dateFin);
      endDate.setHours(0, 0, 0, 0);
      return endDate < today;
    });
  }
  
  getMostRecentExpiredDate(materielId: number): Date | null {
    const affectations = this.getAllMaterielAffectations(materielId);
    const expired = affectations.filter(a => {
      if (!a.dateFin) return false;
      const endDate = new Date(a.dateFin);
      endDate.setHours(0, 0, 0, 0);
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      return endDate < today;
    });
    
    if (expired.length === 0) return null;
    
    const sorted = [...expired].sort((a, b) => 
      new Date(b.dateFin!).getTime() - new Date(a.dateFin!).getTime()
    );
    
    return new Date(sorted[0].dateFin!);
  }
  
  isInActiveMission(materielId: number): boolean {
    return this.getCurrentActiveAffectation(materielId) !== undefined;
  }
  
  isMissionExpired(materielId: number): boolean {
    const hasActive = this.isInActiveMission(materielId);
    const hasExpired = this.hasAnyExpiredAffectation(materielId);
    
    if (!hasActive && hasExpired) return true;
    return false;
  }
  
  getDaysRemaining(materielId: number): number | null {
    const currentActive = this.getCurrentActiveAffectation(materielId);
    if (!currentActive || !currentActive.dateFin) {
      return null;
    }
    
    const today = new Date();
    const endDate = new Date(currentActive.dateFin);
    
    today.setHours(0, 0, 0, 0);
    endDate.setHours(0, 0, 0, 0);
    
    const diffTime = endDate.getTime() - today.getTime();
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    
    return diffDays;
  }
  
  getExpirationStatus(materielId: number): { text: string; color: string; icon: string } {
    const currentActive = this.getCurrentActiveAffectation(materielId);
    const allAffectations = this.getAllMaterielAffectations(materielId);
    
    if (allAffectations.length === 0) {
      return { text: 'No Assignments', color: '#6b7280', icon: 'fas fa-minus-circle' };
    }
    
    if (currentActive) {
      if (!currentActive.dateFin) {
        return { text: 'Ongoing (No End Date)', color: '#10b981', icon: 'fas fa-infinity' };
      }
      
      const daysLeft = this.getDaysRemaining(materielId);
      if (daysLeft !== null && daysLeft <= 3) {
        return { text: `Expires in ${daysLeft} day${daysLeft > 1 ? 's' : ''}`, color: '#f59e0b', icon: 'fas fa-hourglass-half' };
      }
      
      return { text: `Until ${new Date(currentActive.dateFin).toLocaleDateString()}`, color: '#3b82f6', icon: 'fas fa-calendar-check' };
    }
    
    const mostRecentExpired = this.getMostRecentExpiredDate(materielId);
    if (mostRecentExpired) {
      return { text: `Expired since ${mostRecentExpired.toLocaleDateString()}`, color: '#ef4444', icon: 'fas fa-calendar-times' };
    }
    
    return { text: 'Not Assigned', color: '#6b7280', icon: 'fas fa-calendar-alt' };
  }
  
  // ==================== STATUS METHODS ====================
  
  getMaterielStatus(materiel: Materiel): string {
    if (this.isMissionExpired(materiel.idMateriel)) {
      return 'expired';
    }
    
    const repairs = this.getMaterielRepairs(materiel.idMateriel);
    const hasPending = repairs.some(r => r.status === 'PENDING');
    const hasOngoing = repairs.some(r => r.status === 'IN_PROGRESS' || r.status === 'SENT');
    
    if (hasPending) return 'broken';
    if (hasOngoing) return 'repair';
    return 'good';
  }
  
  getStatusIcon(status: string): string {
    switch(status) {
      case 'good': return 'checkmark-circle-outline';
      case 'broken': return 'alert-circle-outline';
      case 'repair': return 'settings-outline';
      case 'expired': return 'calendar-outline';
      default: return 'hard-drive-outline';
    }
  }
  
  getStatusText(status: string): string {
    switch(status) {
      case 'good': return 'Good Condition';
      case 'broken': return 'Broken';
      case 'repair': return 'In Repair';
      case 'expired': return 'Expired';
      default: return 'Unknown';
    }
  }
  
  canDeclareBreakdown(materiel: Materiel): boolean {
    if (this.isMissionExpired(materiel.idMateriel)) {
      return false;
    }
    return this.getMaterielStatus(materiel) === 'good';
  }
  
  // ==================== SUMMARY COUNTS ====================
  
  getGoodCount(): number {
    return this.typeDetail.materiels.filter(m => this.getMaterielStatus(m) === 'good').length;
  }
  
  getBrokenCount(): number {
    return this.typeDetail.materiels.filter(m => this.getMaterielStatus(m) === 'broken').length;
  }
  
  getRepairCount(): number {
    return this.typeDetail.materiels.filter(m => this.getMaterielStatus(m) === 'repair').length;
  }
  
  getExpiredCount(): number {
    return this.typeDetail.materiels.filter(m => this.getMaterielStatus(m) === 'expired').length;
  }
  
  getGoodPercentage(): number {
    const total = this.typeDetail.materiels.length;
    if (total === 0) return 0;
    return (this.getGoodCount() / total) * 100;
  }
  
  getBrokenPercentage(): number {
    const total = this.typeDetail.materiels.length;
    if (total === 0) return 0;
    return (this.getBrokenCount() / total) * 100;
  }
  
  getRepairPercentage(): number {
    const total = this.typeDetail.materiels.length;
    if (total === 0) return 0;
    return (this.getRepairCount() / total) * 100;
  }
  
  getExpiredPercentage(): number {
    const total = this.typeDetail.materiels.length;
    if (total === 0) return 0;
    return (this.getExpiredCount() / total) * 100;
  }
  
  // ==================== UI METHODS ====================
  
  toggleExpand(materielId: number) {
    if (this.expandedMaterielId === materielId) {
      this.expandedMaterielId = null;
    } else {
      this.expandedMaterielId = materielId;
    }
  }
  
  openMaterielDetails(materiel: Materiel) {
    this.dialogService.open(MaterielDetailModalComponent, {
      context: { materielId: materiel.idMateriel },
      dialogClass: 'materiel-detail-dialog',
      hasBackdrop: true,
      closeOnBackdropClick: true
    });
  }
  
  // Helper for date comparison in template
  isDateExpired(dateStr: string): boolean {
    if (!dateStr) return false;
    const date = new Date(dateStr);
    date.setHours(0, 0, 0, 0);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return date < today;
  }
  
  isDateActive(startDate: string, endDate?: string): boolean {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const start = new Date(startDate);
    start.setHours(0, 0, 0, 0);
    
    if (start > today) return false;
    
    if (endDate) {
      const end = new Date(endDate);
      end.setHours(0, 0, 0, 0);
      return end >= today;
    }
    
    return true;
  }
  
  // ==================== BREAKDOWN METHODS ====================
  
  openBreakdownModal(materiel: Materiel, event: Event) {
    event.stopPropagation();
    this.selectedMateriel = materiel;
    this.breakdownForm.reset({
      datePanne: new Date().toISOString().split('T')[0],
      detailProbleme: ''
    });
    this.showBreakdownModal = true;
  }
  
  closeBreakdownModal() {
    this.showBreakdownModal = false;
    this.selectedMateriel = null;
  }
  
  declareBreakdown() {
    if (this.breakdownForm.invalid || !this.selectedMateriel) return;
    
    const currentActive = this.getCurrentActiveAffectation(this.selectedMateriel.idMateriel);
    
    const request: PanneRequest = {
      materielId: this.selectedMateriel.idMateriel,
      datePanne: this.breakdownForm.value.datePanne,
      detailProbleme: this.breakdownForm.value.detailProbleme,
      missionId: this.missionId,
      affectationId: currentActive?.idAffectation
    };
    
    this.isLoading = true;
    this.reparationService.declarePanne(request).subscribe({
      next: () => {
        Swal.fire({ icon: 'success', title: 'Success!', text: 'Breakdown declared' });
        this.closeBreakdownModal();
        this.loadRepairsForAllMateriels();
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message || 'Failed to declare breakdown' });
      }
    });
  }
  
  // ==================== LAUNCH REPAIR METHODS ====================
  
  openLaunchRepairModal(reparation: ReparationItem, event: Event) {
    event.stopPropagation();
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
    
    this.isLoading = true;
    this.reparationService.launchRepair(request).subscribe({
      next: () => {
        Swal.fire({ icon: 'success', title: 'Repair Launched!' });
        this.closeLaunchRepairModal();
        this.loadRepairsForAllMateriels();
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message });
      }
    });
  }
  
  // ==================== COMPLETE REPAIR METHODS ====================
  
  openCompleteRepairModal(reparation: ReparationItem, event: Event) {
    event.stopPropagation();
    this.selectedOngoingRepair = reparation;
    this.completeRepairForm.reset({
      dateReparation: new Date().toISOString().split('T')[0],
      cout: 0
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
    
    this.isLoading = true;
    this.reparationService.completeRepair(request).subscribe({
      next: () => {
        Swal.fire({ icon: 'success', title: 'Repair Completed!' });
        this.closeCompleteRepairModal();
        this.loadRepairsForAllMateriels();
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message });
      }
    });
  }
  // Helper for date comparison in template
isDateFuture(dateStr: string): boolean {
  if (!dateStr) return false;
  const date = new Date(dateStr);
  date.setHours(0, 0, 0, 0);
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  return date > today;
}
  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}