// leaflet.component.ts
import { Component, OnInit, OnDestroy } from '@angular/core';
import * as L from 'leaflet';
import { ProjectService, ProjectResponseDTO, SiteResponse } from '../../../services/project/project.service';
import { MissionService } from '../../../services/mission/mission.service';
import { AuthService } from '../../../services/auth.service';
import { forkJoin } from 'rxjs';
import { takeWhile } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, FormGroup } from '@angular/forms';

// Import polygon clipping
const polygonClipping = require('polygon-clipping');

interface ProjectZone {
  id: number;
  name: string;
  coordinates: L.LatLngExpression[];
  clippedCoordinates: L.LatLngExpression[][][];
  status: string;
  statusLabel: string;
  color: string;
  budget: number;
  progression: number;
  wilaya: string;
  surface: number;
  missionCode: string;
  isClipped: boolean;
  isVisible: boolean;
}

@Component({
  selector: 'ngx-leaflet',
  styleUrls: ['./leaflet.component.scss'],
  template: `
    <nb-card class="map-card">
      <nb-card-header>
        <div class="header-content">
          <i class="fas fa-map-marked-alt"></i>
          <span>Project Zones Map - Algeria</span>
          <div class="legend" *ngIf="filteredZones.length > 0">
            <div class="legend-item">
              <span class="legend-dot planned"></span>
              <span>Planned</span>
            </div>
            <div class="legend-item">
              <span class="legend-dot progress"></span>
              <span>In Progress</span>
            </div>
            <div class="legend-item">
              <span class="legend-dot completed"></span>
              <span>Completed</span>
            </div>
            <div class="legend-item">
              <span class="legend-dot delayed"></span>
              <span>Delayed</span>
            </div>
            <div class="legend-item">
              <span class="legend-dot cancelled"></span>
              <span>Cancelled</span>
            </div>
          </div>
        </div>
        
        <!-- Filter Section -->
        <div class="filter-section">
          <button class="filter-toggle-btn" (click)="showFilters = !showFilters">
            <i class="fas fa-filter"></i>
            <span>Filters</span>
            <span class="filter-count" *ngIf="getActiveFiltersCount() > 0">{{ getActiveFiltersCount() }}</span>
          </button>
        </div>
        
        <!-- Filter Panel -->
        <div class="filter-panel" *ngIf="showFilters" (click)="$event.stopPropagation()">
          <div class="filter-header">
            <h5><i class="fas fa-search"></i> Search Projects</h5>
            <button class="close-filter" (click)="showFilters = false">✕</button>
          </div>
          
          <div class="filter-form">
            <div class="filter-row">
              <div class="filter-field">
                <label><i class="fas fa-tag"></i> Project Name</label>
                <div class="searchable-dropdown">
                  <input 
                    type="text" 
                    [(ngModel)]="projectNameSearch" 
                    (input)="filterProjectNames()"
                    (focus)="showProjectDropdown = true"
                    placeholder="Search or select project..."
                    class="filter-input"
                    autocomplete="off">
                  <div class="dropdown-list" *ngIf="showProjectDropdown && filteredProjectNames.length > 0">
                    <div *ngFor="let project of filteredProjectNames" 
                         class="dropdown-item"
                         (click)="selectProjectName(project)">
                      {{ project }}
                    </div>
                  </div>
                </div>
              </div>
              <div class="filter-field">
                <label><i class="fas fa-code-branch"></i> Mission Code</label>
                <div class="searchable-dropdown">
                  <input 
                    type="text" 
                    [(ngModel)]="missionCodeSearch" 
                    (input)="filterMissionCodes()"
                    (focus)="showMissionDropdown = true"
                    placeholder="Search or select mission..."
                    class="filter-input"
                    autocomplete="off">
                  <div class="dropdown-list" *ngIf="showMissionDropdown && filteredMissionCodes.length > 0">
                    <div *ngFor="let code of filteredMissionCodes" 
                         class="dropdown-item"
                         (click)="selectMissionCode(code)">
                      {{ code }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
            
            <div class="filter-row">
              <div class="filter-field">
                <label><i class="fas fa-map-marker-alt"></i> Wilaya</label>
                <input type="text" [(ngModel)]="wilayaSearch" (input)="applyFilters()" placeholder="Search by wilaya..." class="filter-input">
              </div>
              <div class="filter-field status-filter">
                <label><i class="fas fa-chart-line"></i> Status</label>
                <div class="status-checkboxes">
                  <label *ngFor="let status of statusOptions" class="status-checkbox">
                    <input type="checkbox" [value]="status.value" (change)="onStatusFilterChange($event, status.value)">
                    <span class="status-dot" [style.background]="statusColors[status.value]"></span>
                    <span>{{ status.label }}</span>
                  </label>
                </div>
              </div>
            </div>
            
            <div class="filter-actions">
              <button class="btn-apply" type="button" (click)="applyFilters()">
                <i class="fas fa-check"></i> Apply Filters
              </button>
              <button class="btn-reset" type="button" (click)="resetFilters()">
                <i class="fas fa-undo-alt"></i> Reset
              </button>
            </div>
          </div>
        </div>
      </nb-card-header>
      <nb-card-body>
        <div *ngIf="isLoading" class="loading-container">
          <div class="spinner"></div>
          <p>Loading map data...</p>
        </div>
        
        <div class="stats-bar" *ngIf="!isLoading && allZones.length > 0">
          <div class="stats-info">
            <i class="fas fa-map-marker-alt"></i>
            <span>Showing {{ filteredZones.length }} of {{ allZones.length }} projects</span>
          </div>
        </div>
        
        <div class="no-results" *ngIf="!isLoading && filteredZones.length === 0 && allZones.length > 0">
          <i class="fas fa-search"></i>
          <p>No projects match your filters</p>
          <button class="btn-reset" (click)="resetFilters()">Clear Filters</button>
        </div>
        
        <div leaflet [leafletOptions]="options" (leafletMapReady)="onMapReady($event)" *ngIf="!isLoading"></div>
      </nb-card-body>
    </nb-card>
  `,
  styles: [`
    .map-card {
      border-radius: 20px;
      overflow: hidden;
      box-shadow: 0 20px 35px -8px rgba(0, 0, 0, 0.1);
    }
    
    .header-content {
      display: flex;
      align-items: center;
      gap: 1rem;
      flex-wrap: wrap;
      margin-bottom: 1rem;
    }
    
    .header-content i {
      font-size: 1.5rem;
      color: #10b981;
    }
    
    .header-content span {
      font-size: 1.1rem;
      font-weight: 600;
      flex: 1;
    }
    
    .legend {
      display: flex;
      gap: 1rem;
      flex-wrap: wrap;
    }
    
    .legend-item {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      font-size: 0.75rem;
      padding: 0.25rem 0.75rem;
      background: rgba(0, 0, 0, 0.05);
      border-radius: 20px;
    }
    
    .legend-dot {
      width: 12px;
      height: 12px;
      border-radius: 2px;
    }
    
    .legend-dot.planned { background: #64748b; }
    .legend-dot.progress { background: #3b82f6; }
    .legend-dot.completed { background: #10b981; }
    .legend-dot.delayed { background: #ef4444; }
    .legend-dot.cancelled { background: #94a3b8; }
    
    .filter-section {
      margin-bottom: 0.5rem;
    }
    
    .filter-toggle-btn {
      background: rgba(0, 0, 0, 0.05);
      border: 1px solid #e2e8f0;
      padding: 0.5rem 1rem;
      border-radius: 20px;
      cursor: pointer;
      display: inline-flex;
      align-items: center;
      gap: 0.5rem;
      font-size: 0.85rem;
      transition: all 0.2s;
    }
    
    .filter-toggle-btn:hover {
      background: rgba(16, 185, 129, 0.1);
      border-color: #10b981;
    }
    
    .filter-count {
      background: #10b981;
      color: white;
      border-radius: 50%;
      width: 18px;
      height: 18px;
      font-size: 11px;
      display: inline-flex;
      align-items: center;
      justify-content: center;
    }
    
    .filter-panel {
      margin-top: 1rem;
      padding: 1rem;
      background: white;
      border-radius: 12px;
      border: 1px solid #e2e8f0;
      animation: slideDown 0.3s ease;
    }
    
    @keyframes slideDown {
      from {
        opacity: 0;
        transform: translateY(-10px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }
    
    .filter-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 1rem;
      padding-bottom: 0.5rem;
      border-bottom: 1px solid #e2e8f0;
    }
    
    .filter-header h5 {
      margin: 0;
      font-size: 1rem;
      font-weight: 600;
    }
    
    .close-filter {
      background: none;
      border: none;
      font-size: 1.2rem;
      cursor: pointer;
      color: #94a3b8;
    }
    
    .close-filter:hover {
      color: #ef4444;
    }
    
    .filter-form {
      display: flex;
      flex-direction: column;
      gap: 1rem;
    }
    
    .filter-row {
      display: flex;
      gap: 1rem;
      flex-wrap: wrap;
    }
    
    .filter-field {
      flex: 1;
      min-width: 200px;
      position: relative;
    }
    
    .filter-field label {
      display: block;
      font-size: 0.7rem;
      font-weight: 600;
      margin-bottom: 0.25rem;
      color: #64748b;
      text-transform: uppercase;
    }
    
    .filter-input {
      width: 100%;
      padding: 0.5rem 0.75rem;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      font-size: 0.85rem;
    }
    
    .filter-input:focus {
      outline: none;
      border-color: #10b981;
    }
    
    .searchable-dropdown {
      position: relative;
      width: 100%;
    }
    
    .dropdown-list {
      position: absolute;
      top: 100%;
      left: 0;
      right: 0;
      max-height: 200px;
      overflow-y: auto;
      background: white;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      z-index: 1000;
    }
    
    .dropdown-item {
      padding: 0.5rem 0.75rem;
      cursor: pointer;
      transition: background 0.2s;
    }
    
    .dropdown-item:hover {
      background: #f1f5f9;
    }
    
    .status-checkboxes {
      display: flex;
      flex-wrap: wrap;
      gap: 0.75rem;
    }
    
    .status-checkbox {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      cursor: pointer;
      font-size: 0.8rem;
      padding: 0.25rem 0.5rem;
      background: #f8fafc;
      border-radius: 20px;
    }
    
    .status-checkbox:hover {
      background: #e2e8f0;
    }
    
    .status-dot {
      width: 10px;
      height: 10px;
      border-radius: 2px;
    }
    
    .filter-actions {
      display: flex;
      gap: 0.75rem;
      justify-content: flex-end;
      padding-top: 0.5rem;
      border-top: 1px solid #e2e8f0;
    }
    
    .btn-apply, .btn-reset {
      padding: 0.5rem 1rem;
      border-radius: 8px;
      cursor: pointer;
      font-size: 0.8rem;
      font-weight: 500;
      transition: all 0.2s;
    }
    
    .btn-apply {
      background: #10b981;
      color: white;
      border: none;
    }
    
    .btn-apply:hover {
      background: #059669;
    }
    
    .btn-reset {
      background: #ef4444;
      color: white;
      border: none;
    }
    
    .btn-reset:hover {
      background: #dc2626;
    }
    
    .stats-bar {
      padding: 0.5rem 1rem;
      background: #f8fafc;
      border-bottom: 1px solid #e2e8f0;
      margin-bottom: 0.5rem;
    }
    
    .stats-info {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      font-size: 0.8rem;
      color: #10b981;
    }
    
    .no-results {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      height: 300px;
      text-align: center;
    }
    
    .no-results i {
      font-size: 48px;
      color: #94a3b8;
      margin-bottom: 1rem;
    }
    
    .loading-container {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      height: 500px;
    }
    
    .spinner {
      width: 40px;
      height: 40px;
      border: 3px solid #e2e8f0;
      border-top-color: #10b981;
      border-radius: 50%;
      animation: spin 1s linear infinite;
    }
    
    @keyframes spin {
      to { transform: rotate(360deg); }
    }
    
    ::ng-deep .leaflet-container {
      height: 550px;
      width: 100%;
      border-radius: 12px;
    }
    
    ::ng-deep .project-polygon {
      stroke-width: 2;
      stroke: white;
      transition: all 0.3s ease;
    }
    
    ::ng-deep .project-polygon:hover {
      stroke-width: 3;
      filter: brightness(1.1);
    }
    
    ::ng-deep .custom-tooltip {
      background: rgba(0, 0, 0, 0.85);
      border: none;
      border-radius: 8px;
      color: white;
      font-size: 12px;
      padding: 8px 12px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }
    
    ::ng-deep .project-marker {
      background: transparent;
      border: none;
    }
  `]
})
export class LeafletComponent implements OnInit, OnDestroy {
  
