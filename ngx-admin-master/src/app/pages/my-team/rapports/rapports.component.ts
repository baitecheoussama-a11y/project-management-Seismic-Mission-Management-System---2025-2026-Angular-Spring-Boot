// pages/my-team/rapports/rapports.component.ts
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil, debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MissionService } from '../../../services/mission/mission.service';
import { 
  RapportService, 
  RapportResponse, 
  RapportRequest, 
  RendementResponse, 
  RendementRequest,
  FichierDTO
} from '../../../services/rapport/rapport.service';
import { RapportDetailsService, RapportDetails } from '../../../services/rapport/rapport-details.service';
import { FichierService } from '../../../services/rapport/fichier.service';
import { AuthService } from '../../../services/auth.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-rapports',
  templateUrl: './rapports.component.html',
  styleUrls: ['./rapports.component.scss']
})
export class RapportsComponent implements OnInit, OnDestroy {

  // Data
  rapports: RapportResponse[] = [];
  filteredRapports: RapportResponse[] = [];
  currentMission: any = null;
  currentProject: any = null;
  
  // UI States
  isLoading: boolean = false;
  isMobile: boolean = false;
  
  // Role-based permissions
  canManageReports: boolean = false;
  
  // Modals
  showAddRapportModal: boolean = false;
  showEditRapportModal: boolean = false;
  showRapportDetailModal: boolean = false;
  showAddRendementModal: boolean = false;
  showAddFileModal: boolean = false;
  showFilePreviewModal: boolean = false;
  
  mongodbFields: { key: string; value: string }[] = [];

  rapportForm: FormGroup;
  editRapportForm: FormGroup;
  rendementForm: FormGroup;
  
  editingRapportId: number | null = null;
  selectedRapport: RapportResponse | null = null;
  selectedRapportRendements: RendementResponse[] = [];
  selectedRapportFichiers: FichierDTO[] = [];
  selectedRapportDetails: any = null;
  editingRendementId: number | null = null;
  
  // File upload properties
  selectedFile: File | null = null;
  newFileTitre: string = '';
  newFileType: string = 'DOCUMENT';
  previewFile: FichierDTO | null = null;
  
  // Search and Pagination
  searchTerm: string = '';
  currentMissionId: number = 0;
  
  // Pagination properties
  Math = Math;
  currentPage: number = 1;
  pageSize: number = 6;
  totalPages: number = 1;
  paginatedRapports: RapportResponse[] = [];
  
  private destroy$ = new Subject<void>();
  private searchSubject = new Subject<string>();
  
  constructor(
    private missionService: MissionService,
    private rapportService: RapportService,
    private rapportDetailsService: RapportDetailsService,
    private fichierService: FichierService,
    private authService: AuthService,
    private fb: FormBuilder
  ) {
    this.rapportForm = this.fb.group({
      titre: ['', Validators.required],
      date: [new Date().toISOString().split('T')[0], Validators.required],
      resume: ['', Validators.required],
      mongodbDetails: ['']
    });
    
    this.editRapportForm = this.fb.group({
      titre: ['', Validators.required],
      date: ['', Validators.required],
      resume: ['', Validators.required],
      mongodbDetails: ['']
    });
    
    this.rendementForm = this.fb.group({
      heureDebut: ['08:00', Validators.required],
      heureFin: ['17:00', Validators.required],
      valeurRendement: [0, [Validators.required, Validators.min(0)]],
      uniteRendement: ['m²', Validators.required],
      date: [new Date().toISOString().split('T')[0], Validators.required]
    });
  }
  
