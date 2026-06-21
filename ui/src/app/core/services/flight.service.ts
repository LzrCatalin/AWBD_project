import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Flight, FlightForm } from '../../shared/models/flight.model';
import { PagedResponse } from '../../shared/models/paged-response.model';
import { SearchDTO } from '../../shared/models/search.model';

@Injectable({ providedIn: 'root' })
export class FlightService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/api/flights`;

  search(query: SearchDTO): Observable<PagedResponse<Flight>> {
    return this.http.post<PagedResponse<Flight>>(`${this.base}/search`, query);
  }

  getById(id: string): Observable<Flight> {
    return this.http.get<Flight>(`${this.base}/${id}`);
  }

  create(payload: FlightForm): Observable<Flight> {
    return this.http.post<Flight>(this.base, payload);
  }

  update(id: string, payload: FlightForm): Observable<Flight> {
    return this.http.put<Flight>(`${this.base}/${id}`, payload);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
