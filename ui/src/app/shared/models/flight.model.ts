import { Aircraft } from './aircraft.model';
import { Gate } from './gate.model';

export interface Flight {
  id: string;
  flightNo: string;
  departureTime: string;
  arrivalTime: string;
  departureCity: string;
  arrivalCity: string;
  baseFare: number;
  taxes: number;
  aircraftId: string;
  gateId: string | null;
  aircraft: Aircraft | null;
  gate: Gate | null;
  availableSeats: number | null;
}

export interface FlightForm {
  flightNo: string;
  departureTime: string;
  arrivalTime: string;
  departureCity: string;
  arrivalCity: string;
  baseFare: number | null;
  taxes: number | null;
  aircraftId: string;
  gateId: string | null;
}
