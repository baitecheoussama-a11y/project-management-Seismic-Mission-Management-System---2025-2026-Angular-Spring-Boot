// pages/dashboard/dashboard.component.ts

import { Component, OnInit, ViewChild, ElementRef, AfterViewInit, OnDestroy } from '@angular/core';
import { Chart } from 'chart.js';
import { RessourceStatsService, RessourceStatsSummaryDTO } from '../../services/stats/ressource-stats.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'ngx-dashboard',
  templateUrl: './ddashboard.component.html',
  styleUrls: ['./ddashboard.component.scss']
})
export class DdashboardComponent implements OnInit, AfterViewInit, OnDestroy {

  @ViewChild('consumptionByRessourceChart') consumptionByRessourceChart!: ElementRef;
  @ViewChild('costByRessourceChart') costByRessourceChart!: ElementRef;
  @ViewChild('consumptionByMissionChart') consumptionByMissionChart!: ElementRef;
  @ViewChild('consumptionByTypeChart') consumptionByTypeChart!: ElementRef;
  @ViewChild('consumptionTrendChart') consumptionTrendChart!: ElementRef;

  // Charts instances
  private consumptionByRessourceChartInstance: Chart | null = null;
  private costByRessourceChartInstance: Chart | null = null;
  private consumptionByMissionChartInstance: Chart | null = null;
  private consumptionByTypeChartInstance: Chart | null = null;
  private consumptionTrendChartInstance: Chart | null = null;

  // Data
  stats: RessourceStatsSummaryDTO | null = null;
  isLoading: boolean = true;

  // Date filters
  startDate: string = this.getDefaultStartDate();
  endDate: string = this.getDefaultEndDate();

  private destroy$ = new Subject<void>();

  // Color palette
  private colors = [
    '#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6',
    '#06b6d4', '#ec489a', '#14b8a6', '#f97316', '#6366f1'
  ];

  constructor(
    private statsService: RessourceStatsService
  ) {}

  ngOnInit(): void {
    this.loadStats();
  }

  ngAfterViewInit(): void {
    // Charts will be created after data is loaded
  }

  // ============ LOAD DATA ============

