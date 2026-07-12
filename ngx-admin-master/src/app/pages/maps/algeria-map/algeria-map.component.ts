// algeria-map.component.ts
import { Component, OnDestroy, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { combineLatest, forkJoin } from 'rxjs';
import { takeWhile, debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { NbThemeService } from '@nebular/theme';
import { registerMap } from 'echarts';
import { MissionService, Mission } from '../../../services/mission/mission.service';
import { ProjectService, ProjectResponseDTO, SiteResponse } from '../../../services/project/project.service';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup } from '@angular/forms';

const polygonClipping = require('polygon-clipping');

interface ProjectZone {
  name: string;
  projectId: number;
  coordinates: number[][];
  clippedCoordinates: any[];
  status: string;
  statusLabel: string;
  color: string;
  gradientStart: string;
  gradientEnd: string;
  budget: number;
  progression: number;
  wilaya: string;
  surface: number;
  description: string;
  objectifDebut: string;
  objectifFin: string;
  objectifVP: number;
  dateFinReelle: string;
  isClipped: boolean;
  isVisible: boolean;
  missionCode: string;
  missionId: number;
}

@Component({
  selector: 'ngx-algeria-map',
  styleUrls: ['./algeria-map.component.scss'],
  template: `
    <nb-card class="map-card">
      <nb-card-header>
      
        <div class="card-header-content">
          <div class="title-section">
            <i class="fas fa-map-marked-alt"></i>
            <span>Project Zones Map - Algeria</span>
          </div>
          <div class="filter-section">
            <button class="filter-toggle-btn" (click)="showFilters = !showFilters">
              <i class="fas fa-filter"></i>
              <span>Filters</span>
              <span class="filter-count" *ngIf="activeFiltersCount > 0">{{ activeFiltersCount }}</span>
            </button>
          </div>
        </div>
        
        <!-- Filter Panel -->
        <div class="filter-panel" *ngIf="showFilters" (click)="$event.stopPropagation()">
          <div class="filter-header">
            <h5><i class="fas fa-search"></i> Search Projects</h5>
            <button class="close-filter" (click)="showFilters = false">✕</button>
          </div>
          
          <form [formGroup]="filterForm" class="filter-form">
            <div class="filter-row">
              <div class="filter-field">
                <label><i class="fas fa-tag"></i> Project Name</label>
                <input type="text" formControlName="nom" placeholder="Search by name..." class="filter-input">
              </div>
              <div class="filter-field">
                <label><i class="fas fa-align-left"></i> Description</label>
                <input type="text" formControlName="description" placeholder="Search in description..." class="filter-input">
              </div>
            </div>
            
            <div class="filter-row">
              <div class="filter-field">
                <label><i class="fas fa-coins"></i> Budget Range (DA)</label>
                <div class="range-inputs">
                  <input type="number" formControlName="budgetMin" placeholder="Min" class="filter-input small">
                  <span>-</span>
                  <input type="number" formControlName="budgetMax" placeholder="Max" class="filter-input small">
                </div>
              </div>
              <div class="filter-field">
                <label><i class="fas fa-flag-checkered"></i> Target VP Range</label>
                <div class="range-inputs">
                  <input type="number" formControlName="vpMin" placeholder="Min" class="filter-input small">
                  <span>-</span>
                  <input type="number" formControlName="vpMax" placeholder="Max" class="filter-input small">
                </div>
              </div>
            </div>
            
            <div class="filter-row">
              <div class="filter-field">
                <label><i class="fas fa-calendar-alt"></i> Start Date</label>
                <div class="date-range">
                  <input type="date" formControlName="startDateFrom" class="filter-input">
                  <span>to</span>
                  <input type="date" formControlName="startDateTo" class="filter-input">
                </div>
              </div>
              <div class="filter-field">
                <label><i class="fas fa-calendar-check"></i> End Date</label>
                <div class="date-range">
                  <input type="date" formControlName="endDateFrom" class="filter-input">
                  <span>to</span>
                  <input type="date" formControlName="endDateTo" class="filter-input">
                </div>
              </div>
            </div>
            
            <div class="filter-row">
              <div class="filter-field">
                <label><i class="fas fa-calendar-times"></i> Actual End Date</label>
                <div class="date-range">
                  <input type="date" formControlName="actualEndDateFrom" class="filter-input">
                  <span>to</span>
                  <input type="date" formControlName="actualEndDateTo" class="filter-input">
                </div>
              </div>
              <div class="filter-field status-filter">
                <label><i class="fas fa-chart-line"></i> Status</label>
                <div class="status-checkboxes">
                  <label *ngFor="let status of statusOptions" class="status-checkbox">
                    <input type="checkbox" [value]="status.value" (change)="onStatusFilterChange($event)">
                    <span class="status-dot" [style.background]="statusGradients[status.value]?.end"></span>
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
          </form>
        </div>
        
        <!-- Mission Filter Component -->
        <app-mission-map-filter 
          *ngIf="canViewAllProjects"
          [visible]="canViewAllProjects"
          (missionChanged)="onMissionFilterChange($event)">
        </app-mission-map-filter>
      </nb-card-header>
      
      <div *ngIf="isLoading" class="loading-container">
        <div class="spinner"></div>
        <p>Loading map data...</p>
      </div>
      
      <div class="stats-bar" *ngIf="!isLoading && filteredZones.length > 0">
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
      
      <div echarts [options]="options" class="echarts" *ngIf="!isLoading"></div>
    </nb-card>
  `,
})
export class AlgeriaMapComponent implements OnInit, OnDestroy {

  options: any;
  private alive = true;
  currentMissionId: number = 0;
  selectedMissionId: string = 'all';
  missions: Mission[] = [];
  allZones: ProjectZone[] = [];
  filteredZones: ProjectZone[] = [];
  isLoading: boolean = true;
  showFilters: boolean = false;
  filterForm: FormGroup;
  selectedStatuses: Set<string> = new Set();
  
  canViewAllProjects: boolean = false;
  
  private wilayaPolygons: Map<string, any> = new Map();
  private geoJsonLoaded: boolean = false;
  private pendingProjectsLoad: boolean = false; // Flag to track if we're waiting for GeoJSON

  statusOptions = [
    { value: 'PLANIFIER', label: 'Planned' },
    { value: 'ENCOURS', label: 'In Progress' },
    { value: 'ENATTENTE', label: 'On Hold' },
    { value: 'ENRETARD', label: 'Delayed' },
    { value: 'TERMINI', label: 'Completed' },
    { value: 'ANNULE', label: 'Cancelled' }
  ];

  statusGradients: { [key: string]: { start: string; end: string } } = {
    'PLANIFIER': { start: '#94a3b8', end: '#64748b' },
    'ENCOURS': { start: '#60a5fa', end: '#3b82f6' },
    'ENATTENTE': { start: '#fbbf24', end: '#f59e0b' },
    'ENRETARD': { start: '#f87171', end: '#ef4444' },
    'TERMINI': { start: '#34d399', end: '#10b981' },
    'ANNULE': { start: '#cbd5e1', end: '#94a3b8' }
  };

  statusLabels: { [key: string]: string } = {
    'PLANIFIER': 'Planned',
    'ENCOURS': 'In Progress',
    'ENATTENTE': 'On Hold',
    'ENRETARD': 'Delayed',
    'TERMINI': 'Completed',
    'ANNULE': 'Cancelled'
  };

  get activeFiltersCount(): number {
    let count = 0;
    const values = this.filterForm?.value;
    if (!values) return 0;
    
    if (values.nom && values.nom.trim()) count++;
    if (values.description && values.description.trim()) count++;
    if (values.budgetMin || values.budgetMax) count++;
    if (values.vpMin || values.vpMax) count++;
    if (values.startDateFrom || values.startDateTo) count++;
    if (values.endDateFrom || values.endDateTo) count++;
    if (values.actualEndDateFrom || values.actualEndDateTo) count++;
    count += this.selectedStatuses.size;
    
    return count;
  }

  constructor(
    private theme: NbThemeService,
    private http: HttpClient,
    private missionService: MissionService,
    private projectService: ProjectService,
    private authService: AuthService,
    private router: Router,
    private fb: FormBuilder
  ) {
    this.filterForm = this.fb.group({
      nom: [''],
      description: [''],
      budgetMin: [null],
      budgetMax: [null],
      vpMin: [null],
      vpMax: [null],
      startDateFrom: [''],
      startDateTo: [''],
      endDateFrom: [''],
      endDateTo: [''],
      actualEndDateFrom: [''],
      actualEndDateTo: ['']
    });
  }

  ngOnInit() {
    this.initializePermissions();
    console.log('canViewAllProjects:', this.canViewAllProjects);
    this.loadGeoJson();
    this.setupAutoFilter();
  }

  setupAutoFilter() {
    this.filterForm.valueChanges
      .pipe(
        debounceTime(500),
        distinctUntilChanged(),
        takeWhile(() => this.alive)
      )
      .subscribe(() => {
        this.applyFilters();
      });
  }

  initializePermissions(): void {
    const userRoles = this.authService.getCurrentUser()?.roles || [];
    this.canViewAllProjects = userRoles.includes('DIRECTEUR') || 
                              userRoles.includes('ADMIN') || 
                              userRoles.includes('ADMINISTRATEUR');
    console.log('Can view all projects:', this.canViewAllProjects);
  }

  loadGeoJson() {
    console.log('Loading GeoJSON...');
    this.http.get('assets/map/algeria.json').subscribe({
      next: (geoJson: any) => {
        console.log('GeoJSON loaded successfully');
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
          
          // If there was a pending load request, execute it now
          if (this.pendingProjectsLoad) {
            this.pendingProjectsLoad = false;
            this.loadProjectsBasedOnMission();
          } else if (!this.canViewAllProjects) {
            // Load current mission projects if user can't view all
            this.loadProjectsBasedOnMission();
          } else {
            // Load all projects if user can view all
            this.loadProjectsBasedOnMission();
          }
        } else {
          console.error('Invalid GeoJSON format');
          this.loadMapOnly();
        }
      },
      error: (err) => {
        console.error('Error loading GeoJSON:', err);
        this.loadMapOnly();
      }
    });
  }

  loadProjectsBasedOnMission() {
    if (!this.geoJsonLoaded) {
      console.log('Waiting for GeoJSON to load before loading projects...');
      this.pendingProjectsLoad = true;
      return;
    }

    if (this.selectedMissionId === 'all') {
      if (this.canViewAllProjects) {
        this.loadAllProjects();
      } else {
        this.loadCurrentMissionProjects();
      }
    } else {
      this.loadProjectsByMission(Number(this.selectedMissionId));
    }
  }

  loadCurrentMissionProjects() {
    console.log('Loading current mission projects...');
    this.isLoading = true;
    this.projectService.getProjectsByCurrentMission().subscribe({
      next: (projects) => {
        console.log('Current mission projects loaded:', projects.length);
        this.processProjects(projects);
      },
      error: (err) => {
        console.error('Error loading projects:', err);
        this.isLoading = false;
        this.loadMapOnly();
      }
    });
  }

  loadAllProjects() {
    console.log('Loading all projects...');
    this.isLoading = true;
    this.projectService.getAllProjects().subscribe({
      next: (projects) => {
        console.log('All projects loaded:', projects.length);
        this.processProjects(projects);
      },
      error: (err) => {
        console.error('Error loading all projects:', err);
        this.isLoading = false;
        this.loadMapOnly();
      }
    });
  }

  loadProjectsByMission(missionId: number) {
    console.log('Loading projects for mission:', missionId);
    this.isLoading = true;
    this.projectService.getProjectsByMissionId(missionId).subscribe({
      next: (projects) => {
        console.log('Projects loaded for mission:', projects.length);
        this.processProjects(projects);
      },
      error: (err) => {
        console.error('Error loading projects for mission:', err);
        this.isLoading = false;
        this.loadMapOnly();
      }
    });
  }

  onMissionFilterChange(missionId: string): void {
    console.log('Mission filter changed to:', missionId);
    this.selectedMissionId = missionId;
    
    // Reset zones
    this.allZones = [];
    this.filteredZones = [];
    this.isLoading = true;
    
    // Reset filters when mission changes
    this.resetFiltersWithoutReload();
    
    // Load projects based on selected mission
    this.loadProjectsBasedOnMission();
  }

  resetFiltersWithoutReload() {
    this.filterForm.reset({
      nom: '',
      description: '',
      budgetMin: null,
      budgetMax: null,
      vpMin: null,
      vpMax: null,
      startDateFrom: '',
      startDateTo: '',
      endDateFrom: '',
      endDateTo: '',
      actualEndDateFrom: '',
      actualEndDateTo: ''
    });
    this.selectedStatuses.clear();
    
    const checkboxes = document.querySelectorAll('.status-checkbox input[type="checkbox"]');
    checkboxes.forEach((checkbox: any) => {
      checkbox.checked = false;
    });
  }

  processProjects(projects: ProjectResponseDTO[]) {
    if (!projects || projects.length === 0) {
      console.log('No projects to process');
      this.allZones = [];
      this.filteredZones = [];
      this.isLoading = false;
      this.loadMapOnly();
      return;
    }

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
            const gradient = this.statusGradients[status] || { start: '#94a3b8', end: '#64748b' };
            const wilayaName = site.wilaya?.nom;
            
            let coordinates = site.coordonnees.map(coord => [coord.longitude, coord.latitude]);
            coordinates.push([coordinates[0][0], coordinates[0][1]]);
            
            let clippedPolygons: any[] = [];
            let isClipped = false;
            let isVisible = true;
            
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
                    clippedPolygons = allIntersections;
                    isClipped = true;
                  } else {
                    isVisible = false;
                    continue;
                  }
                } catch (e) {
                  console.error('Error clipping polygon:', e);
                  clippedPolygons = [[coordinates]];
                }
              } else {
                clippedPolygons = [[coordinates]];
              }
            } else {
              clippedPolygons = [[coordinates]];
            }
            
            this.allZones.push({
              name: project.nom,
              projectId: project.id,
              coordinates: coordinates,
              clippedCoordinates: clippedPolygons,
              status: status,
              statusLabel: this.statusLabels[status] || status,
              color: gradient.end,
              gradientStart: gradient.start,
              gradientEnd: gradient.end,
              budget: project.budget || 0,
              progression: project.progression || 0,
              wilaya: wilayaName || 'Unknown',
              surface: site.surface || 0,
              description: project.description || '',
              objectifDebut: project.objectifDebut,
              objectifFin: project.objectifFin,
              objectifVP: project.objectifVP || 0,
              dateFinReelle: project.dateFinReelle || '',
              isClipped: isClipped,
              isVisible: isVisible,
              missionCode: project.missionCode || '',
              missionId: project.missionId || 0
            });
          }
        }
        
        console.log(`Total zones processed: ${this.allZones.length}`);
        this.filteredZones = [...this.allZones];
        this.applyFilters(); // This will also call loadMap()
      },
      error: (err) => {
        console.error('Error loading sites:', err);
        this.isLoading = false;
        this.loadMapOnly();
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

  onStatusFilterChange(event: any) {
    if (event.target.checked) {
      this.selectedStatuses.add(event.target.value);
    } else {
      this.selectedStatuses.delete(event.target.value);
    }
    this.applyFilters();
  }

  applyFilters() {
    const values = this.filterForm.value;
    
    this.filteredZones = this.allZones.filter(zone => {
      if (values.nom && values.nom.trim() && !zone.name.toLowerCase().includes(values.nom.toLowerCase())) return false;
      if (values.description && values.description.trim() && !zone.description.toLowerCase().includes(values.description.toLowerCase())) return false;
      if (values.budgetMin && zone.budget < values.budgetMin) return false;
      if (values.budgetMax && zone.budget > values.budgetMax) return false;
      if (values.vpMin && zone.objectifVP < values.vpMin) return false;
      if (values.vpMax && zone.objectifVP > values.vpMax) return false;
      
      if (values.startDateFrom && zone.objectifDebut && new Date(zone.objectifDebut) < new Date(values.startDateFrom)) return false;
      if (values.startDateTo && zone.objectifDebut && new Date(zone.objectifDebut) > new Date(values.startDateTo)) return false;
      if (values.endDateFrom && zone.objectifFin && new Date(zone.objectifFin) < new Date(values.endDateFrom)) return false;
      if (values.endDateTo && zone.objectifFin && new Date(zone.objectifFin) > new Date(values.endDateTo)) return false;
      
      if (zone.dateFinReelle) {
        if (values.actualEndDateFrom && new Date(zone.dateFinReelle) < new Date(values.actualEndDateFrom)) return false;
        if (values.actualEndDateTo && new Date(zone.dateFinReelle) > new Date(values.actualEndDateTo)) return false;
      } else if (values.actualEndDateFrom || values.actualEndDateTo) return false;
      
      if (this.selectedStatuses.size > 0 && !this.selectedStatuses.has(zone.status)) return false;
      
      return true;
    });
    
    this.loadMap();
  }

  resetFilters() {
    this.resetFiltersWithoutReload();
    this.filteredZones = [...this.allZones];
    this.loadMap();
    this.showFilters = false;
  }

  loadMapOnly() {
    console.log('Loading base map only...');
    this.isLoading = false;
    
    combineLatest([
      this.http.get('assets/map/algeria.json'),
      this.theme.getJsTheme(),
    ])
      .pipe(takeWhile(() => this.alive))
      .subscribe({
        next: ([map, config]: [any, any]) => {
          registerMap('algeria', map);
          const mapTheme = config.variables?.bubbleMap || {};
          
          this.options = {
            title: {
              text: 'Algeria Map',
              left: 'center',
              top: '16px',
              textStyle: {
                color: mapTheme.titleColor || '#333',
                fontSize: 16,
                fontWeight: 'bold',
              },
            },
            geo: {
              map: 'algeria',
              roam: true,
              zoom: 1.2,
              center: [2, 28],
              label: { normal: { show: false }, emphasis: { show: true, color: '#fff' } },
              itemStyle: {
                normal: { areaColor: mapTheme.areaColor || '#e2e8f0', borderColor: '#94a3b8', borderWidth: 1 },
                emphasis: { areaColor: mapTheme.areaHoverColor || '#3b82f6' }
              }
            },
            series: []
          };
        },
        error: (err) => console.error('Error loading Algeria map:', err)
      });
  }

  loadMap() {
    console.log('Loading map with zones, filteredZones length:', this.filteredZones.length);
    
    if (this.filteredZones.length === 0) {
      this.loadMapOnly();
      return;
    }

    combineLatest([
      this.http.get('assets/map/algeria.json'),
      this.theme.getJsTheme(),
    ])
      .pipe(takeWhile(() => this.alive))
      .subscribe({
        next: ([map, config]: [any, any]) => {
          registerMap('algeria', map);
          const mapTheme = config.variables?.bubbleMap || {};
          
          const polygonData: any[] = [];
          
          for (const zone of this.filteredZones) {
            if (!zone.isVisible) continue;
            
            const clippedPolys = zone.clippedCoordinates;
            
            if (clippedPolys && clippedPolys.length > 0) {
              for (let p = 0; p < clippedPolys.length; p++) {
                const clippedPoly = clippedPolys[p];
                if (clippedPoly && clippedPoly.length > 0) {
                  for (let ringIndex = 0; ringIndex < clippedPoly.length; ringIndex++) {
                    const ring = clippedPoly[ringIndex];
                    if (ring && ring.length >= 3) {
                      polygonData.push({
                        name: zone.name,
                        coords: ring,
                        projectId: zone.projectId,
                        status: zone.status,
                        statusLabel: zone.statusLabel,
                        budget: zone.budget,
                        progression: zone.progression,
                        wilaya: zone.wilaya,
                        surface: zone.surface,
                        isClipped: zone.isClipped,
                        borderColor: zone.color,
                        lineStyle: { color: zone.color, width: 2, type: 'solid' }
                      });
                    }
                  }
                }
              }
            }
          }

          const centerPoints = this.filteredZones
            .filter(zone => zone.isVisible)
            .map(zone => {
              let center = { lng: 2, lat: 28 };
              
              if (zone.clippedCoordinates && 
                  zone.clippedCoordinates.length > 0 && 
                  zone.clippedCoordinates[0] && 
                  zone.clippedCoordinates[0].length > 0) {
                const firstRing = zone.clippedCoordinates[0][0];
                if (firstRing && firstRing.length > 0) {
                  center = this.getPolygonCenter(firstRing);
                }
              } else if (zone.coordinates.length > 0) {
                center = this.getPolygonCenter(zone.coordinates);
              }
              
              return {
                name: zone.name,
                value: [center.lng, center.lat],
                projectId: zone.projectId,
                status: zone.status,
                statusLabel: zone.statusLabel,
                color: zone.color,
                budget: zone.budget,
                progression: zone.progression,
                wilaya: zone.wilaya,
                isClipped: zone.isClipped,
                itemStyle: { color: zone.color }
              };
            });

          this.options = {
            title: {
              text: this.filteredZones.length > 0 ? 
                `Project Zones (${this.filteredZones.length} projects)` : 
                'No projects match your filters',
              left: 'center',
              top: '16px',
              textStyle: {
                color: mapTheme.titleColor || '#333',
                fontSize: 16,
                fontWeight: 'bold',
              },
            },
            tooltip: {
              trigger: 'item',
              formatter: (params: any) => {
                if (params.seriesType === 'lines' && params.data) {
                  const clipWarning = params.data.isClipped ? 
                    '<span style="color: #f59e0b;">⚠️ Area clipped to wilaya boundary</span><br/>' : '';
                  return `
                    <div style="padding: 8px; min-width: 220px;">
                      <strong style="font-size: 14px;">📌 ${params.data.name}</strong><br/>
                      <strong>Status:</strong> ${params.data.statusLabel}<br/>
                      <strong>📍 Wilaya:</strong> ${params.data.wilaya}<br/>
                      <strong>🗺️ Surface:</strong> ${(params.data.surface || 0).toLocaleString()} km²<br/>
                      <strong>💰 Budget:</strong> ${(params.data.budget || 0).toLocaleString()} DA<br/>
                      <strong>📊 Progress:</strong> ${params.data.progression}%<br/>
                      ${clipWarning}
                      <i>Click for details</i>
                    </div>
                  `;
                }
                return params.name;
              },
            },
            geo: {
              map: 'algeria',
              roam: true,
              zoom: 1.2,
              center: [2, 28],
              label: { normal: { show: false }, emphasis: { show: true, color: '#fff' } },
              itemStyle: {
                normal: { areaColor: mapTheme.areaColor || '#e2e8f0', borderColor: '#94a3b8', borderWidth: 1 },
                emphasis: { areaColor: mapTheme.areaHoverColor || '#3b82f6' }
              }
            },
            series: [
              {
                name: 'Project Zones',
                type: 'lines',
                coordinateSystem: 'geo',
                data: polygonData,
                polyline: true,
                lineStyle: { width: 2, curveness: 0 },
                areaStyle: { color: (params: any) => params.data.lineStyle?.color, opacity: 0.6 },
                emphasis: { scale: true, lineStyle: { width: 3 }, areaStyle: { opacity: 0.85 } }
              },
              {
                name: 'Project Centers',
                type: 'scatter',
                coordinateSystem: 'geo',
                data: centerPoints,
                symbolSize: 10,
                symbol: 'circle',
                itemStyle: { color: (params: any) => params.data.itemStyle?.color || '#64748b', borderColor: '#fff', borderWidth: 2 },
                label: {
                  show: true,
                  position: 'top',
                  offset: [0, 10],
                  formatter: (params: any) => params.data.name.length > 18 ? params.data.name.substring(0, 16) + '...' : params.data.name,
                  fontSize: 10,
                  fontWeight: 'bold',
                  color: '#333',
                  backgroundColor: 'rgba(255,255,255,0.9)',
                  padding: [2, 8, 2, 8],
                  borderRadius: 4
                },
                emphasis: { scale: 1.3 }
              }
            ]
          };
          
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error loading Algeria map:', err);
          this.isLoading = false;
        }
      });
  }

  private getPolygonCenter(coordinates: any[]): { lat: number; lng: number } {
    if (!coordinates || coordinates.length === 0) return { lat: 28, lng: 2 };
    
    let sumLat = 0, sumLng = 0;
    for (const coord of coordinates) {
      sumLng += coord[0];
      sumLat += coord[1];
    }
    return { lng: sumLng / coordinates.length, lat: sumLat / coordinates.length };
  }

  onProjectClick(projectId: number) {
    this.router.navigate(['/pages/project-overview', projectId]);
  }

  ngOnDestroy() {
    this.alive = false;
  }
}