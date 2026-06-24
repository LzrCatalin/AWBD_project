import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { catchError, of } from 'rxjs';
import { MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { CheckboxModule } from 'primeng/checkbox';
import { ToastModule } from 'primeng/toast';
import { SkeletonModule } from 'primeng/skeleton';

import { FlightService } from '../../core/services/flight.service';
import { AccountService } from '../../core/services/account.service';
import { BookingService } from '../../core/services/booking.service';
import { SeatService } from '../../core/services/seat.service';
import { Flight } from '../../shared/models/flight.model';
import { Seat } from '../../shared/models/seat.model';
import { SearchDTO } from '../../shared/models/search.model';
import { Pagination } from '../../shared/models/pagination.model';

const CHECKED_BAG_PRICE = 34;

@Component({
  selector: 'app-checkout',
  imports: [
    FormsModule,
    RouterLink,
    ButtonModule,
    InputTextModule,
    CheckboxModule,
    ToastModule,
    SkeletonModule,
  ],
  templateUrl: './checkout.component.html',
  styleUrl: './checkout.component.scss',
})
export class CheckoutComponent {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private flightService = inject(FlightService);
  private accountService = inject(AccountService);
  private bookingService = inject(BookingService);
  private seatService = inject(SeatService);
  private messageService = inject(MessageService);

  flight = signal<Flight | null>(null);
  loading = signal(true);
  booking = signal(false);

  seats = signal<Seat[]>([]);
  seatsLoading = signal(true);
  selectedSeat = signal<Seat | null>(null);

  // Traveler details (prefilled from the account)
  fullName = '';
  email = '';
  passport = '';

  // Add-ons
  addCheckedBag = false;

  readonly checkedBagPrice = CHECKED_BAG_PRICE;

  total(): number {
    const f = this.flight();
    if (!f) return 0;
    return (f.baseFare ?? 0) + (f.taxes ?? 0) + (this.addCheckedBag ? CHECKED_BAG_PRICE : 0);
  }

  constructor() {
    const flightId = this.route.snapshot.paramMap.get('flightId');
    if (!flightId) {
      this.router.navigate(['/flights']);
      return;
    }

    this.flightService.getById(flightId).pipe(catchError(() => of(null))).subscribe((f) => {
      this.flight.set(f);
      this.loading.set(false);
    });

    // Load the available seats so the traveler can pick (and confirm) one.
    const seatQuery = new SearchDTO({
      filters: [
        { field: 'flight.id', type: 'EQ', value: flightId },
        { field: 'isAvailable', type: 'EQ', value: true },
      ],
      sorters: [{ field: 'seatNo', direction: 'ASC' }],
      pagination: new Pagination({ page: 0, pageSize: 300 }),
    });
    this.seatService.search(seatQuery).pipe(catchError(() => of(null))).subscribe((res) => {
      const seats = res?.items ?? [];
      this.seats.set(seats);
      this.selectedSeat.set(seats[0] ?? null); // default to the first available seat
      this.seatsLoading.set(false);
    });

    this.accountService.getMe().pipe(catchError(() => of(null))).subscribe((acc) => {
      if (acc) {
        this.fullName = `${acc.firstName ?? ''} ${acc.lastName ?? ''}`.trim();
        this.email = acc.email ?? '';
        this.passport = acc.passportNo ?? '';
      }
    });
  }

  selectSeat(seat: Seat): void {
    this.selectedSeat.set(seat);
  }

  get seatsGrouped(): { label: string; seats: Seat[] }[] {
    const order = ['First Class', 'Business', 'Economy'];
    const known = order
      .map((label) => ({ label, seats: this.seats().filter((s) => s.type === label) }))
      .filter((g) => g.seats.length > 0);
    const other = this.seats().filter((s) => !order.includes(s.type));
    return other.length ? [...known, { label: 'Other', seats: other }] : known;
  }

  cityCode(city: string): string {
    return (city ?? '').trim().slice(0, 3).toUpperCase() || '—';
  }

  duration(): string {
    const f = this.flight();
    if (!f?.departureTime || !f?.arrivalTime) return '—';
    const ms = new Date(f.arrivalTime).getTime() - new Date(f.departureTime).getTime();
    if (ms <= 0) return '—';
    const mins = Math.round(ms / 60000);
    return `${Math.floor(mins / 60)}h ${(mins % 60).toString().padStart(2, '0')}m`;
  }

  formatDateTime(dt: string | undefined): string {
    if (!dt) return '—';
    return new Date(dt).toLocaleString('ro-RO', {
      day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit',
    });
  }

  confirm(): void {
    const f = this.flight();
    const seat = this.selectedSeat();
    if (!f || !seat || this.booking()) return;
    this.booking.set(true);
    this.bookingService.create({ flightId: f.id, seatId: seat.id, addCheckedBag: this.addCheckedBag }).subscribe({
      next: (b) => {
        this.booking.set(false);
        this.messageService.add({
          severity: 'success',
          summary: 'Booked!',
          detail: `Booking confirmed — #${b.bookingNo}`,
        });
        setTimeout(() => this.router.navigate(['/bookings']), 900);
      },
      error: () => {
        this.booking.set(false);
        this.messageService.add({
          severity: 'error',
          summary: 'Booking failed',
          detail: 'Could not complete your booking. Please try again.',
        });
      },
    });
  }
}
