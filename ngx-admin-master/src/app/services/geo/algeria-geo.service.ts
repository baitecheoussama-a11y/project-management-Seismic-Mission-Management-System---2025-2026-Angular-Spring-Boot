// services/geo/algeria-geo.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as pointInPolygon from 'point-in-polygon';

export interface WilayaGeoJSON {
  name: string;
  name_ar: string;
  name_ber: string;
  city_code: string;
  geometry: any;
  bounds?: {
    minLat: number;
    maxLat: number;
    minLng: number;
    maxLng: number;
  };
}

@Injectable({
  providedIn: 'root'
})
export class AlgeriaGeoService {
  private geoData: any = null;
  private wilayasMap: Map<string, WilayaGeoJSON> = new Map();
  private isLoading: boolean = false;

  constructor(private http: HttpClient) {}

  loadGeoData(): Promise<void> {
    return new Promise((resolve, reject) => {
      if (this.geoData) {
        resolve();
        return;
      }
      
      if (this.isLoading) {
        // Wait for loading to complete
        const interval = setInterval(() => {
          if (this.geoData) {
            clearInterval(interval);
            resolve();
          }
        }, 100);
        return;
      }
      
      this.isLoading = true;
      this.http.get('assets/map/algeria.json').subscribe({
        next: (data: any) => {
          this.geoData = data;
          
          // Build index for quick lookup
          if (this.geoData && this.geoData.features) {
            this.geoData.features.forEach((feature: any) => {
              const wilayaInfo: WilayaGeoJSON = {
                name: feature.properties.name,
                name_ar: feature.properties.name_ar,
                name_ber: feature.properties.name_ber,
                city_code: feature.properties.city_code,
                geometry: feature.geometry,
                bounds: this.calculateBounds(feature.geometry)
              };
              this.wilayasMap.set(feature.properties.name, wilayaInfo);
              // Also store by city_code
              this.wilayasMap.set(`code_${feature.properties.city_code}`, wilayaInfo);
            });
          }
          
          console.log('Algeria GeoJSON loaded successfully', this.wilayasMap.size, 'wilayas');
          this.isLoading = false;
          resolve();
        },
        error: (err) => {
          console.error('Error loading Algeria GeoJSON:', err);
          this.isLoading = false;
          reject(err);
        }
      });
    });
  }

  private calculateBounds(geometry: any): { minLat: number; maxLat: number; minLng: number; maxLng: number } {
    let minLat = Infinity, maxLat = -Infinity;
    let minLng = Infinity, maxLng = -Infinity;
    
    const extractCoordinates = (coords: any) => {
      if (typeof coords[0] === 'number') {
        // Point [lng, lat]
        const lng = coords[0];
        const lat = coords[1];
        minLat = Math.min(minLat, lat);
        maxLat = Math.max(maxLat, lat);
        minLng = Math.min(minLng, lng);
        maxLng = Math.max(maxLng, lng);
      } else {
        // Recursive for nested arrays
        coords.forEach(extractCoordinates);
      }
    };
    
    if (geometry.type === 'Polygon') {
      geometry.coordinates.forEach(extractCoordinates);
    } else if (geometry.type === 'MultiPolygon') {
      geometry.coordinates.forEach(polygon => {
        polygon.forEach(extractCoordinates);
      });
    }
    
    return { minLat, maxLat, minLng, maxLng };
  }

  getWilayaByName(name: string): WilayaGeoJSON | undefined {
    return this.wilayasMap.get(name);
  }

  getWilayaByCode(code: string): WilayaGeoJSON | undefined {
    return this.wilayasMap.get(`code_${code}`);
  }

  getAllWilayas(): WilayaGeoJSON[] {
    return Array.from(this.wilayasMap.values()).filter(w => !w.name.startsWith('code_'));
  }

  isPointInWilaya(lat: number, lng: number, wilayaName: string): boolean {
    const wilaya = this.getWilayaByName(wilayaName);
    if (!wilaya) return false;
    
    return this.isPointInPolygon(lat, lng, wilaya.geometry);
  }

  findWilayaByPoint(lat: number, lng: number): WilayaGeoJSON | null {
    for (const wilaya of this.getAllWilayas()) {
      if (this.isPointInPolygon(lat, lng, wilaya.geometry)) {
        return wilaya;
      }
    }
    return null;
  }

  private isPointInPolygon(lat: number, lng: number, geometry: any): boolean {
    // Convert [lng, lat] format to [lng, lat] for point-in-polygon
    const point = [lng, lat];
    
    const checkPolygon = (polygon: any[][]): boolean => {
      // Convert polygon coordinates from [lng, lat] to [lng, lat]
      return pointInPolygon(point, polygon);
    };
    
    if (geometry.type === 'Polygon') {
      // For polygon, take the exterior ring (first array)
      const exteriorRing = geometry.coordinates[0];
      return checkPolygon(exteriorRing);
    } else if (geometry.type === 'MultiPolygon') {
      // For multipolygon, check if point is in any polygon
      for (const polygon of geometry.coordinates) {
        const exteriorRing = polygon[0];
        if (checkPolygon(exteriorRing)) {
          return true;
        }
      }
    }
    
    return false;
  }

  getWilayaBounds(wilayaName: string): { minLat: number; maxLat: number; minLng: number; maxLng: number } | null {
    const wilaya = this.getWilayaByName(wilayaName);
    return wilaya?.bounds || null;
  }
}