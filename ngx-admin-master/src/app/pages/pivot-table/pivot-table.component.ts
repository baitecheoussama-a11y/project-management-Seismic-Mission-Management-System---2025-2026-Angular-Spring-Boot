// src/app/pages/pivot-table/pivot-table.component.ts
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';

interface PivotRowDTO {
  rowLabel: string;
  values: { [key: string]: number };
  rowTotal: number;
  isTotal: boolean;
}

interface PivotResponseDTO {
  headers: string[];
  rows: PivotRowDTO[];
  columnTotals: { [key: string]: number };
  grandTotal: number;
  totalRows: number;
  totalColumns: number;
  aggregator: string;
  valueField: string;
  rowField: string;
  colField: string;
}

@Component({
  selector: 'app-pivot-table',
  templateUrl: './pivot-table.component.html',
  styleUrls: ['./pivot-table.component.scss'],
  standalone: false
})
export class PivotTableComponent implements OnInit {

  // ✅ API URL direct
  private apiUrl = 'http://localhost:8080/api';

  pivotData: any[] = [];
  isLoading: boolean = false;
  errorMessage: string = '';

  // Configuration
  rowField: string = 'missionCode';
  colField: string = 'month';
  valueField: string = 'totalCost';
  aggregator: string = 'sum';
  
  availableFields: string[] = [];
  availableValues: string[] = [];
  availableAggregators: string[] = ['sum', 'avg', 'count', 'min', 'max'];
  
  // Table data
  pivotHeaders: string[] = [];
  pivotRows: any[] = [];
  columnTotals: any = {};
  grandTotal: number = 0;

  // Filters
  missionId: number | null = null;
  status: string = '';
  year: number | null = null;

  // ✅ Status mapping for display in English
  statusOptions = [
    { value: 'PLANIFIER', label: 'Planned' },
    { value: 'ENCOURS', label: 'In Progress' },
    { value: 'ENATTENTE', label: 'On Hold' },
    { value: 'ENRETARD', label: 'Delayed' },
    { value: 'TERMINI', label: 'Completed' },
    { value: 'ANNULE', label: 'Cancelled' }
  ];

  // ✅ Field display name mapping
  private fieldDisplayNames: { [key: string]: string } = {
    'missionCode': 'Mission Code',
    'methodologie': 'Methodology',
    'status': 'Status',
    'month': 'Month',
    'quarter': 'Quarter',
    'year': 'Year',
    'projectName': 'Project Name',
    'totalCost': 'Total Cost',
    'projectCount': 'Project Count',
    'avgProgression': 'Avg Progression',
    'budget': 'Budget',
    'objectifVP': 'Target VP'
  };

  // ✅ Status display name mapping (French to English)
  private statusDisplayNames: { [key: string]: string } = {
    'PLANIFIER': 'Planned',
    'ENCOURS': 'In Progress',
    'ENATTENTE': 'On Hold',
    'ENRETARD': 'Delayed',
    'TERMINI': 'Completed',
    'ANNULE': 'Cancelled'
  };

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadFields();
    this.loadData();
  }

  loadFields(): void {
    this.http.get<string[]>(`${this.apiUrl}/pivot/fields`).subscribe({
      next: (fields) => {
        this.availableFields = fields;
      },
      error: (err) => console.error('Error loading fields:', err)
    });

    this.http.get<string[]>(`${this.apiUrl}/pivot/values`).subscribe({
      next: (values) => {
        this.availableValues = values;
      },
      error: (err) => console.error('Error loading values:', err)
    });
  }

  loadData(): void {
    this.isLoading = true;
    this.errorMessage = '';
    
    const request = {
      rowField: this.rowField,
      colField: this.colField,
      valueField: this.valueField,
      aggregator: this.aggregator,
      missionId: this.missionId,
      status: this.status || '',
      year: this.year
    };

    this.http.post<PivotResponseDTO>(`${this.apiUrl}/pivot/data`, request)
      .subscribe({
        next: (response) => {
          this.pivotHeaders = response.headers;
          this.pivotRows = response.rows;
          this.columnTotals = response.columnTotals;
          this.grandTotal = response.grandTotal;
          this.isLoading = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Error loading pivot data:', err);
          this.errorMessage = 'Failed to load data: ' + (err.message || 'Unknown error');
          this.isLoading = false;
          this.cdr.detectChanges();
        }
      });
  }

  // ✅ Get display name for field
  getDisplayName(field: string): string {
    return this.fieldDisplayNames[field] || field;
  }

  // ✅ Get display label (handles status translation)
  getDisplayLabel(label: string): string {
    // Check if it's a status value
    if (this.statusDisplayNames[label]) {
      return this.statusDisplayNames[label];
    }
    // Check if it's a field name
    if (this.fieldDisplayNames[label]) {
      return this.fieldDisplayNames[label];
    }
    return label;
  }

  formatNumber(value: number): string {
    if (value === undefined || value === null) return '0';
    if (Number.isInteger(value)) {
      return value.toLocaleString();
    }
    return value.toFixed(1);
  }

  updatePivot(): void {
    this.loadData();
  }

  resetData(): void {
    this.rowField = 'missionCode';
    this.colField = 'month';
    this.valueField = 'totalCost';
    this.aggregator = 'sum';
    this.missionId = null;
    this.status = '';
    this.year = null;
    this.loadData();
  }

  exportData(): void {
    if (this.pivotRows.length === 0) return;
    
    let csv = `${this.getDisplayName(this.rowField)},${this.pivotHeaders.map(h => this.getDisplayName(h)).join(',')},Total\n`;
    this.pivotRows.forEach(row => {
      if (row.isTotal) return;
      const rowData = [
        this.getDisplayLabel(row.rowLabel), 
        ...this.pivotHeaders.map(c => row.values[c] || 0), 
        row.rowTotal
      ];
      csv += rowData.join(',') + '\n';
    });
    
    // Add totals row
    const totalRow = this.pivotRows.find(r => r.isTotal);
    if (totalRow) {
      csv += `Total,${this.pivotHeaders.map(c => totalRow.values[c] || 0).join(',')},${totalRow.rowTotal}\n`;
    }
    
    const blob = new Blob([csv], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `pivot-table-${new Date().toISOString().slice(0,10)}.csv`;
    a.click();
    window.URL.revokeObjectURL(url);
  }

  getTotalRows(): number {
    return this.pivotRows.filter(r => !r.isTotal).length;
  }

  getTotalColumns(): number {
    return this.pivotHeaders.length;
  }
}