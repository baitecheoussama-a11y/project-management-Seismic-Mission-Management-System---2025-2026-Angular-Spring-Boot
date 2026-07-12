// pages/attendance/attendance.component.ts

import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NbToastrService } from '@nebular/theme';
import { PointageService, Pointage, PointageStats } from '../../../services/pointage/pointage.service';
import { EmployeService, Employe } from '../../../services/employes/employe.service';
import Swal from 'sweetalert2';

// Extend Pointage interface to include additional fields
interface ExtendedPointage extends Pointage {
  employeEmail?: string;
  fonctionNom?: string;
  selected?: boolean;
}

@Component({
  selector: 'ngx-attendance',
  templateUrl: './attendance.component.html',
  styleUrls: ['./attendance.component.scss']
})
export class AttendanceComponent implements OnInit, OnDestroy {

  selectedDate: Date = new Date();
  pointages: ExtendedPointage[] = [];
  filteredPointages: ExtendedPointage[] = [];
  paginatedPointages: ExtendedPointage[] = [];
  allEmployees: Employe[] = [];
  stats: PointageStats = {
    totalEmployees: 0,
    present: 0,
    absent: 0,
    late: 0,
    onLeave: 0,
    onMission: 0,
    notRecorded: 0
  };

  statusFilter: string = 'all';
  searchTerm: string = '';
  showEditModal: boolean = false;
  editingPointage: ExtendedPointage | null = null;

  // Selection properties
  selectedItems: Set<number> = new Set();
  selectedCount: number = 0;
  bulkStatus: string = '';

  // Pagination properties
  currentPage: number = 1;
  pageSize: number = 10;
  totalPages: number = 1;
  Math = Math;

  private destroy$ = new Subject<void>();

  constructor(
    private pointageService: PointageService,
    private employeService: EmployeService,
    private toastrService: NbToastrService
  ) {}

  ngOnInit(): void {
    this.loadAllEmployees();
  }

  // ============ LOAD DATA ============

  loadAllEmployees(): void {
    this.employeService.getAllEmployes().subscribe({
      next: (employees) => {
        this.allEmployees = employees;
        this.stats.totalEmployees = employees.length;
        this.loadAttendance();
      },
      error: (err) => {
        console.error('Error loading employees:', err);
        this.toastrService.danger('Failed to load employees', 'Error');
      }
    });
  }

  loadAttendance(): void {
    const dateStr = this.formatDate(this.selectedDate);
    this.pointageService.getPointagesByDate(dateStr).subscribe({
      next: (data) => {
        this.pointages = this.mergeWithEmployees(data);
        this.loadStats(dateStr);
        this.applyFilters();
      },
      error: (err) => {
        console.error('Error loading attendance:', err);
        this.toastrService.danger('Failed to load attendance data', 'Error');
      }
    });
  }

  // ============ MERGE DATA ============

  mergeWithEmployees(pointages: Pointage[]): ExtendedPointage[] {
    const pointageMap = new Map<number, Pointage>();
    pointages.forEach(p => {
      if (p.employeId) {
        pointageMap.set(p.employeId, p);
      }
    });

    const merged: ExtendedPointage[] = [];
    this.allEmployees.forEach(emp => {
      const existing = pointageMap.get(emp.id!);
      if (existing) {
        merged.push({
          ...existing,
          employeNom: emp.nom,
          employePrenom: emp.prenom,
          employeEmail: emp.email,
          fonctionNom: emp.fonctionNom,
          statusLabel: this.getStatusLabel(existing.status),
          statusColor: this.getStatusColor(existing.status),
          selected: false
        });
      } else {
        merged.push({
          id: undefined,
          datePointage: this.formatDate(this.selectedDate),
          status: 'NOT_RECORDED',
          motifAbsent: '',
          remarque: '',
          employeId: emp.id!,
          employeNom: emp.nom,
          employePrenom: emp.prenom,
          employeEmail: emp.email,
          fonctionNom: emp.fonctionNom,
          statusLabel: 'Not Recorded',
          statusColor: '#94a3b8',
          selected: false
        });
      }
    });

    return merged;
  }

  loadStats(date: string): void {
    this.pointageService.getStatsByDate(date).subscribe({
      next: (data) => {
        this.stats = data;
        this.stats.totalEmployees = this.allEmployees.length;
      },
      error: (err) => {
        console.error('Error loading stats:', err);
      }
    });
  }

  // ============ DATE NAVIGATION ============

  previousDay(): void {
    this.selectedDate.setDate(this.selectedDate.getDate() - 1);
    this.selectedItems.clear();
    this.selectedCount = 0;
    this.loadAttendance();
  }

  nextDay(): void {
    this.selectedDate.setDate(this.selectedDate.getDate() + 1);
    this.selectedItems.clear();
    this.selectedCount = 0;
    this.loadAttendance();
  }

  goToToday(): void {
    this.selectedDate = new Date();
    this.selectedItems.clear();
    this.selectedCount = 0;
    this.loadAttendance();
  }

  isToday(): boolean {
    const today = new Date();
    return this.selectedDate.toDateString() === today.toDateString();
  }

