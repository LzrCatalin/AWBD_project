import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MessageService, ConfirmationService } from 'primeng/api';
import { TableModule, TableLazyLoadEvent } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { DialogModule } from 'primeng/dialog';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ToastModule } from 'primeng/toast';
import { TooltipModule } from 'primeng/tooltip';

import { AircraftService } from '../../core/services/aircraft.service';
import { Aircraft, AircraftForm } from '../../shared/models/aircraft.model';
import { SearchDTO } from '../../shared/models/search.model';
import { Pagination } from '../../shared/models/pagination.model';

@Component({
  selector: 'app-aircraft',
  imports: [FormsModule, TableModule, ButtonModule, InputTextModule, InputNumberModule,
            DialogModule, ConfirmDialogModule, ToastModule, TooltipModule],
  templateUrl: './aircraft.component.html',
  styleUrl: './aircraft.component.scss',
})
export class AircraftComponent {
  private aircraftService = inject(AircraftService);
  private messageService = inject(MessageService);
  private confirmationService = inject(ConfirmationService);

  aircrafts = signal<Aircraft[]>([]);
  totalRecords = signal(0);
  loading = signal(true);

  searchModel = '';
  pageSize = 10;
  currentPage = 0;
  lastSortField = 'model';
  lastSortOrder = 1;

  dialogVisible = false;
  dialogTitle = '';
  editingId: string | null = null;
  form: AircraftForm = this.emptyForm();

  private emptyForm(): AircraftForm {
    return { model: '', capacity: 0, planeNo: '' };
  }

  loadAircrafts(event?: TableLazyLoadEvent): void {
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
    if (this.searchModel.trim()) {
      filters.push({ field: 'model', type: 'TRIM_LIKE_IGNORE_CASE' as const, value: this.searchModel.trim() });
    }

    const query = new SearchDTO({
      filters,
      sorters: [{ field: this.lastSortField, direction: this.lastSortOrder === 1 ? 'ASC' : 'DESC' }],
      pagination: new Pagination({ page: this.currentPage, pageSize: this.pageSize }),
    });

    this.aircraftService.search(query).subscribe({
      next: res => {
        this.aircrafts.set(res.items);
        this.totalRecords.set(res.totalItems);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Could not load aircraft' });
      },
    });
  }

  onSearch(): void {
    this.currentPage = 0;
    this.loadAircrafts();
  }

  openCreate(): void {
    this.editingId = null;
    this.form = this.emptyForm();
    this.dialogTitle = 'New Aircraft';
    this.dialogVisible = true;
  }

  openEdit(aircraft: Aircraft): void {
    this.editingId = aircraft.id;
    this.form = { model: aircraft.model, capacity: aircraft.capacity, planeNo: aircraft.planeNo };
    this.dialogTitle = 'Edit Aircraft';
    this.dialogVisible = true;
  }

  save(): void {
    const op$ = this.editingId
      ? this.aircraftService.update(this.editingId, this.form)
      : this.aircraftService.create(this.form);

    op$.subscribe({
      next: () => {
        this.dialogVisible = false;
        this.messageService.add({ severity: 'success', summary: 'Saved', detail: `Aircraft ${this.form.model} saved` });
        this.loadAircrafts();
      },
      error: () => {
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Could not save aircraft' });
      },
    });
  }

  confirmDelete(aircraft: Aircraft): void {
    this.confirmationService.confirm({
      message: `Delete <strong>${aircraft.model} (${aircraft.planeNo})</strong>?`,
      header: 'Confirm Delete',
      icon: 'pi pi-trash',
      acceptButtonStyleClass: 'p-button-danger',
      accept: () => {
        this.aircraftService.delete(aircraft.id).subscribe({
          next: () => {
            this.messageService.add({ severity: 'success', summary: 'Deleted', detail: `${aircraft.model} deleted` });
            this.loadAircrafts();
          },
          error: () => {
            this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Could not delete aircraft' });
          },
        });
      },
    });
  }
}
