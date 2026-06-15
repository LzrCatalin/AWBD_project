import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Booking, BookingForm } from '../../shared/models/booking.model';
import { PagedResponse } from '../../shared/models/paged-response.model';
import { SearchDTO } from '../../shared/models/search.model';

@Injectable({ providedIn: 'root' })
export class BookingService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/api/booking`;

  search(query: SearchDTO): Observable<PagedResponse<Booking>> {
    return this.http.post<PagedResponse<Booking>>(`${this.base}/search`, query);
  }

  getById(id: string): Observable<Booking> {
    return this.http.get<Booking>(`${this.base}/${id}`);
  }

  create(payload: BookingForm): Observable<Booking> {
    return this.http.post<Booking>(this.base, payload);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