  options: any;
  private map: any;
  allZones: ProjectZone[] = [];
  filteredZones: ProjectZone[] = [];
  isLoading: boolean = true;
  showFilters: boolean = false;
  selectedStatuses: Set<string> = new Set();
  private alive = true;
  
  // Filter values
  projectNameSearch: string = '';
  missionCodeSearch: string = '';
  wilayaSearch: string = '';
  
  // Dropdown data
  projectNames: string[] = [];
  filteredProjectNames: string[] = [];
  missionCodes: string[] = [];
  filteredMissionCodes: string[] = [];
  
  showProjectDropdown: boolean = false;
  showMissionDropdown: boolean = false;
  
  // Status options for filter
  statusOptions = [
    { value: 'PLANIFIER', label: 'Planned' },
    { value: 'ENCOURS', label: 'In Progress' },
    { value: 'ENATTENTE', label: 'On Hold' },
    { value: 'ENRETARD', label: 'Delayed' },
    { value: 'TERMINI', label: 'Completed' },
    { value: 'ANNULE', label: 'Cancelled' }
  ];
  
  // Algeria bounds
  private algeriaBounds = L.latLngBounds(
    L.latLng(18.0, -8.7),
    L.latLng(38.0, 12.0)
  );
  
  // Wilaya polygons for clipping
  private wilayaPolygons: Map<string, any> = new Map();
  private geoJsonLoaded: boolean = false;
  
