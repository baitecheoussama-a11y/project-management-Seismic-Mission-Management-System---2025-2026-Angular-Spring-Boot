import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-view-button',
  template: `
    <button class="view-btn" (click)="view()" title="View Mission Details">
      <i class="fas fa-eye"></i>
      <span class="btn-text">View</span>
    </button>
  `,
  styles: [`
    .view-btn {
      background: linear-gradient(135deg, #3b82f6, #2563eb);
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

    .view-btn i {
      font-size: 0.85rem;
    }

    .view-btn:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
    }

    @media (max-width: 768px) {
      .view-btn .btn-text {
        display: none;
      }
    }
  `]
})
export class ViewButtonComponent {
  @Input() rowData: any;

  constructor(private router: Router) {}

  view() {
    if (this.rowData?.id) {
      this.router.navigate(['/pages/mission/overview', this.rowData.id]);
    }
  }
}