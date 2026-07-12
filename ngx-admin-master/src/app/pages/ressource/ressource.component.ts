import { Component, OnInit, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CategorieRessourceService, CategorieRessource } from '../../services/ressources/categorie-ressource.service';
import { TypeRessourceService, TypeRessource } from '../../services/ressources/type-ressource.service';
import { RessourceService, Ressource } from '../../services/ressources/ressource.service';
import Swal from 'sweetalert2';
import { Subject } from 'rxjs';
import { takeUntil, debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Component({
  selector: 'ngx-ressource',
  templateUrl: './ressource.component.html',
  styleUrls: ['./ressource.component.scss']
})
export class RessourceComponent implements OnInit, OnDestroy {
  
  @ViewChild('categoryInput') categoryInput!: ElementRef;
  @ViewChild('typeInput') typeInput!: ElementRef;
  @ViewChild('editCategoryInput') editCategoryInput!: ElementRef;
  @ViewChild('editTypeInput') editTypeInput!: ElementRef;


  // Category data
  categories: CategorieRessource[] = [];
  selectedCategory: CategorieRessource | null = null;
  searchTerm: string = '';
  
  // Type data
  types: TypeRessource[] = [];
  selectedTypeId: number | null = null;
  
  // Ressource data
  ressources: Ressource[] = [];
  filteredRessources: Ressource[] = [];
  
  // Category UI States
  isAddingCategory: boolean = false;
  newCategoryName: string = '';
  editingCategoryId: number | null = null;
  editingCategoryName: string = '';
  
  // Type UI States
  isAddingType: boolean = false;
  newTypeName: string = '';
  editingTypeId: number | null = null;
  editingTypeName: string = '';
  
  // Ressource UI States
  isAddingRessource: boolean = false;
  addRessourceForm: FormGroup;
  
  isEditingRessource: boolean = false;
  editingRessourceId: number | null = null;
  editRessourceForm: FormGroup;
  
  // State
  isLoading: boolean = false;
  isMobile: boolean = false;
  sidebarCollapsed: boolean = false;
  private destroy$ = new Subject<void>();
  private searchSubject = new Subject<string>();

  // Counts
  categoryRessourceCounts: Map<number, number> = new Map();
  typeRessourceCounts: Map<number, number> = new Map();


  Math = Math; // للاستخدام في template

// Pagination properties
currentPage: number = 1;
pageSize: number = 12;
totalPages: number = 1;


// Advanced Filters
showAdvancedFilters: boolean = false;
filterUnit: string = '';
filterDateFrom: string = '';
filterDateTo: string = '';
filterQuantityMin: number | null = null;
filterQuantityMax: number | null = null;
filterCostMin: number | null = null;
filterCostMax: number | null = null;
sortField: string = 'titre';
sortDirection: string = 'asc';

  constructor(
    private fb: FormBuilder,
    private categorieService: CategorieRessourceService,
    private typeService: TypeRessourceService,
    private ressourceService: RessourceService
  ) {
    this.addRessourceForm = this.fb.group({
      titre: ['', Validators.required],
      description: [''],
      quantite: [1, [Validators.required, Validators.min(0.1)]],
      unite: ['', Validators.required],
      cout: [0, [Validators.required, Validators.min(0)]],
      dateAchat: [new Date().toISOString().split('T')[0]]
    });
    
    this.editRessourceForm = this.fb.group({
      titre: ['', Validators.required],
      description: [''],
      quantite: [1, [Validators.required, Validators.min(0.1)]],
      unite: ['', Validators.required],
      cout: [0, [Validators.required, Validators.min(0)]],
      dateAchat: ['']
    });
  }

  ngOnInit() {
    this.loadCategories();
    this.checkScreenSize();
    window.addEventListener('resize', () => this.checkScreenSize());
    
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      takeUntil(this.destroy$)
    ).subscribe(() => {
      this.applyFilter();
    });
  }

  // ==================== LOADING METHODS ====================

  loadCategories() {
    this.isLoading = true;
    this.categorieService.getAll().subscribe({
      next: (data) => {
        this.categories = data;
        this.loadCategoryRessourceCounts();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading categories:', err);
        this.isLoading = false;
        Swal.fire({
          icon: 'error',
          title: 'Connection Error',
          text: 'Failed to load categories. Make sure the backend is running.',
          confirmButtonText: 'OK'
        });
      }
    });
  }

  loadCategoryRessourceCounts() {
    this.categories.forEach(category => {
      this.ressourceService.getByCategorie(category.idCategorieRessource).subscribe({
        next: (ressources) => {
          this.categoryRessourceCounts.set(category.idCategorieRessource, ressources.length);
        },
        error: (err) => console.error('Error loading category count:', err)
      });
    });
  }

  loadTypes(categoryId: number) {
    this.isLoading = true;
    this.typeService.getByCategorie(categoryId).subscribe({
      next: (data) => {
        this.types = data;
        this.loadTypeRessourceCounts();
        this.loadRessourcesByCategory(categoryId);
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading types:', err);
        this.isLoading = false;
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'Failed to load types',
          toast: true,
          position: 'top-end'
        });
      }
    });
  }

  loadTypeRessourceCounts() {
    this.types.forEach(type => {
      this.ressourceService.getByType(type.idTypeRessource).subscribe({
        next: (ressources) => {
          this.typeRessourceCounts.set(type.idTypeRessource, ressources.length);
        },
        error: (err) => console.error('Error loading type count:', err)
      });
    });
  }

  loadRessourcesByCategory(categoryId: number) {
    this.isLoading = true;
    this.ressourceService.getByCategorie(categoryId).subscribe({
      next: (data) => {
        this.ressources = data;
        this.applyFilter();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading ressources:', err);
        this.isLoading = false;
      }
    });
  }

  loadRessourcesByType(typeId: number) {
    this.isLoading = true;
    this.ressourceService.getByType(typeId).subscribe({
      next: (data) => {
        this.ressources = data;
        this.applyFilter();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading ressources by type:', err);
        this.isLoading = false;
      }
    });
  }

  // ==================== RESSOURCE CRUD ====================

  startAddRessource() {
    this.isAddingRessource = true;
    this.addRessourceForm.reset({
      titre: '',
      description: '',
      quantite: 1,
      unite: '',
      cout: 0,
      dateAchat: new Date().toISOString().split('T')[0]
    });
  }

  cancelAddRessource() {
    this.isAddingRessource = false;
  }

  createRessource() {
    if (this.addRessourceForm.invalid) {
      Swal.fire({ icon: 'warning', title: 'Invalid', text: 'Please fill required fields' });
      return;
    }

    const selectedType = this.selectedTypeId 
      ? this.types.find(t => t.idTypeRessource === this.selectedTypeId)
      : this.types[0];

    if (!selectedType) {
      Swal.fire({ icon: 'warning', title: 'Missing Type', text: 'Please select a type first' });
      return;
    }

    const ressourceToCreate = {
      titre: this.addRessourceForm.value.titre,
      description: this.addRessourceForm.value.description,
      quantite: this.addRessourceForm.value.quantite,
      unite: this.addRessourceForm.value.unite,
      cout: this.addRessourceForm.value.cout,
      dateAchat: this.addRessourceForm.value.dateAchat,
      typeRessourceId: selectedType.idTypeRessource
    };

    this.isLoading = true;
    this.ressourceService.create(ressourceToCreate).subscribe({
      next: (newRessource) => {
        this.ressources = [newRessource, ...this.ressources];
        this.applyFilter();
        this.cancelAddRessource();
        this.isLoading = false;
        
        const categoryId = this.selectedCategory?.idCategorieRessource;
        if (categoryId) {
          const currentCount = this.categoryRessourceCounts.get(categoryId) || 0;
          this.categoryRessourceCounts.set(categoryId, currentCount + 1);
        }
        
        const typeId = selectedType.idTypeRessource;
        if (typeId) {
          const currentTypeCount = this.typeRessourceCounts.get(typeId) || 0;
          this.typeRessourceCounts.set(typeId, currentTypeCount + 1);
        }
        
        Swal.fire({ icon: 'success', title: 'Success!', text: 'Resource added successfully', toast: true, position: 'top-end', timer: 2000 });
      },
      error: (err) => {
        this.isLoading = false;
        Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message || 'Failed to create resource' });
      }
    });
  }

  startEditRessource(ressource: Ressource, event: Event) {
    event.stopPropagation();
    this.isEditingRessource = true;
    this.editingRessourceId = ressource.idRessource;
    this.editRessourceForm.patchValue({
      titre: ressource.titre,
      description: ressource.description,
      quantite: ressource.quantite,
      unite: ressource.unite,
      cout: ressource.cout,
      dateAchat: ressource.dateAchat
    });
  }

  cancelEditRessource() {
    this.isEditingRessource = false;
    this.editingRessourceId = null;
  }

  saveEditRessource(ressourceId: number | null) {
    if (this.editRessourceForm.invalid || !ressourceId) return;
    
    const ressource = this.ressources.find(r => r.idRessource === ressourceId);
    if (!ressource) return;
    
    const updatedRessource = {
      ...ressource,
      titre: this.editRessourceForm.value.titre,
      description: this.editRessourceForm.value.description,
      quantite: this.editRessourceForm.value.quantite,
      unite: this.editRessourceForm.value.unite,
      cout: this.editRessourceForm.value.cout,
      dateAchat: this.editRessourceForm.value.dateAchat
    };
    
    this.isLoading = true;
    this.ressourceService.update(ressource.idRessource, updatedRessource).subscribe({
      next: (updated) => {
        const index = this.ressources.findIndex(r => r.idRessource === ressource.idRessource);
        if (index !== -1) this.ressources[index] = updated;
        this.applyFilter();
        this.cancelEditRessource();
        this.isLoading = false;
        Swal.fire({ icon: 'success', title: 'Updated!', text: 'Resource updated', toast: true, position: 'top-end', timer: 2000 });
      },
      error: (err) => {
        this.isLoading = false;
        Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message || 'Failed to update' });
      }
    });
  }

  deleteRessource(ressource: Ressource, event: Event) {
    event.stopPropagation();
    Swal.fire({
      title: 'Delete Resource?',
      text: `Are you sure you want to delete "${ressource.titre}"?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      confirmButtonText: 'Yes, delete it!'
    }).then((result) => {
      if (result.isConfirmed) {
        this.isLoading = true;
        this.ressourceService.delete(ressource.idRessource).subscribe({
          next: () => {
            this.ressources = this.ressources.filter(r => r.idRessource !== ressource.idRessource);
            this.applyFilter();
            this.isLoading = false;
            
            const categoryId = this.selectedCategory?.idCategorieRessource;
            if (categoryId) {
              const currentCount = this.categoryRessourceCounts.get(categoryId) || 0;
              this.categoryRessourceCounts.set(categoryId, Math.max(0, currentCount - 1));
            }
            
            const typeId = ressource.typeRessourceId;
            if (typeId) {
              const currentTypeCount = this.typeRessourceCounts.get(typeId) || 0;
              this.typeRessourceCounts.set(typeId, Math.max(0, currentTypeCount - 1));
            }
            
            Swal.fire({ icon: 'success', title: 'Deleted!', toast: true, position: 'top-end', timer: 2000 });
          },
          error: (err) => {
            this.isLoading = false;
            Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message || 'Failed to delete' });
          }
        });
      }
    });
  }

  // ==================== HELPER METHODS ====================

applyFilter() {
  let filtered = [...this.ressources];
  
  // Text search filter
  if (this.searchTerm && this.searchTerm.trim()) {
    const term = this.searchTerm.toLowerCase();
    filtered = filtered.filter(r => 
      r.titre?.toLowerCase().includes(term) ||
      r.description?.toLowerCase().includes(term)
    );
  }
  
  // Unit filter
  if (this.filterUnit) {
    filtered = filtered.filter(r => r.unite === this.filterUnit);
  }
  
  // Date range filter
  if (this.filterDateFrom) {
    const fromDate = new Date(this.filterDateFrom);
    filtered = filtered.filter(r => r.dateAchat && new Date(r.dateAchat) >= fromDate);
  }
  if (this.filterDateTo) {
    const toDate = new Date(this.filterDateTo);
    toDate.setHours(23, 59, 59);
    filtered = filtered.filter(r => r.dateAchat && new Date(r.dateAchat) <= toDate);
  }
  
  // Quantity range filter
  if (this.filterQuantityMin !== null) {
    filtered = filtered.filter(r => r.quantite >= this.filterQuantityMin!);
  }
  if (this.filterQuantityMax !== null) {
    filtered = filtered.filter(r => r.quantite <= this.filterQuantityMax!);
  }
  
  // Cost range filter
  if (this.filterCostMin !== null) {
    filtered = filtered.filter(r => r.cout >= this.filterCostMin!);
  }
  if (this.filterCostMax !== null) {
    filtered = filtered.filter(r => r.cout <= this.filterCostMax!);
  }
  
  // Apply sorting
  filtered = this.sortResources(filtered);
  
  this.filteredRessources = filtered;
  this.updatePagination();
}

sortResources(resources: Ressource[]): Ressource[] {
  return [...resources].sort((a, b) => {
    let aVal: any = a[this.sortField as keyof Ressource];
    let bVal: any = b[this.sortField as keyof Ressource];
    
    // Handle dates specially
    if (this.sortField === 'dateAchat') {
      aVal = aVal ? new Date(aVal).getTime() : 0;
      bVal = bVal ? new Date(bVal).getTime() : 0;
    }
    
    // Handle numbers
    if (typeof aVal === 'number' && typeof bVal === 'number') {
      return this.sortDirection === 'asc' ? aVal - bVal : bVal - aVal;
    }
    
    // Handle strings
    const aStr = String(aVal || '').toLowerCase();
    const bStr = String(bVal || '').toLowerCase();
    
    if (this.sortDirection === 'asc') {
      return aStr.localeCompare(bStr);
    } else {
      return bStr.localeCompare(aStr);
    }
  });
}

  selectType(typeId: number | null) {
    this.selectedTypeId = typeId;
    if (typeId === null) {
      this.loadRessourcesByCategory(this.selectedCategory!.idCategorieRessource);
    } else {
      this.loadRessourcesByType(typeId);
    }
  }

  selectCategory(category: CategorieRessource) {
    this.selectedCategory = category;
    this.selectedTypeId = null;
    this.searchTerm = '';
    this.loadTypes(category.idCategorieRessource);
    if (this.isMobile) this.sidebarCollapsed = true;
  }

  backToCategories() {
    this.selectedCategory = null;
    this.selectedTypeId = null;
    this.types = [];
    this.ressources = [];
    this.filteredRessources = [];
    this.searchTerm = '';
  }

  toggleSidebar() { this.sidebarCollapsed = !this.sidebarCollapsed; }
  checkScreenSize() { this.isMobile = window.innerWidth < 768; if (!this.isMobile) this.sidebarCollapsed = false; }

  get filteredCategories() {
    if (!this.searchTerm) return this.categories;
    return this.categories.filter(cat => cat.nom.toLowerCase().includes(this.searchTerm.toLowerCase()));
  }

  get totalRessourcesCount(): number { return this.ressources.length; }
  
  getRessourceCountByCategory(categoryId: number): number { return this.categoryRessourceCounts.get(categoryId) || 0; }
  getRessourceCountByType(typeId: number): number { return this.typeRessourceCounts.get(typeId) || 0; }

  // ==================== CATEGORY CRUD ====================
  startAddCategory() { this.isAddingCategory = true; this.newCategoryName = ''; setTimeout(() => this.categoryInput?.nativeElement.focus(), 100); }
  cancelAddCategory() { this.isAddingCategory = false; this.newCategoryName = ''; }
  
  createCategory() {
    if (!this.newCategoryName?.trim()) { 
      Swal.fire({ icon: 'warning', title: 'Invalid', text: 'Category name cannot be empty', toast: true }); 
      return; 
    }
    this.isLoading = true;
    this.categorieService.create({ nom: this.newCategoryName.trim() }).subscribe({
      next: (newCat) => { 
        this.categories = [newCat, ...this.categories]; 
        this.cancelAddCategory(); 
        this.isLoading = false; 
        Swal.fire({ icon: 'success', title: 'Success!', text: 'Category created', toast: true }); 
      },
      error: (err) => { 
        this.isLoading = false; 
        Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message || 'Failed to create category' }); 
      }
    });
  }

  startEditCategory(cat: CategorieRessource, event: Event) { 
    event.stopPropagation(); 
    this.editingCategoryId = cat.idCategorieRessource; 
    this.editingCategoryName = cat.nom; 
    setTimeout(() => this.editCategoryInput?.nativeElement.focus(), 100); 
  }
  cancelEditCategory() { this.editingCategoryId = null; this.editingCategoryName = ''; }
  
  saveEditCategory(cat: CategorieRessource) {
    if (!this.editingCategoryName?.trim()) { this.cancelEditCategory(); return; }
    this.isLoading = true;
    this.categorieService.update(cat.idCategorieRessource, { ...cat, nom: this.editingCategoryName.trim() }).subscribe({
      next: () => { 
        const index = this.categories.findIndex(c => c.idCategorieRessource === cat.idCategorieRessource); 
        if (index !== -1) this.categories[index].nom = this.editingCategoryName.trim(); 
        this.cancelEditCategory(); 
        this.isLoading = false; 
        if (this.selectedCategory?.idCategorieRessource === cat.idCategorieRessource) this.selectedCategory.nom = this.editingCategoryName.trim(); 
        Swal.fire({ icon: 'success', title: 'Updated!', toast: true }); 
      },
      error: (err) => { 
        this.isLoading = false; 
        Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message || 'Failed to update category' }); 
      }
    });
  }

  deleteCategory(cat: CategorieRessource, event: Event) {
    event.stopPropagation();
    Swal.fire({ title: 'Delete Category?', text: `Delete "${cat.nom}"? This will delete all types and resources.`, icon: 'warning', showCancelButton: true, confirmButtonColor: '#d33', confirmButtonText: 'Delete' }).then(result => {
      if (result.isConfirmed) {
        this.isLoading = true;
        this.categorieService.delete(cat.idCategorieRessource).subscribe({
          next: () => { 
            this.categories = this.categories.filter(c => c.idCategorieRessource !== cat.idCategorieRessource); 
            if (this.selectedCategory?.idCategorieRessource === cat.idCategorieRessource) this.backToCategories(); 
            this.isLoading = false; 
            Swal.fire({ icon: 'success', title: 'Deleted!', toast: true }); 
          },
          error: (err) => { 
            this.isLoading = false; 
            Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message || 'Failed to delete category' }); 
          }
        });
      }
    });
  }

  // ==================== TYPE CRUD ====================
  startAddType() { this.isAddingType = true; this.newTypeName = ''; setTimeout(() => this.typeInput?.nativeElement.focus(), 100); }
  cancelAddType() { this.isAddingType = false; this.newTypeName = ''; }
  
  createType() {
    if (!this.newTypeName?.trim()) { 
      Swal.fire({ icon: 'warning', title: 'Invalid', text: 'Type name cannot be empty', toast: true }); 
      return; 
    }
    this.isLoading = true;
    // ✅ Fixed: Use 'nom' instead of 'libelle'
    this.typeService.create({ 
      nom: this.newTypeName.trim(), 
      categorieId: this.selectedCategory!.idCategorieRessource 
    }).subscribe({
      next: (newType) => { 
        this.types = [newType, ...this.types]; 
        this.cancelAddType(); 
        this.isLoading = false; 
        Swal.fire({ icon: 'success', title: 'Success!', text: 'Type created', toast: true }); 
      },
      error: (err) => { 
        this.isLoading = false; 
        Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message || 'Failed to create type' }); 
      }
    });
  }

  startEditType(type: TypeRessource, event: Event) { 
    event.stopPropagation(); 
    this.editingTypeId = type.idTypeRessource; 
    this.editingTypeName = type.nom; 
    setTimeout(() => this.editTypeInput?.nativeElement.focus(), 100); 
  }
  cancelEditType() { this.editingTypeId = null; this.editingTypeName = ''; }
  
  saveEditType(type: TypeRessource) {
    if (!this.editingTypeName?.trim()) { this.cancelEditType(); return; }
    this.isLoading = true;
    const updatedType = { ...type, nom: this.editingTypeName.trim() };
    this.typeService.update(type.idTypeRessource, updatedType).subscribe({
      next: () => { 
        const index = this.types.findIndex(t => t.idTypeRessource === type.idTypeRessource); 
        if (index !== -1) this.types[index].nom = this.editingTypeName.trim(); 
        this.cancelEditType(); 
        this.isLoading = false; 
        Swal.fire({ icon: 'success', title: 'Updated!', toast: true }); 
      },
      error: (err) => { 
        this.isLoading = false; 
        Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message || 'Failed to update type' }); 
      }
    });
  }

  deleteType(type: TypeRessource, event: Event) {
    event.stopPropagation();
    Swal.fire({ title: 'Delete Type?', text: `Delete "${type.nom}"? This will delete all resources in this type.`, icon: 'warning', showCancelButton: true, confirmButtonColor: '#d33', confirmButtonText: 'Delete' }).then(result => {
      if (result.isConfirmed) {
        this.isLoading = true;
        this.typeService.delete(type.idTypeRessource).subscribe({
          next: () => { 
            this.types = this.types.filter(t => t.idTypeRessource !== type.idTypeRessource); 
            if (this.selectedTypeId === type.idTypeRessource) this.selectType(null); 
            this.isLoading = false; 
            Swal.fire({ icon: 'success', title: 'Deleted!', toast: true }); 
          },
          error: (err) => { 
            this.isLoading = false; 
            Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message || 'Failed to delete type' }); 
          }
        });
      }
    });
  }


  // ==================== PAGINATION METHODS ====================

get paginatedRessources(): Ressource[] {
  const startIndex = (this.currentPage - 1) * this.pageSize;
  const endIndex = startIndex + this.pageSize;
  return this.filteredRessources.slice(startIndex, endIndex);
}

updatePagination() {
  this.totalPages = Math.ceil(this.filteredRessources.length / this.pageSize);
  if (this.currentPage > this.totalPages) {
    this.currentPage = Math.max(1, this.totalPages);
  }
  if (this.currentPage < 1) {
    this.currentPage = 1;
  }
}

previousPage() {
  if (this.currentPage > 1) {
    this.currentPage--;
  }
}

nextPage() {
  if (this.currentPage < this.totalPages) {
    this.currentPage++;
  }
}

goToPage(page: number) {
  if (page >= 1 && page <= this.totalPages) {
    this.currentPage = page;
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
goToFirstPage() {
  this.goToPage(1);
}

goToLastPage() {
  this.goToPage(this.totalPages);
}


// Add these getter methods:
get uniqueUnits(): string[] {
  const units = new Set(this.ressources.map(r => r.unite).filter(u => u));
  return Array.from(units).sort();
}

// Add these methods:

toggleAdvancedFilters() {
  this.showAdvancedFilters = !this.showAdvancedFilters;
}

hasActiveFilters(): boolean {
  return !!(this.filterUnit || 
    this.filterDateFrom || 
    this.filterDateTo || 
    this.filterQuantityMin !== null || 
    this.filterQuantityMax !== null || 
    this.filterCostMin !== null || 
    this.filterCostMax !== null ||
    this.searchTerm);
}

setSortDirection(direction: string) {
  this.sortDirection = direction;
  this.applyFilter();
}

removeFilter(filterType: string) {
  switch(filterType) {
    case 'unit':
      this.filterUnit = '';
      break;
    case 'date':
      this.filterDateFrom = '';
      this.filterDateTo = '';
      break;
    case 'quantity':
      this.filterQuantityMin = null;
      this.filterQuantityMax = null;
      break;
    case 'cost':
      this.filterCostMin = null;
      this.filterCostMax = null;
      break;
  }
  this.applyFilter();
}

clearAllFilters() {
  this.searchTerm = '';
  this.filterUnit = '';
  this.filterDateFrom = '';
  this.filterDateTo = '';
  this.filterQuantityMin = null;
  this.filterQuantityMax = null;
  this.filterCostMin = null;
  this.filterCostMax = null;
  this.sortField = 'titre';
  this.sortDirection = 'asc';
  this.applyFilter();
}


  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
    window.removeEventListener('resize', () => this.checkScreenSize());
  }
}