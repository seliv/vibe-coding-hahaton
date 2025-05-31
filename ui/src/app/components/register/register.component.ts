import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  template: `
    <div class="register-container">
      <h2>Register for Vibe Chat</h2>
      <form [formGroup]="registerForm" (ngSubmit)="onSubmit()">
        <div class="form-group">
          <label for="username">Username</label>
          <input type="text" id="username" formControlName="username" class="form-control" />
          <div *ngIf="registerForm.get('username')?.errors?.['required'] && registerForm.get('username')?.touched" class="error">
            Username is required
          </div>
          <div *ngIf="registerForm.get('username')?.errors?.['minlength'] && registerForm.get('username')?.touched" class="error">
            Username must be at least 3 characters
          </div>
        </div>
        
        <div class="form-group">
          <label for="password">Password</label>
          <input type="password" id="password" formControlName="password" class="form-control" />
          <div *ngIf="registerForm.get('password')?.errors?.['required'] && registerForm.get('password')?.touched" class="error">
            Password is required
          </div>
          <div *ngIf="registerForm.get('password')?.errors?.['minlength'] && registerForm.get('password')?.touched" class="error">
            Password must be at least 6 characters
          </div>
        </div>
        
        <div class="form-group">
          <label for="confirmPassword">Confirm Password</label>
          <input type="password" id="confirmPassword" formControlName="confirmPassword" class="form-control" />
          <div *ngIf="registerForm.get('confirmPassword')?.errors?.['required'] && registerForm.get('confirmPassword')?.touched" class="error">
            Please confirm your password
          </div>
          <div *ngIf="registerForm.errors?.['passwordMismatch'] && registerForm.get('confirmPassword')?.touched" class="error">
            Passwords do not match
          </div>
        </div>
        
        <div class="form-group">
          <button type="submit" [disabled]="registerForm.invalid || isLoading">
            {{ isLoading ? 'Registering...' : 'Register' }}
          </button>
        </div>
        
        <div *ngIf="error" class="error">
          {{ error }}
        </div>
        
        <div class="login-link">
          Already have an account? <a routerLink="/login">Login</a>
        </div>
      </form>
    </div>
  `,
  styles: [`
    .register-container {
      max-width: 400px;
      margin: 50px auto;
      padding: 20px;
      border: 1px solid #ccc;
      border-radius: 5px;
    }
    
    .form-group {
      margin-bottom: 15px;
    }
    
    .form-control {
      width: 100%;
      padding: 8px;
      border: 1px solid #ccc;
      border-radius: 4px;
    }
    
    button {
      padding: 10px 15px;
      background-color: #4CAF50;
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    }
    
    button:disabled {
      background-color: #cccccc;
      cursor: not-allowed;
    }
    
    .error {
      color: red;
      margin-top: 5px;
    }
    
    .login-link {
      margin-top: 15px;
      text-align: center;
    }
  `]
})
export class RegisterComponent {
  registerForm: FormGroup;
  isLoading = false;
  error = '';
  
  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required]
    }, { validators: this.passwordMatchValidator });
  }
  
  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password')?.value;
    const confirmPassword = form.get('confirmPassword')?.value;
    
    return password === confirmPassword ? null : { passwordMismatch: true };
  }
  
  onSubmit(): void {
    if (this.registerForm.invalid) {
      return;
    }
    
    this.isLoading = true;
    this.error = '';
    
    const { username, password } = this.registerForm.value;
    
    this.authService.register(username, password)
      .subscribe({
        next: () => {
          this.router.navigate(['/']);
        },
        error: error => {
          this.error = error.error?.message || 'Registration failed. Please try again.';
          this.isLoading = false;
        }
      });
  }
}