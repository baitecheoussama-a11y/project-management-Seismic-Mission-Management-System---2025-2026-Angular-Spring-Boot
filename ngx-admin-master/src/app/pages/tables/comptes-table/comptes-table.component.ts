import { Component, OnInit } from '@angular/core';
import { LocalDataSource } from 'ng2-smart-table';
import { CompteService, Compte } from '../../../services/comptes/compte.service';
import { EmployeService, Employe } from '../../../services/employes/employe.service';
import { AuthService } from '../../../services/auth.service';
import { Router, ActivatedRoute } from '@angular/router';
import { NbToastrService } from '@nebular/theme';
import { combineLatest } from 'rxjs';
import { AffectationRoleService } from '../../../services/roles/affectation-role.service';

@Component({
  selector: 'ngx-comptes-table',
  templateUrl: './comptes-table.component.html',
  styleUrls: ['./comptes-table.component.scss'],
})
export class ComptesTableComponent implements OnInit {

  selectedCompte: any = null;
  specificEmployeId: number | null = null;
  specificEmployeName: string = '';
  isSpecificEmployee: boolean = false;
  
  settings: any;
  source: LocalDataSource = new LocalDataSource();
  employees: Employe[] = [];
  rolesMap: Map<number, string> = new Map(); // لتخزين role type لكل compte

