import { Component, OnInit } from '@angular/core';
import { LocalDataSource } from 'ng2-smart-table';
import { ContratService, Contrat } from '../../../services/contracts/contrat.service';
import { AuthService } from '../../../services/auth.service';
import { Router, ActivatedRoute } from '@angular/router';
import { NbToastrService } from '@nebular/theme';

@Component({
  selector: 'ngx-contrats-table',
  templateUrl: './contrats-table.component.html',
  styleUrls: ['./contrats-table.component.scss'],
})
export class ContratsTableComponent implements OnInit {

  employeId: number | null = null;
  employeName: string = '';
  isSpecificEmployee: boolean = false;
  
  // ✅ settings كمتغير عادي (وليس getter)
  settings: any;

  source: LocalDataSource = new LocalDataSource();

  constructor(
    private contratService: ContratService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private toastrService: NbToastrService
  ) {}

  ngOnInit() {
    const user = this.authService.getCurrentUser();
    if (!user || !user.roles.includes('ADMIN')) {
      this.router.navigate(['/pages/dashboard']);
      return;
    }

    // ✅ قراءة الـ ID من الرابط (إذا وجد)
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.employeId = +params['id'];
        this.isSpecificEmployee = true;
      }
      // ✅ بناء الإعدادات بعد تحديد الحالة
      this.buildSettings();
      this.loadContrats();
    });

    // ✅ قراءة اسم الموظف من queryParams
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

  // ✅ دالة تبني settings مرة واحدة فقط
  buildSettings() {
    const baseColumns: any = {
      type: {
        title: 'Contract Type',
        type: 'string',
      },
      dateDebut: {
        title: 'Start Date',
        type: 'date',
        valuePrepareFunction: (date) => {
          return date ? new Date(date).toLocaleDateString() : '-';
        },
      },
      dateFin: {
        title: 'End Date',
        type: 'date',
        valuePrepareFunction: (date) => {
          return date ? new Date(date).toLocaleDateString() : '-';
        },
      },
      salaire: {
        title: 'Salary',
        type: 'number',
        valuePrepareFunction: (value) => {
          return value ? value.toLocaleString() + ' DZD' : '-';
        },
      },
      dureeTravail: {
        title: 'Work Duration',
        type: 'string',
      },
      regimeTravail: {
        title: 'Work Regime',
        type: 'string',
      },
    };

    // ✅ إذا كنا في وضع كل العقود، نظهر أعمدة الموظف
    if (!this.isSpecificEmployee) {
      baseColumns.employePrenom = {
        title: 'Employee First Name',
        type: 'string',
      };
      baseColumns.employeNom = {
        title: 'Employee Last Name',
        type: 'string',
      };
    }

    // ✅ تعيين settings مرة واحدة فقط
    this.settings = {
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
      columns: baseColumns,
    };
  }

  loadContrats() {
    if (this.isSpecificEmployee && this.employeId) {
      // ✅ عرض عقود موظف محدد فقط
      this.contratService.getContratsByEmploye(this.employeId).subscribe({
        next: (data) => {
          this.source.load(data);
        },
        error: (err) => console.error('Error loading contrats for employee:', err),
      });
    } else {
      // ✅ عرض كل العقود
      this.contratService.getAllContrats().subscribe({
        next: (data) => this.source.load(data),
        error: (err) => console.error('Error loading contrats:', err),
      });
    }
  }

  onDeleteConfirm(event): void {
    if (window.confirm('Are you sure you want to delete this contract?')) {
      this.contratService.deleteContrat(event.data.id).subscribe({
        next: () => {
          this.loadContrats();
          event.confirm.resolve();
          this.toastrService.success('Contract deleted successfully', 'Success');
        },
        error: (err) => {
          console.error(err);
          this.toastrService.danger('Error deleting contract', 'Error');
          event.confirm.reject();
        },
      });
    } else {
      event.confirm.reject();
    }
  }

  onCreate(event): void {
    if (this.isSpecificEmployee && this.employeId) {
      // ✅ إذا كنا في وضع موظف محدد، نضيف العقد تلقائياً لهذا الموظف
      const newContrat = {
        ...event.newData,
        employeId: this.employeId
      };
      
      this.contratService.createContrat(newContrat).subscribe({
        next: (created) => {
          this.loadContrats();
          event.confirm.resolve(created);
          this.toastrService.success('Contract added successfully', 'Success');
        },
        error: (err) => {
          console.error(err);
          this.toastrService.danger('Error adding contract', 'Error');
          event.confirm.reject();
        }
      });
    } else {
      // ✅ إذا كنا في وضع كل العقود، نوجه إلى صفحة الموظفين مع رسالة
      event.confirm.reject();
      this.toastrService.info(
        'Please select an employee first from the Employees Table, then click the menu button (⋮) to add a contract for that specific employee.',
        'How to Add a Contract',
        { duration: 10000 }
      );
      this.router.navigate(['/pages/tables/smart-table']);
    }
  }

  onEdit(event): void {
    const updatedContrat = {
      ...event.newData,
      employeId: event.data.employeId
    };

    this.contratService.updateContrat(event.data.id, updatedContrat).subscribe({
      next: (updated) => {
        this.loadContrats();
        event.confirm.resolve(updated);
        this.toastrService.success('Contract updated successfully', 'Success');
      },
      error: (err) => {
        console.error(err);
        this.toastrService.danger('Error updating contract', 'Error');
        event.confirm.reject();
      }
    });
  }

  // ✅ الرجوع إلى صفحة الموظفين
  goBack() {
    this.router.navigate(['/pages/tables/smart-table']);
  }
}