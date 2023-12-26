/**
 * Type representing a queue entry in a game.
 * @property {number} id - The unique identifier of the queue entry.
 * @property {number} userId - The unique identifier of the user who created the queue entry.
 * @property {string} variant - The variant of the game.
 * @property {'MATCHED' | 'PENDING'} status - The status of the queue entry. It can be either 'MATCHED' or 'PENDING'.
 * @property {number | null} gameId - The unique identifier of the game associated with the queue entry. It can be null if no game is associated yet.
 * @property {string} createdAt - The timestamp when the queue entry was created.
 * @property {number} pollingTimeOut - The timeout for polling the queue entry.
 */
export type QueueEntry = {
  id: number;
  userId: number;
  variant: string;
  status: 'MATCHED' | 'PENDING';
  gameId: number | null;
  createdAt: string;
  pollingTimeOut: number;
};