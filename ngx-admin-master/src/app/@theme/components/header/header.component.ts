import { Component, OnDestroy, OnInit } from '@angular/core';
import { NbMediaBreakpointsService, NbMenuService, NbSidebarService, NbThemeService } from '@nebular/theme';
import { LayoutService } from '../../../@core/utils';
import { map, takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { Router } from '@angular/router';
import { AuthService, LoginResponse } from '../../../services/auth.service';
import { NotificationService, NotificationDTO } from '../../../services/notification/notification.service';

@Component({
  selector: 'ngx-header',
  styleUrls: ['./header.component.scss'],
  templateUrl: './header.component.html',
})
export class HeaderComponent implements OnInit, OnDestroy {

  private destroy$: Subject<void> = new Subject<void>();
  userPictureOnly: boolean = false;
  user: LoginResponse | null = null;
  userRoles: string = '';
  userName: string = '';

  // Notification properties
  notifications: NotificationDTO[] = [];
  filteredNotifications: NotificationDTO[] = [];
  unreadCount: number = 0;
  showNotifications: boolean = false;
  showFilters: boolean = false;
  selectedFilter: string = 'all';

  // Available notification types for filtering
  notificationTypes: string[] = [
    'PROJECT_CREATED',
    'PROJECT_UPDATED',
    'PROJECT_COMPLETED',
    'PROJECT_CANCELLED',
    'REPORT_ADDED',
    'INCIDENT_CREATED',
    'INCIDENT_UPDATED',
    'INCIDENT_DELETED',
    'EVENT_CREATED',
    'EVENT_UPDATED',
    'EVENT_DELETED',
    'SYSTEM'
  ];

  // Notification type labels
  notificationTypeLabels: { [key: string]: string } = {
    'PROJECT_CREATED': 'Projects',
    'PROJECT_UPDATED': 'Projects',
    'PROJECT_COMPLETED': 'Projects',
    'PROJECT_CANCELLED': 'Projects',
    'REPORT_ADDED': 'Reports',
    'INCIDENT_CREATED': 'Incidents',
    'INCIDENT_UPDATED': 'Incidents',
    'INCIDENT_DELETED': 'Incidents',
    'EVENT_CREATED': 'Events',
    'EVENT_UPDATED': 'Events',
    'EVENT_DELETED': 'Events',
    'SYSTEM': 'System'
  };

  themes = [
    { value: 'default', name: 'Light' },
    { value: 'dark', name: 'Dark' },
    { value: 'cosmic', name: 'Cosmic' },
    { value: 'corporate', name: 'Corporate' },
  ];

  currentTheme = 'default';

  userMenu = [ 
    { title: 'Profile', icon: 'person-outline' },
    { title: 'Log out', icon: 'log-out-outline' } 
  ];

  constructor(
    private sidebarService: NbSidebarService,
    private menuService: NbMenuService,
    private themeService: NbThemeService,
    private layoutService: LayoutService,
    private breakpointService: NbMediaBreakpointsService,
    private authService: AuthService,
    private router: Router,
    private notificationService: NotificationService
  ) {}

  ngOnInit() {
    this.currentTheme = this.themeService.currentTheme;
    
    this.loadUserData();
    
    this.authService.getCurrentUserObservable().subscribe(user => {
      this.user = user;
      if (user) {
        this.userName = user.username;
        this.userRoles = user.roles.join(' • ');
        this.loadNotifications();
        this.loadUnreadCount();
      }
    });

    const { xl } = this.breakpointService.getBreakpointsMap();
    this.themeService.onMediaQueryChange()
      .pipe(
        map(([, currentBreakpoint]) => currentBreakpoint.width < xl),
        takeUntil(this.destroy$),
      )
      .subscribe((isLessThanXl: boolean) => this.userPictureOnly = isLessThanXl);

    this.themeService.onThemeChange()
      .pipe(
        map(({ name }) => name),
        takeUntil(this.destroy$),
      )
      .subscribe(themeName => this.currentTheme = themeName);

    this.menuService.onItemClick()
      .pipe(takeUntil(this.destroy$))
      .subscribe((event) => {
        if (event.item.title === 'Log out') {
          this.logout();
        } else if (event.item.title === 'Profile') {
          this.router.navigate(['/pages/profile']);
        }
      });

    // Auto-refresh notifications every 30 seconds
    setInterval(() => {
      if (this.user) {
        this.loadUnreadCount();
      }
    }, 30000);
  }

  loadUserData() {
    const currentUser = this.authService.getCurrentUser();
    if (currentUser) {
      this.user = currentUser;
      this.userName = currentUser.username;
      this.userRoles = currentUser.roles.join(' • ');
    }
  }

  // ============ NOTIFICATION METHODS ============

  loadNotifications() {
    this.notificationService.getMyNotifications().subscribe({
      next: (notifications) => {
        this.notifications = notifications;
        this.applyFilter();
      },
      error: (err) => {
        console.error('Error loading notifications:', err);
      }
    });
  }

  loadUnreadCount() {
    this.notificationService.getUnreadCount().subscribe({
      next: (count) => {
        this.unreadCount = count;
      },
      error: (err) => {
        console.error('Error loading unread count:', err);
      }
    });
  }

  toggleNotifications(event: Event) {
    event.stopPropagation();
    this.showNotifications = !this.showNotifications;
    if (this.showNotifications) {
      this.loadNotifications();
      this.showFilters = false; // Close filters when opening
    }
  }

  // ============ FILTER METHODS ============

  setFilter(filter: string) {
    this.selectedFilter = filter;
    this.applyFilter();
    // Close filters after selection on mobile
    if (window.innerWidth < 768) {
      this.showFilters = false;
    }
  }

  applyFilter() {
    if (this.selectedFilter === 'all') {
      this.filteredNotifications = [...this.notifications];
    } else if (this.selectedFilter === 'unread') {
      this.filteredNotifications = this.notifications.filter(n => !n.read);
    } else {
      this.filteredNotifications = this.notifications.filter(n => n.type === this.selectedFilter);
    }
  }

  getFilteredCount(type: string): number {
    return this.notifications.filter(n => n.type === type).length;
  }

  getNotificationTypeLabel(type: string): string {
    return this.notificationTypeLabels[type] || type;
  }

  markAsRead(notificationId: number, event: Event) {
    event.stopPropagation();
    this.notificationService.markAsRead(notificationId).subscribe({
      next: () => {
        const notification = this.notifications.find(n => n.id === notificationId);
        if (notification) {
          notification.read = true;
          this.unreadCount = Math.max(0, this.unreadCount - 1);
          this.applyFilter();
        }
      },
      error: (err) => {
        console.error('Error marking notification as read:', err);
      }
    });
  }

  markAllAsRead(event: Event) {
    event.stopPropagation();
    this.notificationService.markAllAsRead().subscribe({
      next: () => {
        this.notifications.forEach(n => n.read = true);
        this.unreadCount = 0;
        this.applyFilter();
      },
      error: (err) => {
        console.error('Error marking all notifications as read:', err);
      }
    });
  }

  deleteNotification(notificationId: number, event: Event) {
    event.stopPropagation();
    this.notificationService.deleteNotification(notificationId).subscribe({
      next: () => {
        this.notifications = this.notifications.filter(n => n.id !== notificationId);
        this.unreadCount = this.notifications.filter(n => !n.read).length;
        this.applyFilter();
      },
      error: (err) => {
        console.error('Error deleting notification:', err);
      }
    });
  }

  deleteAllRead(event: Event) {
    event.stopPropagation();
    this.notificationService.deleteAllRead().subscribe({
      next: () => {
        this.notifications = this.notifications.filter(n => !n.read);
        this.applyFilter();
      },
      error: (err) => {
        console.error('Error deleting read notifications:', err);
      }
    });
  }

  navigateToNotification(notification: NotificationDTO, event: Event) {
    event.stopPropagation();
    if (!notification.read) {
      this.markAsRead(notification.id, event);
    }
    if (notification.link) {
      this.router.navigate([notification.link]);
      this.showNotifications = false;
    }
  }

  // ============ ICON AND COLOR HELPERS ============

  getNotificationIcon(type: string): string {
    const iconMap: { [key: string]: string } = {
      'INFO': 'info-outline',
      'SUCCESS': 'checkmark-circle-outline',
      'WARNING': 'alert-triangle-outline',
      'ERROR': 'alert-circle-outline',
      'PROJECT_CREATED': 'folder-outline',
      'PROJECT_UPDATED': 'edit-outline',
      'PROJECT_COMPLETED': 'checkmark-circle-outline',
      'PROJECT_CANCELLED': 'close-circle-outline',
      'ACTIVITY_ASSIGNED': 'grid-outline',
      'ACTIVITY_COMPLETED': 'checkmark-circle-outline',
      'REPORT_ADDED': 'file-text-outline',
      'BREAKDOWN_DECLARED': 'alert-triangle-outline',
      'REPAIR_COMPLETED': 'checkmark-circle-outline',
      'EQUIPMENT_ASSIGNED': 'hard-drive-outline',
      'TEAM_ASSIGNED': 'people-outline',
      'MISSION_UPDATED': 'briefcase-outline',
      'SYSTEM': 'settings-outline',
      'INCIDENT_CREATED': 'alert-circle-outline',
      'INCIDENT_UPDATED': 'edit-outline',
      'INCIDENT_DELETED': 'trash-2-outline',
      'EVENT_CREATED': 'calendar-outline',
      'EVENT_UPDATED': 'edit-outline',
      'EVENT_DELETED': 'trash-2-outline'
    };
    return iconMap[type] || 'bell-outline';
  }

  getNotificationColor(type: string): string {
    const colorMap: { [key: string]: string } = {
      'INFO': '#3b82f6',
      'SUCCESS': '#10b981',
      'WARNING': '#f59e0b',
      'ERROR': '#ef4444',
      'PROJECT_CREATED': '#8b5cf6',
      'PROJECT_UPDATED': '#3b82f6',
      'PROJECT_COMPLETED': '#10b981',
      'PROJECT_CANCELLED': '#ef4444',
      'ACTIVITY_ASSIGNED': '#06b6d4',
      'ACTIVITY_COMPLETED': '#10b981',
      'REPORT_ADDED': '#f59e0b',
      'BREAKDOWN_DECLARED': '#ef4444',
      'REPAIR_COMPLETED': '#10b981',
      'EQUIPMENT_ASSIGNED': '#8b5cf6',
      'TEAM_ASSIGNED': '#3b82f6',
      'MISSION_UPDATED': '#f59e0b',
      'SYSTEM': '#64748b',
      'INCIDENT_CREATED': '#ef4444',
      'INCIDENT_UPDATED': '#f59e0b',
      'INCIDENT_DELETED': '#ef4444',
      'EVENT_CREATED': '#8b5cf6',
      'EVENT_UPDATED': '#3b82f6',
      'EVENT_DELETED': '#ef4444'
    };
    return colorMap[type] || '#64748b';
  }

  formatDate(date: string): string {
    if (!date) return '';
    const now = new Date();
    const notifDate = new Date(date);
    const diffMs = now.getTime() - notifDate.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMs / 3600000);
    const diffDays = Math.floor(diffMs / 86400000);

    if (diffMins < 1) return 'Just now';
    if (diffMins < 60) return `${diffMins}m ago`;
    if (diffHours < 24) return `${diffHours}h ago`;
    if (diffDays < 7) return `${diffDays}d ago`;
    return notifDate.toLocaleDateString('en-US', { day: '2-digit', month: 'short', year: 'numeric' });
  }

  // ============ HELPER METHODS ============

  hasReadNotifications(): boolean {
    return this.notifications.some(n => n.read);
  }

  closeNotifications(): void {
    this.showNotifications = false;
    this.showFilters = false;
  }

  // ============ EXISTING METHODS ============

  changeTheme(themeName: string) {
    this.themeService.changeTheme(themeName);
  }

  toggleSidebar(): boolean {
    this.sidebarService.toggle(true, 'menu-sidebar');
    this.layoutService.changeLayoutSize();
    return false;
  }

  navigateHome() {
    this.menuService.navigateHome();
    return false;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}