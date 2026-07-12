// smart-table.component.ts
import { Component, OnInit } from '@angular/core';
import { LocalDataSource } from 'ng2-smart-table';
import { EmployeService, Employe } from '../../../services/employes/employe.service';
import { AuthService } from '../../../services/auth.service';
import { Router, ActivatedRoute } from '@angular/router';
import { NbToastrService } from '@nebular/theme';
import { ActionsDropdownComponent } from './actions-dropdown/actions-dropdown.component';

@Component({
  selector: 'ngx-smart-table',
  templateUrl: './smart-table.component.html',
  styleUrls: ['./smart-table.component.scss'],
})
export class SmartTableComponent implements OnInit {

  settings = {
    actions: {
      columnTitle: 'Actions',
      add: true,
      edit: true,
      delete: true,
    },
    add: {
      addButtonContent: '<i class="nb-plus" title="Add new employee"></i>',
      createButtonContent: '<i class="nb-checkmark"></i>',
      cancelButtonContent: '<i class="nb-close"></i>',
      confirmCreate: true,
    },
    edit: {
      editButtonContent: '<i class="nb-edit" title="Edit"></i>',
      saveButtonContent: '<i class="nb-checkmark"></i>',
      cancelButtonContent: '<i class="nb-close"></i>',
      confirmSave: true,
    },
    delete: {
      deleteButtonContent: '<i class="nb-trash" title="Delete"></i>',
      confirmDelete: true,
    },
    columns: {
      actionsDropdown: {
        title: '',
        type: 'custom',
        filter: false,
        sort: false,
        renderComponent: ActionsDropdownComponent,
        editable: false,   
        addable: false,  
        onComponentInitFunction: (instance: any, data: any) => {
          instance.rowData = data;
          instance.value = data;
        }
      },
      numIdentite: {
        title: 'National ID',
        type: 'string',
      },
      prenom: {
        title: 'First Name',
        type: 'string',
      },
      nom: {
        title: 'Last Name',
        type: 'string',
      },
      dateNaissance: {
        title: 'Birth Date',
        type: 'date',
        valuePrepareFunction: (date) => {
          if (!date) return '-';
          const d = new Date(date);
          return d.toLocaleDateString('en-GB', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
          });
        }
      },
      lieuNaissance: {
        title: 'Birth Place',
        type: 'string',
      },
      email: {
        title: 'Email Address',
        type: 'string',
      },
      adresse: {
        title: 'Address',
        type: 'string',
      },
      numTel: {
        title: 'Phone Number',
        type: 'string',
      },
      sexe: {
        title: 'Gender',
        type: 'string',
        editor: {
          type: 'list',
          config: {
            selectText: 'Select Gender',
            list: [
              { value: 'HOMME', title: '👨 Male' },
              { value: 'FEMME', title: '👩 Female' }
            ],
          },
        },
        valuePrepareFunction: (value) => {
          if (value === 'HOMME') return '👨 Male';
          if (value === 'FEMME') return '👩 Female';
          return value || '-';
        }
      },
      // Add fonction column to display in table
   fonctionNom: {
    title: 'Function',
    type: 'string',
    filter: true,
    valuePrepareFunction: (value) => {
      return value || 'Not assigned';
    }
  },
    },
  };

  source: LocalDataSource = new LocalDataSource();
  fonctionId: number | null = null;
  fonctionName: string | null = null;
  isFilteredByFonction: boolean = false;

  constructor(
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

    // Check if we have a fonctionId in query params or state
    this.route.queryParams.subscribe(params => {
      if (params['fonctionId']) {
        this.fonctionId = +params['fonctionId'];
        this.fonctionName = params['fonctionName'];
        this.isFilteredByFonction = true;
        this.loadEmployesByFonction();
      } else {
        // Check navigation state
        const navigation = this.router.getCurrentNavigation();
        const state = navigation?.extras.state as { fonctionId?: number; fonctionName?: string };
        if (state?.fonctionId) {
          this.fonctionId = state.fonctionId;
          this.fonctionName = state.fonctionName;
          this.isFilteredByFonction = true;
          this.loadEmployesByFonction();
        } else {
          this.loadAllEmployes();
        }
      }
    });
  }

  loadAllEmployes() {
    this.employeService.getAllEmployes().subscribe({
      next: (data) => {
        this.source.load(data);
        this.toastrService.success('Employees loaded successfully', 'Welcome');
      },
      error: (err) => {
        console.error('Error loading employes:', err);
        this.toastrService.danger('Failed to load employees', 'Error');
      }
    });
  }

  loadEmployesByFonction() {
    if (this.fonctionId) {
      this.employeService.getEmployesByFonction(this.fonctionId).subscribe({
        next: (data) => {
          this.source.load(data);
          const message = this.fonctionName 
            ? `Showing employees for: ${this.fonctionName} (${data.length} employees)`
            : `Showing ${data.length} employees for this function`;
          this.toastrService.success(message, 'Filtered View');
        },
        error: (err) => {
          console.error('Error loading employes by fonction:', err);
          this.toastrService.danger('Failed to load employees for this function', 'Error');
        }
      });
    }
  }

  clearFonctionFilter() {
    this.fonctionId = null;
    this.fonctionName = null;
    this.isFilteredByFonction = false;
    this.loadAllEmployes();
    // Remove query params from URL
    this.router.navigate(['/pages/tables/smart-table'], { 
      queryParams: {},
      replaceUrl: true 
    });
    this.toastrService.info('Showing all employees', 'Filter Cleared');
  }

  onDeleteConfirm(event): void {
    const employeeName = `${event.data.prenom} ${event.data.nom}`;
    
    if (window.confirm(`Are you sure you want to delete ${employeeName}?`)) {
      this.employeService.deleteEmploye(event.data.id).subscribe({
        next: () => {
          if (this.isFilteredByFonction) {
            this.loadEmployesByFonction();
          } else {
            this.loadAllEmployes();
          }
          event.confirm.resolve();
          this.toastrService.success(`${employeeName} deleted successfully`, 'Employee Deleted');
        },
        error: (err) => {
          console.error(err);
          this.toastrService.danger(`Failed to delete ${employeeName}`, 'Error');
          event.confirm.reject();
        },
      });
    } else {
      event.confirm.reject();
    }
  }

  onCreate(event): void {
    const data = event.newData;
    const employeeName = `${data.prenom} ${data.nom}`;
    
    let sexeValue = data.sexe;
    if (sexeValue === '👨 Male') sexeValue = 'HOMME';
    if (sexeValue === '👩 Female') sexeValue = 'FEMME';
    
    const newEmploye = {
      ...data,
      sexe: sexeValue || 'HOMME',
      typeContrat: 'CDI',
      contratDateDebut: new Date().toISOString().split('T')[0],
    };
    
    this.employeService.createEmploye(newEmploye).subscribe({
      next: (created) => {
        if (this.isFilteredByFonction) {
          this.loadEmployesByFonction();
        } else {
          this.loadAllEmployes();
        }
        event.confirm.resolve(created);
        this.toastrService.success(`${employeeName} added successfully`, 'Employee Created');
      },
      error: (err) => {
        console.error(err);
        this.toastrService.danger(`Failed to add ${employeeName}`, 'Error');
        event.confirm.reject();
      }
    });
  }

  onEdit(event): void {
    const updatedEmploye = { ...event.newData };
    const employeeName = `${updatedEmploye.prenom} ${updatedEmploye.nom}`;
    
    if (updatedEmploye.sexe === '👨 Male') {
      updatedEmploye.sexe = 'HOMME';
    } else if (updatedEmploye.sexe === '👩 Female') {
      updatedEmploye.sexe = 'FEMME';
    }
    
    this.employeService.updateEmploye(event.data.id, updatedEmploye).subscribe({
      next: (updated) => {
        if (this.isFilteredByFonction) {
          this.loadEmployesByFonction();
        } else {
          this.loadAllEmployes();
        }
        event.confirm.resolve(updated);
        this.toastrService.success(`${employeeName} updated successfully`, 'Employee Updated');
      },
      error: (err) => {
        console.error(err);
        this.toastrService.danger(`Failed to update ${employeeName}`, 'Error');
        event.confirm.reject();
      }
    });
  }
}