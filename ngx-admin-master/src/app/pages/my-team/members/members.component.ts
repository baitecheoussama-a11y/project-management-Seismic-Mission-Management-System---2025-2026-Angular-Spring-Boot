// pages/my-team/members/members.component.ts
import { Component, OnInit, OnDestroy, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil, debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { MissionService, EquipeDTO, EmployeDTO, EquipeRequest, MissionTeamDTO } from '../../../services/mission/mission.service';
import { EquipeDetailService } from '../../../services/team/equipe-detail.service';
import { AuthService } from '../../../services/auth.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-members',
  templateUrl: './members.component.html',
  styleUrls: ['./members.component.scss']
})
export class MembersComponent implements OnInit, OnDestroy, AfterViewInit {
  
  @ViewChild('teamNameInput') teamNameInput!: ElementRef;
  @ViewChild('editTeamInput') editTeamInput!: ElementRef;

  // Team data
  equipes: EquipeDTO[] = [];
  selectedEquipe: EquipeDTO | null = null;
  teamMembers: EmployeDTO[] = [];
  filteredMembers: EmployeDTO[] = [];
  
  // Available members for assignment (ONLY mission members)
  availableMembersForAssignment: EmployeDTO[] = [];
  selectedMembersForTeam: EmployeDTO[] = [];
  
  // Mission Team Data
  missionTeam: MissionTeamDTO | null = null;
  
  teamSearchTerm: string = '';
  filteredEquipes: EquipeDTO[] = [];

  // UI States
  isLoading: boolean = false;
  isMobile: boolean = false;
  sidebarCollapsed: boolean = false;
  
  // Team CRUD States
  isAddingTeam: boolean = false;
  newTeamName: string = '';
  newTeamType: string = 'TOPOGRAPHIE';
  editingTeamId: number | null = null;
  editingTeamName: string = '';
  editingTeamType: string = '';
  
  // Modal States
  showAddMemberModal: boolean = false;
  
  // Member Filter for Modal (like mission-overview)
  selectedMemberFilter: 'all' | 'assigned' | 'not-assigned' = 'all';
  
  // Search and Filters
  searchTerm: string = '';
  memberSearchTerm: string = '';
  filteredAvailableMembersList: EmployeDTO[] = [];
  
  // Pagination for main list
  currentPage: number = 1;
  itemsPerPage: number = 12;
  totalPages: number = 1;
  paginatedMembers: EmployeDTO[] = [];
  
  // Pagination for modal
  modalCurrentPage: number = 1;
  modalItemsPerPage: number = 10;
  modalTotalPages: number = 1;
  paginatedAvailableMembers: EmployeDTO[] = [];
  
  // Role-based access flags
  canManageTeams: boolean = false;
  canManageMembers: boolean = false;
  
  // Current mission ID
  currentMissionId: number = 0;
  
  // Type options
  typeOptions = [
    { value: 'TOPOGRAPHIE', label: 'Topography', icon: 'fas fa-map' },
    { value: 'LAYONNAGE', label: 'Layout', icon: 'fas fa-draw-polygon' },
    { value: 'ENERGISREMENT', label: 'Energization', icon: 'fas fa-bolt' },
    { value: 'POSE', label: 'Installation', icon: 'fas fa-tools' },
    { value: 'RAMASSAGE', label: 'Collection', icon: 'fas fa-truck' }
  ];
  
  private destroy$ = new Subject<void>();
  private searchSubject = new Subject<string>();
  
  constructor(
    private router: Router,
    private missionService: MissionService,
    private equipeDetailService: EquipeDetailService,
    private authService: AuthService
  ) {}
  
  ngOnInit(): void {
    this.loadCurrentMissionAndTeams();
    this.checkScreenSize();
    window.addEventListener('resize', this.handleResize.bind(this));
    
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      takeUntil(this.destroy$)
    ).subscribe(() => {
      this.filterMembers();
    });
    
    this.initializePermissions();
  }
  
  ngAfterViewInit(): void {
    // ViewChild initialization
  }
  
  loadCurrentMissionAndTeams(): void {
    this.isLoading = true;
    this.missionService.getMyCurrentMission()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.currentMissionId = response.missionId;
          this.loadMissionTeamData();
        },
        error: (err) => {
          console.error('Error loading current mission:', err);
          this.isLoading = false;
          Swal.fire({
            icon: 'error',
            title: 'Error',
            text: 'Failed to load current mission'
          });
        }
      });
  }
  
