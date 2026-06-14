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
import { TooltipModule } from 'primeng/tooltip';
import { PasswordModule } from 'primeng/password';

import { AccountService } from '../../core/services/account.service';
import { RoleService } from '../../core/services/role.service';
import { Account, AccountForm } from '../../shared/models/account.model';
import { SearchDTO } from '../../shared/models/search.model';
import { Pagination } from '../../shared/models/pagination.model';

@Component({
  selector: 'app-accounts',
  imports: [FormsModule, TableModule, ButtonModule, InputTextModule, DialogModule,
            ConfirmDialogModule, ToastModule, TagModule, TooltipModule, PasswordModule],
  templateUrl: './accounts.component.html',
  styleUrl: './accounts.component.scss',
})
export class AccountsComponent {
  private accountService = inject(AccountService);
  private messageService = inject(MessageService);
  private confirmationService = inject(ConfirmationService);
  protected roles = inject(RoleService);

  accounts = signal<Account[]>([]);
  totalRecords = signal(0);
  loading = signal(true);

  searchEmail     = '';
  searchFirstName = '';
  searchLastName  = '';
  pageSize = 10;
  currentPage = 0;
  lastSortField = 'lastName';
  lastSortOrder = 1;

  dialogVisible = false;
  dialogTitle = '';
  editingId: string | null = null;
  form: AccountForm = this.emptyForm();

  private emptyForm(): AccountForm {
    return { email: '', password: '', firstName: '', lastName: '', passportNo: '' };
  }

  loadAccounts(event?: TableLazyLoadEvent): void {
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
    if (this.searchEmail.trim())     filters.push({ field: 'email',     type: 'TRIM_LIKE_IGNORE_CASE' as const, value: this.searchEmail.trim() });
    if (this.searchFirstName.trim()) filters.push({ field: 'firstName', type: 'TRIM_LIKE_IGNORE_CASE' as const, value: this.searchFirstName.trim() });
    if (this.searchLastName.trim())  filters.push({ field: 'lastName',  type: 'TRIM_LIKE_IGNORE_CASE' as const, value: this.searchLastName.trim() });

    const query = new SearchDTO({
      filters,
      sorters: [{ field: this.lastSortField, direction: this.lastSortOrder === 1 ? 'ASC' : 'DESC' }],
      pagination: new Pagination({ page: this.currentPage, pageSize: this.pageSize }),
    });

    this.accountService.search(query).subscribe({
      next: res => {
        this.accounts.set(res.items);
        this.totalRecords.set(res.totalItems);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Could not load accounts' });
      },
    });
  }

  onSearch(): void {
    this.currentPage = 0;
    this.loadAccounts();
  }

  openCreate(): void {
    this.editingId = null;
    this.form = this.emptyForm();
    this.dialogTitle = 'New Account';
    this.dialogVisible = true;
  }

  openEdit(account: Account): void {
    this.editingId = account.id;
    this.form = {
      email: account.email,
      password: '',
      firstName: account.firstName,
      lastName: account.lastName,
      passportNo: account.passportNo,
    };
    this.dialogTitle = 'Edit Account';
    this.dialogVisible = true;
  }

  save(): void {
    const op$ = this.editingId
      ? this.accountService.update(this.editingId, this.form)
      : this.accountService.create(this.form);

    op$.subscribe({
      next: () => {
        this.dialogVisible = false;
        this.messageService.add({ severity: 'success', summary: 'Saved', detail: `Account ${this.form.email} saved` });
        this.loadAccounts();
      },
      error: () => {
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Could not save account' });
      },
    });
  }

  confirmDelete(account: Account): void {
    this.confirmationService.confirm({
      message: `Delete account <strong>${account.email}</strong>?`,
      header: 'Confirm Delete',
      icon: 'pi pi-trash',
      acceptButtonStyleClass: 'p-button-danger',
      accept: () => {
        this.accountService.delete(account.id).subscribe({
          next: () => {
            this.messageService.add({ severity: 'success', summary: 'Deleted', detail: `Account ${account.email} deleted` });
            this.loadAccounts();
          },
          error: () => {
            this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Could not delete account' });
          },
        });
      },
    });
  }

  roleSeverity(role: string): 'success' | 'warn' | 'info' {
    if (role === 'ROLE_ADMIN')     return 'warn';
    if (role === 'ROLE_STAFF')     return 'info';
    return 'success';
  }

  roleLabel(role: string): string {
    return role.replace('ROLE_', '');
  }
}
