import { Component, inject, signal } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { AuthService } from '@auth0/auth0-angular';
import { catchError, of } from 'rxjs';
import { SkeletonModule } from 'primeng/skeleton';
import { AvatarModule } from 'primeng/avatar';
import { AccountService } from '../../core/services/account.service';
import { BookingService } from '../../core/services/booking.service';
import { Account } from '../../shared/models/account.model';
import { SearchDTO } from '../../shared/models/search.model';
import { Pagination } from '../../shared/models/pagination.model';

interface TravelStats {
  flights: number;
  destinations: number;
  spent: number;
}

@Component({
  selector: 'app-profile',
  imports: [AsyncPipe, SkeletonModule, AvatarModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss',
})
export class ProfileComponent {
  protected auth = inject(AuthService);
  private accountService = inject(AccountService);
  private bookingService = inject(BookingService);

  account = signal<Account | null>(null);
  loading = signal(true);
  stats = signal<TravelStats | null>(null);

  constructor() {
    this.accountService
      .getMe()
      .pipe(catchError(() => of(null)))
      .subscribe((acc) => {
        this.account.set(acc);
        this.loading.set(false);
      });

    // Travel stats from the current user's bookings.
    this.bookingService
      .search(new SearchDTO({ pagination: new Pagination({ page: 0, pageSize: 500 }) }))
      .pipe(catchError(() => of(null)))
      .subscribe((res) => {
        const items = res?.items ?? [];
        const destinations = new Set(
          items.map((b) => b.flight?.arrivalCity).filter((c): c is string => !!c),
        );
        const spent = items.reduce((sum, b) => sum + (b.totalPrice ?? 0), 0);
        this.stats.set({ flights: items.length, destinations: destinations.size, spent });
      });
  }
}
