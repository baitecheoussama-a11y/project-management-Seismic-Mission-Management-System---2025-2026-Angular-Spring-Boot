import { Component, OnInit, Input } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NbDialogRef } from '@nebular/theme';
import { AffectationMaterielService, BatchAffectationRequest } from '../../../services/materiel/affectation-materiel.service';
import { MissionService, Mission } from '../../../services/mission/mission.service';
import { Materiel } from '../../../services/materiel/materiel.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'ngx-assign-mission-modal',
  templateUrl: './assign-mission-modal.component.html',
  styleUrls: ['./assign-mission-modal.component.scss']
})
export class AssignMissionModalComponent implements OnInit {
  @Input() selectedMateriels: Materiel[] = [];
  
  missions: Mission[] = [];
  assignForm: FormGroup;
  isLoading = false;
  isLoadingMissions = false;
  availabilityInfo: Map<number, { available: boolean; info: string }> = new Map();

  constructor(
    protected dialogRef: NbDialogRef<AssignMissionModalComponent>,
    private fb: FormBuilder,
    private affectationService: AffectationMaterielService,
    private missionService: MissionService
  ) {
    // Assign form - NO quantityAssigned
    this.assignForm = this.fb.group({
      missionId: ['', Validators.required],
      dateDebut: ['', Validators.required],
      dateFin: ['']
    });
  }

  ngOnInit() {
    this.loadMissions();
    this.checkAvailabilityForAll();
    
    // Watch for date changes
    this.assignForm.get('dateDebut')?.valueChanges.subscribe(() => {
      this.checkAvailabilityForAll();
    });
    
    this.assignForm.get('dateFin')?.valueChanges.subscribe(() => {
      this.checkAvailabilityForAll();
    });
  }

  async checkAvailabilityForAll() {
    const startDate = this.assignForm.get('dateDebut')?.value;
    if (!startDate) return;

    const endDate = this.assignForm.get('dateFin')?.value;
    
    this.availabilityInfo.clear();
    let allAvailable = true;

    for (const materiel of this.selectedMateriels) {
      try {
        const result = await this.affectationService.checkAvailability(
          materiel.idMateriel, 
          startDate, 
          endDate
        ).toPromise();
        
        if (result) {
          const isAvailable = result.isAvailable;
          this.availabilityInfo.set(materiel.idMateriel, {
            available: isAvailable,
            info: isAvailable ? 'Available for this period' : 'Not available for this period'
          });
          
          if (!isAvailable) {
            allAvailable = false;
          }
        }
      } catch (error) {
        console.error('Error checking availability:', error);
        this.availabilityInfo.set(materiel.idMateriel, {
          available: false,
          info: 'Error checking availability'
        });
        allAvailable = false;
      }
    }
  }

  isMaterielAvailable(materielId: number): boolean {
    return this.availabilityInfo.get(materielId)?.available || false;
  }

  getAvailabilityInfoForMateriel(materielId: number): string {
    return this.availabilityInfo.get(materielId)?.info || 'Checking availability...';
  }

  isAnyMaterielAvailable(): boolean {
    for (const info of this.availabilityInfo.values()) {
      if (info.available) return true;
    }
    return false;
  }

  areAllMaterielsAvailable(): boolean {
    for (const materiel of this.selectedMateriels) {
      if (!this.isMaterielAvailable(materiel.idMateriel)) {
        return false;
      }
    }
    return true;
  }

  loadMissions() {
    this.isLoadingMissions = true;
    this.missionService.getAllMissions().subscribe({
      next: (data) => {
        this.missions = data;
        this.isLoadingMissions = false;
      },
      error: (err) => {
        console.error('Error loading missions:', err);
        this.isLoadingMissions = false;
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'Failed to load missions',
          toast: true,
          position: 'top-end'
        });
      }
    });
  }

  submit() {
    if (this.assignForm.invalid) {
      if (!this.assignForm.get('missionId')?.value) {
        Swal.fire({ icon: 'warning', title: 'Missing Mission', text: 'Please select a mission', toast: true });
        return;
      }
      if (!this.assignForm.get('dateDebut')?.value) {
        Swal.fire({ icon: 'warning', title: 'Missing Start Date', text: 'Please select a start date', toast: true });
        return;
      }
      return;
    }

    // Check if all selected equipment are available
    for (const materiel of this.selectedMateriels) {
      if (!this.isMaterielAvailable(materiel.idMateriel)) {
        Swal.fire({
          icon: 'error',
          title: 'Equipment Not Available',
          text: `${materiel.codeMateriel} - ${materiel.marque} ${materiel.modele} is not available for the selected period`,
          toast: true,
          position: 'top-end'
        });
        return;
      }
    }

    const batchRequest: BatchAffectationRequest = {
      materielIds: this.selectedMateriels.map(m => m.idMateriel),
      missionId: Number(this.assignForm.value.missionId),
      dateDebut: this.assignForm.value.dateDebut,
      dateFin: this.assignForm.value.dateFin || null
    };

    console.log('Sending batch request:', batchRequest);

    this.isLoading = true;
    this.affectationService.createBatch(batchRequest).subscribe({
      next: (response) => {
        this.isLoading = false;
        Swal.fire({
          icon: 'success',
          title: 'Success!',
          text: `${this.selectedMateriels.length} equipment(s) assigned to mission`,
          toast: true,
          position: 'top-end',
          timer: 3000
        });
        this.dialogRef.close(true);
      },
      error: (err) => {
        console.error('Error assigning missions:', err);
        this.isLoading = false;
        let errorMessage = 'Failed to assign equipment to mission';
        if (err.error?.message) {
          errorMessage = err.error.message;
        }
        Swal.fire({
          icon: 'error',
          title: 'Assignment Failed',
          text: errorMessage,
          confirmButtonText: 'OK'
        });
      }
    });
  }

  close() {
    this.dialogRef.close(false);
  }

  getSelectedMissionName(): string {
    const missionId = this.assignForm.get('missionId')?.value;
    if (!missionId) return '';
    const mission = this.missions.find(m => m.id === Number(missionId));
    return mission ? `${mission.codeMission} - ${mission.description || mission.methodologie}` : '';
  }
}