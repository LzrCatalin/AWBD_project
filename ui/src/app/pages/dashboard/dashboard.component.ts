import { Component, inject } from '@angular/core';
import { AuthService } from '@auth0/auth0-angular';
import { AsyncPipe } from '@angular/common';
import { Router } from '@angular/router';
import { AvatarModule } from 'primeng/avatar';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { DividerModule } from 'primeng/divider';

interface StatCard {
  label: string;
  value: string;
  icon: string;
  colorClass: string;
}

interface QuickAction {
  label: string;
  icon: string;
  route: string;
}

@Component({
  selector: 'app-dashboard',
  imports: [AsyncPipe, AvatarModule, ButtonModule, CardModule, DividerModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent {
  protected auth = inject(AuthService);
  private router = inject(Router);

  stats: StatCard[] = [
    { label: 'Flights', value: '—', icon: 'pi pi-send', colorClass: 'stat-blue' },
    { label: 'Bookings', value: '—', icon: 'pi pi-ticket', colorClass: 'stat-green' },
    { label: 'Aircraft', value: '—', icon: 'pi pi-box', colorClass: 'stat-purple' },
    { label: 'Gates', value: '—', icon: 'pi pi-map-marker', colorClass: 'stat-orange' },
  ];

  quickActions: QuickAction[] = [
    { label: 'Flights', icon: 'pi pi-send', route: '/flights' },
    { label: 'Bookings', icon: 'pi pi-ticket', route: '/bookings' },
    { label: 'Aircraft', icon: 'pi pi-box', route: '/aircraft' },
    { label: 'Gates', icon: 'pi pi-map-marker', route: '/gates' },
    { label: 'Seats', icon: 'pi pi-th-large', route: '/seats' },
    { label: 'Accounts', icon: 'pi pi-users', route: '/accounts' },
  ];

  navigate(route: string): void {
    this.router.navigate([route]);
  }

  logout(): void {
    this.auth.logout({ logoutParams: { returnTo: window.location.origin } });
  }
}
