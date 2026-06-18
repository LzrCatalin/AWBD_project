import { Component, inject, computed, effect, signal } from '@angular/core';
import { AuthService } from '@auth0/auth0-angular';
import { AsyncPipe } from '@angular/common';
import { Router } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError, filter, take } from 'rxjs/operators';
import { CardModule } from 'primeng/card';
import { DividerModule } from 'primeng/divider';
import { SkeletonModule } from 'primeng/skeleton';

import { RoleService } from '../../core/services/role.service';
import { FlightService } from '../../core/services/flight.service';
import { BookingService } from '../../core/services/booking.service';
import { AircraftService } from '../../core/services/aircraft.service';
import { GateService } from '../../core/services/gate.service';
import { SearchDTO } from '../../shared/models/search.model';
import { Pagination } from '../../shared/models/pagination.model';

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
  imports: [AsyncPipe, CardModule, DividerModule, SkeletonModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent {
  protected auth = inject(AuthService);
  protected roles = inject(RoleService);
  private router = inject(Router);
  private flightService = inject(FlightService);
  private bookingService = inject(BookingService);
  private aircraftService = inject(AircraftService);
  private gateService = inject(GateService);

  stats = signal<StatCard[]>([]);
  statsLoading = signal(true);

  private readonly oneItem = new SearchDTO({ pagination: new Pagination({ page: 0, pageSize: 1 }) });

  quickActions = computed<QuickAction[]>(() => {
    if(this.roles.canManage()) {
      return [
        { label: 'Flights',  icon: 'pi pi-send',       route: '/flights'  },
        { label: 'Bookings', icon: 'pi pi-ticket',     route: '/bookings' },
        { label: 'Aircraft', icon: 'pi pi-box',        route: '/aircraft' },
        { label: 'Gates',    icon: 'pi pi-map-marker', route: '/gates'    },
        { label: 'Seats',    icon: 'pi pi-table',      route: '/seats'    },
        ...(this.roles.adminOnly() ? [{ label: 'Accounts', icon: 'pi pi-users', route: '/accounts' }] : []),
      ];
    }else {
      return [
        { label: 'Browse Flights', icon: 'pi pi-send',   route: '/flights'  },
        { label: 'My Bookings',    icon: 'pi pi-ticket', route: '/bookings' },
      ];
    }
  });

  constructor() {
    effect(() => {
      if(this.roles.canManage()) {
        this.loadAdminStats();
      }else if(this.roles.isPassenger()) {
        this.loadPassengerStats();
      }
    });
  }

  private loadAdminStats(): void {
    forkJoin({
      flights:  this.flightService.search(this.oneItem).pipe(catchError(() => of(null))),
      bookings: this.bookingService.search(this.oneItem).pipe(catchError(() => of(null))),
      aircraft: this.aircraftService.search(this.oneItem).pipe(catchError(() => of(null))),
      gates:    this.gateService.search(this.oneItem).pipe(catchError(() => of(null))),
    }).subscribe(({ flights, bookings, aircraft, gates }) => {
      this.stats.set([
        { label: 'Flights',  value: flights?.totalItems  != null ? String(flights.totalItems)  : '—', icon: 'pi pi-send',       colorClass: 'stat-blue'   },
        { label: 'Bookings', value: bookings?.totalItems != null ? String(bookings.totalItems) : '—', icon: 'pi pi-ticket',     colorClass: 'stat-green'  },
        { label: 'Aircraft', value: aircraft?.totalItems != null ? String(aircraft.totalItems) : '—', icon: 'pi pi-box',         colorClass: 'stat-purple' },
        { label: 'Gates',    value: gates?.totalItems    != null ? String(gates.totalItems)    : '—', icon: 'pi pi-map-marker', colorClass: 'stat-orange' },
      ]);
      this.statsLoading.set(false);
    });
  }

  private loadPassengerStats(): void {
    forkJoin({
      flights:  this.flightService.search(this.oneItem).pipe(catchError(() => of(null))),
      bookings: this.bookingService.search(this.oneItem).pipe(catchError(() => of(null))),
    }).subscribe(({ flights, bookings }) => {
      this.stats.set([
        { label: 'Available Flights', value: flights?.totalItems  != null ? String(flights.totalItems)  : '—', icon: 'pi pi-send',   colorClass: 'stat-blue'  },
        { label: 'My Bookings',       value: bookings?.totalItems != null ? String(bookings.totalItems) : '—', icon: 'pi pi-ticket', colorClass: 'stat-green' },
      ]);
      this.statsLoading.set(false);
    });
  }

  navigate(route: string): void {
    this.router.navigate([route]);
  }
}
