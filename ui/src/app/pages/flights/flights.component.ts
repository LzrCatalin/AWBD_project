import {Component, inject, OnInit, signal} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {ConfirmationService, MessageService} from 'primeng/api';
import {TableLazyLoadEvent, TableModule} from 'primeng/table';
import {ButtonModule} from 'primeng/button';
import {InputTextModule} from 'primeng/inputtext';
import {DialogModule} from 'primeng/dialog';
import {ConfirmDialogModule} from 'primeng/confirmdialog';
import {ToastModule} from 'primeng/toast';
import {TagModule} from 'primeng/tag';
import {DatePickerModule} from 'primeng/datepicker';
import {SelectModule} from 'primeng/select';
import {TooltipModule} from 'primeng/tooltip';

import {FlightService} from '../../core/services/flight.service';
import {AircraftService} from '../../core/services/aircraft.service';
import {GateService} from '../../core/services/gate.service';
import {SeatService} from '../../core/services/seat.service';
import {BookingService} from '../../core/services/booking.service';
import {AccountService} from '../../core/services/account.service';
import {RoleService} from '../../core/services/role.service';
import {Flight, FlightForm} from '../../shared/models/flight.model';
import {Seat} from '../../shared/models/seat.model';
import {SearchDTO} from '../../shared/models/search.model';
import {Pagination} from '../../shared/models/pagination.model';

@Component({
  selector: 'app-flights',
  imports: [
    FormsModule,
    TableModule,
    ButtonModule,
    InputTextModule,
    DialogModule,
    ConfirmDialogModule,
    ToastModule,
    TagModule,
    DatePickerModule,
    SelectModule,
    TooltipModule,
  ],
  templateUrl: './flights.component.html',
  styleUrl: './flights.component.scss',
})
export class FlightsComponent implements OnInit {

  protected roles = inject(RoleService);

  flights = signal<Flight[]>([]);
  totalRecords = signal(0);
  loading = signal(true);

  searchFlightNo = '';
  searchFrom = '';
  searchTo = '';
  pageSize = 10;
  currentPage = 0;
  lastSortField = 'departureTime';
  lastSortOrder = 1;

  dialogVisible = false;
  dialogTitle = '';
  editingId: string | null = null;

  form: FlightForm = this.emptyForm();

  aircraftOptions: { label: string; value: string }[] = [];
  gateOptions: { label: string; value: string | null }[] = [];

  // Booking
  bookingDialogVisible = false;
  selectedFlight: Flight | null = null;
  availableSeats = signal<Seat[]>([]);
  selectedSeat: Seat | null = null;
  seatsLoading = signal(false);
  bookingLoading = signal(false);

  private flightService = inject(FlightService);
  private aircraftService = inject(AircraftService);
  private gateService = inject(GateService);
  private seatService = inject(SeatService);
  private bookingService = inject(BookingService);
  private accountService = inject(AccountService);
  private messageService = inject(MessageService);
  private confirmationService = inject(ConfirmationService);
  private currentAccountId: string | null = null;

  ngOnInit(): void {
    if (this.roles.canManage()) {
      this.loadDropdowns();
    }
  }

  private emptyForm(): FlightForm {
    return {
      flightNo: '',
      departureTime: '',
      arrivalTime: '',
      departureCity: '',
      arrivalCity: '',
      aircraftId: '',
      gateId: null,
    };
  }

  private loadDropdowns(): void {
    const allPages = new SearchDTO({pagination: new Pagination({page: 0, pageSize: 100})});

    this.aircraftService.search(allPages).subscribe(res => {
      this.aircraftOptions = res.items.map(a => ({
        label: `${a.model} (${a.planeNo})`,
        value: a.id,
      }));
    });

    this.gateService.search(allPages).subscribe(res => {
      this.gateOptions = [
        {label: 'None', value: null},
        ...res.items.map(g => ({label: `${g.gateNo} — ${g.terminal}`, value: g.id})),
      ];
    });
  }

  loadFlights(event?: TableLazyLoadEvent): void {
    if (event) {
      this.currentPage = Math.floor((event.first ?? 0) / (event.rows ?? this.pageSize));
      this.pageSize = event.rows ?? this.pageSize;
      if (event.sortField) {
        this.lastSortField = Array.isArray(event.sortField) ? event.sortField[0] : event.sortField;
        this.lastSortOrder = event.sortOrder ?? 1;
      }
    }

    this.loading.set(true);

    const filters = [];
    if (this.searchFlightNo.trim()) filters.push({
      field: 'flightNo',
      type: 'TRIM_LIKE_IGNORE_CASE' as const,
      value: this.searchFlightNo.trim()
    });
    if (this.searchFrom.trim()) filters.push({
      field: 'departureCity',
      type: 'TRIM_LIKE_IGNORE_CASE' as const,
      value: this.searchFrom.trim()
    });
    if (this.searchTo.trim()) filters.push({
      field: 'arrivalCity',
      type: 'TRIM_LIKE_IGNORE_CASE' as const,
      value: this.searchTo.trim()
    });

    const query = new SearchDTO({
      filters,
      sorters: [{field: this.lastSortField, direction: this.lastSortOrder === 1 ? 'ASC' : 'DESC'}],
      pagination: new Pagination({page: this.currentPage, pageSize: this.pageSize}),
    });

    this.flightService.search(query).subscribe({
      next: res => {
        this.flights.set(res.items);
        this.totalRecords.set(res.totalItems);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.messageService.add({severity: 'error', summary: 'Error', detail: 'Could not load flights'});
      },
    });
  }

