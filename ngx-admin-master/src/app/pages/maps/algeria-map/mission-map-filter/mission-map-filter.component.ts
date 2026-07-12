// mission-map-filter.component.ts
import { Component, Output, EventEmitter, OnInit, Input } from '@angular/core';
import { MissionService, Mission } from '../../../../services/mission/mission.service';

@Component({
  selector: 'app-mission-map-filter',
  template: `
    <div class="mission-filter-container" *ngIf="visible">
      <div class="mission-filter">
        <label><i class="fas fa-bullseye"></i> Filter by Mission</label>
        <select [(ngModel)]="selectedMissionId" (change)="onMissionChange()" class="mission-select">
          <option value="all">📊 All Missions</option>
          <option *ngFor="let mission of missions" [value]="mission.id">
            🚀 {{ mission.codeMission }} - {{ mission.description | slice:0:60 }}
          </option>
        </select>
      </div>
    </div>
  `,
  styles: [`
    .mission-filter-container {
      margin-top: 1rem;
      padding-top: 1rem;
      border-top: 1px solid rgba(0, 0, 0, 0.08);
    }
    
    .mission-filter label {
      display: block;
      font-size: 0.75rem;
      font-weight: 600;
      margin-bottom: 0.5rem;
      color: #64748b;
    }
    
    .mission-filter label i {
      margin-right: 0.5rem;
    }
    
    .mission-select {
      width: 100%;
      max-width: 350px;
      padding: 0.5rem 0.75rem;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      background: white;
      font-size: 0.85rem;
      cursor: pointer;
    }
    
    .mission-select:focus {
      outline: none;
      border-color: #10b981;
    }
  `]
})
export class MissionMapFilterComponent implements OnInit {
  @Input() visible: boolean = false;
  @Output() missionChanged = new EventEmitter<string>();
  
  missions: Mission[] = [];
  selectedMissionId: string = 'all';
  isLoading: boolean = false;

  constructor(private missionService: MissionService) {}

  ngOnInit() {
    this.loadMissions();
  }

  loadMissions() {
    this.isLoading = true;
    this.missionService.getAllMissions().subscribe({
      next: (missions) => {
        this.missions = missions;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading missions:', err);
        this.isLoading = false;
      }
    });
  }

  onMissionChange() {
    this.missionChanged.emit(this.selectedMissionId);
  }

  resetToAllMissions() {
    this.selectedMissionId = 'all';
    this.onMissionChange();
  }
}