  formatDate(date: Date): string {
    return date.toISOString().split('T')[0];
  }

  // ============ FILTERS ============

  applyFilters(): void {
    let filtered = [...this.pointages];

    if (this.statusFilter !== 'all') {
      if (this.statusFilter === 'NOT_RECORDED') {
        filtered = filtered.filter(p => p.status === 'NOT_RECORDED' || !p.status);
      } else {
        filtered = filtered.filter(p => p.status === this.statusFilter);
      }
    }

    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase().trim();
      filtered = filtered.filter(p =>
        p.employeNom?.toLowerCase().includes(term) ||
        p.employePrenom?.toLowerCase().includes(term) ||
        (p.employeNom + ' ' + p.employePrenom)?.toLowerCase().includes(term) ||
        (p as ExtendedPointage).fonctionNom?.toLowerCase().includes(term)
      );
    }

    this.filteredPointages = filtered;
    this.selectedItems.clear();
    this.selectedCount = 0;
    this.bulkStatus = '';
    this.currentPage = 1;
    this.updatePagination();
  }

  // ============ PAGINATION ============

  updatePagination(): void {
    this.totalPages = Math.ceil(this.filteredPointages.length / this.pageSize);
    if (this.totalPages === 0) this.totalPages = 1;
    if (this.currentPage > this.totalPages) {
      this.currentPage = this.totalPages;
    }
    const startIndex = (this.currentPage - 1) * this.pageSize;
    this.paginatedPointages = this.filteredPointages.slice(startIndex, startIndex + this.pageSize);
  }

  previousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.selectedItems.clear();
      this.selectedCount = 0;
      this.bulkStatus = '';
      this.updatePagination();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.selectedItems.clear();
      this.selectedCount = 0;
      this.bulkStatus = '';
      this.updatePagination();
    }
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.selectedItems.clear();
      this.selectedCount = 0;
      this.bulkStatus = '';
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

  // ============ SELECTION ============

  toggleSelection(item: ExtendedPointage): void {
    const id = item.employeId || item.id || 0;
    if (this.selectedItems.has(id)) {
      this.selectedItems.delete(id);
    } else {
      this.selectedItems.add(id);
    }
    this.selectedCount = this.selectedItems.size;
    this.bulkStatus = '';
  }

  isSelected(item: ExtendedPointage): boolean {
    const id = item.employeId || item.id || 0;
    return this.selectedItems.has(id);
  }

  isAllSelected(): boolean {
    return this.paginatedPointages.length > 0 && 
           this.paginatedPointages.every(item => this.isSelected(item));
  }

  isSomeSelected(): boolean {
    const selected = this.paginatedPointages.filter(item => this.isSelected(item)).length;
    return selected > 0 && selected < this.paginatedPointages.length;
  }

  toggleSelectAll(): void {
    if (this.isAllSelected()) {
      this.paginatedPointages.forEach(item => {
        const id = item.employeId || item.id || 0;
        this.selectedItems.delete(id);
      });
    } else {
      this.paginatedPointages.forEach(item => {
        const id = item.employeId || item.id || 0;
        this.selectedItems.add(id);
      });
    }
    this.selectedCount = this.selectedItems.size;
    this.bulkStatus = '';
  }

  // ============ BULK UPDATE ============

  applyBulkUpdate(): void {
    if (!this.bulkStatus || this.selectedCount === 0) return;

    const selectedPointages = this.filteredPointages.filter(item => {
      const id = item.employeId || item.id || 0;
      return this.selectedItems.has(id);
    });

    Swal.fire({
      title: 'Bulk Update',
      text: `Update ${this.selectedCount} employee(s) to "${this.getStatusLabel(this.bulkStatus)}"?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Yes, Update',
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        let completed = 0;
        let errors = 0;

        selectedPointages.forEach(item => {
          const request = {
            employeId: item.employeId,
            datePointage: item.datePointage,
            status: this.bulkStatus,
            motifAbsent: item.motifAbsent || '',
            remarque: item.remarque || ''
          };

          if (item.id) {
            // Update existing
            this.pointageService.updatePointage(item.id, request).subscribe({
              next: () => {
                completed++;
                if (completed + errors === selectedPointages.length) {
                  this.handleBulkComplete(completed, errors);
                }
              },
              error: (err) => {
                errors++;
                console.error('Error updating pointage:', err);
                if (completed + errors === selectedPointages.length) {
                  this.handleBulkComplete(completed, errors);
                }
              }
            });
          } else {
            // Create new
            this.pointageService.createPointage(request).subscribe({
              next: () => {
                completed++;
                if (completed + errors === selectedPointages.length) {
                  this.handleBulkComplete(completed, errors);
                }
              },
              error: (err) => {
                errors++;
                console.error('Error creating pointage:', err);
                if (completed + errors === selectedPointages.length) {
                  this.handleBulkComplete(completed, errors);
                }
              }
            });
          }
        });
      } else {
        this.bulkStatus = '';
      }
    });
  }

  handleBulkComplete(completed: number, errors: number): void {
    if (errors === 0) {
      this.toastrService.success(`${completed} employee(s) updated successfully`, 'Success');
    } else {
      this.toastrService.warning(`${completed} updated, ${errors} failed`, 'Partial Success');
    }
    this.selectedItems.clear();
    this.selectedCount = 0;
    this.bulkStatus = '';
    this.loadAttendance();
  }

  // ============ STATUS HELPERS ============

  getStatusColor(status: string): string {
    const colors: { [key: string]: string } = {
      'PRESENT': '#10b981',
      'ABSENT': '#ef4444',
      'RETARD': '#f59e0b',
      'CONGE': '#3b82f6',
      'MISSION': '#8b5cf6',
      'NOT_RECORDED': '#94a3b8'
    };
    return colors[status] || '#94a3b8';
  }

  getStatusLabel(status: string): string {
    const labels: { [key: string]: string } = {
      'PRESENT': 'Present',
      'ABSENT': 'Absent',
      'RETARD': 'Late',
      'CONGE': 'On Leave',
      'MISSION': 'On Mission',
      'NOT_RECORDED': 'Not Recorded'
    };
    return labels[status] || 'Not Recorded';
  }

  getInitials(item: ExtendedPointage): string {
    const firstName = item.employePrenom || '';
    const lastName = item.employeNom || '';
    return (firstName.charAt(0) + lastName.charAt(0)).toUpperCase() || '?';
  }

  getAvatarColor(item: ExtendedPointage): string {
    const colors = ['#3b82f6', '#10b981', '#f59e0b', '#8b5cf6', '#ef4444', '#06b6d4', '#ec489a'];
    const index = (item.employeId || 0) % colors.length;
    return colors[index];
  }

  // ============ MARK ALL PRESENT ============

  markAllPresent(): void {
    const dateStr = this.formatDate(this.selectedDate);
    Swal.fire({
      title: 'Mark All Present?',
      text: `This will mark all employees as PRESENT for ${dateStr}.`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Yes, Mark All',
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        this.pointageService.markAllPresent(dateStr).subscribe({
          next: () => {
            this.toastrService.success('All employees marked as present', 'Success');
            this.selectedItems.clear();
            this.selectedCount = 0;
            this.loadAttendance();
          },
          error: (err) => {
            console.error('Error marking all present:', err);
            this.toastrService.danger('Failed to mark all present', 'Error');
          }
        });
      }
    });
  }

  // ============ EDIT POINTAGE ============

  editPointage(pointage: ExtendedPointage): void {
    if (pointage.status === 'NOT_RECORDED' || !pointage.id) {
      this.editingPointage = {
        ...pointage,
        status: 'PRESENT',
        statusLabel: 'Present',
        statusColor: '#10b981'
      };
    } else {
      this.editingPointage = { ...pointage };
    }
    this.showEditModal = true;
  }

  closeEditModal(): void {
    this.showEditModal = false;
    this.editingPointage = null;
  }

  saveEdit(): void {
    if (!this.editingPointage) return;

    const request = {
      employeId: this.editingPointage.employeId,
      datePointage: this.editingPointage.datePointage,
      status: this.editingPointage.status,
      motifAbsent: this.editingPointage.motifAbsent || '',
      remarque: this.editingPointage.remarque || ''
    };

    if (this.editingPointage.id) {
      this.pointageService.updatePointage(this.editingPointage.id, request).subscribe({
        next: () => {
          this.toastrService.success('Attendance updated successfully', 'Success');
          this.closeEditModal();
          this.selectedItems.clear();
          this.selectedCount = 0;
          this.loadAttendance();
        },
        error: (err) => {
          console.error('Error updating attendance:', err);
          this.toastrService.danger('Failed to update attendance', 'Error');
        }
      });
    } else {
      this.pointageService.createPointage(request).subscribe({
        next: () => {
          this.toastrService.success('Attendance added successfully', 'Success');
          this.closeEditModal();
          this.selectedItems.clear();
          this.selectedCount = 0;
          this.loadAttendance();
        },
        error: (err) => {
          console.error('Error adding attendance:', err);
          this.toastrService.danger('Failed to add attendance', 'Error');
        }
      });
    }
  }

  // ============ DELETE POINTAGE ============

  deletePointage(pointage: ExtendedPointage): void {
    if (!pointage.id) {
      this.toastrService.warning('Cannot delete a non-existing record', 'Warning');
      return;
    }

    Swal.fire({
      title: 'Delete Attendance Record?',
      text: `Are you sure you want to delete this attendance record for ${pointage.employePrenom} ${pointage.employeNom}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      confirmButtonText: 'Yes, Delete',
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        this.pointageService.deletePointage(pointage.id!).subscribe({
          next: () => {
            this.toastrService.success('Attendance record deleted', 'Success');
            this.selectedItems.clear();
            this.selectedCount = 0;
            this.loadAttendance();
          },
          error: (err) => {
            console.error('Error deleting attendance:', err);
            this.toastrService.danger('Failed to delete attendance', 'Error');
          }
        });
      }
    });
  }

  // ============ LIFECYCLE ============

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}