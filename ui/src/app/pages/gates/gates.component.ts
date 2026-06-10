import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MessageService, ConfirmationService } from 'primeng/api';
import { TableModule, TableLazyLoadEvent } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { DialogModule } from 'primeng/dialog';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ToastModule } from 'primeng/toast';
import { TooltipModule } from 'primeng/tooltip';

import { GateService } from '../../core/services/gate.service';
import { Gate, GateForm } from '../../shared/models/gate.model';
import { SearchDTO } from '../../shared/models/search.model';
import { Pagination } from '../../shared/models/pagination.model';

@Component({
  selector: 'app-gates',
  imports: [FormsModule, TableModule, ButtonModule, InputTextModule,
            DialogModule, ConfirmDialogModule, ToastModule, TooltipModule],
  templateUrl: './gates.component.html',
  styleUrl: './gates.component.scss',
})
export class GatesComponent {
  private gateService = inject(GateService);
  private messageService = inject(MessageService);
  private confirmationService = inject(ConfirmationService);

  gates = signal<Gate[]>([]);
  totalRecords = signal(0);
  loading = signal(true);

  searchGateNo = '';
  searchTerminal = '';
  pageSize = 10;
  currentPage = 0;
  lastSortField = 'gateNo';
  lastSortOrder = 1;

  dialogVisible = false;
  dialogTitle = '';
  editingId: string | null = null;
  form: GateForm = { gateNo: '', terminal: '' };

  loadGates(event?: TableLazyLoadEvent): void {
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
    if (this.searchGateNo.trim())   filters.push({ field: 'gateNo',   type: 'TRIM_LIKE_IGNORE_CASE' as const, value: this.searchGateNo.trim() });
    if (this.searchTerminal.trim()) filters.push({ field: 'terminal', type: 'TRIM_LIKE_IGNORE_CASE' as const, value: this.searchTerminal.trim() });

    const query = new SearchDTO({
      filters,
      sorters: [{ field: this.lastSortField, direction: this.lastSortOrder === 1 ? 'ASC' : 'DESC' }],
      pagination: new Pagination({ page: this.currentPage, pageSize: this.pageSize }),
    });

    this.gateService.search(query).subscribe({
      next: res => {
        this.gates.set(res.items);
        this.totalRecords.set(res.totalItems);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Could not load gates' });
      },
    });
  }

  onSearch(): void {
    this.currentPage = 0;
    this.loadGates();
  }

  openCreate(): void {
    this.editingId = null;
    this.form = { gateNo: '', terminal: '' };
    this.dialogTitle = 'New Gate';
    this.dialogVisible = true;
  }

  openEdit(gate: Gate): void {
    this.editingId = gate.id;
    this.form = { gateNo: gate.gateNo, terminal: gate.terminal };
    this.dialogTitle = 'Edit Gate';
    this.dialogVisible = true;
  }

  save(): void {
    const op$ = this.editingId
      ? this.gateService.update(this.editingId, this.form)
      : this.gateService.create(this.form);

    op$.subscribe({
      next: () => {
        this.dialogVisible = false;
        this.messageService.add({ severity: 'success', summary: 'Saved', detail: `Gate ${this.form.gateNo} saved` });
        this.loadGates();
      },
      error: () => {
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Could not save gate' });
      },
    });
  }

  confirmDelete(gate: Gate): void {
    this.confirmationService.confirm({
      message: `Delete gate <strong>${gate.gateNo}</strong>?`,
      header: 'Confirm Delete',
      icon: 'pi pi-trash',
      acceptButtonStyleClass: 'p-button-danger',
      accept: () => {
        this.gateService.delete(gate.id).subscribe({
          next: () => {
            this.messageService.add({ severity: 'success', summary: 'Deleted', detail: `Gate ${gate.gateNo} deleted` });
            this.loadGates();
          },
          error: () => {
            this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Could not delete gate' });
          },
        });
      },
    });
  }
}
