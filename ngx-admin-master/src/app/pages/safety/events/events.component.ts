// src/app/pages/safety/events/events.component.ts

import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, takeUntil } from 'rxjs/operators';
import { EventApiService, EventResponse, EventType, EventRequest } from '../../../services/events/event-api.service';
import { MissionService, Mission } from '../../../services/mission/mission.service';
import { AuthService } from '../../../services/auth.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-events',
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.scss']
})
export class EventsComponent implements OnInit, OnDestroy {
  
  // Data
  events: EventResponse[] = [];
  filteredEvents: EventResponse[] = [];
  eventTypes: EventType[] = [];
  missions: Mission[] = [];
  
  // UI State
  isLoading: boolean = false;
  showAddModal: boolean = false;
  showEditModal: boolean = false;
  showFilters: boolean = false;
  selectedEvent: EventResponse | null = null;
  
  // Form
  eventForm: FormGroup;
  
  currentMissionId: number = 0;
  selectedMissionId: string = 'all'; // 'all' or specific mission id

  // Filters
  searchTerm: string = '';
  statusFilter: string = 'all';
  typeFilter: string = 'all';
  sortBy: string = 'date';
  sortOrder: string = 'asc';
  
  // Pagination
  currentPage: number = 1;
  pageSize: number = 9;
  totalPages: number = 1;
  paginatedEvents: EventResponse[] = [];
  
  Math = Math;
  
  // Role-based permissions
  canManageEvents: boolean = false;
  canViewAllEvents: boolean = false; // NEW: For DIRECTEUR and ADMIN
  
  // Priority options
  priorityOptions = [
    { value: 'ELEVEE', label: 'High', color: '#ef4444' },
    { value: 'MOYENNE', label: 'Medium', color: '#f59e0b' },
    { value: 'FAIBLE', label: 'Low', color: '#10b981' }
  ];
  
