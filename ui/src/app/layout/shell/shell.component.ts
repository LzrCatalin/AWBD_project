import { Component, inject, signal } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '@auth0/auth0-angular';
import { AsyncPipe } from '@angular/common';
import { AvatarModule } from 'primeng/avatar';
import { ButtonModule } from 'primeng/button';
import { TooltipModule } from 'primeng/tooltip';

interface NavItem {
  label: string;
  icon: string;
  route: string;
}

@Component({
  selector: 'app-shell',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, AsyncPipe, AvatarModule, ButtonModule, TooltipModule],
  templateUrl: './shell.component.html',
  styleUrl: './shell.component.scss',
})
export class ShellComponent {
  protected auth = inject(AuthService);

  collapsed = signal(false);

  navItems: NavItem[] = [
    { label: 'Dashboard', icon: 'pi pi-home',       route: '/dashboard' },
    { label: 'Flights',   icon: 'pi pi-send',        route: '/flights'   },
    { label: 'Bookings',  icon: 'pi pi-ticket',      route: '/bookings'  },
    { label: 'Aircraft',  icon: 'pi pi-box',          route: '/aircraft'  },
    { label: 'Gates',     icon: 'pi pi-map-marker',  route: '/gates'     },
    { label: 'Seats',     icon: 'pi pi-table',       route: '/seats'     },
    { label: 'Accounts',  icon: 'pi pi-users',       route: '/accounts'  },
  ];

  toggleSidebar(): void {
    this.collapsed.update(v => !v);
  }

  logout(): void {
    this.auth.logout({ logoutParams: { returnTo: window.location.origin } });
  }
}
