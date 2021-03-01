import type { TaskStatus } from "./TaskStatus";

export class ResultTask {
    constructor(
        public mode: string,
        public displayText: string,
        public url: string,
        public payload: any,
        public active: boolean,
        public status: TaskStatus,
        public date: Date
    ) { }

    clone(): ResultTask {
        return new ResultTask(this.mode, this.displayText, this.url, { ...this.payload }, this.active, this.status, this.date)
    }
}
