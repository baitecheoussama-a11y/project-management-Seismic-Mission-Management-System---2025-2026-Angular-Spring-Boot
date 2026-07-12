import { Component, Input } from '@angular/core';
import { NbDialogRef } from '@nebular/theme';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AffectationMaterielToActiveService, AssignMaterielToActiveRequest } from '../../../services/materiel/affectation-materiel-to-active.service';
import { Materiel } from '../../../services/materiel/materiel.service';
import { ActiveDTO } from '../../../services/mission/mission.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'ngx-assign-activity-modal',
  templateUrl: './assign-activity-modal.component.html',
  styleUrls: ['./assign-activity-modal.component.scss']
})
export class AssignActivityModalComponent {
  @Input() selectedMateriels: Materiel[] = [];
  @Input() availableActivities: ActiveDTO[] = [];
  @Input() projectId: number | null = null;
  @Input() missionId: number | null = null;

  assignForm: FormGroup;
  isLoading = false;

  constructor(
    protected dialogRef: NbDialogRef<AssignActivityModalComponent>,
    private fb: FormBuilder,
    private affectationToActiveService: AffectationMaterielToActiveService
  ) {
    this.assignForm = this.fb.group({
      activeId: [null, Validators.required],
      dateDebut: [new Date().toISOString().split('T')[0], Validators.required],
      dateFin: ['']
    });
  }

  close() {
    this.dialogRef.close(false);
  }

submit() {
  console.log('[DEBUG] Form values:', this.assignForm.value);
  console.log('[DEBUG] Available activities:', this.availableActivities);
  
  if (this.assignForm.invalid) {
    Swal.fire({ icon: 'warning', title: 'Invalid', text: 'Please fill all required fields' });
    return;
  }

  if (!this.projectId || !this.missionId) {
    Swal.fire({ icon: 'warning', title: 'Error', text: 'No active project or mission found' });
    return;
  }

  // ✅ FIX: Convert activeId to number
  const selectedActivityId = Number(this.assignForm.value.activeId);
  
  // ✅ FIX: Compare with number
  const selectedActivity = this.availableActivities.find(a => a.id === selectedActivityId);
  
  console.log('[DEBUG] Selected activity ID (converted):', selectedActivityId);
  console.log('[DEBUG] Selected activity:', selectedActivity);
  
  if (!selectedActivity) {
    Swal.fire({ 
      icon: 'error', 
      title: 'Error', 
      text: `Selected activity not found. Available activities: ${this.availableActivities.map(a => a.id).join(', ')}` 
    });
    return;
  }

  this.isLoading = true;
  let completed = 0;
  let errors = 0;

  this.selectedMateriels.forEach(materiel => {
    const request: AssignMaterielToActiveRequest = {
      materielId: materiel.idMateriel,
      activeId: selectedActivityId, // ✅ Use the converted number
      projectId: this.projectId!,
      dateDebut: this.assignForm.value.dateDebut,
      dateFin: this.assignForm.value.dateFin || null
    };

    this.affectationToActiveService.assignMaterielToActive(request).subscribe({
      next: () => {
        completed++;
        if (completed + errors === this.selectedMateriels.length) {
          this.isLoading = false;
          if (errors === 0) {
            Swal.fire({
              icon: 'success',
              title: 'Success!',
              text: `Assigned ${completed} equipment(s) to activity "${selectedActivity.codeActive}"`,
              toast: true,
              position: 'top-end',
              showConfirmButton: false,
              timer: 3000
            });
            this.dialogRef.close(true);
          } else {
            Swal.fire({
              icon: 'warning',
              title: 'Partial Success',
              text: `Assigned ${completed} equipment(s), ${errors} failed`,
              confirmButtonText: 'OK'
            });
            this.dialogRef.close(true);
          }
        }
      },
      error: (err) => {
        errors++;
        console.error(`Error assigning materiel ${materiel.idMateriel}:`, err);
        if (completed + errors === this.selectedMateriels.length) {
          this.isLoading = false;
          Swal.fire({
            icon: 'warning',
            title: 'Partial Success',
            text: `Assigned ${completed} equipment(s), ${errors} failed`,
            confirmButtonText: 'OK'
          });
          this.dialogRef.close(true);
        }
      }
    });
  });
}
}