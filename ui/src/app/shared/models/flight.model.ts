export interface Flight {
  id: string;
  flightNo: string;
  departureTime: string;
  arrivalTime: string;
  departureCity: string;
  arrivalCity: string;
  aircraftId: string;
  gateId: string | null;
}

export interface FlightForm {
  flightNo: string;
  departureTime: string;
  arrivalTime: string;
  departureCity: string;
  arrivalCity: string;
  aircraftId: string;
  gateId: string | null;
}
