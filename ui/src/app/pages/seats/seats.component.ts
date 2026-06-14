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
import { ToggleSwitchModule } from 'primeng/toggleswitch';
import { TooltipModule } from 'primeng/tooltip';

import { SeatService } from '../../core/services/seat.service';
import { FlightService } from '../../core/services/flight.service';
import { RoleService } from '../../core/services/role.service';
import { Seat, SeatForm } from '../../shared/models/seat.model';
import { SearchDTO } from '../../shared/models/search.model';
import { Pagination } from '../../shared/models/pagination.model';

@Component({
  selector: 'app-seats',
  imports: [FormsModule, TableModule, ButtonModule, InputTextModule, DialogModule,
            ConfirmDialogModule, ToastModule, TagModule, SelectModule, ToggleSwitchModule, TooltipModule],
  templateUrl: './seats.component.html',
  styleUrl: './seats.component.scss',
})
export class SeatsComponent {
  private seatService = inject(SeatService);
  private flightService = inject(FlightService);
  private messageService = inject(MessageService);
  private confirmationService = inject(ConfirmationService);
  protected roles = inject(RoleService);

  seats = signal<Seat[]>([]);
  totalRecords = signal(0);
  loading = signal(true);

  filterFlightId = '';
  filterSeatNo = '';
  pageSize = 10;
  currentPage = 0;
  lastSortField = 'seatNo';
  lastSortOrder = 1;

  dialogVisible = false;
  dialogTitle = '';
  editingId: string | null = null;
  form: SeatForm = this.emptyForm();

  flightOptions: { label: string; value: string }[] = [];

  seatTypeOptions = [
    { label: 'Economy',     value: 'Economy'     },
    { label: 'Business',    value: 'Business'    },
    { label: 'First Class', value: 'First Class' },
  ];

  ngOnInit(): void {
    this.flightService.search(new SearchDTO({ pagination: new Pagination({ page: 0, pageSize: 100 }) }))
      .subscribe(res => {
        this.flightOptions = res.items.map(f => ({
          label: `${f.flightNo} — ${f.departureCity} → ${f.arrivalCity}`,
          value: f.id,
        }));
      });
  }

  private emptyForm(): SeatForm {
    return { seatNo: '', type: 'Economy', available: true, flightId: '' };
  }

  loadSeats(event?: TableLazyLoadEvent): void {
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
    if (this.filterFlightId) filters.push({ field: 'flight.id', type: 'EQ' as const, value: this.filterFlightId });
    if (this.filterSeatNo.trim()) filters.push({ field: 'seatNo', type: 'TRIM_LIKE_IGNORE_CASE' as const, value: this.filterSeatNo.trim() });

    const query = new SearchDTO({
      filters,
      sorters: [{ field: this.lastSortField, direction: this.lastSortOrder === 1 ? 'ASC' : 'DESC' }],
      pagination: new Pagination({ page: this.currentPage, pageSize: this.pageSize }),
    });

    this.seatService.search(query).subscribe({
      next: res => {
        this.seats.set(res.items);
        this.totalRecords.set(res.totalItems);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Could not load seats' });
      },
    });
  }

  onSearch(): void {
    this.currentPage = 0;
    this.loadSeats();
  }

  openCreate(): void {
    this.editingId = null;
    this.form = this.emptyForm();
    this.dialogTitle = 'New Seat';
    this.dialogVisible = true;
  }

  openEdit(seat: Seat): void {
    this.editingId = seat.id;
    this.form = { seatNo: seat.seatNo, type: seat.type, available: seat.available, flightId: seat.flightId };
    this.dialogTitle = 'Edit Seat';
    this.dialogVisible = true;
  }

  save(): void {
    const op$ = this.editingId
      ? this.seatService.update(this.editingId, this.form)
      : this.seatService.create(this.form);

    op$.subscribe({
      next: () => {
        this.dialogVisible = false;
        this.messageService.add({ severity: 'success', summary: 'Saved', detail: `Seat ${this.form.seatNo} saved` });
        this.loadSeats();
      },
      error: () => {
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Could not save seat' });
      },
    });
  }

  confirmDelete(seat: Seat): void {
    this.confirmationService.confirm({
      message: `Delete seat <strong>${seat.seatNo}</strong>?`,
      header: 'Confirm Delete',
      icon: 'pi pi-trash',
      acceptButtonStyleClass: 'p-button-danger',
      accept: () => {
        this.seatService.delete(seat.id).subscribe({
          next: () => {
            this.messageService.add({ severity: 'success', summary: 'Deleted', detail: `Seat ${seat.seatNo} deleted` });
            this.loadSeats();
          },
          error: () => {
            this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Could not delete seat' });
          },
        });
      },
    });
  }

  flightLabel(id: string): string {
    return this.flightOptions.find(f => f.value === id)?.label ?? id;
  }
}
