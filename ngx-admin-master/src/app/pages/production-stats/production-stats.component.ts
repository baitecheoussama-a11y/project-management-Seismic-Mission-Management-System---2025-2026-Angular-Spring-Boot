// pages/stats/production-stats/production-stats.component.ts

import { Component, OnInit, ViewChild, ElementRef, AfterViewInit, OnDestroy } from '@angular/core';
import { Chart } from 'chart.js';
import { ProductionStatsService, ProductionStatsDTO } from '../../services/stats/production-stats.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'ngx-production-stats',
  templateUrl: './production-stats.component.html',
  styleUrls: ['./production-stats.component.scss']
})
export class ProductionStatsComponent implements OnInit, AfterViewInit, OnDestroy {

  @ViewChild('productionByTeamChart') productionByTeamChart!: ElementRef;
  @ViewChild('productionByActivityChart') productionByActivityChart!: ElementRef;
  @ViewChild('productionTrendChart') productionTrendChart!: ElementRef;
  @ViewChild('productionByMissionChart') productionByMissionChart!: ElementRef;
  @ViewChild('productivityByTeamChart') productivityByTeamChart!: ElementRef;

  // Chart instances
  private productionByTeamChartInstance: Chart | null = null;
  private productionByActivityChartInstance: Chart | null = null;
  private productionTrendChartInstance: Chart | null = null;
  private productionByMissionChartInstance: Chart | null = null;
  private productivityByTeamChartInstance: Chart | null = null;

  // Data
  stats: ProductionStatsDTO | null = null;
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
    private statsService: ProductionStatsService
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
    this.statsService.getProductionStats(this.startDate, this.endDate)
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
          console.error('Error loading production stats:', err);
          this.isLoading = false;
        }
      });
  }

  // ============ INIT CHARTS ============

  private initCharts(): void {
    if (!this.stats) return;

    this.createProductionByTeamChart();
    this.createProductionByActivityChart();
    this.createProductionTrendChart();
    this.createProductionByMissionChart();
    this.createProductivityByTeamChart();
  }

  private createProductionByTeamChart(): void {
    if (!this.stats?.productionByTeam || !this.productionByTeamChart) return;

    const data = this.stats.productionByTeam;
    const labels = data.map(d => d.teamName);
    const values = data.map(d => d.productionValue);

    this.destroyChart(this.productionByTeamChartInstance);

    this.productionByTeamChartInstance = new Chart(
      this.productionByTeamChart.nativeElement.getContext('2d'),
      {
        type: 'bar',
        data: {
          labels: labels,
          datasets: [{
            label: 'Production',
            data: values,
            backgroundColor: this.getColors(labels.length),
            borderColor: this.getColors(labels.length),
            borderWidth: 1
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          legend: { display: false },
          scales: {
            yAxes: [{
              ticks: { beginAtZero: true },
              scaleLabel: { display: true, labelString: 'Production' }
            }]
          }
        }
      }
    );
  }

  private createProductionByActivityChart(): void {
    if (!this.stats?.productionByActivity || !this.productionByActivityChart) return;

    const data = this.stats.productionByActivity;
    const labels = data.map(d => d.activityCode);
    const values = data.map(d => d.productionValue);

    this.destroyChart(this.productionByActivityChartInstance);

    this.productionByActivityChartInstance = new Chart(
      this.productionByActivityChart.nativeElement.getContext('2d'),
      {
        type: 'bar',
        data: {
          labels: labels,
          datasets: [{
            label: 'Production',
            data: values,
            backgroundColor: this.getColors(labels.length),
            borderColor: this.getColors(labels.length),
            borderWidth: 1
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          legend: { display: false },
          scales: {
            yAxes: [{
              ticks: { beginAtZero: true },
              scaleLabel: { display: true, labelString: 'Production' }
            }]
          }
        }
      }
    );
  }

  private createProductionTrendChart(): void {
    if (!this.stats?.productionTrend || !this.productionTrendChart) return;

    const data = this.stats.productionTrend;
    const labels = data.map(d => d.month);
    const values = data.map(d => d.productionValue);

    this.destroyChart(this.productionTrendChartInstance);

    this.productionTrendChartInstance = new Chart(
      this.productionTrendChart.nativeElement.getContext('2d'),
      {
        type: 'line',
        data: {
          labels: labels,
          datasets: [{
            label: 'Production',
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
          legend: { display: false },
          scales: {
            yAxes: [{
              ticks: { beginAtZero: true },
              scaleLabel: { display: true, labelString: 'Production' }
            }],
            xAxes: [{
              scaleLabel: { display: true, labelString: 'Month' }
            }]
          }
        }
      }
    );
  }

  private createProductionByMissionChart(): void {
    if (!this.stats?.productionByMission || !this.productionByMissionChart) return;

    const data = this.stats.productionByMission;
    const labels = data.map(d => d.missionCode);
    const values = data.map(d => d.productionValue);

    this.destroyChart(this.productionByMissionChartInstance);

    this.productionByMissionChartInstance = new Chart(
      this.productionByMissionChart.nativeElement.getContext('2d'),
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
            labels: { padding: 20, usePointStyle: true }
          }
        }
      }
    );
  }

  private createProductivityByTeamChart(): void {
    if (!this.stats?.productivityByTeam || !this.productivityByTeamChart) return;

    const data = this.stats.productivityByTeam;
    const labels = data.map(d => d.teamName);
    const values = data.map(d => d.productivity);

    this.destroyChart(this.productivityByTeamChartInstance);

    this.productivityByTeamChartInstance = new Chart(
      this.productivityByTeamChart.nativeElement.getContext('2d'),
      {
        type: 'bar',
        data: {
          labels: labels,
          datasets: [{
            label: 'Productivity',
            data: values,
            backgroundColor: this.getColors(labels.length),
            borderColor: this.getColors(labels.length),
            borderWidth: 1
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          legend: { display: false },
          scales: {
            yAxes: [{
              ticks: { beginAtZero: true },
              scaleLabel: { display: true, labelString: 'Productivity (units/hour)' }
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

  formatNumber(value: number): string {
    if (value === undefined || value === null) return '0';
    if (value >= 1000) {
      return (value / 1000).toFixed(1) + 'k';
    }
    return value.toFixed(1);
  }

  formatDuration(value: number): string {
    if (value === undefined || value === null) return '0 days';
    if (value >= 1) {
      return value.toFixed(1) + ' days';
    }
    return (value * 24).toFixed(1) + ' hours';
  }

  // ============ LIFECYCLE ============

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();

    this.destroyChart(this.productionByTeamChartInstance);
    this.destroyChart(this.productionByActivityChartInstance);
    this.destroyChart(this.productionTrendChartInstance);
    this.destroyChart(this.productionByMissionChartInstance);
    this.destroyChart(this.productivityByTeamChartInstance);
  }
}