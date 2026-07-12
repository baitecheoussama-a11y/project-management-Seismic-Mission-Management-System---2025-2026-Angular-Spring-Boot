import { Component, OnInit } from '@angular/core';
import { LocalDataSource } from 'ng2-smart-table';
import { IncidentService, Incident, IncidentDTO } from '../../../services/dossier-medical/incident.service';
import { EmployeService, Employe } from '../../../services/employes/employe.service';
import { AuthService } from '../../../services/auth.service';
import { Router, ActivatedRoute } from '@angular/router';
import { NbToastrService } from '@nebular/theme';

@Component({
  selector: 'ngx-incident',
  templateUrl: './incident.component.html',
  styleUrls: ['./incident.component.scss'],
})
export class IncidentComponent implements OnInit {

  settings: any;
  source: LocalDataSource = new LocalDataSource();
  employees: Employe[] = [];
  filterEmployeId: number | null = null;
  filterEmployeName: string | null = null;
  
  typeOptions = [
    { value: 'ACCIDENT_TRAVAIL', title: '🔧 Work Accident' },
    { value: 'MALADIE_PROFESSIONNELLE', title: '🩺 Occupational Disease' },
    { value: 'INCIDENT_SECURITE', title: '⚠️ Security Incident' },
    { value: 'INCIDENT_ENVIRONNEMENTAL', title: '🌍 Environmental Incident' },
    { value: 'AUTRE', title: '📋 Other' }
  ];
  
  graviteOptions = [
    { value: 'FAIBLE', title: '🟢 Low' },
    { value: 'MOYEN', title: '🟡 Medium' },
    { value: 'ELEVE', title: '🟠 High' },
    { value: 'CRITIQUE', title: '🔴 Critical' }
  ];

  constructor(
    private incidentService: IncidentService,
    private employeService: EmployeService,
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
    
    // Check for employee filter from query params
    this.route.queryParams.subscribe(params => {
      if (params['employeId']) {
        this.filterEmployeId = +params['employeId'];
        this.filterEmployeName = params['employeName'] || 'Employee';
        this.toastrService.info(
          `Showing incidents for: ${this.filterEmployeName}`,
          'Filter Applied',
          { duration: 3000 }
        );
      }
    });
    
    this.loadEmployees();
    this.buildSettings();
    this.loadIncidents();
  }

  loadEmployees() {
    this.employeService.getAllEmployes().subscribe({
      next: (data) => {
        this.employees = data;
      },
      error: (err) => {
        console.error('Error loading employees:', err);
      }
    });
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
      columns: {
        type: {
          title: 'Type',
          type: 'html',
          editor: {
            type: 'list',
            config: {
              selectText: 'Select Type',
              list: this.typeOptions,
            },
          },
          valuePrepareFunction: (value: string) => {
            const option = this.typeOptions.find(o => o.value === value);
            return option ? option.title : value;
          },
        },
        description: {
          title: 'Description',
          type: 'string',
          editable: true,
        },
        dateIncident: {
          title: 'Incident Date',
          type: 'date',
          valuePrepareFunction: (date: string) => {
            return date ? new Date(date).toLocaleDateString() : '-';
          },
          filter: {
            type: 'date',
          },
          editor: {
            type: 'date',
          },
        },
        niveauGravite: {
          title: 'Severity',
          type: 'html',
          editor: {
            type: 'list',
            config: {
              selectText: 'Select Severity',
              list: this.graviteOptions,
            },
          },
          valuePrepareFunction: (value: string) => {
            const option = this.graviteOptions.find(o => o.value === value);
            if (!option) return value;
            
            let badgeClass = '';
            switch(value) {
              case 'FAIBLE': badgeClass = 'severity-low'; break;
              case 'MOYEN': badgeClass = 'severity-medium'; break;
              case 'ELEVE': badgeClass = 'severity-high'; break;
              case 'CRITIQUE': badgeClass = 'severity-critical'; break;
            }
            return `<span class="severity-badge ${badgeClass}">${option.title}</span>`;
          },
        },
        employeNomComplet: {
          title: 'Employee',
          type: 'string',
          editable: false,
          addable: false,
        },
      },
    };
  }

  loadIncidents() {
    if (this.filterEmployeId) {
      // Load only incidents for specific employee
      this.incidentService.getIncidentsByEmployeId(this.filterEmployeId).subscribe({
        next: (data) => {
          const incidentsWithName = data.map(incident => ({
            ...incident,
            employeNomComplet: incident.employeNomComplet || this.filterEmployeName || `Employee #${incident.employeId}`
          }));
          this.source.load(incidentsWithName);
        },
        error: (err) => {
          console.error('Error loading incidents for employee:', err);
          this.toastrService.danger('Failed to load incidents', 'Error');
        },
      });
    } else {
      // Load all incidents
      this.incidentService.getAllIncidents().subscribe({
        next: (data) => {
          const incidentsWithName = data.map(incident => ({
            ...incident,
            employeNomComplet: incident.employeNomComplet || `Employee #${incident.employeId}`
          }));
          this.source.load(incidentsWithName);
        },
        error: (err) => {
          console.error('Error loading incidents:', err);
          this.toastrService.danger('Failed to load incidents', 'Error');
        },
      });
    }
  }

  onCreate(event): void {
    const newIncident: IncidentDTO = {
      type: event.newData.type,
      description: event.newData.description,
      dateIncident: event.newData.dateIncident,
      niveauGravite: event.newData.niveauGravite,
      employeId: this.filterEmployeId || event.newData.employeId,
    };
    
    this.incidentService.createIncident(newIncident).subscribe({
      next: (created) => {
        this.loadIncidents();
        event.confirm.resolve(created);
        this.toastrService.success('Incident added successfully', 'Success');
      },
      error: (err) => {
        console.error(err);
        this.toastrService.danger('Error adding incident', 'Error');
        event.confirm.reject();
      }
    });
  }

  onEdit(event): void {
    const updatedIncident: IncidentDTO = {
      type: event.newData.type,
      description: event.newData.description,
      dateIncident: event.newData.dateIncident,
      niveauGravite: event.newData.niveauGravite,
      employeId: event.data.employeId,
    };
    
    this.incidentService.updateIncident(event.data.id, updatedIncident).subscribe({
      next: (updated) => {
        this.loadIncidents();
        event.confirm.resolve(updated);
        this.toastrService.success('Incident updated successfully', 'Success');
      },
      error: (err) => {
        console.error(err);
        this.toastrService.danger('Error updating incident', 'Error');
        event.confirm.reject();
      }
    });
  }

  onDeleteConfirm(event): void {
    if (window.confirm(`Are you sure you want to delete this incident?`)) {
      this.incidentService.deleteIncident(event.data.id).subscribe({
        next: () => {
          this.loadIncidents();
          event.confirm.resolve();
          this.toastrService.success('Incident deleted successfully', 'Success');
        },
        error: (err) => {
          console.error(err);
          this.toastrService.danger('Error deleting incident', 'Error');
          event.confirm.reject();
        },
      });
    } else {
      event.confirm.reject();
    }
  }

  clearFilter() {
    this.filterEmployeId = null;
    this.filterEmployeName = null;
    this.loadIncidents();
    this.toastrService.info('Showing all incidents', 'Filter Cleared', { duration: 2000 });
  }

  getEmployeeName(employeeId: number): string {
    const employee = this.employees.find(e => e.id === employeeId);
    return employee ? `${employee.prenom} ${employee.nom}` : `Employee #${employeeId}`;
  }
}