import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  
  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // جلب التوكن من localStorage عبر AuthService
    const token = this.authService.getToken();
    
    console.log('🔵 Interceptor - URL:', req.url);
    console.log('🔵 Interceptor - Token exists:', !!token);
    
    if (token) {
      // إضافة التوكن إلى الـ headers
      const cloned = req.clone({
        setHeaders: {
          'Authorization': `Bearer ${token}`
        }
      });
      return next.handle(cloned);
    }
    
    // إذا لا يوجد توكن، نرسل الطلب بدون تعديل
    return next.handle(req);
  }
}