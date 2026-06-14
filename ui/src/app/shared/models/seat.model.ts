export interface Seat {
  id: string;
  seatNo: string;
  type: string;
  available: boolean;
  flightId: string;
}

export interface SeatForm {
  seatNo: string;
  type: string;
  available: boolean;
  flightId: string;
}
