export class Problem {
  type: string;
  title: string;
  status: number;
  detail?: string;
  instance?: string;

  constructor(type: string, title: string, status: number, detail?: string | null, instance?: string | null) {
    this.type = type;
    this.title = title;
    this.status = status;
    this.detail = detail;
    this.instance = instance;
  }
}

export const problemMediaType = 'application/problem+json';

export function isProblem(obj: unknown): obj is Problem {
  return obj instanceof Problem && obj.type !== undefined && obj.title !== undefined && obj.status !== undefined;
}