  // Status colors
  private statusColors: { [key: string]: string } = {
    'PLANIFIER': '#64748b',
    'ENCOURS': '#3b82f6',
    'ENATTENTE': '#f59e0b',
    'ENRETARD': '#ef4444',
    'TERMINI': '#10b981',
    'ANNULE': '#94a3b8'
  };
  
  private statusLabels: { [key: string]: string } = {
    'PLANIFIER': 'Planned',
    'ENCOURS': 'In Progress',
    'ENATTENTE': 'On Hold',
    'ENRETARD': 'Delayed',
    'TERMINI': 'Completed',
    'ANNULE': 'Cancelled'
  };

  constructor(
    private projectService: ProjectService,
    private missionService: MissionService,
    private authService: AuthService,
    private http: HttpClient,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.initMap();
    this.loadGeoJson();
  }
  
  initMap() {
    this.options = {
      layers: [
        L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png', {
          attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> &copy; <a href="https://carto.com/attributions">CARTO</a>',
          subdomains: 'abcd',
          maxZoom: 19,
          minZoom: 5
        }),
      ],
      zoom: 6,
      center: L.latLng(28.0, 2.0),
      maxBounds: this.algeriaBounds,
      maxBoundsViscosity: 1.0,
      zoomControl: true,
      fadeAnimation: true,
      attributionControl: true
    };
  }
  
  loadGeoJson() {
    console.log('Loading GeoJSON for clipping...');
    this.http.get('assets/map/algeria.json').subscribe({
      next: (geoJson: any) => {
        if (geoJson && geoJson.features) {
          geoJson.features.forEach((feature: any) => {
            const wilayaName = feature.properties.name;
            const geometry = feature.geometry;
            const polygons: any[] = [];
            
            if (geometry.type === 'Polygon') {
              const polygon = geometry.coordinates[0].map((coord: number[]) => [coord[0], coord[1]]);
              polygons.push(polygon);
            } else if (geometry.type === 'MultiPolygon') {
              geometry.coordinates.forEach((poly: any) => {
                const polygon = poly[0].map((coord: number[]) => [coord[0], coord[1]]);
                polygons.push(polygon);
              });
            }
            
            this.wilayaPolygons.set(wilayaName, polygons);
          });
          
          this.geoJsonLoaded = true;
          console.log('GeoJSON loaded, wilayas:', this.wilayaPolygons.size);
          this.loadProjects();
        }
      },
      error: (err) => {
        console.error('Error loading GeoJSON:', err);
        this.loadProjects();
      }
    });
  }
  
  loadProjects() {
    this.isLoading = true;
    
    const userRoles = this.authService.getCurrentUser()?.roles || [];
    const canViewAll = userRoles.includes('DIRECTEUR') || 
                       userRoles.includes('ADMIN') || 
                       userRoles.includes('ADMINISTRATEUR');
    
    let projects$;
    
    if (canViewAll) {
      projects$ = this.projectService.getAllProjects();
    } else {
      projects$ = this.projectService.getProjectsByCurrentMission();
    }
    
    projects$.subscribe({
      next: (projects) => {
        // Extract unique project names and mission codes for dropdowns
      // استبدل هذين السطرين في دالة loadProjects:

this.projectNames = [...new Set(projects.map(p => p.nom).filter(name => name !== undefined && name !== null))] as string[];
this.missionCodes = [...new Set(projects.map(p => p.missionCode).filter(code => code !== undefined && code !== null))] as string[];
        this.filteredProjectNames = [...this.projectNames];
        this.filteredMissionCodes = [...this.missionCodes];
        
        this.loadProjectsSites(projects);
      },
      error: (err) => {
        console.error('Error loading projects:', err);
        this.isLoading = false;
      }
    });
  }
  
  loadProjectsSites(projects: ProjectResponseDTO[]) {
    const projectSiteRequests = projects.map(project => 
      this.projectService.getSiteByProjectId(project.id).pipe(
        takeWhile(() => this.alive)
      )
    );
    


    forkJoin(projectSiteRequests).subscribe({
      next: (sites: (SiteResponse | null)[]) => {
        this.allZones = [];
        
        for (let i = 0; i < projects.length; i++) {
          const project = projects[i];
          const site = sites[i];
          
          if (site && site.coordonnees && site.coordonnees.length >= 3) {
            const status = this.getProjectStatus(project);
            const color = this.statusColors[status] || '#64748b';
            const wilayaName = site.wilaya?.nom;
            
            // Convert coordinates to [lng, lat] for clipping
            let coordinates = site.coordonnees.map(coord => [coord.longitude, coord.latitude]);
            coordinates.push([coordinates[0][0], coordinates[0][1]]);
            
            let clippedCoordinates: any[] = [];
            let isClipped = false;
            let isVisible = true;
            
            // Clip polygon to wilaya boundaries
            if (wilayaName && this.geoJsonLoaded && this.wilayaPolygons.has(wilayaName)) {
              const wilayaPolys = this.wilayaPolygons.get(wilayaName);
              
              if (wilayaPolys && wilayaPolys.length > 0) {
                try {
                  const projectPolygon = [coordinates];
                  let allIntersections: any[] = [];
                  
                  for (const wilayaPoly of wilayaPolys) {
                    const wilayaPolygon = [wilayaPoly];
                    const intersection = polygonClipping.intersection(
                      projectPolygon,
                      wilayaPolygon
                    );
                    
                    if (intersection && intersection.length > 0) {
                      allIntersections = allIntersections.concat(intersection);
                    }
                  }
                  
                  if (allIntersections.length > 0) {
                    clippedCoordinates = allIntersections;
                    isClipped = true;
                  } else {
                    isVisible = false;
                    continue;
                  }
                } catch (e) {
                  console.error('Error clipping polygon:', e);
                  clippedCoordinates = [[coordinates]];
                }
              } else {
                clippedCoordinates = [[coordinates]];
              }
            } else {
              clippedCoordinates = [[coordinates]];
            }
            
            // Convert clipped coordinates to Leaflet format
            const leafletPolygons: L.LatLngExpression[][][] = [];
            if (clippedCoordinates && clippedCoordinates.length > 0) {
              for (const poly of clippedCoordinates) {
                const rings: L.LatLngExpression[][] = [];
                for (const ring of poly) {
                  const leafletRing = ring.map((point: number[]) => L.latLng(point[1], point[0]));
                  rings.push(leafletRing);
                }
                leafletPolygons.push(rings);
              }
            }
            
            this.allZones.push({
              id: project.id,
              name: project.nom,
              coordinates: [],
              clippedCoordinates: leafletPolygons,
              status: status,
              statusLabel: this.statusLabels[status] || status,
              color: color,
              budget: project.budget || 0,
              progression: project.progression || 0,
              wilaya: wilayaName || 'Unknown',
              surface: site.surface || 0,
              missionCode: project.missionCode || 'N/A',
              isClipped: isClipped,
              isVisible: isVisible
            });
          }
        }
        
        this.filteredZones = [...this.allZones];
        this.isLoading = false;
        
        if (this.map) {
          this.drawPolygons();
        }
      },
      error: (err) => {
        console.error('Error loading sites:', err);
        this.isLoading = false;
      }
    });
  }
  
  getProjectStatus(project: any): string {
    if (project.etatAvancements && project.etatAvancements.length > 0) {
      const projectStatus = project.etatAvancements.find(
        (e: any) => !e.activeId || e.activeId === null
      );
      if (projectStatus && projectStatus.status) {
        return projectStatus.status;
      }
    }
    if (project.progression >= 100) return 'TERMINI';
    if (project.progression >= 75) return 'ENCOURS';
    if (project.progression >= 50) return 'ENATTENTE';
    if (project.progression >= 25) return 'ENRETARD';
    return 'PLANIFIER';
  }
  
  filterProjectNames() {
    this.filteredProjectNames = this.projectNames.filter(name =>
      name.toLowerCase().includes(this.projectNameSearch.toLowerCase())
    );
  }
  
  filterMissionCodes() {
    this.filteredMissionCodes = this.missionCodes.filter(code =>
      code.toLowerCase().includes(this.missionCodeSearch.toLowerCase())
    );
  }
  
  selectProjectName(project: string) {
    this.projectNameSearch = project;
    this.showProjectDropdown = false;
    this.applyFilters();
  }
  
  selectMissionCode(code: string) {
    this.missionCodeSearch = code;
    this.showMissionDropdown = false;
    this.applyFilters();
  }
  
  onStatusFilterChange(event: any, statusValue: string) {
    if (event.target.checked) {
      this.selectedStatuses.add(statusValue);
    } else {
      this.selectedStatuses.delete(statusValue);
    }
    this.applyFilters();
  }
  
  getActiveFiltersCount(): number {
    let count = 0;
    if (this.projectNameSearch && this.projectNameSearch.trim()) count++;
    if (this.missionCodeSearch && this.missionCodeSearch.trim()) count++;
    if (this.wilayaSearch && this.wilayaSearch.trim()) count++;
    count += this.selectedStatuses.size;
    return count;
  }
  
  applyFilters() {
    this.filteredZones = this.allZones.filter(zone => {
      // Project name filter
      if (this.projectNameSearch && this.projectNameSearch.trim()) {
        if (!zone.name.toLowerCase().includes(this.projectNameSearch.toLowerCase())) {
          return false;
        }
      }
      
      // Mission code filter
      if (this.missionCodeSearch && this.missionCodeSearch.trim()) {
        if (!zone.missionCode.toLowerCase().includes(this.missionCodeSearch.toLowerCase())) {
          return false;
        }
      }
      
      // Wilaya filter
      if (this.wilayaSearch && this.wilayaSearch.trim()) {
        if (!zone.wilaya.toLowerCase().includes(this.wilayaSearch.toLowerCase())) {
          return false;
        }
      }
      
      // Status filter
      if (this.selectedStatuses.size > 0 && !this.selectedStatuses.has(zone.status)) {
        return false;
      }
      
      return true;
    });
    
    this.drawPolygons();
  }
  
  resetFilters() {
    this.projectNameSearch = '';
    this.missionCodeSearch = '';
    this.wilayaSearch = '';
    this.selectedStatuses.clear();
    
    // Reset checkboxes
    const checkboxes = document.querySelectorAll('.status-checkbox input[type="checkbox"]');
    checkboxes.forEach((checkbox: any) => {
      checkbox.checked = false;
    });
    
    this.filteredZones = [...this.allZones];
    this.drawPolygons();
    this.showFilters = false;
  }
  
  drawPolygons() {
    if (!this.map) return;
    
    // Clear existing polygons
    if (this.map._polygons) {
      this.map._polygons.forEach((polygon: any) => {
        this.map.removeLayer(polygon);
      });
    }
    this.map._polygons = [];
    
    // Draw each filtered polygon
    this.filteredZones.forEach(zone => {
      if (!zone.isVisible) return;
      
      if (zone.clippedCoordinates && zone.clippedCoordinates.length > 0) {
        for (const polygonRings of zone.clippedCoordinates) {
          for (let ringIndex = 0; ringIndex < polygonRings.length; ringIndex++) {
            const ring = polygonRings[ringIndex];
            if (ring && ring.length >= 3) {
              const polygon = L.polygon(ring, {
                color: zone.color,
                weight: 2,
                opacity: 0.8,
                fillColor: zone.color,
                fillOpacity: 0.5,
                className: 'project-polygon'
              }).addTo(this.map);
              
              const clipWarning = zone.isClipped ? 
                '<span style="color: #f59e0b;">⚠️ Clipped to wilaya boundary</span><br/>' : '';
              
              polygon.bindTooltip(`
                <div style="padding: 4px;">
                  <strong style="font-size: 13px;">📌 ${zone.name}</strong><br/>
                  <span style="color: #10b981;">🚀 Mission:</span> <strong>${zone.missionCode}</strong><br/>
                  <span style="color: ${zone.color};">●</span> <strong>Status:</strong> ${zone.statusLabel}<br/>
                  <span style="color: #8b5cf6;">📍 Wilaya:</span> ${zone.wilaya}<br/>
                  <span style="color: #3b82f6;">📊 Progress:</span> ${zone.progression}%<br/>
                  <span style="color: #f59e0b;">🗺️ Surface:</span> ${zone.surface.toLocaleString()} km²<br/>
                  <span style="color: #10b981;">💰 Budget:</span> ${zone.budget.toLocaleString()} DA<br/>
                  ${clipWarning}
                </div>
              `, {
                sticky: true,
                className: 'custom-tooltip'
              });
              
              polygon.on('click', () => {
                this.onProjectClick(zone.id);
              });
              
              if (!this.map._polygons) this.map._polygons = [];
              this.map._polygons.push(polygon);
            }
          }
        }
        
        // Add center marker
        if (zone.clippedCoordinates.length > 0 && zone.clippedCoordinates[0].length > 0) {
          const firstRing = zone.clippedCoordinates[0][0];
          if (firstRing && firstRing.length > 0) {
            const center = this.getPolygonCenter(firstRing);
            const marker = L.marker(center, {
              icon: L.divIcon({
                html: `<div style="background: ${zone.color}; width: 12px; height: 12px; border-radius: 50%; border: 2px solid white; box-shadow: 0 0 4px rgba(0,0,0,0.3);"></div>`,
                iconSize: [12, 12],
                className: 'project-marker'
              })
            }).addTo(this.map);
            
            marker.bindTooltip(zone.name, { sticky: true });
            marker.on('click', () => {
              this.onProjectClick(zone.id);
            });
          }
        }
      }
    });
    
    // Fit map to show all polygons
    if (this.filteredZones.length > 0) {
      const allBounds: L.LatLngBounds = L.latLngBounds([]);
      this.filteredZones.forEach(zone => {
        if (zone.clippedCoordinates && zone.clippedCoordinates.length > 0) {
          zone.clippedCoordinates.forEach(polygonRings => {
            polygonRings.forEach(ring => {
              ring.forEach(point => {
                allBounds.extend(point);
              });
            });
          });
        }
      });
      if (allBounds.isValid()) {
        this.map.fitBounds(allBounds, { padding: [50, 50] });
      }
    }
  }
  
  private getPolygonCenter(coordinates: L.LatLngExpression[]): L.LatLng {
    let sumLat = 0, sumLng = 0;
    let count = 0;
    coordinates.forEach(coord => {
      const latLng = L.latLng(coord);
      sumLat += latLng.lat;
      sumLng += latLng.lng;
      count++;
    });
    return L.latLng(sumLat / count, sumLng / count);
  }
  
  onProjectClick(projectId: number) {
    window.open(`/pages/project-overview/${projectId}`, '_blank');
  }
  
  onMapReady(map: L.Map) {
    this.map = map;
    
    // Add Algeria border
    const algeriaBorder = L.rectangle(this.algeriaBounds, {
      color: "#ff7800",
      weight: 2,
      fill: false,
      dashArray: "5, 5"
    }).addTo(this.map);
    
    algeriaBorder.bindPopup("Algeria Boundaries");
    
    if (this.filteredZones.length > 0) {
      this.drawPolygons();
    }
  }
  
  ngOnDestroy() {
    this.alive = false;
    if (this.map) {
      this.map.remove();
    }
  }
}