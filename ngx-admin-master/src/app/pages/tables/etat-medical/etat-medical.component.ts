import { Component, OnInit } from '@angular/core';
import { LocalDataSource } from 'ng2-smart-table';
import { EtatMedicalService, EtatMedical } from '../../../services/dossier-medical/etat-medical.service';
import { AuthService } from '../../../services/auth.service';
import { Router, ActivatedRoute } from '@angular/router';
import { NbToastrService } from '@nebular/theme';
import { MedicalHistoryButtonComponent } from './medical-history-button/medical-history-button.component';

@Component({
  selector: 'ngx-etat-medical',
  templateUrl: './etat-medical.component.html',
  styleUrls: ['./etat-medical.component.scss'],
})
export class EtatMedicalComponent implements OnInit {

  employeId: number | null = null;
  employeName: string = '';
  isSpecificEmployee: boolean = false;
  
  settings: any;
  source: LocalDataSource = new LocalDataSource();

  constructor(
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

    this.route.params.subscribe(params => {
      if (params['id']) {
        this.employeId = +params['id'];
        this.isSpecificEmployee = true;
      }
      this.buildSettings();
      this.loadEtatMedicals();
    });

    this.route.queryParams.subscribe(params => {
      if (params['employeName']) {
        this.employeName = params['employeName'];
      }
      if (params['employeId']) {
        this.employeId = +params['employeId'];
        this.isSpecificEmployee = true;
      }
    });
  }

  buildSettings() {
    const baseColumns: any = {
      medicalHistory: {
        title: '',
        type: 'custom',
        renderComponent: MedicalHistoryButtonComponent,
        filter: false,
        sort: false,
        onComponentInitFunction: (instance: any, data: any) => {
          instance.rowData = data;
          instance.value = data;
        }
      },
      
      groupeSanguin: {
        title: 'Blood Type',
        type: 'html',
        valuePrepareFunction: (value: string) => {
          if (!value) return '-';
          return `<span class="medical-badge blood">${value}</span>`;
        },
      },
      
      allergies: {
        title: 'Allergies',
        type: 'string',
        valuePrepareFunction: (value: string) => {
          if (!value || value === 'None') return '—';
          return value;
        },
      },
      vaccinations: {
        title: 'Vaccinations',
        type: 'string',
        valuePrepareFunction: (value: string) => {
          if (!value) return '—';
          return value;
        },
      },
      medicationsActuelles: {
        title: 'Current Medications',
        type: 'string',
        valuePrepareFunction: (value: string) => {
          if (!value || value === 'None') return '—';
          return value;
        },
      },
      medecinTraitant: {
        title: 'Primary Doctor',
        type: 'string',
        valuePrepareFunction: (value: string) => {
          if (!value) return '—';
          return value;
        },
      },
      derniereVisiteMedicale: {
        title: 'Last Visit',
        type: 'date',
        valuePrepareFunction: (date: string) => {
          return date ? new Date(date).toLocaleDateString() : '—';
        },
        filter: {
          type: 'date',
        },
        editor: {
          type: 'date',
        },
      },
      
    };

    if (!this.isSpecificEmployee) {
      baseColumns.employePrenom = {
        title: 'First Name',
        type: 'string',
      };
      baseColumns.employeNom = {
        title: 'Last Name',
        type: 'string',
      };
    }

    this.settings = {
      actions: {
        add: true,
        edit: true,
        delete: true,
        position: 'right',
      },
      add: {
        addButtonContent: '<i class="nb-plus"></i> ',
        createButtonContent: '<i class="nb-checkmark"></i> ',
        cancelButtonContent: '<i class="nb-close"></i> ',
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
      columns: baseColumns,
    };
  }

  loadEtatMedicals() {
    if (this.isSpecificEmployee && this.employeId) {
      this.etatMedicalService.getEtatMedicalByEmployeId(this.employeId).subscribe({
        next: (data) => {
          this.source.load([data]);
        },
        error: (err) => {
          if (err.status === 404) {
            this.source.load([]);
            this.toastrService.info('No medical record found for this employee', 'Info');
          } else {
            console.error('Error loading medical record:', err);
            this.toastrService.danger('Failed to load medical record', 'Error');
          }
        },
      });
    } else {
      this.etatMedicalService.getAllEtatMedicals().subscribe({
        next: (data) => this.source.load(data),
        error: (err) => console.error('Error loading medical records:', err),
      });
    }
  }

  onDeleteConfirm(event): void {
    if (window.confirm('Are you sure you want to delete this medical record?')) {
      this.etatMedicalService.deleteEtatMedical(event.data.id).subscribe({
        next: () => {
          this.loadEtatMedicals();
          event.confirm.resolve();
          this.toastrService.success('Medical record deleted successfully', 'Success');
        },
        error: (err) => {
          console.error(err);
          this.toastrService.danger('Error deleting medical record', 'Error');
          event.confirm.reject();
        },
      });
    } else {
      event.confirm.reject();
    }
  }

  onCreate(event): void {
    if (this.isSpecificEmployee && this.employeId) {
      const newEtatMedical = {
        ...event.newData,
        employeId: this.employeId
      };
      
      this.etatMedicalService.createOrUpdateEtatMedical(newEtatMedical).subscribe({
        next: (created) => {
          this.loadEtatMedicals();
          event.confirm.resolve(created);
          this.toastrService.success('Medical record saved successfully', 'Success');
        },
        error: (err) => {
          console.error(err);
          this.toastrService.danger('Error saving medical record', 'Error');
          event.confirm.reject();
        }
      });
    } else {
      event.confirm.reject();
      this.toastrService.info(
        'Please select an employee first from the Employees Table',
        'How to Add Medical Record',
        { duration: 5000 }
      );
      this.router.navigate(['/pages/tables/smart-table']);
    }
  }

  onEdit(event): void {
    const updatedEtatMedical = {
      ...event.newData,
      employeId: event.data.employeId
    };

    this.etatMedicalService.createOrUpdateEtatMedical(updatedEtatMedical).subscribe({
      next: (updated) => {
        this.loadEtatMedicals();
        event.confirm.resolve(updated);
        this.toastrService.success('Medical record updated successfully', 'Success');
      },
      error: (err) => {
        console.error(err);
        this.toastrService.danger('Error updating medical record', 'Error');
        event.confirm.reject();
      }
    });
  }

  goBack() {
    this.router.navigate(['/pages/tables/smart-table']);
  }
}