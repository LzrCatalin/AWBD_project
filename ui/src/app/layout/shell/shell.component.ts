import { Component, computed, inject, signal } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '@auth0/auth0-angular';
import { AsyncPipe } from '@angular/common';
import { AvatarModule } from 'primeng/avatar';
import { ButtonModule } from 'primeng/button';
import { TooltipModule } from 'primeng/tooltip';
import { RoleService } from '../../core/services/role.service';

interface NavItem {
  label: string;
  icon: string;
  route: string;
  adminOnly?: boolean;
  manageOnly?: boolean;
}

@Component({
  selector: 'app-shell',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, AsyncPipe, AvatarModule, ButtonModule, TooltipModule],
  templateUrl: './shell.component.html',
  styleUrl: './shell.component.scss',
})
export class ShellComponent {
  protected auth = inject(AuthService);
  protected roleService = inject(RoleService);

  collapsed = signal(false);

  /** STAFF/ADMIN get the management sidebar; passengers get a clean top-nav. */
  canManage = computed(() => this.roleService.canManage());

  private sidebarItems: NavItem[] = [
    { label: 'Dashboard', icon: 'pi pi-home',      route: '/dashboard' },
    { label: 'Flights',   icon: 'pi pi-send',       route: '/flights'   },
    { label: 'Bookings',  icon: 'pi pi-ticket',     route: '/bookings'  },
    { label: 'Aircraft',  icon: 'pi pi-box',         route: '/aircraft',  manageOnly: true },
    { label: 'Gates',     icon: 'pi pi-map-marker', route: '/gates',     manageOnly: true },
    { label: 'Seats',     icon: 'pi pi-table',      route: '/seats',     manageOnly: true },
    { label: 'Accounts',  icon: 'pi pi-users',      route: '/accounts',  adminOnly: true  },
  ];

  // Passenger top-nav
  travelerItems: NavItem[] = [
    { label: 'Book Flights', icon: 'pi pi-send',   route: '/flights'  },
    { label: 'My Flights',   icon: 'pi pi-ticket', route: '/bookings' },
    { label: 'Profile',      icon: 'pi pi-user',   route: '/profile'  },
  ];

  navItems = computed(() => this.sidebarItems.filter(item => {
    if (item.adminOnly)  return this.roleService.adminOnly();
    if (item.manageOnly) return this.roleService.canManage();
    return true;
  }));

  toggleSidebar(): void {
    this.collapsed.update(v => !v);
  }

  logout(): void {
    this.auth.logout({ logoutParams: { returnTo: window.location.origin } });
  }
}
