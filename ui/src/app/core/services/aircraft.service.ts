import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Aircraft, AircraftForm } from '../../shared/models/aircraft.model';
import { PagedResponse } from '../../shared/models/paged-response.model';
import { SearchDTO } from '../../shared/models/search.model';

@Injectable({ providedIn: 'root' })
export class AircraftService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/api/aircrafts`;

  search(query: SearchDTO): Observable<PagedResponse<Aircraft>> {
    return this.http.post<PagedResponse<Aircraft>>(`${this.base}/search`, query);
  }

  getById(id: string): Observable<Aircraft> {
    return this.http.get<Aircraft>(`${this.base}/${id}`);
  }

  create(payload: AircraftForm): Observable<Aircraft> {
    return this.http.post<Aircraft>(this.base, payload);
  }

  update(id: string, payload: AircraftForm): Observable<Aircraft> {
    return this.http.put<Aircraft>(`${this.base}/${id}`, payload);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
