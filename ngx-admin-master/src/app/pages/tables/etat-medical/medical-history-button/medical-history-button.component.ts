import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';
import { NbToastrService } from '@nebular/theme';

@Component({
  selector: 'medical-history-button',
  template: `
    <button class="medical-history-btn" (click)="navigateToAntecedents($event)" title="View Medical History">
      <i class="fas fa-notes-medical"></i>
      <span class="btn-text">History</span>
    </button>
  `,
  styles: [`
    .medical-history-btn {
      background: linear-gradient(135deg, #10b981, #059669);
      border: none;
      border-radius: 8px;
      padding: 6px 14px;
      color: white;
      cursor: pointer;
      font-size: 0.75rem;
      font-weight: 500;
      display: inline-flex;
      align-items: center;
      gap: 6px;
      transition: all 0.3s ease;
    }

    .medical-history-btn i {
      font-size: 0.85rem;
    }

    .medical-history-btn:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(16, 185, 129, 0.4);
      background: linear-gradient(135deg, #059669, #047857);
    }

    .medical-history-btn:active {
      transform: translateY(0);
    }

    @media (max-width: 768px) {
      .medical-history-btn .btn-text {
        display: none;
      }
      
      .medical-history-btn {
        padding: 6px 10px;
      }
    }
  `]
})
export class MedicalHistoryButtonComponent {
  @Input() rowData: any;
  @Input() value: any;

  constructor(
    private router: Router,
    private toastrService: NbToastrService
  ) {}

  navigateToAntecedents(event: Event) {
    event.stopPropagation();
    
    const etatMedicalId = this.rowData?.id;
    const employeeName = this.rowData?.employePrenom && this.rowData?.employeNom 
      ? `${this.rowData.employePrenom} ${this.rowData.employeNom}`
      : 'Employee';
    
    if (!etatMedicalId) {
      console.error('No etatMedicalId found');
      this.toastrService.danger('Error loading medical history', 'Error');
      return;
    }
    
    console.log('Navigating to antecedents for etatMedicalId:', etatMedicalId);
    
    this.router.navigate(['/pages/tables/etat-medical', etatMedicalId, 'antecedents'], {
      queryParams: {
        etatMedicalId: etatMedicalId,
        employeeName: employeeName
      }
    });
    
    this.toastrService.info(
      `Loading medical history for ${employeeName}`,
      'Medical History',
      { duration: 2000 }
    );
  }
}