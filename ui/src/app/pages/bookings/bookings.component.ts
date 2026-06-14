import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MessageService, ConfirmationService } from 'primeng/api';
import { TableModule, TableLazyLoadEvent } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { DialogModule } from 'primeng/dialog';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ToastModule } from 'primeng/toast';
import { TagModule } from 'primeng/tag';
import { SelectModule } from 'primeng/select';
import { TooltipModule } from 'primeng/tooltip';

import { BookingService } from '../../core/services/booking.service';
import { FlightService } from '../../core/services/flight.service';
import { SeatService } from '../../core/services/seat.service';
import { RoleService } from '../../core/services/role.service';
import { Booking, BookingForm } from '../../shared/models/booking.model';
import { SearchDTO } from '../../shared/models/search.model';
import { Pagination } from '../../shared/models/pagination.model';

type SelectOption = { label: string; value: string };

@Component({
  selector: 'app-bookings',
  imports: [FormsModule, TableModule, ButtonModule, InputTextModule, DialogModule,
            ConfirmDialogModule, ToastModule, TagModule, SelectModule, TooltipModule],
  templateUrl: './bookings.component.html',
  styleUrl: './bookings.component.scss',
})
export class BookingsComponent {
  private bookingService = inject(BookingService);
  private flightService = inject(FlightService);
  private seatService = inject(SeatService);
  private messageService = inject(MessageService);
  private confirmationService = inject(ConfirmationService);
  protected roles = inject(RoleService);

  bookings = signal<Booking[]>([]);
  totalRecords = signal(0);
  loading = signal(true);

  filterFlightId = '';
  pageSize = 10;
  currentPage = 0;
  lastSortField = 'bookingDate';
  lastSortOrder = -1;

  dialogVisible = false;
  form: BookingForm = this.emptyForm();

  flightOptions: SelectOption[] = [];
  seatOptions  = signal<SelectOption[]>([]);
  loadingSeats = signal(false);

  // Labels cache for table display
  private flightMap = new Map<string, string>();
  private seatMap   = new Map<string, string>();

  ngOnInit(): void {
    this.flightService
      .search(new SearchDTO({ pagination: new Pagination({ page: 0, pageSize: 100 }) }))
      .subscribe(res => {
        this.flightOptions = res.items.map(f => ({
          label: `${f.flightNo} — ${f.departureCity} → ${f.arrivalCity}`,
          value: f.id,
        }));
        res.items.forEach(f => this.flightMap.set(f.id, `${f.flightNo} (${f.departureCity} → ${f.arrivalCity})`));
      });
  }

  private emptyForm(): BookingForm {
    return { flightId: '', accountId: '', seatId: '' };
  }

  onFlightSelected(): void {
    this.form.seatId = '';
    this.seatOptions.set([]);
    if (!this.form.flightId) return;

    this.loadingSeats.set(true);
    const query = new SearchDTO({
      filters: [
        { field: 'flight.id', type: 'EQ', value: this.form.flightId },
        { field: 'available', type: 'EQ', value: true },
      ],
      pagination: new Pagination({ page: 0, pageSize: 200 }),
    });

    this.seatService.search(query).subscribe({
      next: res => {
        this.seatOptions.set(res.items.map(s => ({
          label: `${s.seatNo} — ${s.type || 'Economy'}`,
          value: s.id,
        })));
        res.items.forEach(s => this.seatMap.set(s.id, s.seatNo));
        this.loadingSeats.set(false);
      },
      error: () => this.loadingSeats.set(false),
    });
  }

  loadBookings(event?: TableLazyLoadEvent): void {
    if (event) {
      this.currentPage = Math.floor((event.first ?? 0) / (event.rows ?? this.pageSize));
      this.pageSize = event.rows ?? this.pageSize;
      if (event.sortField) {
        this.lastSortField = Array.isArray(event.sortField) ? event.sortField[0] : event.sortField;
        this.lastSortOrder = event.sortOrder ?? -1;
      }
    }

    this.loading.set(true);

    const filters = [];
    if (this.filterFlightId) filters.push({ field: 'flight.id', type: 'EQ' as const, value: this.filterFlightId });

    const query = new SearchDTO({
      filters,
      sorters: [{ field: this.lastSortField, direction: this.lastSortOrder === 1 ? 'ASC' : 'DESC' }],
      pagination: new Pagination({ page: this.currentPage, pageSize: this.pageSize }),
    });

    this.bookingService.search(query).subscribe({
      next: res => {
        this.bookings.set(res.items);
        this.totalRecords.set(res.totalItems);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Could not load bookings' });
      },
    });
  }

  onSearch(): void {
    this.currentPage = 0;
    this.loadBookings();
  }

  openCreate(): void {
    this.form = this.emptyForm();
    this.seatOptions.set([]);
    this.dialogVisible = true;
  }

  save(): void {
    this.bookingService.create(this.form).subscribe({
      next: (b) => {
        this.dialogVisible = false;
        this.messageService.add({ severity: 'success', summary: 'Booked', detail: `Booking ${b.bookingNo} created` });
        this.loadBookings();
      },
      error: () => {
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Could not create booking' });
      },
    });
  }

  confirmDelete(booking: Booking): void {
    this.confirmationService.confirm({
      message: `Cancel booking <strong>${booking.bookingNo}</strong>?`,
      header: 'Confirm Cancellation',
      icon: 'pi pi-trash',
      acceptButtonStyleClass: 'p-button-danger',
      accept: () => {
        this.bookingService.delete(booking.id).subscribe({
          next: () => {
            this.messageService.add({ severity: 'success', summary: 'Cancelled', detail: `Booking ${booking.bookingNo} cancelled` });
            this.loadBookings();
          },
          error: () => {
            this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Could not cancel booking' });
          },
        });
      },
    });
  }

  flightLabel(id: string): string {
    return this.flightMap.get(id) ?? id;
  }

  seatLabel(id: string): string {
    return this.seatMap.get(id) ?? id;
  }

  formatDate(dt: string): string {
    if (!dt) return '—';
    return new Date(dt).toLocaleString('ro-RO', {
      day: '2-digit', month: '2-digit', year: 'numeric',
      hour: '2-digit', minute: '2-digit',
    });
  }
}
