export interface Booking {
  id: string;
  bookingNo: string;
  flightId: string;
  accountId: string;
  seatId: string;
  bookingDate: string;
}

export interface BookingForm {
  flightId: string;
  accountId: string;
  seatId: string;
}