  private destroy$ = new Subject<void>();
  private searchSubject = new Subject<string>();

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private eventApiService: EventApiService,
    private missionService: MissionService,
    private authService: AuthService
  ) {
    this.eventForm = this.fb.group({
      titre: ['', [Validators.required, Validators.minLength(3)]],
      description: [''],
      date: ['', Validators.required],
      heure: [''],
      missionId: [null],
      typeEvenementId: [null, Validators.required]
    });
  }

  ngOnInit(): void {
    this.initializePermissions();
    this.loadMissions();
    
    // Load events based on user role
    if (this.canViewAllEvents) {
      // DIRECTEUR or ADMIN - load all events first
      this.loadAllEvents();
    } else {
      // Regular user - load events for their current mission
      this.loadCurrentMission();
    }
    
    this.loadEventTypes();
    
    this.searchSubject.pipe(
      debounceTime(500),
      distinctUntilChanged(),
      takeUntil(this.destroy$)
    ).subscribe(() => {
      this.applyFilters();
    });
  }

  // ========== PERMISSIONS ==========
  
  initializePermissions(): void {
    const userRoles = this.authService.getCurrentUser()?.roles || [];
    const userRole = userRoles[0];
    console.log('User roles:', userRoles);
    
    // Check if user is DIRECTEUR or ADMIN
    this.canViewAllEvents = userRoles.includes('DIRECTEUR') || 
                            userRoles.includes('ADMIN') || 
                            userRoles.includes('ADMINISTRATEUR');
    
    // Only CHEF_TERRAIN, DIRECTEUR and ADMINISTRATEUR can manage events
    this.canManageEvents = userRole === 'CHEF_TERRAIN' || 
                          userRoles.includes('DIRECTEUR') ||
                          userRoles.includes('ADMIN') ||
                          userRoles.includes('ADMINISTRATEUR');
    
    console.log('Can view all events:', this.canViewAllEvents);
    console.log('Can manage events:', this.canManageEvents);
  }

  // ========== LOAD CURRENT MISSION (for regular users) ==========

  loadCurrentMission(): void {
    this.isLoading = true;
    this.missionService.getMyCurrentMission()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (missionResponse) => {
          if (missionResponse && missionResponse.missionId) {
            this.currentMissionId = missionResponse.missionId;
            this.selectedMissionId = this.currentMissionId.toString();
            console.log('Current mission ID:', this.currentMissionId);
            this.loadEventsByMission(this.currentMissionId);
          } else {
            console.warn('No current mission found');
            this.isLoading = false;
            this.events = [];
            this.filteredEvents = [];
          }
        },
        error: (err) => {
          console.error('Error loading current mission:', err);
          this.isLoading = false;
          Swal.fire('Error', 'Failed to load current mission', 'error');
        }
      });
  }

  // ========== LOAD ALL EVENTS (for DIRECTEUR and ADMIN) ==========

  loadAllEvents(): void {
    this.isLoading = true;
    this.eventApiService.getAllEvents()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (events) => {
          this.events = events;
          this.applyFilters();
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error loading all events:', err);
          this.isLoading = false;
          Swal.fire('Error', 'Failed to load events', 'error');
        }
      });
  }

  // ========== LOAD EVENTS BY SPECIFIC MISSION ==========

  loadEventsByMission(missionId: number): void {
    this.isLoading = true;
    this.eventApiService.getEventsByMission(missionId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (events) => {
          this.events = events;
          this.applyFilters();
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error loading events for mission:', err);
          this.isLoading = false;
          Swal.fire('Error', 'Failed to load events', 'error');
        }
      });
  }

  // ========== LOAD ALL DATA ==========
  
  loadEvents(): void {
    if (this.canViewAllEvents && this.selectedMissionId === 'all') {
      this.loadAllEvents();
    } else if (this.selectedMissionId !== 'all') {
      this.loadEventsByMission(parseInt(this.selectedMissionId));
    } else if (this.currentMissionId) {
      this.loadEventsByMission(this.currentMissionId);
    } else {
      this.loadAllEvents();
    }
  }

  // NEW: Handle mission filter change
  onMissionFilterChange(): void {
    if (this.selectedMissionId === 'all') {
      this.loadAllEvents();
    } else {
      this.loadEventsByMission(parseInt(this.selectedMissionId));
    }
  }

  loadEventTypes(): void {
    this.eventApiService.getActiveEventTypes().subscribe({
      next: (types) => {
        this.eventTypes = types;
      },
      error: (err) => {
        console.error('Error loading event types:', err);
      }
    });
  }

  loadMissions(): void {
    this.missionService.getAllMissions().subscribe({
      next: (missions) => {
        this.missions = missions;
      },
      error: (err) => {
        console.error('Error loading missions:', err);
      }
    });
  }

  refreshEventsForMission(missionId: number): void {
    this.currentMissionId = missionId;
    this.loadEvents();
  }

  // ========== NAVIGATION ==========
  
  goToEventTypes(): void {
    if (!this.canManageEvents) {
      Swal.fire('Access Denied', 'You do not have permission to manage event types', 'warning');
      return;
    }
    this.router.navigate(['/pages/safety/events/types']);
  }

  // ========== FILTERS & SORTING ==========

  applyFilters(): void {
    let filtered = [...this.events];
    
    // Search filter
    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase().trim();
      filtered = filtered.filter(event =>
        event.titre.toLowerCase().includes(term) ||
        (event.description && event.description.toLowerCase().includes(term)) ||
        (event.missionNom && event.missionNom.toLowerCase().includes(term))
      );
    }
    
    // Status filter (upcoming, today, past)
    if (this.statusFilter !== 'all') {
      filtered = filtered.filter(event => {
        if (this.statusFilter === 'upcoming') return event.isUpcoming;
        if (this.statusFilter === 'today') return event.isToday;
        if (this.statusFilter === 'past') return event.isPast;
        return true;
      });
    }
    
    // Type filter
    if (this.typeFilter !== 'all') {
      filtered = filtered.filter(event => 
        event.typeEvenementId?.toString() === this.typeFilter
      );
    }
    
    // Sorting
    filtered = this.sortEvents(filtered);
    
    this.filteredEvents = filtered;
    this.currentPage = 1;
    this.updatePagination();
  }

  sortEvents(events: EventResponse[]): EventResponse[] {
    return events.sort((a, b) => {
      let comparison = 0;
      
      switch (this.sortBy) {
        case 'titre':
          comparison = a.titre.localeCompare(b.titre);
          break;
        case 'date':
          comparison = new Date(a.date).getTime() - new Date(b.date).getTime();
          break;
        case 'priority':
          const priorityOrder = { 'ELEVEE': 3, 'MOYENNE': 2, 'FAIBLE': 1 };
          const priorityA = priorityOrder[a.niveauPriorite as keyof typeof priorityOrder] || 0;
          const priorityB = priorityOrder[b.niveauPriorite as keyof typeof priorityOrder] || 0;
          comparison = priorityA - priorityB;
          break;
        case 'type':
          comparison = (a.typeEvenementNom || '').localeCompare(b.typeEvenementNom || '');
          break;
        default:
          comparison = 0;
      }
      
      return this.sortOrder === 'asc' ? comparison : -comparison;
    });
  }

  onSearchInput(): void {
    this.searchSubject.next(this.searchTerm);
  }

  clearSearch(): void {
    this.searchTerm = '';
    this.applyFilters();
  }

  resetFilters(): void {
    this.searchTerm = '';
    this.statusFilter = 'all';
    this.typeFilter = 'all';
    this.sortBy = 'date';
    this.sortOrder = 'asc';
    this.applyFilters();
  }

  isFilterApplied(): boolean {
    return this.searchTerm !== '' ||
           this.statusFilter !== 'all' ||
           this.typeFilter !== 'all' ||
           this.sortBy !== 'date' ||
           this.sortOrder !== 'asc';
  }

  toggleFilters(): void {
    this.showFilters = !this.showFilters;
  }

  // ========== PAGINATION ==========

  updatePagination(): void {
    this.totalPages = Math.ceil(this.filteredEvents.length / this.pageSize);
    if (this.totalPages === 0) this.totalPages = 1;
    
    if (this.currentPage > this.totalPages) {
      this.currentPage = this.totalPages;
    }
    
    const startIndex = (this.currentPage - 1) * this.pageSize;
    this.paginatedEvents = this.filteredEvents.slice(startIndex, startIndex + this.pageSize);
  }

  previousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.updatePagination();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.updatePagination();
    }
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.updatePagination();
    }
  }

  getPageNumbers(): number[] {
    const pages: number[] = [];
    const maxVisible = 5;
    let startPage = Math.max(1, this.currentPage - Math.floor(maxVisible / 2));
    let endPage = Math.min(this.totalPages, startPage + maxVisible - 1);
    
    if (endPage - startPage + 1 < maxVisible) {
      startPage = Math.max(1, endPage - maxVisible + 1);
    }
    
    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }
    return pages;
  }

  // ========== MODAL CONTROLS ==========

  openAddModal(): void {
    if (!this.canManageEvents) {
      Swal.fire('Access Denied', 'You do not have permission to create events', 'warning');
      return;
    }
    this.eventForm.reset({
      titre: '',
      description: '',
      date: new Date().toISOString().split('T')[0],
      heure: '',
      missionId: null,
      typeEvenementId: null
    });
    this.showAddModal = true;
  }

  closeAddModal(): void {
    this.showAddModal = false;
  }

  openEditModal(event: EventResponse): void {
    if (!this.canManageEvents) {
      Swal.fire('Access Denied', 'You do not have permission to edit events', 'warning');
      return;
    }
    this.selectedEvent = event;
    this.eventForm.patchValue({
      titre: event.titre,
      description: event.description,
      date: event.date,
      heure: event.heure,
      missionId: event.missionId,
      typeEvenementId: event.typeEvenementId
    });
    this.showEditModal = true;
  }

  closeEditModal(): void {
    this.showEditModal = false;
    this.selectedEvent = null;
  }

  // ========== CRUD OPERATIONS ==========

  createEvent(): void {
    if (!this.canManageEvents) {
      Swal.fire('Access Denied', 'You do not have permission to create events', 'warning');
      return;
    }
    
    if (this.eventForm.invalid) {
      Swal.fire('Warning', 'Please fill all required fields', 'warning');
      return;
    }
    
    const eventRequest: EventRequest = this.eventForm.value;
    
    this.isLoading = true;
    this.eventApiService.createEvent(eventRequest).subscribe({
      next: () => {
        Swal.fire('Success', 'Event created successfully', 'success');
        this.closeAddModal();
        this.loadEvents();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error creating event:', err);
        this.isLoading = false;
        Swal.fire('Error', 'Failed to create event', 'error');
      }
    });
  }

  updateEvent(): void {
    if (!this.canManageEvents) {
      Swal.fire('Access Denied', 'You do not have permission to update events', 'warning');
      return;
    }
    
    if (this.eventForm.invalid || !this.selectedEvent) {
      Swal.fire('Warning', 'Please fill all required fields', 'warning');
      return;
    }
    
    const eventRequest: EventRequest = this.eventForm.value;
    
    this.isLoading = true;
    this.eventApiService.updateEvent(this.selectedEvent.id, eventRequest).subscribe({
      next: () => {
        Swal.fire('Success', 'Event updated successfully', 'success');
        this.closeEditModal();
        this.loadEvents();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error updating event:', err);
        this.isLoading = false;
        Swal.fire('Error', 'Failed to update event', 'error');
      }
    });
  }

  deleteEvent(event: EventResponse): void {
    if (!this.canManageEvents) {
      Swal.fire('Access Denied', 'You do not have permission to delete events', 'warning');
      return;
    }
    
    Swal.fire({
      title: 'Delete Event?',
      text: `Are you sure you want to delete "${event.titre}"?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      confirmButtonText: 'Yes, Delete',
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        this.isLoading = true;
        this.eventApiService.deleteEvent(event.id).subscribe({
          next: () => {
            Swal.fire('Deleted', 'Event deleted successfully', 'success');
            this.loadEvents();
            this.isLoading = false;
          },
          error: (err) => {
            console.error('Error deleting event:', err);
            this.isLoading = false;
            Swal.fire('Error', 'Failed to delete event', 'error');
          }
        });
      }
    });
  }

  // ========== HELPER METHODS ==========

  getPriorityColor(priority: string): string {
    const option = this.priorityOptions.find(p => p.value === priority);
    return option?.color || '#6b7280';
  }

  getPriorityLabel(priority: string): string {
    const option = this.priorityOptions.find(p => p.value === priority);
    return option?.label || priority;
  }

  getStatusBadgeClass(event: EventResponse): string {
    if (event.isToday) return 'today';
    if (event.isUpcoming) return 'upcoming';
    if (event.isPast) return 'past';
    return '';
  }

  getStatusLabel(event: EventResponse): string {
    if (event.isToday) return 'Today';
    if (event.isUpcoming) return 'Upcoming';
    if (event.isPast) return 'Past';
    return '';
  }

  formatDate(date: string): string {
    if (!date) return 'N/A';
    return new Date(date).toLocaleDateString('en-US', {
      day: '2-digit',
      month: 'long',
      year: 'numeric'
    });
  }

  formatTime(time: string): string {
    if (!time) return '';
    return time.substring(0, 5);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}