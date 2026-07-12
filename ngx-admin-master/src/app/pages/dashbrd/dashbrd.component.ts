import { Component, OnDestroy, OnInit, AfterViewInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { DashboardService, DashboardStats } from '../../services/dashboards/dashboard.service';
import { NbToastrService, NbThemeService } from '@nebular/theme';
import { takeWhile } from 'rxjs/operators';
import * as Chart from 'chart.js';

@Component({
  selector: 'ngx-dashbrd',
  templateUrl: './dashbrd.component.html',
  styleUrls: ['./dashbrd.component.scss']
})
export class DashbrdComponent implements OnInit, AfterViewInit, OnDestroy {

  private alive = true;
  
  stats: any = null;
  loading = true;
  userName = '';
  currentTime = new Date();
  
  // Pagination properties
  currentPage = 1;
  itemsPerPage = 4;
  
  // Charts
  accountsChart: any;
  rolesChart: any;
  growthChart: any;
  
  // Theme specific configurations
  chartColorsByTheme: any = {
    default: {
      primary: '#3366ff',
      success: '#00d68f',
      warning: '#ffaa00',
      danger: '#ff3d71',
      grid: '#edf1f7',
      text: '#2d3e5f',
      background: '#ffffff'
    },
    dark: {
      primary: '#3366ff',
      success: '#00d68f',
      warning: '#ffaa00',
      danger: '#ff3d71',
      grid: '#2d3e5f',
      text: '#ffffff',
      background: '#1a1a2e'
    },
    cosmic: {
      primary: '#7c4dff',
      success: '#00d68f',
      warning: '#ffaa00',
      danger: '#ff3d71',
      grid: '#3d3d5c',
      text: '#ffffff',
      background: '#1b1b38'
    },
    corporate: {
      primary: '#3366ff',
      success: '#00d68f',
      warning: '#ffaa00',
      danger: '#ff3d71',
      grid: '#e5e5e5',
      text: '#2d3e5f',
      background: '#ffffff'
    }
  };

  currentThemeColors: any;
  currentThemeName: string = 'default';

  constructor(
    private dashboardService: DashboardService,
    private authService: AuthService,
    private router: Router,
    private toastrService: NbToastrService,
    private themeService: NbThemeService
  ) {}

  ngOnInit() {
    const user = this.authService.getCurrentUser();
    if (!user) {
      this.router.navigate(['/auth/login']);
      return;
    }
    
    this.userName = user.username || 'Admin';
    
    this.themeService.getJsTheme()
      .pipe(takeWhile(() => this.alive))
      .subscribe(theme => {
        this.currentThemeName = theme.name;
        this.currentThemeColors = this.chartColorsByTheme[theme.name] || this.chartColorsByTheme.default;
        
        if (this.accountsChart) {
          this.updateChartsTheme();
        }
      });
    
    this.loadStats();
    
    setInterval(() => {
      this.currentTime = new Date();
    }, 60000);
  }

  ngAfterViewInit() {}

  ngOnDestroy() {
    this.alive = false;
    if (this.accountsChart) this.accountsChart.destroy();
    if (this.rolesChart) this.rolesChart.destroy();
    if (this.growthChart) this.growthChart.destroy();
  }

  // Computed properties for pagination
get totalPages(): number {
  if (!this.stats?.recentEmployees) return 0;
  return Math.ceil(this.stats.recentEmployees.length / this.itemsPerPage);
}

get paginatedEmployees(): any[] {
  if (!this.stats?.recentEmployees) return [];
  const startIndex = (this.currentPage - 1) * this.itemsPerPage;
  const endIndex = startIndex + this.itemsPerPage;
  return this.stats.recentEmployees.slice(startIndex, endIndex);
}

  
get pageNumbers(): (number | string)[] {
  const pages: (number | string)[] = [];
  const maxVisible = 5;
  
  if (this.totalPages <= maxVisible) {
    for (let i = 1; i <= this.totalPages; i++) {
      pages.push(i);
    }
  } else {
    if (this.currentPage <= 3) {
      for (let i = 1; i <= 4; i++) pages.push(i);
      pages.push('...');
      pages.push(this.totalPages);
    } else if (this.currentPage >= this.totalPages - 2) {
      pages.push(1);
      pages.push('...');
      for (let i = this.totalPages - 3; i <= this.totalPages; i++) pages.push(i);
    } else {
      pages.push(1);
      pages.push('...');
      for (let i = this.currentPage - 1; i <= this.currentPage + 1; i++) pages.push(i);
      pages.push('...');
      pages.push(this.totalPages);
    }
  }
  
  return pages;
}


  loadStats() {
    this.loading = true;
    this.dashboardService.getStats().subscribe({
      next: (data) => {
        console.log('📊 Raw data from backend:', data);
        
        const transformedData = this.transformBackendData(data);
        this.stats = transformedData;
        this.loading = false;
        
        // Reset pagination when new data arrives
        this.resetPagination();
        
        setTimeout(() => {
          this.initCharts();
        }, 200);
      },
      error: (err) => {
        console.error('Error loading dashboard stats:', err);
        this.loading = false;
        this.toastrService.danger('Failed to load dashboard data', 'Error');
      }
    });
  }

  resetPagination() {
    this.currentPage = 1;
  }

  goToPage(page: number) {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      const element = document.querySelector('.recent-table-card');
      if (element) {
        element.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
      }
    }
  }

  transformBackendData(data: any): any {
    let rolesArray: any[] = [];
    if (data.rolesDistribution && typeof data.rolesDistribution === 'object') {
      rolesArray = Object.keys(data.rolesDistribution).map(key => ({
        roleName: key,
        count: data.rolesDistribution[key],
        color: this.getRoleColor(key)
      }));
    }
    
    const recentEmployees = (data.recentEmployees || []).map((emp: any) => ({
      id: emp.id,
      name: `${emp.prenom || ''} ${emp.nom || ''}`.trim(),
      nom: emp.nom,
      prenom: emp.prenom,
      email: emp.email,
      createdAt: emp.dateCreation || emp.createdAt || new Date().toISOString()
    }));
    
    let employeesGrowth = data.employeesGrowth || [];
    if (!employeesGrowth.length) {
      const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'];
      employeesGrowth = months.map((month, i) => ({
        month: month,
        count: Math.floor(Math.random() * 50) + 10
      }));
    }
    
    const alerts = data.alerts || [];
    const recentActivities = data.recentActivities || [];
    
    const activeAccounts = data.activeAccounts || 0;
    const totalAccounts = data.totalAccounts || 0;
    const suspendedAccounts = data.suspendedAccounts || 0;
    const desactiveAccounts = data.desactiveAccounts || (totalAccounts - activeAccounts - suspendedAccounts);
    
    return {
      ...data,
      rolesDistribution: rolesArray,
      recentEmployees: recentEmployees,
      employeesGrowth: employeesGrowth,
      alerts: alerts,
      recentActivities: recentActivities,
      suspendedAccounts: suspendedAccounts,
      desactiveAccounts: desactiveAccounts,
      activeContracts: data.activeContracts || data.totalContracts || 0,
      expiringSoonContracts: data.expiringSoonContracts || 0
    };
  }

  getRoleColor(roleName: string): string {
    const colors: any = {
      'ADMINISTRATEUR': '#ff3d71',
      'CHEF_MISSION': '#3366ff',
      'CHEF_TERRAIN': '#00d68f',
      'DIRECTEUR': '#ffaa00',
      'AUTRE': '#8f9bb3'
    };
    return colors[roleName] || '#8f9bb3';
  }

  initCharts() {
    if (!this.stats) return;
    
    this.initAccountsChart();
    this.initRolesChart();
    this.initGrowthChart();
  }

  updateChartsTheme() {
    if (this.accountsChart) {
      this.accountsChart.destroy();
      this.rolesChart.destroy();
      this.growthChart.destroy();
      this.initCharts();
    }
  }

  initAccountsChart() {
    const ctx = document.getElementById('accountsChart') as HTMLCanvasElement;
    if (!ctx) return;
    
    const active = this.stats.activeAccounts || 0;
    const suspended = this.stats.suspendedAccounts || 0;
    const desactive = this.stats.desactiveAccounts || 0;
    
    this.accountsChart = new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: ['🟢 ACTIVE', '🟡 SUSPENDED', '🔴 DESACTIVE'],
        datasets: [{
          data: [active, suspended, desactive],
          backgroundColor: [
            this.currentThemeColors.success,
            this.currentThemeColors.warning,
            this.currentThemeColors.danger
          ],
          borderWidth: 0,
          hoverOffset: 10
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        cutout: '60%',
        plugins: {
          legend: {
            position: 'bottom',
            labels: {
              font: { size: 12, family: 'Poppins' },
              padding: 15,
              color: this.currentThemeColors.text
            }
          },
          tooltip: {
            callbacks: {
              label: (context) => {
                const label = context.label || '';
                const value = context.parsed || 0;
                const total = active + suspended + desactive;
                const percentage = total > 0 ? ((value / total) * 100).toFixed(1) : 0;
                return `${label}: ${value} (${percentage}%)`;
              }
            }
          }
        }
      }
    });
  }

  initRolesChart() {
    const ctx = document.getElementById('rolesChart') as HTMLCanvasElement;
    if (!ctx) return;
    
    const rolesArray = this.stats.rolesDistribution || [];
    
    if (!rolesArray.length) {
      console.warn('No roles distribution data');
      return;
    }
    
    const labels = rolesArray.map((r: any) => r.roleName);
    const data = rolesArray.map((r: any) => r.count);
    const chartColors = rolesArray.map((r: any) => r.color || this.currentThemeColors.primary);
    
    this.rolesChart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [{
          label: 'Number of Assignments',
          data: data,
          backgroundColor: chartColors,
          borderRadius: 10,
          barPercentage: 0.7,
          categoryPercentage: 0.8
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: {
            callbacks: {
              label: (context) => `📊 ${context.raw} users`
            }
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            grid: { color: this.currentThemeColors.grid, drawBorder: false },
            ticks: { stepSize: 1, font: { size: 11 }, color: this.currentThemeColors.text }
          },
          x: {
            grid: { display: false },
            ticks: { font: { size: 11, weight: '500' }, color: this.currentThemeColors.text }
          }
        },
        animation: {
          duration: 1000,
          easing: 'easeOutQuart'
        }
      }
    });
  }

  initGrowthChart() {
    const ctx = document.getElementById('growthChart') as HTMLCanvasElement;
    if (!ctx) return;
    
    const growth = this.stats.employeesGrowth || [];
    
    if (!growth.length) {
      console.warn('No employees growth data');
      return;
    }
    
    const labels = growth.map((g: any) => g.month);
    const data = growth.map((g: any) => g.count);
    
    this.growthChart = new Chart(ctx, {
      type: 'line',
      data: {
        labels: labels,
        datasets: [{
          label: 'Employees',
          data: data,
          borderColor: this.currentThemeColors.primary,
          backgroundColor: `${this.currentThemeColors.primary}0D`,
          borderWidth: 3,
          pointBackgroundColor: this.currentThemeColors.primary,
          pointBorderColor: '#fff',
          pointBorderWidth: 2,
          pointRadius: 4,
          pointHoverRadius: 6,
          tension: 0.4,
          fill: true
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: {
            callbacks: {
              label: (context) => `👥 ${context.raw} employees`
            }
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            grid: { color: this.currentThemeColors.grid },
            ticks: { stepSize: 1, font: { size: 11 }, color: this.currentThemeColors.text }
          },
          x: {
            grid: { display: false },
            ticks: { font: { size: 11 }, color: this.currentThemeColors.text }
          }
        },
        animation: {
          duration: 1000,
          easing: 'easeOutQuart'
        }
      }
    });
  }

  getAlertIcon(type: string): string {
    switch(type) {
      case 'warning': return 'alert-triangle-outline';
      case 'danger': return 'close-circle-outline';
      case 'info': return 'info-outline';
      default: return 'checkmark-circle-outline';
    }
  }

  getAlertClass(type: string): string {
    switch(type) {
      case 'warning': return 'alert-warning';
      case 'danger': return 'alert-danger';
      case 'info': return 'alert-info';
      default: return 'alert-success';
    }
  }

  getActivityIcon(type: string): string {
    switch(type) {
      case 'CREATE_ACCOUNT': return 'person-add-outline';
      case 'ASSIGN_ROLE': return 'people-outline';
      case 'CREATE_EMPLOYEE': return 'person-done-outline';
      case 'CREATE_CONTRACT': return 'file-text-outline';
      default: return 'activity-outline';
    }
  }

  getTimeAgo(date: string): string {
    if (!date) return 'Recently';
    const now = new Date();
    const past = new Date(date);
    const diffMs = now.getTime() - past.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMins / 60);
    const diffDays = Math.floor(diffHours / 24);
    
    if (diffMins < 1) return 'Just now';
    if (diffMins < 60) return `${diffMins} min ago`;
    if (diffHours < 24) return `${diffHours} hour${diffHours > 1 ? 's' : ''} ago`;
    if (diffDays < 7) return `${diffDays} day${diffDays > 1 ? 's' : ''} ago`;
    return past.toLocaleDateString();
  }

  navigateTo(route: string, params?: any) {
    if (params) {
      this.router.navigate([route], { queryParams: params });
    } else {
      this.router.navigate([route]);
    }
  }

  quickAction(action: string) {
    switch(action) {
      case 'addEmployee':
        this.navigateTo('/pages/tables/smart-table');
        break;
      case 'createAccount':
        this.navigateTo('/pages/tables/comptes');
        break;
      case 'assignRole':
        this.navigateTo('/pages/tables/roles');
        break;
      case 'addContract':
        this.navigateTo('/pages/tables/contrats');
        break;
    }
  }
}