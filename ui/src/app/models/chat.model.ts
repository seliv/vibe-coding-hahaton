import { User } from './user.model';

export enum ChatType {
  DIRECT = 'DIRECT',
  GROUP = 'GROUP'
}

export interface Chat {
  id: number;
  name: string;
  type: ChatType;
  owner: User;
  participants: User[];
  createdAt?: Date;
  updatedAt?: Date;
}