import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

// employe.service.ts - Update the Employe interface
export interface Employe {
  id?: number;
  nom: string;
  prenom: string;
  dateNaissance: string;
  email: string;
  numTel: string;
  adresse: string;
  lieuNaissance: string;
  sexe: string;
  numIdentite: string;
  typeContrat?: string;
  salaire?: number;
  groupeSanguin?: string;
  fonctionNom?: string;  // Add this
  fonctionId?: number;    // Add this
}


export interface FonctionSummary {
  id: number;
  nom: string;
}



// ✅ إضافة Interface للـ Request (لإنشاء وتعديل الموظف)
export interface EmployeRequest {
  nom: string;
  prenom: string;
  dateNaissance?: string;
  email: string;
  numTel?: string;
  adresse?: string;
  lieuNaissance?: string;
  sexe?: string;
  numIdentite?: string;
  typeContrat?: string;
  contratDateDebut?: string;
  contratDateFin?: string | null;
  salaire?: number;
  dureeTravail?: string;
  regimeTravail?: string;
  groupeSanguin?: string;
  antecedentsMedicaux?: string;
  allergies?: string;
  vaccinations?: string;
  medicationsActuelles?: string;
  medecinTraitant?: string;
  derniereVisiteMedicale?: string;
}

@Injectable({
  providedIn: 'root'
})
export class EmployeService {
  private apiUrl = 'http://localhost:8080/api/employes';
  

  constructor(private http: HttpClient) {}

  // ✅ جلب جميع الموظفين
  getAllEmployes(): Observable<Employe[]> {
    return this.http.get<Employe[]>(this.apiUrl);
  }

  // ✅ جلب موظف حسب ID
  getEmployeById(id: number): Observable<Employe> {
    return this.http.get<Employe>(`${this.apiUrl}/${id}`);
  }

  // ✅ إنشاء موظف جديد
  createEmploye(employe: EmployeRequest): Observable<Employe> {
    return this.http.post<Employe>(this.apiUrl, employe);
  }

  // ✅ تحديث موظف
  updateEmploye(id: number, employe: EmployeRequest): Observable<Employe> {
    return this.http.put<Employe>(`${this.apiUrl}/${id}`, employe);
  }

  // ✅ حذف موظف
  deleteEmploye(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

// أضف هذه الدالة داخل EmployeService
getEmployeAccountDetails(employeId: number): Observable<EmployeAccountDetails> {
  return this.http.get<EmployeAccountDetails>(`${this.apiUrl}/${employeId}/account-details`);
}

  assignFonctionToEmploye(employeId: number, fonctionId: number): Observable<Employe> {
    return this.http.put<Employe>(`${this.apiUrl}/${employeId}/assign-fonction/${fonctionId}`, {});
  }

  removeFonctionFromEmploye(employeId: number): Observable<Employe> {
    return this.http.delete<Employe>(`${this.apiUrl}/${employeId}/remove-fonction`);
  }
  // employe.service.ts - Add this method
getEmployesByFonction(fonctionId: number): Observable<Employe[]> {
  return this.http.get<Employe[]>(`${this.apiUrl}/by-fonction/${fonctionId}`);
}
}


// أضف هذه الواجهات
export interface EmployeAccountDetails {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  numTel: string;
  adresse: string;
  numIdentite: string;
  dateNaissance: string;
  lieuNaissance: string;
  sexe: string;
  username: string;
  compteStatus: string;
  contrats: ContratInfo[];
  dossierMedical: DossierMedicalInfo | null;
  roles: RoleInfo[];
    fonction?: {  // Add this
    id: number;
    nom: string;
    description?: string;
    nombreEmployes?: number;
  };
}

export interface ContratInfo {
  id: number;
  type: string;
  dateDebut: string;
  dateFin: string;
  salaire: string;
  dureeTravail: string;
  regimeTravail: string;
}

export interface DossierMedicalInfo {
  groupeSanguin: string;
  antecedentsMedicaux: string;
  allergies: string;
  vaccinations: string;
  medicationsActuelles: string;
  medecinTraitant: string;
  derniereVisiteMedicale: string;
}

export interface RoleInfo {
  id: number;
  name: string;
  type: string;
  dateDebut: string;
  dateFin: string;
  active: boolean;
}

