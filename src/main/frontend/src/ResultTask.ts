import type { TaskStatus } from "./TaskStatus";

export interface ResultTask {
    mode: string;
    displayText: string;
    url: string;
    payload: any;
    active: boolean;
    status: TaskStatus;
    date: Date;
}
