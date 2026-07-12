import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { EmployeService, EmployeAccountDetails } from '../../services/employes/employe.service';
import { CompteService } from '../../services/comptes/compte.service';
import { AuthService } from '../../services/auth.service';
import { NbToastrService } from '@nebular/theme';
import { FonctionService } from '../../services/fonctions/fonction.service';
import { Location } from '@angular/common';  // Add this import

@Component({
  selector: 'ngx-employe-account',
  templateUrl: './employe-account.component.html',
  styleUrls: ['./employe-account.component.scss']
})
export class EmployeAccountComponent implements OnInit {
  
  employeDetails: EmployeAccountDetails | null = null;
  loading = true;
  employeName = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private location: Location,  // Add this
    private employeService: EmployeService,
    private compteService: CompteService,
    private fonctionService: FonctionService,
    public authService: AuthService,
    private toastrService: NbToastrService
  ) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      const employeId = params['employeId'];
      this.employeName = params['employeName'] || 'Employee';
      
      if (employeId) {
        this.loadEmployeDetails(employeId);
      } else {
        this.toastrService.danger('No employee ID provided', 'Error');
        this.router.navigate(['/pages/tables/smart-table']);
      }
    });
  }

  loadEmployeDetails(employeId: number) {
    this.loading = true;
    this.employeService.getEmployeAccountDetails(employeId).subscribe({
      next: (data) => {
        this.employeDetails = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading account details:', err);
        this.toastrService.danger('Failed to load employee account details', 'Error');
        this.loading = false;
      }
    });
  }

  // Updated goBack() to go to previous page
  goBack() {
    this.location.back();  // This goes back to the previous page in browser history
  }

  formatDate(date: string): string {
    if (!date) return '-';
    return new Date(date).toLocaleDateString('en-GB');
  }

  getStatusBadgeClass(status: string): string {
    switch(status) {
      case 'ACTIF': return 'status-success';
      case 'ACTIVE': return 'status-success';
      case 'INACTIF': return 'status-warning';
      case 'SUSPENDED': return 'status-warning';
      case 'DESACTIVE': return 'status-danger';
      default: return 'status-danger';
    }
  }

  // Create new account
  createAccount() {
    if (this.employeDetails && this.employeDetails.id) {
      this.router.navigate(['/pages/tables/accounts', this.employeDetails.id], {
        queryParams: {
          employeName: `${this.employeDetails.prenom} ${this.employeDetails.nom}`
        }
      });
    }
  }

  // Edit account
  editAccount() {
    if (this.employeDetails && this.employeDetails.id) {
      this.router.navigate(['/pages/tables/accounts', this.employeDetails.id], {
        queryParams: {
          employeName: `${this.employeDetails.prenom} ${this.employeDetails.nom}`,
          employeId: this.employeDetails.id,
          editMode: 'true'
        }
      });
    }
  }

  // Reset password
  resetPassword() {
    if (!this.employeDetails || !this.employeDetails.id) {
      this.toastrService.warning('No account found to reset password', 'Warning');
      return;
    }

    const confirmReset = confirm(`Are you sure you want to reset password for ${this.employeDetails.prenom} ${this.employeDetails.nom}?`);
    
    if (confirmReset) {
      this.compteService.resetPassword(this.employeDetails.id).subscribe({
        next: () => {
          this.toastrService.success(
            'Password reset successfully! New password has been sent to console/email.',
            'Password Reset',
            { duration: 5000 }
          );
        },
        error: (err) => {
          console.error(err);
          this.toastrService.danger('Failed to reset password', 'Error');
        }
      });
    }
  }

  goToRoles() {
    if (this.employeDetails?.id) {
      this.router.navigate(['/pages/tables/affectation-roles', this.employeDetails.id], {
        queryParams: {
          employeName: this.employeDetails.prenom + ' ' + this.employeDetails.nom
        }
      });
    } else {
      this.toastrService.warning('No account found for this employee. Please create an account first.', 'Warning');
    }
  }

  // Edit function
  editFunction() {
    if (this.employeDetails?.id) {
      this.router.navigate(['/pages/tables/assign-function', this.employeDetails.id], {
        queryParams: {
          employeName: `${this.employeDetails.prenom} ${this.employeDetails.nom}`
        }
      });
    }
  }

  // Assign function
  assignFunction() {
    this.editFunction();
  }
}