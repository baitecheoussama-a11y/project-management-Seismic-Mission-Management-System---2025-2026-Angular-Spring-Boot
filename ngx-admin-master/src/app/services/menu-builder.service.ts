import { Injectable } from '@angular/core';
import { CategorieMaterielService, CategorieMateriel } from './materiel/categorie-materiel.service';
import { NbMenuItem } from '@nebular/theme';

@Injectable({ providedIn: 'root' })
export class MenuBuilderService {
  private searchTerm: string = '';

  constructor(private categorieService: CategorieMaterielService) {}

  // 🔹 دالة لفلترة الكاتيغوريات حسب البحث
  setSearchTerm(term: string) {
    this.searchTerm = term.toLowerCase();
  }

  getSearchTerm(): string {
    return this.searchTerm;
  }

  buildMaterielMenuItems(categories: CategorieMateriel[]): NbMenuItem[] {
    // فلترة حسب مصطلح البحث
    let filteredCategories = categories;
    if (this.searchTerm) {
      filteredCategories = categories.filter(cat => 
        cat.nom.toLowerCase().includes(this.searchTerm)
      );
    }

    return filteredCategories.map(cat => ({
      title: cat.nom,
      link: `/pages/materiel/${cat.idCategorie}`,
      data: { 
        action: 'view-category',
        categorieId: cat.idCategorie,
        categorieNom: cat.nom
      },
      // ✨ إضافة Edit و Delete كـ children (تظهر عند hover)
      children: [
        {
          title: '✏️ Edit',
          icon: 'edit-outline',
          data: { 
            action: 'edit-category',
            categorieId: cat.idCategorie,
            categorieNom: cat.nom
          }
        },
        {
          title: '🗑️ Delete',
          icon: 'trash-2-outline',
          data: { 
            action: 'delete-category',
            categorieId: cat.idCategorie,
            categorieNom: cat.nom
          }
        }
      ]
    }));
  }

  loadMaterielMenu(): Promise<NbMenuItem[]> {
    return new Promise((resolve, reject) => {
      this.categorieService.getAll().subscribe({
        next: (cats) => resolve(this.buildMaterielMenuItems(cats)),
        error: (err) => reject(err)
      });
    });
  }

  // 🔹 إعادة تحميل المينيو بعد الإضافة أو الحذف
  async reloadMenu(): Promise<NbMenuItem[]> {
    const cats = await this.categorieService.getAll().toPromise();
    return this.buildMaterielMenuItems(cats || []);
  }
}