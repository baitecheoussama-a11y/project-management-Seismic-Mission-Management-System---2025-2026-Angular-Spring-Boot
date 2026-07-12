// src/app/pages/dashboard/dashboard.component.ts
import { Component, OnInit, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { DashboardService, KPIDashboardDTO, CostByMissionDTO, TrendDataDTO } from '../../../services/analytics/dashboard.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { Chart } from 'chart.js';

@Component({
  selector: 'ngx-dashboard',
  templateUrl: './dashboardy.component.html',
  styleUrls: ['./dashboardy.component.scss']
})
export class DashboardyComponent implements OnInit, OnDestroy {

  @ViewChild('statusChart') statusChartRef!: ElementRef;
  @ViewChild('costChart') costChartRef!: ElementRef;
  @ViewChild('trendChart') trendChartRef!: ElementRef;

  // Data
  kpis: KPIDashboardDTO | null = null;
  costByMission: CostByMissionDTO[] = [];
  trendData: TrendDataDTO[] = [];
  isLoading: boolean = true;

  // Chart instances
  private statusChart: Chart | null = null;
  private costChart: Chart | null = null;
  private trendChart: Chart | null = null;

  private destroy$ = new Subject<void>();

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.loadDashboardData();
  }

  ngAfterViewInit(): void {
    // Charts will be initialized after data loads
  }

  loadDashboardData(): void {
    this.isLoading = true;

    // 1. Load KPIs
    this.dashboardService.getKPIs()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.kpis = data;
          this.isLoading = false;
          setTimeout(() => this.initCharts(), 300);
        },
        error: (err) => {
          console.error('Error loading KPIs:', err);
          this.isLoading = false;
        }
      });

    // 2. Load Cost by Mission
    this.dashboardService.getCostByMission()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.costByMission = data;
          setTimeout(() => this.initCharts(), 300);
        },
        error: (err) => console.error('Error loading cost data:', err)
      });

    // 3. Load Trend Data
    this.dashboardService.getTrendData(1, 30)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.trendData = data;
          setTimeout(() => this.initCharts(), 300);
        },
        error: (err) => console.error('Error loading trend data:', err)
      });
  }

  initCharts(): void {
    this.initStatusChart();
    this.initCostChart();
    this.initTrendChart();
  }

  // ========== 1. STATUS CHART (Doughnut) ==========
  initStatusChart(): void {
    if (!this.statusChartRef || !this.kpis) return;

    if (this.statusChart) {
      this.statusChart.destroy();
    }

    const ctx = this.statusChartRef.nativeElement.getContext('2d');

    this.statusChart = new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: ['Active', 'Completed', 'Delayed', 'Cancelled'],
        datasets: [{
          data: [
            this.kpis.activeProjects || 0,
            this.kpis.completedProjects || 0,
            this.kpis.delayedProjects || 0,
            this.kpis.cancelledProjects || 0
          ],
          backgroundColor: ['#3b82f6', '#10b981', '#ef4444', '#94a3b8'],
          borderWidth: 2
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
        },
        tooltips: {
          callbacks: {
            label: function(tooltipItem: any, data: any) {
              const dataset = data.datasets[tooltipItem.datasetIndex];
              const total = dataset.data.reduce((a: number, b: number) => a + b, 0);
              const percentage = ((tooltipItem.yLabel / total) * 100).toFixed(1);
              return `${tooltipItem.xLabel}: ${tooltipItem.yLabel} (${percentage}%)`;
            }
          }
        }
      }
    });
  }

  // ========== 2. COST CHART (Bar) ==========
  initCostChart(): void {
    if (!this.costChartRef || this.costByMission.length === 0) return;

    if (this.costChart) {
      this.costChart.destroy();
    }

    const ctx = this.costChartRef.nativeElement.getContext('2d');

    const labels = this.costByMission.map(item => item.missionCode || 'N/A');
    const costs = this.costByMission.map(item => item.totalCost || 0);
    const budgets = this.costByMission.map(item => item.totalBudget || 0);

    this.costChart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [
          {
            label: 'Total Cost (DA)',
            data: costs,
            backgroundColor: '#3b82f6',
            borderColor: '#3b82f6',
            borderWidth: 1
          },
          {
            label: 'Budget',
            data: budgets,
            backgroundColor: '#10b981',
            borderColor: '#10b981',
            borderWidth: 1
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        legend: {
          position: 'top',
          labels: {
            usePointStyle: true,
            padding: 20
          }
        },
        scales: {
          yAxes: [{
            ticks: {
              beginAtZero: true
            },
            scaleLabel: {
              display: true,
              labelString: 'Amount (DA)'
            }
          }],
          xAxes: [{
            scaleLabel: {
              display: true,
              labelString: 'Mission'
            }
          }]
        },
        tooltips: {
          callbacks: {
            label: function(tooltipItem: any) {
              return `${tooltipItem.datasetLabel}: ${tooltipItem.yLabel.toLocaleString()} DA`;
            }
          }
        }
      }
    });
  }

  // ========== 3. TREND CHART (Line) ==========
  initTrendChart(): void {
    if (!this.trendChartRef || this.trendData.length === 0) return;

    if (this.trendChart) {
      this.trendChart.destroy();
    }

    const ctx = this.trendChartRef.nativeElement.getContext('2d');

    const labels = this.trendData.map(item => {
      const date = new Date(item.date);
      return date.toLocaleDateString('en-US', { day: '2-digit', month: 'short' });
    });

    const progression = this.trendData.map(item => item.avgProgression || 0);
    const costs = this.trendData.map(item => item.totalCost || 0);

    this.trendChart = new Chart(ctx, {
      type: 'line',
      data: {
        labels: labels,
        datasets: [
          {
            label: 'Avg Progression %',
            data: progression,
            borderColor: '#3b82f6',
            backgroundColor: 'rgba(59, 130, 246, 0.1)',
            fill: true,
            tension: 0.4,
            pointBackgroundColor: '#3b82f6',
            pointRadius: 4
          },
          {
            label: 'Total Cost (DA)',
            data: costs,
            borderColor: '#ef4444',
            backgroundColor: 'rgba(239, 68, 68, 0.1)',
            fill: true,
            tension: 0.4,
            pointBackgroundColor: '#ef4444',
            pointRadius: 4,
            yAxisID: 'y1'
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        legend: {
          position: 'top',
          labels: {
            usePointStyle: true,
            padding: 20
          }
        },
        scales: {
          yAxes: [{
            id: 'y',
            position: 'left',
            ticks: {
              beginAtZero: true
            },
            scaleLabel: {
              display: true,
              labelString: 'Progression %'
            }
          }, {
            id: 'y1',
            position: 'right',
            ticks: {
              beginAtZero: true
            },
            scaleLabel: {
              display: true,
              labelString: 'Cost (DA)'
            },
            gridLines: {
              drawOnChartArea: false
            }
          }],
          xAxes: [{
            scaleLabel: {
              display: true,
              labelString: 'Date'
            }
          }]
        },
        tooltips: {
          mode: 'index',
          intersect: false,
          callbacks: {
            label: function(tooltipItem: any) {
              if (tooltipItem.datasetIndex === 0) {
                return `${tooltipItem.datasetLabel}: ${tooltipItem.yLabel}%`;
              } else {
                return `${tooltipItem.datasetLabel}: ${tooltipItem.yLabel.toLocaleString()} DA`;
              }
            }
          }
        }
      }
    });
  }

  formatCurrency(value: number): string {
    if (!value) return '0 DA';
    return new Intl.NumberFormat('fr-DZ', {
      style: 'currency',
      currency: 'DZD',
      minimumFractionDigits: 0
    }).format(value);
  }

  getProgressColor(progress: number): string {
    if (progress >= 80) return '#10b981';
    if (progress >= 50) return '#f59e0b';
    return '#ef4444';
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();

    // Destroy charts
    if (this.statusChart) {
      this.statusChart.destroy();
      this.statusChart = null;
    }
    if (this.costChart) {
      this.costChart.destroy();
      this.costChart = null;
    }
    if (this.trendChart) {
      this.trendChart.destroy();
      this.trendChart = null;
    }
  }
}