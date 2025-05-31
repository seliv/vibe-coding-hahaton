export interface User {
  id: number;
  username: string;
  contacts?: User[];
  createdAt?: Date;
  lastLoginAt?: Date;
}