import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Seat, SeatForm } from '../../shared/models/seat.model';
import { PagedResponse } from '../../shared/models/paged-response.model';
import { SearchDTO } from '../../shared/models/search.model';

@Injectable({ providedIn: 'root' })
export class SeatService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/api/seats`;

  search(query: SearchDTO): Observable<PagedResponse<Seat>> {
    return this.http.post<PagedResponse<Seat>>(`${this.base}/search`, query);
  }

  getById(id: string): Observable<Seat> {
    return this.http.get<Seat>(`${this.base}/${id}`);
  }

  create(payload: SeatForm): Observable<Seat> {
    return this.http.post<Seat>(this.base, payload);
  }

  update(id: string, payload: SeatForm): Observable<Seat> {
    return this.http.put<Seat>(`${this.base}/${id}`, payload);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
