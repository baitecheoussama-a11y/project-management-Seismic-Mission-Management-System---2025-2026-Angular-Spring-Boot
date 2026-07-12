import { Component, OnInit } from '@angular/core';
import { MENU_ITEMS, CustomMenuItem } from './pages-menu';
import { AuthService } from '../services/auth.service';
import { NbMenuService } from '@nebular/theme';
import { Router } from '@angular/router';

@Component({
  selector: 'ngx-pages',
  template: `
    <ngx-one-column-layout>
      <nb-menu [items]="menu"></nb-menu>
      <router-outlet></router-outlet>
    </ngx-one-column-layout>
  `,
})
export class PagesComponent implements OnInit {

  menu: CustomMenuItem[] = [];

  constructor(private authService: AuthService  , private menuService: NbMenuService, 
  private router: Router   ) {}

ngOnInit() {

  // 🔹 فلترة المينيو (تاعك راهو صحيح)
  this.menu = MENU_ITEMS.filter(item => {
    if (item.hiddenForRoles) {
      return !this.authService.hasAnyRole(item.hiddenForRoles);
    }
    return true;
  });

  // 🔥 هذا المهم: مراقبة الضغط على المينيو
  this.menuService.onItemClick().subscribe((event) => {
    const item = event.item;

    if (item.data?.action === 'logout') {
      this.authService.logout();          // حذف token
      this.router.navigate(['/auth/login']); // رجوع للـ login
    }
  });
}
}