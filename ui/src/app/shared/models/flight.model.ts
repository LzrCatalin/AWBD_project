import { Aircraft } from './aircraft.model';
import { Gate } from './gate.model';

export interface Flight {
  id: string;
  flightNo: string;
  departureTime: string;
  arrivalTime: string;
  departureCity: string;
  arrivalCity: string;
  aircraftId: string;
  gateId: string | null;
  aircraft: Aircraft | null;
  gate: Gate | null;
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