  ngOnInit(): void {
    this.initializePermissions();
    this.loadCurrentMission();
    this.checkScreenSize();
    window.addEventListener('resize', () => this.checkScreenSize());
    
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      takeUntil(this.destroy$)
    ).subscribe(() => {
      this.filterRapports();
    });
  }
  
  // ==================== PERMISSIONS ====================
  
  initializePermissions(): void {
    const userRoles = this.authService.getCurrentUser()?.roles || [];
    const hasChefTerrainRole = userRoles.includes('CHEF_TERRAIN');
    const hasAdminRole = userRoles.includes('ADMIN') || userRoles.includes('DIRECTEUR');
    
    this.canManageReports = hasChefTerrainRole || hasAdminRole;
    console.log('[DEBUG] Can manage reports:', this.canManageReports);
  }
  
  // ==================== LOAD METHODS ====================
  
  loadCurrentMission(): void {
    this.isLoading = true;
    this.missionService.getMyCurrentMission().pipe(
      takeUntil(this.destroy$)
    ).subscribe({
      next: (missionResponse) => {
        if (missionResponse && missionResponse.missionId) {
          this.currentMissionId = missionResponse.missionId;
          this.currentMission = missionResponse;
          this.loadCurrentProject();
        } else {
          this.isLoading = false;
          Swal.fire({
            icon: 'warning',
            title: 'No Mission',
            text: 'You are not assigned to any mission'
          });
        }
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
  
  loadCurrentProject(): void {
    this.missionService.getCurrentProjectByMission(this.currentMissionId).subscribe({
      next: (project) => {
        if (project) {
          this.currentProject = project;
          this.loadRapports();
        } else {
          this.isLoading = false;
          Swal.fire({
            icon: 'info',
            title: 'No Project',
            text: 'No active project found for this mission'
          });
        }
      },
      error: (err) => {
        console.error('Error loading current project:', err);
        this.isLoading = false;
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'Failed to load current project'
        });
      }
    });
  }
  
  loadRapports(): void {
    if (!this.currentMissionId) return;
    
    this.isLoading = true;
    this.rapportService.getRapportsForCurrentProject(this.currentMissionId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (reports) => {
          this.rapports = reports;
          this.filteredRapports = [...reports];
          this.currentPage = 1;
          this.updatePagination();
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error loading reports:', err);
          this.isLoading = false;
          Swal.fire({
            icon: 'error',
            title: 'Error',
            text: 'Failed to load reports'
          });
        }
      });
  }
  
  // ==================== RAPPORT CRUD ====================
  
  openAddRapportModal(): void {
    if (!this.canManageReports) {
      Swal.fire({ 
        icon: 'warning', 
        title: 'Access Denied', 
        text: 'You do not have permission to add reports' 
      });
      return;
    }
    this.rapportForm.reset({
      titre: '',
      date: new Date().toISOString().split('T')[0],
      resume: '',
      mongodbDetails: ''
    });
    this.mongodbFields = [{ key: '', value: '' }];
    this.showAddRapportModal = true;
  }
  
  closeAddRapportModal(): void {
    this.showAddRapportModal = false;
  }
  
  submitAddRapport(): void {
    if (!this.canManageReports) {
      Swal.fire({ 
        icon: 'warning', 
        title: 'Access Denied', 
        text: 'You do not have permission to add reports' 
      });
      return;
    }
    
    if (this.rapportForm.invalid) {
      Swal.fire({ icon: 'warning', title: 'Warning', text: 'Please fill all required fields' });
      return;
    }
    
    const request: RapportRequest = {
      titre: this.rapportForm.value.titre,
      date: this.rapportForm.value.date,
      resume: this.rapportForm.value.resume
    };
    
    const mongodbDetails = this.rapportForm.value.mongodbDetails;
    
    this.isLoading = true;
    
    this.rapportService.addRapportToCurrentProject(this.currentMissionId, request)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (newRapport) => {
          // If MongoDB details provided, save them
          if (mongodbDetails && mongodbDetails.trim()) {
            try {
              const details = JSON.parse(mongodbDetails);
              this.rapportDetailsService.saveDetails(newRapport.id, details)
                .pipe(takeUntil(this.destroy$))
                .subscribe({
                  next: () => {
                    console.log('MongoDB details saved successfully');
                  },
                  error: (err) => {
                    console.error('Error saving MongoDB details:', err);
                    Swal.fire({
                      icon: 'warning',
                      title: 'Partial Success',
                      text: 'Report saved but additional details could not be saved'
                    });
                  }
                });
            } catch (e) {
              console.warn('Invalid JSON for MongoDB details:', e);
            }
          }
          
          Swal.fire({ icon: 'success', title: 'Success', text: 'Report added successfully' });
          this.closeAddRapportModal();
          this.loadRapports();
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error adding report:', err);
          this.isLoading = false;
          Swal.fire({ icon: 'error', title: 'Error', text: 'Failed to add report' });
        }
      });
  }
  
  openEditRapportModal(rapport: RapportResponse, event: Event): void {
    if (!this.canManageReports) {
      Swal.fire({ 
        icon: 'warning', 
        title: 'Access Denied', 
        text: 'You do not have permission to edit reports' 
      });
      return;
    }
    event.stopPropagation();
    this.editingRapportId = rapport.id;
    this.editRapportForm.patchValue({
      titre: rapport.titre,
      date: rapport.date,
      resume: rapport.resume
    });
    
    // Load MongoDB details if exists
    this.rapportDetailsService.getDetails(rapport.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (details) => {
          if (details && details.details) {
            const jsonString = JSON.stringify(details.details);
            this.editRapportForm.patchValue({
              mongodbDetails: jsonString
            });
            this.loadMongoDBFieldsFromJson(jsonString);
          }
        },
        error: () => {
          this.mongodbFields = [];
        }
      });
    
    this.showEditRapportModal = true;
  }
  
  closeEditRapportModal(): void {
    this.showEditRapportModal = false;
    this.editingRapportId = null;
    this.editRapportForm.reset();
  }
  
  addMongoDBField(): void {
    this.mongodbFields.push({ key: '', value: '' });
  }

  removeMongoDBField(index: number): void {
    this.mongodbFields.splice(index, 1);
    this.updateMongoDBDetails();
  }

  updateMongoDBDetails(): void {
    const details: any = {};
    this.mongodbFields.forEach(field => {
      if (field.key && field.key.trim()) {
        let value: any = field.value;
        
        try {
          if (typeof value === 'string' && value.trim().startsWith('[') && value.trim().endsWith(']')) {
            value = JSON.parse(value);
          } else if (typeof value === 'string' && value.trim().startsWith('{') && value.trim().endsWith('}')) {
            value = JSON.parse(value);
          } else {
            if (!isNaN(Number(value)) && value.trim() !== '') {
              value = Number(value);
            } else if (value.toLowerCase() === 'true') {
              value = true;
            } else if (value.toLowerCase() === 'false') {
              value = false;
            } else if (value.toLowerCase() === 'null') {
              value = null;
            }
          }
        } catch (e) {
          // Keep as string
        }
        
        details[field.key.trim()] = value;
      }
    });
    
    const jsonString = Object.keys(details).length > 0 ? JSON.stringify(details) : '';
    this.rapportForm.patchValue({
      mongodbDetails: jsonString
    });
    
    if (this.editRapportForm) {
      this.editRapportForm.patchValue({
        mongodbDetails: jsonString
      });
    }
  }
  
  getMongoDBJson(): string {
    const details = this.rapportForm.get('mongodbDetails')?.value;
    if (!details) return '';
    try {
      const parsed = JSON.parse(details);
      return JSON.stringify(parsed, null, 2);
    } catch (e) {
      return details;
    }
  }

  loadMongoDBFieldsFromJson(jsonString: string): void {
    if (!jsonString) {
      this.mongodbFields = [{ key: '', value: '' }];
      return;
    }
    
    try {
      const details = JSON.parse(jsonString);
      const fields = Object.keys(details).map(key => ({
        key: key,
        value: typeof details[key] === 'object' 
          ? JSON.stringify(details[key]) 
          : String(details[key])
      }));
      this.mongodbFields = fields.length > 0 ? fields : [{ key: '', value: '' }];
    } catch (e) {
      this.mongodbFields = [{ key: '', value: '' }];
    }
  }

  submitEditRapport(): void {
    if (!this.canManageReports) {
      Swal.fire({ 
        icon: 'warning', 
        title: 'Access Denied', 
        text: 'You do not have permission to edit reports' 
      });
      return;
    }
    
    if (this.editRapportForm.invalid || !this.editingRapportId) {
      Swal.fire({ icon: 'warning', title: 'Warning', text: 'Please fill all required fields' });
      return;
    }
    
    const request: RapportRequest = {
      titre: this.editRapportForm.value.titre,
      date: this.editRapportForm.value.date,
      resume: this.editRapportForm.value.resume
    };
    
    const mongodbDetails = this.editRapportForm.value.mongodbDetails;
    
    this.rapportService.updateRapport(this.editingRapportId, request)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          if (mongodbDetails && mongodbDetails.trim()) {
            try {
              const details = JSON.parse(mongodbDetails);
              this.rapportDetailsService.updateDetails(this.editingRapportId!, details)
                .pipe(takeUntil(this.destroy$))
                .subscribe({
                  next: () => console.log('MongoDB details updated'),
                  error: (err) => console.error('Error updating MongoDB details:', err)
                });
            } catch (e) {
              console.warn('Invalid JSON for MongoDB details:', e);
            }
          }
          
          Swal.fire({ icon: 'success', title: 'Success', text: 'Report updated successfully' });
          this.closeEditRapportModal();
          this.loadRapports();
        },
        error: (err) => {
          console.error('Error updating report:', err);
          Swal.fire({ icon: 'error', title: 'Error', text: 'Failed to update report' });
        }
      });
  }
  
  deleteRapport(rapport: RapportResponse, event: Event): void {
    if (!this.canManageReports) {
      Swal.fire({ 
        icon: 'warning', 
        title: 'Access Denied', 
        text: 'You do not have permission to delete reports' 
      });
      return;
    }
    
    event.stopPropagation();
    
    Swal.fire({
      title: 'Delete Report?',
      text: `Are you sure you want to delete "${rapport.titre}"?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      confirmButtonText: 'Yes, Delete',
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        this.isLoading = true;
        this.rapportService.deleteRapport(rapport.id)
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: () => {
              this.rapportDetailsService.deleteDetails(rapport.id)
                .pipe(takeUntil(this.destroy$))
                .subscribe({
                  next: () => console.log('MongoDB details deleted'),
                  error: () => console.warn('Failed to delete MongoDB details')
                });
              
              Swal.fire({ icon: 'success', title: 'Deleted', text: 'Report deleted successfully' });
              this.loadRapports();
            },
            error: (err) => {
              console.error('Error deleting report:', err);
              this.isLoading = false;
              Swal.fire({ icon: 'error', title: 'Error', text: 'Failed to delete report' });
            }
          });
      }
    });
  }
  
  viewRapportDetails(rapport: RapportResponse): void {
    this.selectedRapport = rapport;
    this.loadRendementsForRapport(rapport.id);
    this.loadFichiersForRapport(rapport.id);
    
    this.rapportDetailsService.getDetails(rapport.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (details) => {
          if (details) {
            this.selectedRapportDetails = details.details;
          } else {
            this.selectedRapportDetails = null;
          }
        },
        error: () => {
          this.selectedRapportDetails = null;
        }
      });
    
    this.showRapportDetailModal = true;
  }
  
  closeRapportDetailModal(): void {
    this.showRapportDetailModal = false;
    this.selectedRapport = null;
    this.selectedRapportRendements = [];
    this.selectedRapportFichiers = [];
    this.selectedRapportDetails = null;
  }
  
  // ==================== RENDEMENT CRUD ====================
  
  loadRendementsForRapport(rapportId: number): void {
    this.rapportService.getRapportById(rapportId).subscribe({
      next: (rapport) => {
        this.selectedRapportRendements = rapport.rendements || [];
      },
      error: (err) => {
        console.error('Error loading rendements:', err);
        this.selectedRapportRendements = [];
      }
    });
  }
  
  openAddRendementModal(): void {
    if (!this.canManageReports) {
      Swal.fire({ 
        icon: 'warning', 
        title: 'Access Denied', 
        text: 'You do not have permission to add productivity records' 
      });
      return;
    }
    this.rendementForm.reset({
      heureDebut: '08:00',
      heureFin: '17:00',
      valeurRendement: 0,
      uniteRendement: 'm²',
      date: new Date().toISOString().split('T')[0]
    });
    this.editingRendementId = null;
    this.showAddRendementModal = true;
  }
  
  openEditRendementModal(rendement: RendementResponse): void {
    if (!this.canManageReports) {
      Swal.fire({ 
        icon: 'warning', 
        title: 'Access Denied', 
        text: 'You do not have permission to edit productivity records' 
      });
      return;
    }
    this.editingRendementId = rendement.id;
    this.rendementForm.patchValue({
      heureDebut: rendement.heureDebut,
      heureFin: rendement.heureFin,
      valeurRendement: rendement.valeurRendement,
      uniteRendement: rendement.uniteRendement,
      date: rendement.date
    });
    this.showAddRendementModal = true;
  }
  
  closeAddRendementModal(): void {
    this.showAddRendementModal = false;
    this.editingRendementId = null;
    this.rendementForm.reset();
  }
  
  submitRendement(): void {
    if (!this.canManageReports) {
      Swal.fire({ 
        icon: 'warning', 
        title: 'Access Denied', 
        text: 'You do not have permission to modify productivity records' 
      });
      return;
    }
    
    if (this.rendementForm.invalid || !this.selectedRapport) {
      Swal.fire({ icon: 'warning', title: 'Warning', text: 'Please fill all required fields' });
      return;
    }
    
    const request: RendementRequest = this.rendementForm.value;
    
    if (this.editingRendementId) {
      this.rapportService.updateRendement(this.editingRendementId, request)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            Swal.fire({ icon: 'success', title: 'Success', text: 'Productivity record updated successfully' });
            this.closeAddRendementModal();
            this.loadRendementsForRapport(this.selectedRapport!.id);
          },
          error: (err) => {
            console.error('Error updating rendement:', err);
            Swal.fire({ icon: 'error', title: 'Error', text: 'Failed to update productivity record' });
          }
        });
    } else {
      this.rapportService.addRendementToRapport(this.selectedRapport.id, request)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            Swal.fire({ icon: 'success', title: 'Success', text: 'Productivity record added successfully' });
            this.closeAddRendementModal();
            this.loadRendementsForRapport(this.selectedRapport!.id);
          },
          error: (err) => {
            console.error('Error adding rendement:', err);
            Swal.fire({ icon: 'error', title: 'Error', text: 'Failed to add productivity record' });
          }
        });
    }
  }
  
  deleteRendement(rendement: RendementResponse, event: Event): void {
    if (!this.canManageReports) {
      Swal.fire({ 
        icon: 'warning', 
        title: 'Access Denied', 
        text: 'You do not have permission to delete productivity records' 
      });
      return;
    }
    
    event.stopPropagation();
    
    Swal.fire({
      title: 'Delete Record?',
      text: `Are you sure you want to delete this productivity record?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      confirmButtonText: 'Yes, Delete',
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        this.rapportService.deleteRendement(rendement.id)
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: () => {
              Swal.fire({ icon: 'success', title: 'Deleted', text: 'Productivity record deleted successfully' });
              this.loadRendementsForRapport(this.selectedRapport!.id);
            },
            error: (err) => {
              console.error('Error deleting rendement:', err);
              Swal.fire({ icon: 'error', title: 'Error', text: 'Failed to delete productivity record' });
            }
          });
      }
    });
  }
  
  calculateTotalProductivity(): number {
    return this.selectedRapportRendements.reduce((sum, r) => sum + (r.valeurRendement || 0), 0);
  }
  
  calculateTotalHours(): number {
    return this.selectedRapportRendements.reduce((sum, r) => sum + (r.dureeHeures || 0), 0);
  }
  
  // ==================== FILE METHODS ====================
  
  loadFichiersForRapport(rapportId: number): void {
    this.fichierService.getFichiersByRapport(rapportId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (fichiers) => {
          this.selectedRapportFichiers = fichiers;
        },
        error: (err) => {
          console.error('Error loading fichiers:', err);
          this.selectedRapportFichiers = [];
        }
      });
  }
  
  openAddFileModal(): void {
    if (!this.canManageReports) {
      Swal.fire({ 
        icon: 'warning', 
        title: 'Access Denied', 
        text: 'You do not have permission to upload files' 
      });
      return;
    }
    this.selectedFile = null;
    this.newFileTitre = '';
    this.newFileType = 'DOCUMENT';
    this.showAddFileModal = true;
  }
  
  closeAddFileModal(): void {
    this.showAddFileModal = false;
    this.selectedFile = null;
  }
  
  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      if (!this.newFileTitre) {
        this.newFileTitre = file.name;
      }
    }
  }
  
  onDragOver(event: DragEvent): void {
    event.preventDefault();
    const target = event.currentTarget as HTMLElement;
    target.classList.add('dragover');
  }
  
  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    const target = event.currentTarget as HTMLElement;
    target.classList.remove('dragover');
  }
  
  onDrop(event: DragEvent): void {
    event.preventDefault();
    const target = event.currentTarget as HTMLElement;
    target.classList.remove('dragover');
    
    const files = event.dataTransfer?.files;
    if (files && files.length > 0) {
      this.selectedFile = files[0];
      if (!this.newFileTitre) {
        this.newFileTitre = this.selectedFile.name;
      }
    }
  }
  
  submitFileUpload(): void {
    if (!this.selectedFile || !this.selectedRapport) {
      Swal.fire({ icon: 'warning', title: 'Warning', text: 'Please select a file' });
      return;
    }
  
    this.isLoading = true;
    this.fichierService.uploadFile(
      this.selectedRapport.id,
      this.selectedFile,
      this.newFileTitre || this.selectedFile.name,
      this.newFileType
    ).pipe(takeUntil(this.destroy$))
    .subscribe({
      next: (fichier) => {
        Swal.fire({ icon: 'success', title: 'Success', text: 'File uploaded successfully' });
        this.closeAddFileModal();
        this.loadFichiersForRapport(this.selectedRapport!.id);
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error uploading file:', err);
        Swal.fire({ icon: 'error', title: 'Error', text: 'Failed to upload file' });
        this.isLoading = false;
      }
    });
  }
  
  deleteFile(fichier: FichierDTO, event: Event): void {
    if (!this.canManageReports) {
      Swal.fire({ 
        icon: 'warning', 
        title: 'Access Denied', 
        text: 'You do not have permission to delete files' 
      });
      return;
    }
    
    event.stopPropagation();
    
    Swal.fire({
      title: 'Delete File?',
      text: `Are you sure you want to delete "${fichier.titre}"?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      confirmButtonText: 'Yes, Delete',
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        this.fichierService.deleteFichier(fichier.id)
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: () => {
              Swal.fire({ icon: 'success', title: 'Deleted', text: 'File deleted successfully' });
              this.loadFichiersForRapport(this.selectedRapport!.id);
            },
            error: (err) => {
              console.error('Error deleting file:', err);
              Swal.fire({ icon: 'error', title: 'Error', text: 'Failed to delete file' });
            }
          });
      }
    });
  }
  
  viewFile(fichier: FichierDTO): void {
    this.previewFile = fichier;
    this.showFilePreviewModal = true;
  }
  
  closeFilePreviewModal(): void {
    this.showFilePreviewModal = false;
    this.previewFile = null;
  }
  
  getFileUrl(fichierId: number): string {
    return this.fichierService.getFileUrl(fichierId);
  }
  
  downloadFile(fichier: FichierDTO): void {
    const url = this.fichierService.getFileUrl(fichier.id);
    window.open(url, '_blank');
  }
  
  formatFileSize(bytes: number): string {
    if (!bytes) return '0 B';
    const units = ['B', 'KB', 'MB', 'GB'];
    let size = bytes;
    let unitIndex = 0;
    while (size >= 1024 && unitIndex < units.length - 1) {
      size /= 1024;
      unitIndex++;
    }
    return `${size.toFixed(1)} ${units[unitIndex]}`;
  }
  
  // ==================== FILTERS & PAGINATION ====================
  
  filterRapports(): void {
    if (!this.searchTerm.trim()) {
      this.filteredRapports = [...this.rapports];
    } else {
      const term = this.searchTerm.toLowerCase();
      this.filteredRapports = this.rapports.filter(rapport =>
        rapport.titre.toLowerCase().includes(term) ||
        (rapport.resume && rapport.resume.toLowerCase().includes(term)) ||
        (rapport.projectName && rapport.projectName.toLowerCase().includes(term))
      );
    }
    this.currentPage = 1;
    this.updatePagination();
  }
  
  updatePagination(): void {
    this.totalPages = Math.ceil(this.filteredRapports.length / this.pageSize);
    if (this.totalPages === 0) this.totalPages = 1;
    if (this.currentPage > this.totalPages) {
      this.currentPage = Math.max(1, this.totalPages);
    }
    const startIndex = (this.currentPage - 1) * this.pageSize;
    this.paginatedRapports = this.filteredRapports.slice(startIndex, startIndex + this.pageSize);
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
  
  goToFirstPage(): void {
    this.goToPage(1);
  }
  
  goToLastPage(): void {
    this.goToPage(this.totalPages);
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
  
  // ==================== HELPERS ====================
  
  formatDate(date: string): string {
    if (!date) return 'N/A';
    return new Date(date).toLocaleDateString('en-US', { 
      day: '2-digit', 
      month: 'short', 
      year: 'numeric' 
    });
  }
  
  formatTime(time: string): string {
    if (!time) return '--:--';
    return time.substring(0, 5);
  }
  
  truncateText(text: string, maxLength: number = 150): string {
    if (!text) return '';
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength) + '...';
  }
  
  checkScreenSize(): void {
    this.isMobile = window.innerWidth < 768;
  }
  
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    window.removeEventListener('resize', () => this.checkScreenSize());
  }
}