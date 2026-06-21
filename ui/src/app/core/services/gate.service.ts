import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Gate, GateForm } from '../../shared/models/gate.model';
import { PagedResponse } from '../../shared/models/paged-response.model';
import { SearchDTO } from '../../shared/models/search.model';

@Injectable({ providedIn: 'root' })
export class GateService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/api/gates`;

  search(query: SearchDTO): Observable<PagedResponse<Gate>> {
    return this.http.post<PagedResponse<Gate>>(`${this.base}/search`, query);
  }

  getById(id: string): Observable<Gate> {
    return this.http.get<Gate>(`${this.base}/${id}`);
  }

  create(payload: GateForm): Observable<Gate> {
    return this.http.post<Gate>(this.base, payload);
  }

  update(id: string, payload: GateForm): Observable<Gate> {
    return this.http.put<Gate>(`${this.base}/${id}`, payload);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
