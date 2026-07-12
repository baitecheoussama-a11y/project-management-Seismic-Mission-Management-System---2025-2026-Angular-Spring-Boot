import { Component, OnInit } from '@angular/core';
import { LocalDataSource } from 'ng2-smart-table';
import { AffectationRoleService, AffectationRole } from '../../../services/roles/affectation-role.service';
import { RoleService, Role } from '../../../services/roles/role.service';
import { AuthService } from '../../../services/auth.service';
import { Router, ActivatedRoute } from '@angular/router';
import { NbToastrService } from '@nebular/theme';
import { combineLatest } from 'rxjs';

@Component({
  selector: 'ngx-affectation-roles-table',
  templateUrl: './affectation-roles-table.component.html',
  styleUrls: ['./affectation-roles-table.component.scss']
})
export class AffectationRolesTableComponent implements OnInit {

  specificCompteId: number | null = null;
  specificEmployeName: string = '';
  
  settings: any;
  source: LocalDataSource = new LocalDataSource();
  allRoles: Role[] = [];

  constructor(
    private affectationRoleService: AffectationRoleService,
    private roleService: RoleService,
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

    // ✅ استخدام combineLatest لانتظار params و queryParams معاً
    combineLatest([
      this.route.params,
      this.route.queryParams
    ]).subscribe(([params, queryParams]) => {
      console.log('🔍 Params:', params);
      console.log('🔍 QueryParams:', queryParams);

      // قراءة الـ params (من الرابط: affectation-roles/:id)
      if (params['id']) {
        this.specificCompteId = +params['id'];
        console.log('✅ CompteId from params:', this.specificCompteId);
      }

      // قراءة الـ queryParams
      if (queryParams['employeName']) {
        this.specificEmployeName = queryParams['employeName'];
        console.log('✅ EmployeName from queryParams:', this.specificEmployeName);
      }

      // ✅ بعد ما كل شيء جاهز، نبني الإعدادات ونحمل البيانات
      this.buildSettings();
      this.loadRoles();
    });
  }

  // ✅ بناء الإعدادات
  buildSettings() {
    this.settings = {
      actions: {
        columnTitle: 'Actions',
        add: true,
        edit: true,
        delete: true,
        position: 'right'
      },
      add: {
        addButtonContent: '<i class="nb-plus" title="Assign Role"></i>',
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
        deleteButtonContent: '<i class="nb-trash" title="Remove Role"></i>',
        confirmDelete: true,
      },
      columns: {
        roleType: {
          title: 'Role',
          type: 'string',
          editor: {
            type: 'list',
            config: {
              selectText: 'Select Role',
              list: this.allRoles.map(role => ({
                value: role.type,
                title: role.name
              }))
            }
          },
          valuePrepareFunction: (value) => {
            const map: { [key: string]: string } = {
              'ADMINISTRATEUR': '👑 Admin',
              'CHEF_MISSION': '🎯 Mission Chief',
              'CHEF_TERRAIN': '🌍 Field Chief',
              'DIRECTEUR': '📊 Director'
            };
            return map[value] || value;
          }
        },
        dateDebut: {
          title: 'Start Date',
          type: 'date',
          valuePrepareFunction: (date) => date ? new Date(date).toLocaleDateString() : '-',
        },
        dateFin: {
          title: 'End Date',
          type: 'date',
          valuePrepareFunction: (date) => date ? new Date(date).toLocaleDateString() : '-',
        },
        active: {
          title: 'Status',
          type: 'string',
          editor: {
            type: 'list',
            config: {
              selectText: 'Select Status',
              list: [
                { value: 'true', title: '✅ ACTIVE' },
                { value: 'false', title: '❌ INACTIVE' }
              ],
            },
          },
          valuePrepareFunction: (value) => value ? '✅ ACTIVE' : '❌ INACTIVE',
          filter: {
            type: 'list',
            config: {
              selectText: 'Filter by Status',
              list: [
                { value: 'true', title: '✅ ACTIVE' },
                { value: 'false', title: '❌ INACTIVE' }
              ],
            },
          },
        },
      },
    };
  }

  loadRoles() {
    console.log('🔄 Fetching roles from API');
    
    this.roleService.getAllRoles().subscribe({
      next: (data) => {
        console.log('✅ Roles loaded successfully:', data);
        this.allRoles = data;
        
        if (this.allRoles.length === 0) {
          this.toastrService.warning('No roles found in database. Please add roles first.', 'Warning');
        }
        
        // ✅ تحديث إعدادات الجدول بعد تحميل الأدوار
        this.buildSettings();
        this.loadAssignedRoles();
      },
      error: (err) => {
        console.error('❌ Error loading roles:', err);
        
        // عرض رسالة خطأ مناسبة
        if (err.status === 0) {
          this.toastrService.danger('Cannot connect to server. Please check if backend is running on port 8080.', 'Connection Error');
        } else if (err.status === 403) {
          this.toastrService.danger('Access denied. Please login again.', 'Access Denied');
        } else if (err.status === 404) {
          this.toastrService.danger('Roles API not found. Please check backend configuration.', 'API Error');
        } else {
          this.toastrService.danger('Failed to load roles: ' + (err.error?.message || err.message), 'Error');
        }
        
        // تحميل الأدوار المعينة حتى لو فشل تحميل قائمة الأدوار
        this.loadAssignedRoles();
      }
    });
  }

