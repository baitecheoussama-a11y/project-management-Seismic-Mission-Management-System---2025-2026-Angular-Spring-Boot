import { Component, OnInit } from '@angular/core';
import { LocalDataSource } from 'ng2-smart-table';
import { AntecedentsMedicalService } from '../../../../services/dossier-medical/antecedents-medical.service';
import { EtatMedicalService, AntecedentsMedical } from '../../../../services/dossier-medical/etat-medical.service';
import { AuthService } from '../../../../services/auth.service';
import { Router, ActivatedRoute } from '@angular/router';
import { NbToastrService } from '@nebular/theme';

@Component({
  selector: 'ngx-antecedents-medical',
  templateUrl: './antecedents-medical.component.html',
  styleUrls: ['./antecedents-medical.component.scss'],
})
export class AntecedentsMedicalComponent implements OnInit {

  etatMedicalId: number | null = null;
  employeName: string = '';
  
  settings: any;
  source: LocalDataSource = new LocalDataSource();

  constructor(
    private antecedentsService: AntecedentsMedicalService,
    private etatMedicalService: EtatMedicalService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private toastrService: NbToastrService
  ) {}

  ngOnInit() {
    const user = this.authService.getCurrentUser();
    if (!user || !user.roles.includes('ADMIN')) {
      this.router.navigate(['/pages/dashboard']);
      this.toastrService.warning('Access denied. Admin privileges required.', 'Access Denied');
      return;
    }

    // Get etatMedicalId from route params
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.etatMedicalId = +params['id'];
      }
      this.loadData();
    });

    // Get employee name from query params
    this.route.queryParams.subscribe(params => {
      if (params['employeeName']) {
        this.employeName = params['employeeName'];
      }
    });
  }

  loadData() {
    if (this.etatMedicalId) {
      this.buildSettings();
      this.loadAntecedents();
      this.loadEtatMedicalInfo();
    }
  }

  loadEtatMedicalInfo() {
    if (this.etatMedicalId) {
      this.etatMedicalService.getEtatMedicalById(this.etatMedicalId).subscribe({
        next: (data) => {
          if (!this.employeName) {
            this.employeName = `${data.employePrenom} ${data.employeNom}`;
          }
        },
        error: (err) => {
          console.error('Error loading medical record info:', err);
        }
      });
    }
  }

  buildSettings() {
    this.settings = {
      actions: {
        add: true,
        edit: true,
        delete: true,
        position: 'right',
      },
      add: {
        addButtonContent: '<i class="nb-plus"></i>',
        createButtonContent: '<i class="nb-checkmark"></i>',
        cancelButtonContent: '<i class="nb-close"></i>',
        confirmCreate: true,
      },
      edit: {
        editButtonContent: '<i class="nb-edit"></i>',
        saveButtonContent: '<i class="nb-checkmark"></i>',
        cancelButtonContent: '<i class="nb-close"></i>',
        confirmSave: true,
      },
      delete: {
        deleteButtonContent: '<i class="nb-trash"></i>',
        confirmDelete: true,
      },
      columns: {
        nom: {
          title: 'Condition Name',
          type: 'string',
          editable: true,
        },
        description: {
          title: 'Description',
          type: 'string',
          editable: true,
        },
        dateDiagnostic: {
          title: 'Diagnosis Date',
          type: 'date',
          valuePrepareFunction: (date: string) => {
            return date ? new Date(date).toLocaleDateString() : '-';
          },
          editable: true,
        },
      },
    };
  }

  loadAntecedents() {
    if (this.etatMedicalId) {
      this.antecedentsService.getAntecedentsByEtatMedicalId(this.etatMedicalId).subscribe({
        next: (data) => {
          this.source.load(data);
        },
        error: (err) => {
          console.error('Error loading medical history:', err);
          this.toastrService.danger('Failed to load medical history', 'Error');
        },
      });
    }
  }

  onDeleteConfirm(event): void {
    if (window.confirm('Are you sure you want to delete this medical history record?')) {
      this.antecedentsService.deleteAntecedent(event.data.id).subscribe({
        next: () => {
          this.loadAntecedents();
          event.confirm.resolve();
          this.toastrService.success('Medical history record deleted successfully', 'Success');
        },
        error: (err) => {
          console.error(err);
          this.toastrService.danger('Error deleting medical history record', 'Error');
          event.confirm.reject();
        },
      });
    } else {
      event.confirm.reject();
    }
  }

  onCreate(event): void {
    if (this.etatMedicalId) {
      const newAntecedent = {
        ...event.newData,
        etatMedicalId: this.etatMedicalId
      };
      
      this.antecedentsService.createOrUpdateAntecedent(newAntecedent).subscribe({
        next: (created) => {
          this.loadAntecedents();
          event.confirm.resolve(created);
          this.toastrService.success('Medical history record added successfully', 'Success');
        },
        error: (err) => {
          console.error(err);
          this.toastrService.danger('Error saving medical history record', 'Error');
          event.confirm.reject();
        }
      });
    } else {
      event.confirm.reject();
      this.toastrService.danger('No medical record associated', 'Error');
    }
  }

  onEdit(event): void {
    const updatedAntecedent = {
      ...event.newData,
      etatMedicalId: this.etatMedicalId
    };

    this.antecedentsService.createOrUpdateAntecedent(updatedAntecedent).subscribe({
      next: (updated) => {
        this.loadAntecedents();
        event.confirm.resolve(updated);
        this.toastrService.success('Medical history record updated successfully', 'Success');
      },
      error: (err) => {
        console.error(err);
        this.toastrService.danger('Error updating medical history record', 'Error');
        event.confirm.reject();
      }
    });
  }

  goBack() {
    this.router.navigate(['/pages/tables/etat-medical']);
  }
}