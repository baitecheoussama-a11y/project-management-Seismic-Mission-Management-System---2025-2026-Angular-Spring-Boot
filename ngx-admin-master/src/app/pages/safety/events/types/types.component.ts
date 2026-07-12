// src/app/pages/safety/events/types/types.component.ts

import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { EventApiService, EventType, EventTypeRequest } from '../../../../services/events/event-api.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-event-types',
  templateUrl: './types.component.html',
  styleUrls: ['./types.component.scss']
})
export class TypesComponent implements OnInit {
  
  eventTypes: EventType[] = [];
  filteredTypes: EventType[] = [];
  isLoading: boolean = false;
  showAddModal: boolean = false;
  showEditModal: boolean = false;
  selectedType: EventType | null = null;
  
  searchTerm: string = '';
  statusFilter: string = 'all';
  
  typeForm: FormGroup;
  
  priorityOptions = [
    { value: 'ELEVEE', label: 'High', color: '#ef4444' },
    { value: 'MOYENNE', label: 'Medium', color: '#f59e0b' },
    { value: 'FAIBLE', label: 'Low', color: '#10b981' }
  ];

  constructor(
    private fb: FormBuilder,
    private location: Location,
    private eventApiService: EventApiService
  ) {
    this.typeForm = this.fb.group({
      nom: ['', [Validators.required, Validators.minLength(2)]],
      description: [''],
      niveauPriorite: ['MOYENNE', Validators.required],
      actif: [true]
    });
  }

  ngOnInit(): void {
    this.loadEventTypes();
  }

  goBack(): void {
    this.location.back();
  }

  loadEventTypes(): void {
    this.isLoading = true;
    this.eventApiService.getAllEventTypes().subscribe({
      next: (types) => {
        this.eventTypes = types;
        this.applyFilters();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading event types:', err);
        this.isLoading = false;
        Swal.fire('Error', 'Failed to load event types', 'error');
      }
    });
  }

  applyFilters(): void {
    let filtered = [...this.eventTypes];
    
    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(type =>
        type.nom.toLowerCase().includes(term) ||
        (type.description && type.description.toLowerCase().includes(term))
      );
    }
    
    if (this.statusFilter !== 'all') {
      filtered = filtered.filter(type =>
        this.statusFilter === 'active' ? type.actif : !type.actif
      );
    }
    
    this.filteredTypes = filtered;
  }

  clearSearch(): void {
    this.searchTerm = '';
    this.applyFilters();
  }

  openAddModal(): void {
    this.typeForm.reset({
      nom: '',
      description: '',
      niveauPriorite: 'MOYENNE',
      actif: true
    });
    this.showAddModal = true;
  }

  closeAddModal(): void {
    this.showAddModal = false;
  }

  openEditModal(type: EventType): void {
    this.selectedType = type;
    this.typeForm.patchValue({
      nom: type.nom,
      description: type.description,
      niveauPriorite: type.niveauPriorite,
      actif: type.actif
    });
    this.showEditModal = true;
  }

  closeEditModal(): void {
    this.showEditModal = false;
    this.selectedType = null;
  }

  createType(): void {
    if (this.typeForm.invalid) {
      Swal.fire('Warning', 'Please fill all required fields', 'warning');
      return;
    }
    
    const typeRequest: EventTypeRequest = this.typeForm.value;
    
    this.isLoading = true;
    this.eventApiService.createEventType(typeRequest).subscribe({
      next: () => {
        Swal.fire('Success', 'Event type created successfully', 'success');
        this.closeAddModal();
        this.loadEventTypes();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error creating event type:', err);
        this.isLoading = false;
        Swal.fire('Error', 'Failed to create event type', 'error');
      }
    });
  }

  updateType(): void {
    if (this.typeForm.invalid || !this.selectedType) {
      Swal.fire('Warning', 'Please fill all required fields', 'warning');
      return;
    }
    
    const typeRequest: EventTypeRequest = this.typeForm.value;
    
    this.isLoading = true;
    this.eventApiService.updateEventType(this.selectedType.id, typeRequest).subscribe({
      next: () => {
        Swal.fire('Success', 'Event type updated successfully', 'success');
        this.closeEditModal();
        this.loadEventTypes();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error updating event type:', err);
        this.isLoading = false;
        Swal.fire('Error', 'Failed to update event type', 'error');
      }
    });
  }

 // Add this method to your TypesComponent class
toggleStatus(type: EventType): void {
  Swal.fire({
    title: `${type.actif ? 'Deactivate' : 'Activate'} Type?`,
    text: `Are you sure you want to ${type.actif ? 'deactivate' : 'activate'} "${type.nom}"?`,
    icon: 'question',
    showCancelButton: true,
    confirmButtonColor: type.actif ? '#ef4444' : '#10b981',
    confirmButtonText: `Yes, ${type.actif ? 'Deactivate' : 'Activate'}`,
    cancelButtonText: 'Cancel'
  }).then((result) => {
    if (result.isConfirmed) {
      this.isLoading = true;
      this.eventApiService.toggleEventTypeStatus(type.id).subscribe({
        next: (updatedType) => {
          // Update the specific type in the local array
          const index = this.eventTypes.findIndex(t => t.id === type.id);
          if (index !== -1) {
            this.eventTypes[index] = updatedType;
          }
          this.applyFilters(); // Refresh the filtered list
          this.isLoading = false;
          Swal.fire('Success', `Type ${updatedType.actif ? 'activated' : 'deactivated'} successfully`, 'success');
        },
        error: (err) => {
          console.error('Error toggling status:', err);
          this.isLoading = false;
          Swal.fire('Error', 'Failed to update status. Please try again.', 'error');
        }
      });
    }
  });
}

  deleteType(type: EventType): void {
    Swal.fire({
      title: 'Delete Type?',
      text: `Are you sure you want to delete "${type.nom}"? This will affect all events of this type.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      confirmButtonText: 'Yes, Delete',
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        this.isLoading = true;
        this.eventApiService.deleteEventType(type.id).subscribe({
          next: () => {
            Swal.fire('Deleted', 'Event type deleted successfully', 'success');
            this.loadEventTypes();
            this.isLoading = false;
          },
          error: (err) => {
            console.error('Error deleting event type:', err);
            this.isLoading = false;
            Swal.fire('Error', 'Failed to delete event type', 'error');
          }
        });
      }
    });
  }

getPriorityLabel(priority: string): string {
 
  const option = this.priorityOptions.find(p => p.value === priority);
  return option?.label || priority;
}

getPriorityClass(priority: string): string {
  switch(priority) {
    case 'ELEVEE': return 'priority-high';
    case 'MOYENNE': return 'priority-medium';
    case 'FAIBLE': return 'priority-low';
    default: return '';
  }
}

  getPriorityColor(priority: string): string {
    const option = this.priorityOptions.find(p => p.value === priority);
    return option?.color || '#6b7280';
  }
}