loadMissionTeamData(): void {
  this.missionService.getMissionTeam(this.currentMissionId)
    .pipe(takeUntil(this.destroy$))
    .subscribe({
      next: (missionTeam) => {
        this.missionTeam = missionTeam;
        // Calculate member counts for each equipe based on current mission
        this.equipes = missionTeam.equipes.map(equipe => ({
          ...equipe,
          memberCount: missionTeam.membersByEquipe?.[equipe.nom]?.length || 0
        }));
        this.filteredEquipes = [...this.equipes];
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading mission team:', err);
        this.isLoading = false;
      }
    });
}

viewEmployeeProfile(employeeId: number, firstName: string, lastName: string): void {
  const employeeName = `${firstName} ${lastName}`;
  this.router.navigate(['/pages/employe-account'], {
    queryParams: {
      employeId: employeeId,
      employeName: employeeName
    }
  });
}
  
  initializePermissions(): void {
    if (this.authService.hasRole('GESTIONNAIRE')) {
      this.canManageTeams = true;
      this.canManageMembers = true;
    } 
    else if (this.authService.hasRole('ADMIN')) {
      this.canManageTeams = true;
      this.canManageMembers = true;
    }
    else {
      this.canManageTeams = false;
      this.canManageMembers = false;
    }
    
    console.log('[DEBUG] Permissions - Manage Teams:', this.canManageTeams);
    console.log('[DEBUG] Permissions - Manage Members:', this.canManageMembers);
  }
  
  canEdit(): boolean {
    return this.canManageTeams || this.canManageMembers;
  }
  
  // ==================== HELPER METHODS ====================
  
  getInitials(member: EmployeDTO): string {
    return `${member.prenom?.charAt(0) || ''}${member.nom?.charAt(0) || ''}`;
  }
  
  getTotalMembers(): number {
    return this.equipes.reduce((sum, t) => sum + (t.memberCount || 0), 0);
  }
  
  getCurrentPageEnd(): number {
    return Math.min(this.currentPage * this.itemsPerPage, this.filteredMembers.length);
  }
  
  onSearchInput(): void {
    this.searchSubject.next(this.searchTerm);
  }
  
  clearSearch(): void {
    this.searchTerm = '';
    this.filterMembers();
  }
  
  // ==================== LOAD METHODS ====================
  
  filterTeams(): void {
    if (!this.teamSearchTerm?.trim()) {
      this.filteredEquipes = [...this.equipes];
    } else {
      const term = this.teamSearchTerm.toLowerCase().trim();
      this.filteredEquipes = this.equipes.filter(team =>
        team.nom?.toLowerCase().includes(term) ||
        this.getTypeLabel(team.type)?.toLowerCase().includes(term)
      );
    }
  }
  
  clearTeamSearch(): void {
    this.teamSearchTerm = '';
    this.filterTeams();
  }
  
  loadTeamMembers(equipeId: number): void {
    this.isLoading = true;
    this.equipeDetailService.getEquipeDetail(equipeId, this.currentMissionId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (equipeDetail) => {
          this.teamMembers = equipeDetail.membres.map(member => ({
            id: member.id,
            nom: member.nom,
            prenom: member.prenom,
            email: member.email,
            numTel: member.numTel,
            poste: member.poste,
            available: true,
            fullName: `${member.prenom} ${member.nom}`
          } as EmployeDTO));
          this.filteredMembers = [...this.teamMembers];
          this.currentPage = 1;
          this.updatePagination();
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error loading team members:', err);
          this.isLoading = false;
          Swal.fire({
            icon: 'error',
            title: 'Error',
            text: 'Failed to load team members'
          });
        }
      });
  }
  
  // ==================== TEAM SELECTION ====================
  
  selectTeam(equipe: EquipeDTO): void {
    if (!equipe || !equipe.id) return;
    this.selectedEquipe = equipe;
    this.loadTeamMembers(equipe.id);
    if (this.isMobile) {
      this.sidebarCollapsed = true;
    }
  }
  
  clearSelection(): void {
    this.selectedEquipe = null;
    this.teamMembers = [];
    this.filteredMembers = [];
    this.searchTerm = '';
    this.currentPage = 1;
  }
  
  // ==================== TEAM CRUD ====================
  
  startAddTeam(): void {
    if (!this.canManageTeams) {
      Swal.fire({ 
        icon: 'warning', 
        title: 'Access Denied', 
        text: 'You don\'t have permission to create teams' 
      });
      return;
    }
    this.isAddingTeam = true;
    this.newTeamName = '';
    this.newTeamType = 'TOPOGRAPHIE';
    setTimeout(() => {
      if (this.teamNameInput?.nativeElement) {
        this.teamNameInput.nativeElement.focus();
      }
    }, 100);
  }
  
  cancelAddTeam(): void {
    this.isAddingTeam = false;
    this.newTeamName = '';
  }
  
  createTeam(): void {
    if (!this.canManageTeams) {
      Swal.fire({ 
        icon: 'warning', 
        title: 'Access Denied', 
        text: 'You don\'t have permission to create teams' 
      });
      return;
    }
    
    if (!this.newTeamName?.trim()) {
      Swal.fire({ 
        icon: 'warning', 
        title: 'Invalid', 
        text: 'Team name cannot be empty', 
        toast: true 
      });
      return;
    }
    
    const request: EquipeRequest = {
      nom: this.newTeamName.trim(),
      type: this.newTeamType
    };
    
    this.isLoading = true;
    this.missionService.createEquipe(request).subscribe({
      next: (newEquipe) => {
        this.equipes = [newEquipe, ...this.equipes];
        this.filterTeams();
        this.cancelAddTeam();
        this.isLoading = false;
        Swal.fire({ 
          icon: 'success', 
          title: 'Success!', 
          text: 'Team created successfully', 
          toast: true 
        });
      },
      error: (err) => {
        this.isLoading = false;
        Swal.fire({ 
          icon: 'error', 
          title: 'Error', 
          text: err.error?.message || 'Failed to create team' 
        });
      }
    });
  }
  
  startEditTeam(equipe: EquipeDTO, event: Event): void {
    if (!this.canManageTeams) {
      Swal.fire({ 
        icon: 'warning', 
        title: 'Access Denied', 
        text: 'You don\'t have permission to edit teams' 
      });
      return;
    }
    event.stopPropagation();
    this.editingTeamId = equipe.id;
    this.editingTeamName = equipe.nom;
    this.editingTeamType = equipe.type;
    setTimeout(() => {
      if (this.editTeamInput?.nativeElement) {
        this.editTeamInput.nativeElement.focus();
      }
    }, 100);
  }
  
  cancelEditTeam(): void {
    this.editingTeamId = null;
    this.editingTeamName = '';
  }
  
  saveEditTeam(equipe: EquipeDTO): void {
    if (!this.canManageTeams) {
      Swal.fire({ 
        icon: 'warning', 
        title: 'Access Denied', 
        text: 'You don\'t have permission to edit teams' 
      });
      return;
    }
    
    if (!this.editingTeamName?.trim()) {
      this.cancelEditTeam();
      return;
    }
    
    this.isLoading = true;
    this.equipeDetailService.updateEquipe(equipe.id, {
      nom: this.editingTeamName.trim(),
      type: this.editingTeamType
    }).subscribe({
      next: () => {
        const index = this.equipes.findIndex(e => e.id === equipe.id);
        if (index !== -1) {
          this.equipes[index] = { 
            ...this.equipes[index], 
            nom: this.editingTeamName.trim(), 
            type: this.editingTeamType 
          };
          this.equipes = [...this.equipes];
        }
        if (this.selectedEquipe?.id === equipe.id) {
          this.selectedEquipe = { 
            ...this.selectedEquipe, 
            nom: this.editingTeamName.trim(), 
            type: this.editingTeamType 
          };
        }
        this.filterTeams();
        this.cancelEditTeam();
        this.isLoading = false;
        Swal.fire({ 
          icon: 'success', 
          title: 'Updated!', 
          text: 'Team updated successfully', 
          toast: true 
        });
      },
      error: (err) => {
        this.isLoading = false;
        Swal.fire({ 
          icon: 'error', 
          title: 'Error', 
          text: err.error?.message || 'Failed to update team' 
        });
      }
    });
  }
  
  deleteTeam(equipe: EquipeDTO, event: Event): void {
    if (!this.canManageTeams) {
      Swal.fire({ 
        icon: 'warning', 
        title: 'Access Denied', 
        text: 'You don\'t have permission to delete teams' 
      });
      return;
    }
    
    event.stopPropagation();
    
    Swal.fire({
      title: 'Delete Team?',
      text: `Are you sure you want to delete "${equipe.nom}"? This will remove all members from this team.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      confirmButtonText: 'Yes, Delete',
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        this.isLoading = true;
        this.equipeDetailService.deleteEquipe(equipe.id).subscribe({
          next: () => {
            this.equipes = this.equipes.filter(e => e.id !== equipe.id);
            this.filterTeams();
            if (this.selectedEquipe?.id === equipe.id) {
              this.clearSelection();
            }
            this.isLoading = false;
            Swal.fire({ 
              icon: 'success', 
              title: 'Deleted!', 
              text: 'Team deleted successfully', 
              toast: true 
            });
          },
          error: (err) => {
            this.isLoading = false;
            Swal.fire({ 
              icon: 'error', 
              title: 'Error', 
              text: err.error?.message || 'Failed to delete team' 
            });
          }
        });
      }
    });
  }
  
  // ==================== MEMBER MANAGEMENT (UPDATED LIKE MISSION-OVERVIEW) ====================
  
  /**
   * Open Add Member Modal - Shows ONLY mission members (like Manage Equipe Members Modal)
   */
  openAddMemberModal(): void {
    if (!this.canManageMembers) {
      Swal.fire({ 
        icon: 'warning', 
        title: 'Access Denied', 
        text: 'You don\'t have permission to add members' 
      });
      return;
    }
    
    if (!this.selectedEquipe) {
      Swal.fire({ 
        icon: 'warning', 
        title: 'No Team Selected', 
        text: 'Please select a team first' 
      });
      return;
    }
    
    // Load mission members (ONLY employees assigned to current mission)
    this.missionService.getMissionTeam(this.currentMissionId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (missionTeam) => {
          // Get all unique members from the mission
          const allMissionMembers: EmployeDTO[] = [];
          const memberIds = new Set<number>();
          
          // Add from members array (unassigned members)
          if (missionTeam.members) {
            missionTeam.members.forEach(m => {
              if (!memberIds.has(m.id)) {
                memberIds.add(m.id);
                allMissionMembers.push(m);
              }
            });
          }
          
          // Add from membersByEquipe (assigned to teams)
          if (missionTeam.membersByEquipe) {
            Object.values(missionTeam.membersByEquipe).forEach((members: EmployeDTO[]) => {
              members.forEach(m => {
                if (!memberIds.has(m.id)) {
                  memberIds.add(m.id);
                  allMissionMembers.push(m);
                }
              });
            });
          }
          
          // Filter out members already in current team
          const currentMemberIds = new Set(this.teamMembers.map(m => m.id));
          this.availableMembersForAssignment = allMissionMembers.filter(m => !currentMemberIds.has(m.id));
          
          // Reset selections and filters
          this.selectedMembersForTeam = [];
          this.selectedMemberFilter = 'all';
          this.memberSearchTerm = '';
          this.modalCurrentPage = 1;
          this.filterAvailableMembers();
          
          this.showAddMemberModal = true;
        },
        error: (err) => {
          console.error('Error loading mission members:', err);
          Swal.fire({ 
            icon: 'error', 
            title: 'Error', 
            text: 'Failed to load available members' 
          });
        }
      });
  }
  
  closeAddMemberModal(): void {
    this.showAddMemberModal = false;
    this.selectedMembersForTeam = [];
    this.memberSearchTerm = '';
    this.selectedMemberFilter = 'all';
  }
  
  /**
   * Filter available members based on search term and filter type
   */
  filterAvailableMembers(): void {
    let filtered = [...this.availableMembersForAssignment];
    
    // Apply search filter
    if (this.memberSearchTerm?.trim()) {
      const term = this.memberSearchTerm.toLowerCase().trim();
      filtered = filtered.filter(member =>
        (member.nom?.toLowerCase().includes(term) ||
        member.prenom?.toLowerCase().includes(term) ||
        member.email?.toLowerCase().includes(term) ||
        member.numTel?.toLowerCase().includes(term))
      );
    }
    
    // Apply assignment filter (like mission-overview)
    switch (this.selectedMemberFilter) {
      case 'assigned':
        // Members assigned to ANY team (excluding current team)
        filtered = filtered.filter(emp => this.getEmployeeTeamName(emp.id) !== null);
        break;
      case 'not-assigned':
        // Members NOT assigned to any team
        filtered = filtered.filter(emp => this.getEmployeeTeamName(emp.id) === null);
        break;
      default: // 'all'
        break;
    }
    
    this.filteredAvailableMembersList = filtered;
    this.modalCurrentPage = 1;
    this.updateModalPagination();
  }
  
  /**
   * Get team name for an employee (from mission team data)
   */
  getEmployeeTeamName(employeeId: number): string | null {
    if (!this.missionTeam?.membersByEquipe) return null;
    
    for (const [teamName, members] of Object.entries(this.missionTeam.membersByEquipe)) {
      // Skip "Non assigné" and current team
      if (teamName === "Non assigné") continue;
      if (this.selectedEquipe && teamName === this.selectedEquipe.nom) continue;
      
      if (members && Array.isArray(members) && members.some(m => m && m.id === employeeId)) {
        return teamName;
      }
    }
    return null;
  }
  
  /**
   * Check if employee is currently in the selected team
   */
  isEmployeeInCurrentTeam(employeeId: number): boolean {
    return this.teamMembers.some(m => m.id === employeeId);
  }
  
  /**
   * Get assigned members count (for modal filter)
   */
  getAssignedMembersCount(): number {
    return this.availableMembersForAssignment.filter(emp => this.getEmployeeTeamName(emp.id) !== null).length;
  }
  
  /**
   * Get not assigned members count (for modal filter)
   */
  getNotAssignedMembersCount(): number {
    return this.availableMembersForAssignment.filter(emp => this.getEmployeeTeamName(emp.id) === null).length;
  }
  
  clearMemberSearch(): void {
    this.memberSearchTerm = '';
    this.filterAvailableMembers();
  }
  
  toggleMemberSelection(member: EmployeDTO): void {
    const index = this.selectedMembersForTeam.findIndex(m => m.id === member.id);
    if (index === -1) {
      this.selectedMembersForTeam.push(member);
    } else {
      this.selectedMembersForTeam.splice(index, 1);
    }
  }
  
  isMemberSelected(member: EmployeDTO): boolean {
    return this.selectedMembersForTeam.some(m => m.id === member.id);
  }
  
  submitAddMembers(): void {
    if (!this.canManageMembers) {
      Swal.fire({ 
        icon: 'warning', 
        title: 'Access Denied', 
        text: 'You don\'t have permission to add members' 
      });
      return;
    }
    
    if (this.selectedMembersForTeam.length === 0 || !this.selectedEquipe) {
      Swal.fire({ 
        icon: 'warning', 
        title: 'Warning', 
        text: 'Please select at least one member' 
      });
      return;
    }
    
    const memberIds = this.selectedMembersForTeam.map(m => m.id);
    
    this.isLoading = true;
    this.equipeDetailService.addMembersToEquipe(
      this.selectedEquipe.id, 
      memberIds, 
      this.currentMissionId
    ).subscribe({
      next: () => {
        Swal.fire({ 
          icon: 'success', 
          title: 'Success', 
          text: 'Members added successfully' 
        });
        this.closeAddMemberModal();
        this.loadTeamMembers(this.selectedEquipe!.id);
        this.loadMissionTeamData(); // Refresh mission team data
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error adding members:', err);
        this.isLoading = false;
        Swal.fire({ 
          icon: 'error', 
          title: 'Error', 
          text: 'Failed to add members' 
        });
      }
    });
  }
  
  removeMember(member: EmployeDTO): void {
    if (!this.canManageMembers) {
      Swal.fire({ 
        icon: 'warning', 
        title: 'Access Denied', 
        text: 'You don\'t have permission to remove members' 
      });
      return;
    }
    
    if (!this.selectedEquipe) return;
    
    Swal.fire({
      title: 'Remove Member',
      text: `Are you sure you want to remove ${member.prenom} ${member.nom} from this team?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      confirmButtonText: 'Yes, Remove',
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        this.isLoading = true;
        this.equipeDetailService.removeMemberFromEquipe(
          this.selectedEquipe!.id, 
          member.id, 
          this.currentMissionId
        ).subscribe({
          next: () => {
            Swal.fire({ 
              icon: 'success', 
              title: 'Removed', 
              text: 'Member removed successfully' 
            });
            this.loadTeamMembers(this.selectedEquipe!.id);
            this.loadMissionTeamData(); // Refresh mission team data
            this.isLoading = false;
          },
          error: (err) => {
            console.error('Error removing member:', err);
            this.isLoading = false;
            Swal.fire({ 
              icon: 'error', 
              title: 'Error', 
              text: 'Failed to remove member' 
            });
          }
        });
      }
    });
  }
  
  // ==================== FILTERS & PAGINATION ====================
  
  filterMembers(): void {
    if (!this.searchTerm?.trim()) {
      this.filteredMembers = [...this.teamMembers];
    } else {
      const term = this.searchTerm.toLowerCase().trim();
      this.filteredMembers = this.teamMembers.filter(member =>
        (member.nom?.toLowerCase().includes(term) ||
        member.prenom?.toLowerCase().includes(term) ||
        member.email?.toLowerCase().includes(term))
      );
    }
    this.currentPage = 1;
    this.updatePagination();
  }
  
  updatePagination(): void {
    this.totalPages = Math.max(1, Math.ceil(this.filteredMembers.length / this.itemsPerPage));
    
    if (this.currentPage > this.totalPages) {
      this.currentPage = this.totalPages;
    }
    
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    this.paginatedMembers = this.filteredMembers.slice(startIndex, startIndex + this.itemsPerPage);
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
  
  // Modal Pagination
  updateModalPagination(): void {
    this.modalTotalPages = Math.max(1, Math.ceil(this.filteredAvailableMembersList.length / this.modalItemsPerPage));
    
    if (this.modalCurrentPage > this.modalTotalPages) {
      this.modalCurrentPage = this.modalTotalPages;
    }
    
    const startIndex = (this.modalCurrentPage - 1) * this.modalItemsPerPage;
    this.paginatedAvailableMembers = this.filteredAvailableMembersList.slice(startIndex, startIndex + this.modalItemsPerPage);
  }
  
  previousModalPage(): void {
    if (this.modalCurrentPage > 1) {
      this.modalCurrentPage--;
      this.updateModalPagination();
    }
  }
  
  nextModalPage(): void {
    if (this.modalCurrentPage < this.modalTotalPages) {
      this.modalCurrentPage++;
      this.updateModalPagination();
    }
  }
  
  // ==================== SIDEBAR & HELPERS ====================
  
  toggleSidebar(): void {
    this.sidebarCollapsed = !this.sidebarCollapsed;
  }
  
  checkScreenSize(): void {
    this.isMobile = window.innerWidth < 768;
    if (!this.isMobile) {
      this.sidebarCollapsed = false;
    }
  }
  
  handleResize(): void {
    this.checkScreenSize();
  }
  
  getTypeIcon(type: string): string {
    const option = this.typeOptions.find(t => t.value === type);
    return option?.icon || 'fas fa-users';
  }
  
  getTypeLabel(type: string): string {
    const option = this.typeOptions.find(t => t.value === type);
    return option?.label || type;
  }
  
  getTypeColor(type: string): string {
    const colors: { [key: string]: string } = {
      'TOPOGRAPHIE': '#3b82f6',
      'LAYONNAGE': '#10b981',
      'ENERGISREMENT': '#f59e0b',
      'POSE': '#8b5cf6',
      'RAMASSAGE': '#14b8a6'
    };
    return colors[type] || '#64748b';
  }
  
  getAvatarColor(id: number): string {
    const colors = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#ec489a'];
    return colors[Math.abs(id) % colors.length];
  }
  
  goBack(): void {
    this.router.navigate(['/pages/my-team']);
  }
  
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    window.removeEventListener('resize', this.handleResize.bind(this));
  }
  getModalPageNumbers(): number[] {
  const pages: number[] = [];
  const maxVisible = 5;
  let startPage = Math.max(1, this.modalCurrentPage - Math.floor(maxVisible / 2));
  let endPage = Math.min(this.modalTotalPages, startPage + maxVisible - 1);
  
  if (endPage - startPage + 1 < maxVisible) {
    startPage = Math.max(1, endPage - maxVisible + 1);
  }
  
  for (let i = startPage; i <= endPage; i++) {
    pages.push(i);
  }
  return pages;
}

goToModalPage(page: number): void {
  if (page >= 1 && page <= this.modalTotalPages) {
    this.modalCurrentPage = page;
    this.updateModalPagination();
  }
}
loadEquipes(): void {
  this.isLoading = true;
  // Use getMissionTeam which returns equipes with correct member counts for current mission
  this.missionService.getMissionTeam(this.currentMissionId)
    .pipe(takeUntil(this.destroy$))
    .subscribe({
      next: (missionTeam) => {
        // Map the equipes from missionTeam and calculate member counts from membersByEquipe
        this.equipes = missionTeam.equipes.map(equipe => ({
          ...equipe,
          memberCount: missionTeam.membersByEquipe?.[equipe.nom]?.length || 0
        }));
        this.filteredEquipes = [...this.equipes];
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading equipes:', err);
        this.isLoading = false;
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'Failed to load teams'
        });
      }
    });
}

}