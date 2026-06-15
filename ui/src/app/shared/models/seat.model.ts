import { Flight } from './flight.model';

export interface Seat {
  id: string;
  seatNo: string;
  type: string;
  available: boolean;
  flightId: string;
  flight: Flight | null;
}

export interface SeatForm {
  seatNo: string;
  type: string;
  available: boolean;
  flightId: string;
}