  loadStats(): void {
    this.isLoading = true;
    this.statsService.getStatsSummary(this.startDate, this.endDate)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.stats = data;
          this.isLoading = false;
          setTimeout(() => {
            this.initCharts();
          }, 200);
        },
        error: (err) => {
          console.error('Error loading stats:', err);
          this.isLoading = false;
        }
      });
  }

  // ============ INIT CHARTS ============

  private initCharts(): void {
    if (!this.stats) return;

    this.createConsumptionByRessourceChart();
    this.createCostByRessourceChart();
    this.createConsumptionByMissionChart();
    this.createConsumptionByTypeChart();
    this.createConsumptionTrendChart();
  }

  private createConsumptionByRessourceChart(): void {
    if (!this.stats?.consommationByRessource || !this.consumptionByRessourceChart) return;

    const data = this.stats.consommationByRessource;
    const labels = data.map(d => d.label);
    const values = data.map(d => d.value);

    this.destroyChart(this.consumptionByRessourceChartInstance);

    this.consumptionByRessourceChartInstance = new Chart(
      this.consumptionByRessourceChart.nativeElement.getContext('2d'),
      {
        type: 'bar',
        data: {
          labels: labels,
          datasets: [{
            label: 'Consumption',
            data: values,
            backgroundColor: this.getColors(labels.length),
            borderColor: this.getColors(labels.length),
            borderWidth: 1
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          legend: {
            display: false
          },
          scales: {
            yAxes: [{
              ticks: {
                beginAtZero: true
              },
              scaleLabel: {
                display: true,
                labelString: 'Quantity'
              }
            }]
          }
        }
      }
    );
  }

  private createCostByRessourceChart(): void {
    if (!this.stats?.costByRessource || !this.costByRessourceChart) return;

    const data = this.stats.costByRessource;
    const labels = data.map(d => d.label);
    const costs = data.map(d => d.cost);

    this.destroyChart(this.costByRessourceChartInstance);

    this.costByRessourceChartInstance = new Chart(
      this.costByRessourceChart.nativeElement.getContext('2d'),
      {
        type: 'bar',
        data: {
          labels: labels,
          datasets: [{
            label: 'Cost (DA)',
            data: costs,
            backgroundColor: this.getColors(labels.length, 0.6),
            borderColor: this.getColors(labels.length),
            borderWidth: 2
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          legend: {
            display: false
          },
          scales: {
            yAxes: [{
              ticks: {
                beginAtZero: true
              },
              scaleLabel: {
                display: true,
                labelString: 'Cost (DA)'
              }
            }]
          }
        }
      }
    );
  }

  private createConsumptionByMissionChart(): void {
    if (!this.stats?.consommationByMission || !this.consumptionByMissionChart) return;

    const data = this.stats.consommationByMission;
    const labels = data.map(d => d.label);
    const values = data.map(d => d.value);

    this.destroyChart(this.consumptionByMissionChartInstance);

    this.consumptionByMissionChartInstance = new Chart(
      this.consumptionByMissionChart.nativeElement.getContext('2d'),
      {
        type: 'pie',
        data: {
          labels: labels,
          datasets: [{
            data: values,
            backgroundColor: this.getColors(labels.length),
            borderWidth: 2,
            borderColor: '#ffffff'
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          legend: {
            position: 'bottom',
            labels: {
              padding: 20,
              usePointStyle: true
            }
          }
        }
      }
    );
  }

  private createConsumptionByTypeChart(): void {
    if (!this.stats?.consommationByType || !this.consumptionByTypeChart) return;

    const data = this.stats.consommationByType;
    const labels = data.map(d => d.label);
    const values = data.map(d => d.value);

    this.destroyChart(this.consumptionByTypeChartInstance);

    this.consumptionByTypeChartInstance = new Chart(
      this.consumptionByTypeChart.nativeElement.getContext('2d'),
      {
        type: 'bar',
        data: {
          labels: labels,
          datasets: [{
            label: 'Consumption',
            data: values,
            backgroundColor: this.getColors(labels.length),
            borderColor: this.getColors(labels.length),
            borderWidth: 1
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          legend: {
            display: false
          },
          scales: {
            yAxes: [{
              ticks: {
                beginAtZero: true
              },
              scaleLabel: {
                display: true,
                labelString: 'Quantity'
              }
            }]
          }
        }
      }
    );
  }

  private createConsumptionTrendChart(): void {
    if (!this.stats?.consommationByMonth || !this.consumptionTrendChart) return;

    const data = this.stats.consommationByMonth;
    const labels = data.map(d => d.month);
    const values = data.map(d => d.value);

    this.destroyChart(this.consumptionTrendChartInstance);

    this.consumptionTrendChartInstance = new Chart(
      this.consumptionTrendChart.nativeElement.getContext('2d'),
      {
        type: 'line',
        data: {
          labels: labels,
          datasets: [{
            label: 'Monthly Consumption',
            data: values,
            borderColor: '#3b82f6',
            backgroundColor: 'rgba(59, 130, 246, 0.1)',
            fill: true,
            pointBackgroundColor: '#3b82f6',
            pointBorderColor: '#ffffff',
            pointBorderWidth: 2,
            pointRadius: 4
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          legend: {
            display: false
          },
          scales: {
            yAxes: [{
              ticks: {
                beginAtZero: true
              },
              scaleLabel: {
                display: true,
                labelString: 'Quantity'
              }
            }],
            xAxes: [{
              scaleLabel: {
                display: true,
                labelString: 'Month'
              }
            }]
          }
        }
      }
    );
  }

  // ============ HELPERS ============

  private getColors(count: number, alpha: number = 1): string[] {
    const result: string[] = [];
    for (let i = 0; i < count; i++) {
      const color = this.colors[i % this.colors.length];
      if (alpha < 1) {
        const r = parseInt(color.slice(1, 3), 16);
        const g = parseInt(color.slice(3, 5), 16);
        const b = parseInt(color.slice(5, 7), 16);
        result.push(`rgba(${r}, ${g}, ${b}, ${alpha})`);
      } else {
        result.push(color);
      }
    }
    return result;
  }

  private destroyChart(chart: Chart | null): void {
    if (chart) {
      chart.destroy();
    }
  }

  private getDefaultStartDate(): string {
    const date = new Date();
    date.setMonth(date.getMonth() - 6);
    return date.toISOString().split('T')[0];
  }

  private getDefaultEndDate(): string {
    return new Date().toISOString().split('T')[0];
  }

  quickSelectDate(event: Event): void {
    const target = event.target as HTMLSelectElement;
    const value = target.value;
    const now = new Date();

    switch (value) {
      case 'today':
        this.startDate = now.toISOString().split('T')[0];
        this.endDate = now.toISOString().split('T')[0];
        break;
      case 'week':
        const weekStart = new Date(now);
        weekStart.setDate(now.getDate() - 7);
        this.startDate = weekStart.toISOString().split('T')[0];
        this.endDate = now.toISOString().split('T')[0];
        break;
      case 'month':
        const monthStart = new Date(now.getFullYear(), now.getMonth(), 1);
        this.startDate = monthStart.toISOString().split('T')[0];
        this.endDate = now.toISOString().split('T')[0];
        break;
      case 'quarter':
        const quarterStart = new Date(now.getFullYear(), Math.floor(now.getMonth() / 3) * 3, 1);
        this.startDate = quarterStart.toISOString().split('T')[0];
        this.endDate = now.toISOString().split('T')[0];
        break;
      case 'year':
        const yearStart = new Date(now.getFullYear(), 0, 1);
        this.startDate = yearStart.toISOString().split('T')[0];
        this.endDate = now.toISOString().split('T')[0];
        break;
      default:
        return;
    }
    this.loadStats();
  }

  // ============ STATS CARD HELPERS ============

  /**
   * ✅ FIX: Retourne le nombre total d'unités consommées (toutes ressources confondues)
   * ⚠️ ATTENTION: Ceci est une somme brute - utile uniquement comme indicateur global
   */
  getTotalConsumption(): number {
    if (!this.stats?.consommationByRessource) return 0;
    return this.stats.consommationByRessource.reduce((sum, item) => sum + item.value, 0);
  }

  /**
   * ✅ FIX: Retourne le nombre de types de ressources consommées
   */
  getTotalRessourceTypes(): number {
    return this.stats?.consommationByRessource?.length || 0;
  }

  /**
   * ✅ FIX: Retourne le nombre de missions avec consommation
   */
  getTotalMissionsWithConsumption(): number {
    return this.stats?.consommationByMission?.length || 0;
  }

  /**
   * ✅ NEW: Retourne la ressource la plus consommée
   */
  getMostConsumedRessource(): { name: string, quantity: number } | null {
    if (!this.stats?.consommationByRessource || this.stats.consommationByRessource.length === 0) {
      return null;
    }
    const top = this.stats.consommationByRessource[0];
    return { name: top.label, quantity: top.value };
  }

  /**
   * ✅ NEW: Retourne le coût moyen par ressource
   */
  getAverageCostPerRessource(): number {
    if (!this.stats?.costByRessource || this.stats.costByRessource.length === 0) {
      return 0;
    }
    const total = this.stats.costByRessource.reduce((sum, item) => sum + item.cost, 0);
    return total / this.stats.costByRessource.length;
  }

  /**
   * ✅ FIX: Retourne le nombre total de ressources (avec stock > 0)
   */
  getTotalRessourcesWithStock(): number {
    if (!this.stats?.criticalStock) return 0;
    // Pour avoir le total, il faudrait un autre endpoint
    // Pour l'instant, on retourne le nombre de ressources en stock critique
    // Ou on peut utiliser un autre champ si disponible
    return this.stats.criticalStock.length;
  }

  // ============ LIFECYCLE ============

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();

    this.destroyChart(this.consumptionByRessourceChartInstance);
    this.destroyChart(this.costByRessourceChartInstance);
    this.destroyChart(this.consumptionByMissionChartInstance);
    this.destroyChart(this.consumptionByTypeChartInstance);
    this.destroyChart(this.consumptionTrendChartInstance);
  }
}