  loadAssignedRoles() {
    if (this.specificCompteId) {
      console.log('📡 Loading assigned roles for compteId:', this.specificCompteId);
      this.affectationRoleService.getRolesByCompteId(this.specificCompteId).subscribe({
        next: (data) => {
          console.log('📡 Assigned roles loaded:', data);
          // البيانات تحتوي بالفعل على roleType من الـ API
          this.source.load(data);
        },
        error: (err) => {
          console.error('Error loading assigned roles:', err);
          this.toastrService.danger('Failed to load assigned roles', 'Error');
          this.source.load([]);
        }
      });
    }
  }

  // ✅ CREATE - إضافة دور جديد
  onCreate(event): void {
    const newData = event.newData;

    if (!newData.roleType) {
      this.toastrService.warning('Please select a role', 'Warning');
      event.confirm.reject();
      return;
    }

    const selectedRole = this.allRoles.find(r => r.type === newData.roleType);

    if (!selectedRole) {
      this.toastrService.danger('Invalid role', 'Error');
      event.confirm.reject();
      return;
    }

    const newAffectation = {
      compteId: this.specificCompteId,
      roleId: selectedRole.id,
      dateDebut: newData.dateDebut || new Date().toISOString().split('T')[0],
      dateFin: newData.dateFin || null,
      active: newData.active === 'true' || newData.active === true
    };

    this.affectationRoleService.create(newAffectation).subscribe({
      next: (created) => {
        this.loadAssignedRoles();
        event.confirm.resolve(created);
        this.toastrService.success(
          `✅ Role assigned successfully to ${this.specificEmployeName || 'employee'}`,
          'Success'
        );
      },
      error: (err) => {
        console.error(err);
        this.toastrService.danger(err.error?.message || 'Failed to assign role', 'Error');
        event.confirm.reject();
      }
    });
  }

  // ✅ EDIT - تعديل دور
  onEdit(event): void {
    const updatedData = event.newData;
    const oldData = event.data;

    const selectedRole = this.allRoles.find(r => r.type === updatedData.roleType);

    const updateData: any = {};

    // فقط نضيف الحقول التي تغيرت
    if (selectedRole && selectedRole.id !== oldData.roleId) {
      updateData.roleId = selectedRole.id;
    }
    
    if (updatedData.dateDebut && updatedData.dateDebut !== oldData.dateDebut) {
      updateData.dateDebut = updatedData.dateDebut;
    }
    
    if (updatedData.dateFin !== oldData.dateFin) {
      updateData.dateFin = updatedData.dateFin === '-' ? null : updatedData.dateFin;
    }
    
    const newActiveStatus = updatedData.active === 'true' || updatedData.active === true;
    if (newActiveStatus !== oldData.active) {
      updateData.active = newActiveStatus;
    }

    if (Object.keys(updateData).length === 0) {
      this.toastrService.info('No changes detected', 'Info');
      event.confirm.reject();
      return;
    }

    this.affectationRoleService.update(oldData.id, updateData).subscribe({
      next: (updated) => {
        this.loadAssignedRoles();
        event.confirm.resolve(updated);
        this.toastrService.success('Role updated successfully', 'Success');
      },
      error: (err) => {
        console.error(err);
        this.toastrService.danger(err.error?.message || 'Failed to update role', 'Error');
        event.confirm.reject();
      }
    });
  }

  // ✅ DELETE - حذف دور
  onDeleteConfirm(event): void {
    const roleName = event.data.roleName || event.data.roleType || 'this role';
    
    if (window.confirm(`Remove "${roleName}" from ${this.specificEmployeName || 'this employee'}?`)) {
      this.affectationRoleService.delete(event.data.id).subscribe({
        next: () => {
          this.loadAssignedRoles();
          event.confirm.resolve();
          this.toastrService.success('Role removed successfully', 'Success');
        },
        error: (err) => {
          console.error(err);
          this.toastrService.danger('Failed to remove role', 'Error');
          event.confirm.reject();
        }
      });
    } else {
      event.confirm.reject();
    }
  }

  // ✅ الرجوع إلى صفحة الموظف
  goBack() {
    this.router.navigate(['/pages/employe-account'], {
      queryParams: {
        employeId: this.specificCompteId,
        employeName: this.specificEmployeName
      }
    });
  }
}