
export type QueueEntry = {
  id: number;
  userId: number;
  variant: string;
  status: 'MATCHED' | 'PENDING';
  gameId: number | null;
  createdAt: string;
  pollingTimOut: number;
};