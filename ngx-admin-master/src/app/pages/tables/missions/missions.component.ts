import { Component, OnInit } from '@angular/core';
import { LocalDataSource } from 'ng2-smart-table';
import { MissionService, Mission } from '../../../services/mission/mission.service';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';
import { NbToastrService } from '@nebular/theme';
import { ViewButtonComponent } from './view-button/view-button.component'; // ✅ Import

@Component({
  selector: 'ngx-missions',
  templateUrl: './missions.component.html',
  styleUrls: ['./missions.component.scss'],
})
export class MissionsComponent implements OnInit {

  isAdmin: boolean = false;
 
  settings: any;
  source: LocalDataSource = new LocalDataSource();

  private methodologyOptions = [
    { value: 'D2', title: '2D' },
    { value: 'D3', title: '3D' }
  ];

  constructor(
    private missionService: MissionService,
    private authService: AuthService,
    private router: Router,
    private toastrService: NbToastrService
  ) {}

  ngOnInit() {
    const user = this.authService.getCurrentUser();
    if (!user) {
      this.router.navigate(['/auth/login']);
      return;
    }
this.isAdmin = user.roles?.includes('ADMIN') || user.roles?.includes('DIRECTEUR') || false;
    if (!this.isAdmin) {
      this.toastrService.warning('Access denied. Admin privileges required.', 'Access Denied');
    }

    this.buildSettings();
    this.loadMissions();
  }

  buildSettings() {
    this.settings = {
      add: {
        addButtonContent: '<i class="nb-plus"></i>',
        createButtonContent: '<i class="nb-checkmark"></i>',
        cancelButtonContent: '<i class="nb-close"></i>',
        confirmCreate: true,
        ...((!this.isAdmin) ? { addButtonContent: '' } : {})
      },
      edit: {
        editButtonContent: '<i class="nb-edit"></i>',
        saveButtonContent: '<i class="nb-checkmark"></i>',
        cancelButtonContent: '<i class="nb-close"></i>',
        confirmSave: true,
        ...((!this.isAdmin) ? { editButtonContent: '' } : {})
      },
      delete: {
        deleteButtonContent: '<i class="nb-trash"></i>',
        confirmDelete: true,
        ...((!this.isAdmin) ? { deleteButtonContent: '' } : {})
      },
      actions: {
        columnTitle: 'Actions',
        add: this.isAdmin,
        edit: this.isAdmin,
        delete: this.isAdmin,
        position: 'right',
      },
      columns: {
        // ✅ Using renderComponent - the correct way
        view: {
          title: 'Overview',
          type: 'custom',
          filter: false,
          sort: false,
          width: '100px',
          renderComponent: ViewButtonComponent, // ✅ ربط المكون
        },
        codeMission: {
          title: 'Mission Code',
          type: 'string',
          editable: true,
          addable: true,
        },
        methodologie: {
          title: 'Methodology',
          type: 'string',
          editable: true,
          addable: true,
          editor: {
            type: 'list',
            config: {
              list: this.methodologyOptions,
            },
          },
          valuePrepareFunction: (value) => {
            const option = this.methodologyOptions.find(opt => opt.value === value);
            return option ? option.title : value;
          },
        },
        description: {
          title: 'Description',
          type: 'string',
          editable: true,
          addable: true,
        },
        employeCount: {
          title: 'Employees',
          type: 'number',
          editable: false,
          addable: false,
          valuePrepareFunction: (value) => {
            return value || 0;
          },
        },
        projectCount: {
          title: 'Projects',
          type: 'number',
          editable: false,
          addable: false,
          valuePrepareFunction: (value) => {
            return value || 0;
          },
        },
        createdAt: {
          title: 'Created Date',
          type: 'date',
          editable: false,
          addable: false,
          valuePrepareFunction: (date) => {
            return date ? new Date(date).toLocaleDateString() : '-';
          },
        },
      },
    };
  }

  loadMissions() {
    this.missionService.getAllMissions().subscribe({
      next: (data) => {
        const enhancedData = data.map(mission => ({
          ...mission,
          employeCount: mission.affectations?.length || 0,
          projectCount: mission.projects?.length || 0,
          createdAt: mission.createdAt || new Date(),
        }));
        this.source.load(enhancedData);
      },
      error: (err) => {
        console.error('Error loading missions:', err);
        this.toastrService.danger('Failed to load missions', 'Error');
      },
    });
  }

  onDeleteConfirm(event): void {
    if (window.confirm('Are you sure you want to delete this mission? All associated projects and affectations will be affected.')) {
      this.missionService.deleteMission(event.data.id).subscribe({
        next: () => {
          this.loadMissions();
          event.confirm.resolve();
          this.toastrService.success('Mission deleted successfully', 'Success');
        },
        error: (err) => {
          console.error(err);
          this.toastrService.danger('Error deleting mission', 'Error');
          event.confirm.reject();
        },
      });
    } else {
      event.confirm.reject();
    }
  }

  onCreate(event): void {
    let methodologyValue = event.newData.methodologie;
    
    if (methodologyValue === '2D') methodologyValue = 'D2';
    if (methodologyValue === '3D') methodologyValue = 'D3';
    
    const newMission: Mission = {
      codeMission: event.newData.codeMission,
      methodologie: methodologyValue,
      description: event.newData.description,
    };
    
    this.missionService.createMission(newMission).subscribe({
      next: (created) => {
        this.loadMissions();
        event.confirm.resolve(created);
        this.toastrService.success('Mission created successfully', 'Success');
      },
      error: (err) => {
        console.error(err);
        this.toastrService.danger('Error creating mission', 'Error');
        event.confirm.reject();
      }
    });
  }

  onEdit(event): void {
    let methodologyValue = event.newData.methodologie;
    
    if (methodologyValue === '2D') methodologyValue = 'D2';
    if (methodologyValue === '3D') methodologyValue = 'D3';
    
    const updatedMission: Mission = {
      id: event.data.id,
      codeMission: event.newData.codeMission,
      methodologie: methodologyValue,
      description: event.newData.description,
    };

    this.missionService.updateMission(event.data.id, updatedMission).subscribe({
      next: (updated) => {
        this.loadMissions();
        event.confirm.resolve(updated);
        this.toastrService.success('Mission updated successfully', 'Success');
      },
      error: (err) => {
        console.error(err);
        this.toastrService.danger('Error updating mission', 'Error');
        event.confirm.reject();
      }
    });
  }
}