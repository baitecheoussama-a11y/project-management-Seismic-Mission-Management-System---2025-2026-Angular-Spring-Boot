import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const expectedRoles = route.data['roles'] as string[];
    
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return false;
    }
    
    if (expectedRoles && !this.authService.hasAnyRole(expectedRoles)) {
      this.router.navigate(['/unauthorized']); // صفحة غير مصرح
      return false;
    }
    
    return true;
  }
}

/*
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { AuthGuard } from './guards/auth.guard';
import { RoleGuard } from './guards/role.guard';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { 
    path: 'dashboard', 
    component: DashboardComponent,
    canActivate: [AuthGuard]  // ✅ يحتاج تسجيل دخول
  },
  {
    path: 'admin',
    component: AdminComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN'] }  // ✅ فقط ADMIN
  },
  { path: '', redirectTo: '/login', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
*/



/*
<!-- dashboard.component.html -->
<div *ngIf="authService.hasRole('ADMIN')">
  <button>إدارة المستخدمين (للمدير فقط)</button>
</div>

<div *ngIf="authService.hasAnyRole(['ADMIN', 'MANAGER'])">
  <button>تقارير متقدمة (مدير أو مسؤول)</button>
</div>

<div>
  <button (click)="logout()">تسجيل خروج</button>
</div>




and in compents.ts 

// dashboard.component.ts
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent {
  
  constructor(
    public authService: AuthService,  // public عشان نستخدمها في HTML
    private router: Router
  ) {}

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
*/