import { User } from './user.model';
import { Chat } from './chat.model';

export enum MessageType {
  TEXT = 'TEXT',
  IMAGE = 'IMAGE'
}

export interface Message {
  id: number;
  chat: Chat;
  sender: User;
  content: string;
  type: MessageType;
  attachmentUrl?: string;
  sentAt: Date;
  delivered: boolean;
  read: boolean;
}