  constructor(
    private compteService: CompteService,
    private employeService: EmployeService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private toastrService: NbToastrService,
    private affectationRoleService: AffectationRoleService // ✅ أضف هذا
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

      // قراءة الـ params (من الرابط: accounts/:id)
      if (params['id']) {
        this.specificEmployeId = +params['id'];
        this.isSpecificEmployee = true;
        console.log('✅ ID from params:', this.specificEmployeId);
      }

      // قراءة الـ queryParams (من navigate)
      if (queryParams['employeName']) {
        this.specificEmployeName = queryParams['employeName'];
        console.log('✅ EmployeName from queryParams:', this.specificEmployeName);
      }

      if (queryParams['employeId']) {
        this.specificEmployeId = +queryParams['employeId'];
        this.isSpecificEmployee = true;
        console.log('✅ EmployeId from queryParams:', this.specificEmployeId);
      }

      // رسالة edit mode
      if (queryParams['editMode'] === 'true') {
        setTimeout(() => {
          this.source.getElements().then(elements => {
            if (elements && elements.length > 0) {
              this.toastrService.info('Click on the row to edit account details', 'Edit Mode');
            }
          });
        }, 1000);
      }

      // ✅ بعد ما كل شيء جاهز، نبني الإعدادات ونحمل البيانات
      this.buildSettings();
      this.loadEmployees();
    });
  }

  // ✅ بناء الإعدادات حسب الحالة
  buildSettings() {
    const baseColumns: any = {
      id: {
        title: 'ID',
        type: 'number',
      
      },
      username: {
        title: 'Username',
        type: 'string',
        editor: { type: 'text' },
      },
      roleType: { // ✅ إضافة عمود role type
        title: 'Role',
        type: 'string',
        editable: false,   
  addable: false, 
        valuePrepareFunction: (value, row) => {
          // جلب role type من الـ map
          const roleType = this.rolesMap.get(row.id);
          if (!roleType) return 'No Role';
          
          const map: { [key: string]: string } = {
            'ADMINISTRATEUR': 'Admin',
            'CHEF_MISSION': 'Mission Chief',
            'CHEF_TERRAIN': 'Field Chief',
            'DIRECTEUR': 'Director'
          };
          return map[roleType] || roleType;
        },
        filter: {
          type: 'list',
          config: {
            selectText: 'Filter by Role',
            list: [
              { value: 'ADMINISTRATEUR', title: 'Admin' },
              { value: 'CHEF_MISSION', title: 'Mission Chief' },
              { value: 'CHEF_TERRAIN', title: 'Field Chief' },
              { value: 'DIRECTEUR', title: 'Director' }
            ],
          },
        },
      },
      status: {
        title: 'Status',
        type: 'string',
        editor: {
          type: 'list',
          config: {
            selectText: 'Select Status',
            list: [
              { value: 'ACTIVE', title: '🟢 ACTIVE' },
              { value: 'SUSPENDED', title: '🟡 SUSPENDED' },
              { value: 'DESACTIVE', title: '🔴 DESACTIVE' }
            ],
          },
        },
        valuePrepareFunction: (value) => {
          if (value === 'ACTIVE') return '🟢 ACTIVE';
          if (value === 'SUSPENDED') return '🟡 SUSPENDED';
          if (value === 'DESACTIVE') return '🔴 DESACTIVE';
          return value;
        }
      },
    };

    // ✅ إذا كنا في وضع كل الحسابات، نظهر أعمدة الموظف
    if (!this.isSpecificEmployee) {
      baseColumns.employePrenom = {
        title: 'First Name',
        type: 'string',
      };
      baseColumns.employeNom = {
        title: 'Last Name',
        type: 'string',
      };
      baseColumns.employeEmail = {
        title: 'Email',
        type: 'string',
      };
    }

    this.settings = {
      actions: {
        columnTitle: 'Actions',
        add: true,
        edit: true,
        delete: true,
        position: 'right'
      },
      add: {
        addButtonContent: '<i class="nb-plus" title="Create Account"></i>',
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
        deleteButtonContent: '<i class="nb-trash" title="Delete Account"></i>',
        confirmDelete: true,
      },
      columns: baseColumns,
    };
  }

  loadEmployees() {
    this.employeService.getAllEmployes().subscribe({
      next: (data) => {
        this.employees = data;
        this.loadComptes();
      },
      error: (err) => console.error('Error loading employees:', err)
    });
  }

  // ✅ تحميل الـ roles لكل compte
  loadRolesForComptes(comptes: any[]) {
    const roleRequests = comptes.map(compte => 
      this.affectationRoleService.getRolesByCompteId(compte.id).toPromise()
        .then(roles => {
          if (roles && roles.length > 0) {
            // افترض أن أول دور active هو الدور الرئيسي
            const activeRole = roles.find(r => r.active === true);
            if (activeRole) {
              this.rolesMap.set(compte.id, activeRole.roleType);
            } else if (roles.length > 0) {
              this.rolesMap.set(compte.id, roles[0].roleType);
            }
          }
        })
        .catch(err => console.error(`Error loading roles for compte ${compte.id}:`, err))
    );

    Promise.all(roleRequests).then(() => {
      // ✅ تحديث الجدول بعد تحميل كل الـ roles
      this.source.refresh();
    });
  }

  loadComptes() {
    console.log('📡 Loading comptes - isSpecificEmployee:', this.isSpecificEmployee);
    console.log('📡 specificEmployeId:', this.specificEmployeId);
    
    if (this.isSpecificEmployee && this.specificEmployeId) {
      // ✅ عرض حساب موظف محدد فقط
      this.compteService.getCompteByEmployeId(this.specificEmployeId).subscribe({
        next: (data) => {
          console.log('📡 API Response for specific employee:', data);
          
          // ✅ التعامل مع response سواء كان object أو array
          let comptesData: any[] = [];
          if (data) {
            if (Array.isArray(data)) {
              comptesData = data;
            } else {
              comptesData = [data];
            }
          }
          
          this.source.load(comptesData);
          
          // ✅ تحميل الـ roles لكل compte
          if (comptesData.length > 0) {
            this.loadRolesForComptes(comptesData);
          }
          
          if (comptesData.length === 0 && this.specificEmployeName) {
            this.toastrService.info(
              `📝 No account found for ${this.specificEmployeName}. Click + to create one.`,
              'Create Account',
              { duration: 5000 }
            );
          }
        },
        error: (err) => {
          console.error('Error loading compte for employee:', err);
          this.source.load([]);
        }
      });
    } else {
      // ✅ عرض كل الحسابات
      this.compteService.getAllComptes().subscribe({
        next: (data) => {
          console.log('📡 All comptes:', data);
          this.source.load(data);
          
          // ✅ تحميل الـ roles لكل compte
          if (data && data.length > 0) {
            this.loadRolesForComptes(data);
          }
        },
        error: (err) => {
          console.error('Error loading comptes:', err);
          this.toastrService.danger('Failed to load accounts', 'Error');
        }
      });
    }
  }

  onUserRowSelect(event: any): void {
    this.selectedCompte = event.data;
  }

  // ✅ CREATE - محسن
  onCreate(event): void {
    // إذا كان وضع موظف محدد
    if (this.isSpecificEmployee && this.specificEmployeId) {
      const username = prompt(`Enter username for ${this.specificEmployeName}:`);
      if (!username) {
        event.confirm.reject();
        return;
      }

      const newCompte = {
        username: username,
        status: 'ACTIVE',
        employeId: this.specificEmployeId
      };

      this.compteService.createCompte(newCompte).subscribe({
        next: (created) => {
          this.loadComptes();
          event.confirm.resolve(created);
          this.toastrService.success(
            `✅ Account created for ${this.specificEmployeName}! Password shown in console.`,
            'Account Created',
            { duration: 5000 }
          );
        },
        error: (err) => {
          console.error(err);
          this.toastrService.danger(err.error?.message || 'Failed to create account', 'Error');
          event.confirm.reject();
        }
      });
    } 
    // إذا كان وضع كل الحسابات
    else {
      this.toastrService.info(
        'To add an account, please go to the Employees Table, click the menu button (⋮) for a specific employee, then select Account.',
        'How to Create Account',
        { duration: 10000 }
      );
      event.confirm.reject();
      this.router.navigate(['/pages/tables/smart-table']);
    }
  }

  // ✅ EDIT
  onEdit(event): void {
    const updatedData = event.newData;
    const oldData = event.data;
    
    // تعديل username
    if (updatedData.username !== oldData.username) {
      this.compteService.updateUsername(oldData.id, updatedData.username).subscribe({
        next: () => {
          this.loadComptes();
          this.toastrService.success('Username updated successfully', 'Success');
        },
        error: (err) => {
          console.error(err);
          this.toastrService.danger('Failed to update username', 'Error');
        }
      });
    }
    
    // تعديل status
    if (updatedData.status !== oldData.status) {
      let statusValue = updatedData.status;
      if (statusValue === '🟢 ACTIVE') statusValue = 'ACTIVE';
      if (statusValue === '🟡 SUSPENDED') statusValue = 'SUSPENDED';
      if (statusValue === '🔴 DESACTIVE') statusValue = 'DESACTIVE';
      
      this.compteService.updateStatus(oldData.id, statusValue).subscribe({
        next: () => {
          this.loadComptes();
          this.toastrService.success('Status updated successfully', 'Success');
        },
        error: (err) => {
          console.error(err);
          this.toastrService.danger('Failed to update status', 'Error');
        }
      });
    }
    
    event.confirm.resolve(updatedData);
  }

  // ✅ DELETE
  onDeleteConfirm(event): void {
    const compteName = `${event.data.employePrenom || this.specificEmployeName} ${event.data.employeNom || ''}`;
    
    if (window.confirm(`Are you sure you want to delete account for ${compteName}?`)) {
      this.compteService.deleteCompte(event.data.id).subscribe({
        next: () => {
          this.loadComptes();
          event.confirm.resolve();
          this.toastrService.success(`Account deleted for ${compteName}`, 'Deleted');
        },
        error: (err) => {
          console.error(err);
          this.toastrService.danger('Failed to delete account', 'Error');
          event.confirm.reject();
        },
      });
    } else {
      event.confirm.reject();
    }
  }

  // ✅ إعادة تعيين كلمة المرور
  onResetPassword(): void {
    if (!this.selectedCompte) {
      this.toastrService.warning('Please select an account first', 'No Selection');
      return;
    }
    
    const name = `${this.selectedCompte.employePrenom || this.specificEmployeName} ${this.selectedCompte.employeNom || ''}`;
    if (window.confirm(`Reset password for ${name}?`)) {
      this.compteService.resetPassword(this.selectedCompte.id).subscribe({
        next: () => {
          this.toastrService.success(
            `Password reset! New password shown in console.`,
            'Password Reset',
            { duration: 5000 }
          );
        },
        error: (err) => {
          console.error(err);
          this.toastrService.danger('Failed to reset password', 'Error');
        }
      });
    }
  }

  // ✅ الرجوع إلى صفحة الموظفين
  goBack() {
    this.router.navigate(['/pages/tables/smart-table']);
  }
}