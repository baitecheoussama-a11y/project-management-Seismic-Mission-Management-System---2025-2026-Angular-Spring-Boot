// src/app/services/rapport/fichier.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface FichierDTO {
  id: number;
  chemin: string;
  titre: string;
  type: 'VIDEO' | 'IMAGE' | 'DOCUMENT';
  dateUpload: string;
  taille: number;
  rapportId: number;
}

@Injectable({
  providedIn: 'root'
})
export class FichierService {
  private apiUrl = 'http://localhost:8080/api/fichiers';

  constructor(private http: HttpClient) {}

  // Upload a file
  uploadFile(rapportId: number, file: File, titre: string, type: string): Observable<FichierDTO> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('rapportId', rapportId.toString());
    formData.append('titre', titre);
    formData.append('type', type);

    return this.http.post<FichierDTO>(`${this.apiUrl}/upload`, formData);
  }

  // Get all fichiers for a rapport
  getFichiersByRapport(rapportId: number): Observable<FichierDTO[]> {
    return this.http.get<FichierDTO[]>(`${this.apiUrl}/rapport/${rapportId}`);
  }

  // Get fichiers by rapport and type
  getFichiersByRapportAndType(rapportId: number, type: string): Observable<FichierDTO[]> {
    return this.http.get<FichierDTO[]>(`${this.apiUrl}/rapport/${rapportId}/type/${type}`);
  }

  // Get fichier by ID
  getFichierById(id: number): Observable<FichierDTO> {
    return this.http.get<FichierDTO>(`${this.apiUrl}/${id}`);
  }

  // ✅ Download file
  downloadFile(id: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${id}/download`, {
      responseType: 'blob'
    });
  }

  // ✅ View file (opens in browser)
  viewFile(id: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${id}/view`, {
      responseType: 'blob'
    });
  }

  // ✅ Get file URL for preview (use this in img/video tags)
  getFileUrl(id: number): string {
    return `${this.apiUrl}/${id}/view`;
  }

  // Delete fichier
  deleteFichier(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Delete all fichiers for a rapport
  deleteAllFichiersByRapport(rapportId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/rapport/${rapportId}`);
  }

  // Count fichiers by rapport
  countFichiersByRapport(rapportId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/rapport/${rapportId}/count`);
  }
}