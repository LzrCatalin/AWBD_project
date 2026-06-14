import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Account, AccountForm } from '../../shared/models/account.model';
import { PagedResponse } from '../../shared/models/paged-response.model';
import { SearchDTO } from '../../shared/models/search.model';

@Injectable({ providedIn: 'root' })
export class AccountService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/api/accounts`;

  search(query: SearchDTO): Observable<PagedResponse<Account>> {
    return this.http.post<PagedResponse<Account>>(`${this.base}/search`, query);
  }

  getById(id: string): Observable<Account> {
    return this.http.get<Account>(`${this.base}/${id}`);
  }

  create(payload: AccountForm): Observable<Account> {
    return this.http.post<Account>(this.base, payload);
  }

  update(id: string, payload: AccountForm): Observable<Account> {
    return this.http.put<Account>(`${this.base}/${id}`, payload);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
