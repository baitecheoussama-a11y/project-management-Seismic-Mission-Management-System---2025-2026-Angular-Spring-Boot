import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {

  loginData = {
    username: '',
    password: '',
    rememberMe: false
  };

  errorMessage: string = '';
  successMessage: string = '';
  isLoading: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit() {
    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.authService.login({
      username: this.loginData.username,
      password: this.loginData.password
    }).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.successMessage = 'Login successful! Redirecting...';
        console.log('SUCCESS:', res);
        
        // ✅ توجيه المستخدم بعد 1 ثانية
        setTimeout(() => {
          this.router.navigate(['/pages/mission-dashboard']); // غير المسار حسب صفحتك الرئيسية
        }, 1);
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = 'Incorrect username or password';
        console.log('ERROR:', err);
      }
    });
  }
}