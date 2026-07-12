import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { NbToastrService, NbMenuItem, NbMenuService } from '@nebular/theme';
import { filter, takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';

@Component({
  selector: 'actions-dropdown',
  template: `
    <button 
      nbButton 
      status="basic" 
      size="small" 
      [nbContextMenu]="items" 
      [nbContextMenuTag]="'employee-actions-' + rowData?.id">
      ⋮
    </button>
  `,
  styles: [`
    button {
      padding: 0.25rem 0.6rem;
      font-size: 1.2rem;
      font-weight: bold;
      line-height: 1;
      background: transparent;
      border: none;
      cursor: pointer;
      
      &:hover {
        background: rgba(0, 0, 0, 0.05);
        border-radius: 4px;
      }
    }
  `]
})
export class ActionsDropdownComponent implements OnInit, OnDestroy {
  @Input() rowData: any;
  @Input() value: any;
  
  items: NbMenuItem[] = [];
  private destroy$ = new Subject<void>();

  constructor(
    private router: Router,
    private toastrService: NbToastrService,
    private nbMenuService: NbMenuService
  ) {}

  ngOnInit() {
    if (!this.rowData) return;

    this.items = [
      {
        title: '⚠️ Incidents',
        icon: 'alert-triangle-outline',
        data: { action: 'incidents' }
      },
      {
        title: '🏥 Medical Record',
        icon: 'heart-outline',
        data: { action: 'medical' }
      },
      {
        title: '👤 Account',
        icon: 'person-outline',
        data: { action: 'account' }
      }
    ];

    // ✅ Each row listens only to its own tag
    this.nbMenuService.onItemClick()
      .pipe(
        filter(({ tag }) => tag === 'employee-actions-' + this.rowData.id),
        takeUntil(this.destroy$)
      )
      .subscribe((event) => {
        this.onContextMenuClick(event);
      });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onContextMenuClick(event: any) {
    const action = event.item.data?.action;
    const employee = this.rowData;

    if (!employee) return;

    console.log('🎯 Action clicked:', action);
    console.log('👤 Employee:', employee.prenom, employee.nom, 'ID:', employee.id);

    if (action === 'incidents') {
      this.router.navigate(['/pages/tables/incidents'], {
        queryParams: { 
          employeId: employee.id,
          employeName: `${employee.prenom} ${employee.nom}`
        }
      });
      this.toastrService.info(
        `Viewing incidents for: ${employee.prenom} ${employee.nom}`, 
        'Incidents',
        { duration: 3000 }
      );
    }
    else if (action === 'medical') {
      this.router.navigate(['/pages/tables/etat-medical', employee.id], {
        queryParams: { 
          employeName: `${employee.prenom} ${employee.nom}`,
          employeId: employee.id
        }
      });
      this.toastrService.info(
        `Viewing medical record for: ${employee.prenom} ${employee.nom}`, 
        'Medical Record',
        { duration: 3000 }
      );
    }
    else if (action === 'account') {
      this.router.navigate(['/pages/employe-account'], {
        queryParams: { 
          employeId: employee.id,
          employeName: `${employee.prenom} ${employee.nom}`
        }
      });
      this.toastrService.info(
        `Managing account for: ${employee.prenom} ${employee.nom}`, 
        'Account',
        { duration: 3000 }
      );
    }
  }
}