  onSearch(): void {
    this.currentPage = 0;
    this.loadFlights();
  }

  clearSearch(): void {
    this.searchFlightNo = '';
    this.searchFrom = '';
    this.searchTo = '';
    this.onSearch();
  }

  openCreate(): void {
    this.editingId = null;
    this.form = this.emptyForm();
    this.dialogTitle = 'New Flight';
    this.dialogVisible = true;
  }

  openEdit(flight: Flight): void {
    this.editingId = flight.id;
    this.form = {
      flightNo: flight.flightNo,
      departureTime: flight.departureTime,
      arrivalTime: flight.arrivalTime,
      departureCity: flight.departureCity,
      arrivalCity: flight.arrivalCity,
      aircraftId: flight.aircraftId,
      gateId: flight.gateId,
    };
    this.dialogTitle = 'Edit Flight';
    this.dialogVisible = true;
  }

  save(): void {
    const op$ = this.editingId
      ? this.flightService.update(this.editingId, this.form)
      : this.flightService.create(this.form);

    op$.subscribe({
      next: () => {
        this.dialogVisible = false;
        this.messageService.add({
          severity: 'success',
          summary: 'Saved',
          detail: `Flight ${this.form.flightNo} saved`,
        });
        this.loadFlights();
      },
      error: () => {
        this.messageService.add({severity: 'error', summary: 'Error', detail: 'Could not save flight'});
      },
    });
  }

  confirmDelete(flight: Flight): void {
    this.confirmationService.confirm({
      message: `Delete flight <strong>${flight.flightNo}</strong>?`,
      header: 'Confirm Delete',
      icon: 'pi pi-trash',
      acceptButtonStyleClass: 'p-button-danger',
      accept: () => {
        this.flightService.delete(flight.id).subscribe({
          next: () => {
            this.messageService.add({
              severity: 'success',
              summary: 'Deleted',
              detail: `Flight ${flight.flightNo} deleted`
            });
            this.loadFlights();
          },
          error: () => {
            this.messageService.add({severity: 'error', summary: 'Error', detail: 'Could not delete flight'});
          },
        });
      },
    });
  }

  openBooking(flight: Flight): void {
    this.selectedFlight = flight;
    this.selectedSeat = null;
    this.availableSeats.set([]);
    this.bookingDialogVisible = true;
    this.seatsLoading.set(true);

    const loadSeats = () => {
      const query = new SearchDTO({
        filters: [
          {field: 'flight.id', type: 'EQ' as const, value: flight.id},
          {field: 'isAvailable', type: 'EQ' as const, value: true},
        ],
        sorters: [{field: 'type', direction: 'ASC'}],
        pagination: new Pagination({page: 0, pageSize: 200}),
      });
      this.seatService.search(query).subscribe({
        next: res => {
          this.availableSeats.set(res.items);
          this.seatsLoading.set(false);
        },
        error: () => {
          this.seatsLoading.set(false);
          this.messageService.add({severity: 'error', summary: 'Error', detail: 'Could not load seats'});
        },
      });
    };

    if (this.currentAccountId) {
      loadSeats();
    } else {
      this.accountService.getMe().subscribe({
        next: account => {
          this.currentAccountId = account.id;
          loadSeats();
        },
        error: () => {
          this.seatsLoading.set(false);
          this.bookingDialogVisible = false;
          this.messageService.add({severity: 'error', summary: 'Error', detail: 'Could not load your account'});
        },
      });
    }
  }

  get seatsGrouped(): { label: string; seats: Seat[] }[] {
    const order = ['First Class', 'Business', 'Economy'];
    return order
      .map(label => ({label, seats: this.availableSeats().filter(s => s.type === label)}))
      .filter(g => g.seats.length > 0);
  }

  selectSeat(seat: Seat): void {
    this.selectedSeat = this.selectedSeat?.id === seat.id ? null : seat;
  }

  confirmBooking(): void {
    if (!this.selectedSeat || !this.selectedFlight || !this.currentAccountId) return;
    this.bookingLoading.set(true);
    this.bookingService.create({
      flightId: this.selectedFlight.id,
      seatId: this.selectedSeat.id,
    }).subscribe({
      next: booking => {
        this.bookingLoading.set(false);
        this.bookingDialogVisible = false;
        this.messageService.add({
          severity: 'success',
          summary: 'Booked!',
          detail: `Booking confirmed — #${booking.bookingNo}`,
        });
      },
      error: () => {
        this.bookingLoading.set(false);
        this.messageService.add({severity: 'error', summary: 'Error', detail: 'Could not create booking'});
      },
    });
  }

  formatDateTime(dt: string): string {
    if (!dt) return '—';
    return new Date(dt).toLocaleString('ro-RO', {
      day: '2-digit', month: '2-digit', year: 'numeric',
      hour: '2-digit', minute: '2-digit',
    });
  }

}
