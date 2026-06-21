export interface Account {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  passportNo: string;
  roles: string[];
}

export interface AccountForm {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  passportNo: string;
}
