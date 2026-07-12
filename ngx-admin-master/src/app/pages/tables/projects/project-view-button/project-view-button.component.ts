import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-project-view-button',
  template: `
    <button class="view-btn" (click)="view()" title="View Project Details">
      <i class="fas fa-eye"></i>
      <span class="btn-text">View</span>
    </button>
  `,
  styles: [`
    .view-btn {
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

    .view-btn i {
      font-size: 0.85rem;
    }

    .view-btn:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(16, 185, 129, 0.4);
    }

    @media (max-width: 768px) {
      .view-btn .btn-text {
        display: none;
      }
    }
  `]
})
export class ProjectViewButtonComponent {
  @Input() rowData: any;

  constructor(private router: Router) {}

  view() {
    if (this.rowData?.id) {
      this.router.navigate(['/pages/project-overview', this.rowData.id]);
    }
  }
}