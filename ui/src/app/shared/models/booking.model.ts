import { Flight } from './flight.model';
import { Account } from './account.model';
import { Seat } from './seat.model';

export interface Booking {
  id: string;
  bookingNo: string;
  flightId: string;
  accountId: string;
  seatId: string;
  totalPrice: number | null;
  bookingDate: string;
  flight: Flight | null;
  account: Account | null;
  seat: Seat | null;
}

export interface BookingForm {
  flightId: string;
  seatId?: string;
  addCheckedBag?: boolean;
}
