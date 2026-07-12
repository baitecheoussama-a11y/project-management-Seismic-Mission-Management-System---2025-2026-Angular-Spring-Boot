// pages/fonctions/fonction-table/fonction-table.component.ts
import { Component, OnInit } from '@angular/core';
import { LocalDataSource } from 'ng2-smart-table';
import { NbToastrService } from '@nebular/theme';
import { FonctionService, Fonction } from '../../../services/fonctions/fonction.service';
import { Router } from '@angular/router';

@Component({
  selector: 'ngx-fonction-table',
  templateUrl: './fonction-table.component.html',
  styleUrls: ['./fonction-table.component.scss']
})
export class FonctionTableComponent implements OnInit {
  settings = {
    actions: {
      columnTitle: 'Actions',
      add: true,
      edit: true,
      delete: true,
      position: 'right'
    },
    add: {
      addButtonContent: '<i class="nb-plus" title="Add new function"></i>',
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
      nom: {
        title: 'Function Name',
        type: 'string',
        width: '30%',
        filter: true,
      },
      description: {
        title: 'Description',
        type: 'string',
        width: '50%',
        filter: true,
      },
      nombreEmployes: {
        title: 'Employees',
        type: 'number',
        width: '20%',
        filter: false,
        valuePrepareFunction: (value: number) => {
          if (value === 0) return '📭 No employees';
          if (value === 1) return '👤 1 employee';
          return `👥 ${value} employees`;
        }
      },
    },
  };

  source: LocalDataSource = new LocalDataSource();
  fonctions: Fonction[] = [];

  constructor(
    private fonctionService: FonctionService,
    private toastrService: NbToastrService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadFonctions();
  }

  loadFonctions() {
    this.fonctionService.getAllFonctions().subscribe({
      next: (data) => {
        this.fonctions = data;
        this.source.load(data);
        this.toastrService.success('Functions loaded successfully', 'Success');
      },
      error: (err) => {
        console.error('Error loading functions:', err);
        this.toastrService.danger('Failed to load functions', 'Error');
      }
    });
  }

  onCreate(event: any): void {
    const data = event.newData;
    
    if (!data.nom || data.nom.trim() === '') {
      this.toastrService.danger('Function name is required', 'Validation Error');
      event.confirm.reject();
      return;
    }

    const newFonction = {
      nom: data.nom.trim(),
      description: data.description?.trim() || ''
    };

    this.fonctionService.createFonction(newFonction).subscribe({
      next: (created) => {
        this.loadFonctions();
        event.confirm.resolve(created);
        this.toastrService.success(`Function "${created.nom}" created successfully`, 'Success');
      },
      error: (err) => {
        console.error(err);
        let errorMessage = 'Failed to create function';
        if (err.error?.message) {
          errorMessage = err.error.message;
        }
        this.toastrService.danger(errorMessage, 'Error');
        event.confirm.reject();
      }
    });
  }

  onEdit(event: any): void {
    const originalData = event.data;
    const updatedData = event.newData;
    
    if (!updatedData.nom || updatedData.nom.trim() === '') {
      this.toastrService.danger('Function name cannot be empty', 'Validation Error');
      event.confirm.reject();
      return;
    }

    const updateDTO: any = {};
    if (updatedData.nom !== originalData.nom) {
      updateDTO.nom = updatedData.nom.trim();
    }
    if (updatedData.description !== originalData.description) {
      updateDTO.description = updatedData.description?.trim() || '';
    }

    if (Object.keys(updateDTO).length === 0) {
      event.confirm.resolve();
      return;
    }

    this.fonctionService.updateFonction(originalData.id, updateDTO).subscribe({
      next: (updated) => {
        this.loadFonctions();
        event.confirm.resolve(updated);
        this.toastrService.success(`Function "${updated.nom}" updated successfully`, 'Success');
      },
      error: (err) => {
        console.error(err);
        let errorMessage = 'Failed to update function';
        if (err.error?.message) {
          errorMessage = err.error.message;
        }
        this.toastrService.danger(errorMessage, 'Error');
        event.confirm.reject();
      }
    });
  }

  onDeleteConfirm(event: any): void {
    const fonction = event.data;
    
    if (fonction.nombreEmployes > 0) {
      this.toastrService.warning(
        `Cannot delete "${fonction.nom}" because it has ${fonction.nombreEmployes} employee(s) assigned.`,
        'Cannot Delete'
      );
      event.confirm.reject();
      return;
    }

    if (window.confirm(`Are you sure you want to delete the function "${fonction.nom}"?`)) {
      this.fonctionService.deleteFonction(fonction.id).subscribe({
        next: () => {
          this.loadFonctions();
          event.confirm.resolve();
          this.toastrService.success(`Function "${fonction.nom}" deleted successfully`, 'Deleted');
        },
        error: (err) => {
          console.error(err);
          let errorMessage = 'Failed to delete function';
          if (err.error?.message) {
            errorMessage = err.error.message;
          }
          this.toastrService.danger(errorMessage, 'Error');
          event.confirm.reject();
        }
      });
    } else {
      event.confirm.reject();
    }
  }

 // fonction-table.component.ts - Update the viewEmployes method
viewEmployes(fonction: Fonction) {
  // Navigate to smart table with query params
  this.router.navigate(['/pages/tables/smart-table'], {
    queryParams: {
      fonctionId: fonction.id,
      fonctionName: fonction.nom
    }
  });
}
}