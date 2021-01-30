<script lang="ts">
    import { createEventDispatcher } from "svelte";
    import type { ResultTask } from "../ResultTask";
    import { TaskStatus } from "../TaskStatus";

    export let payload = { host: "", count: 5, wait: 1 };

    const dispatch = createEventDispatcher();

    function runTask() {
        dispatch("createResult", <ResultTask>{
            active: true,
            displayText: "Ping to " + payload.host,
            payload: payload,
            url: "ping",
            mode: "ping",
            date: new Date(),
            status: TaskStatus.PREPARED,
        });
    }
</script>

<div class="tab-pane">
    <div class="card-body">
        <form
            on:submit|preventDefault={() =>
                payload.host !== "" ? runTask() : null}
        >
            <div class="row">
                <div class="input-group mb-3">
                    <div class="input-group-prepend">
                        <span class="input-group-text" id="ping-host"> ? </span>
                    </div>
                    <input
                        bind:value={payload.host}
                        autofocus
                        type="text"
                        class="form-control"
                        placeholder="IP / Hostname"
                    />
                    <div class="input-group-append">
                        <button
                            disabled={payload.host === ""}
                            class:disabled={payload.host === ""}
                            on:click={runTask}
                            on:submit={runTask}
                            class="btn btn-primary"
                            type="button"
                            id="submit-ping">Run Ping</button
                        >
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col">
                    <label for="ping-count">Count</label>
                    <div class="input-group mb-3">
                        <div class="input-group-prepend">
                            <span class="input-group-text" id="ping-count">
                                ?
                            </span>
                        </div>
                        <input
                            type="number"
                            min="1"
                            max="100"
                            step="1"
                            class="form-control"
                            placeholder="Count"
                            aria-label="Count"
                            aria-describedby="ping-count"
                            bind:value={payload.count}
                        />
                    </div>
                </div>
                <div class="col">
                    <label for="ping-wait">Wait</label>
                    <div class="input-group mb-3">
                        <div class="input-group-prepend">
                            <span class="input-group-text" id="ping-wait">
                                ?
                            </span>
                        </div>
                        <input
                            type="number"
                            min="1"
                            max="100"
                            step="1"
                            class="form-control"
                            placeholder="Wait"
                            aria-label="Wait"
                            aria-describedby="ping-wait"
                            bind:value={payload.wait}
                        />
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>

<style>
    .tab-pane {
        border-left: 1px solid #ddd;
        border-right: 1px solid #ddd;
        border-bottom: 1px solid #ddd;
        border-radius: 0px 0px 5px 5px;
        padding: 10px;
    }
</style>
