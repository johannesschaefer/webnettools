<script lang="ts">
    import { createEventDispatcher } from "svelte";
    import type { ResultTask } from "../ResultTask";
    import { TaskStatus } from "../TaskStatus";

    let payload = { host: "" };

    const dispatch = createEventDispatcher();

    function runTraceroute() {
        dispatch("createResult", <ResultTask>{
            active: true,
            displayText: "Trace Route to " + payload.host,
            payload: payload,
            url: "traceroute",
            mode: "traceroute",
            date: new Date(),
            status: TaskStatus.PREPARED,
        });
    }
</script>

<div class="tab-pane">
    <div class="card-body">
        <form>
            <div class="row">
                <div class="input-group mb-3">
                    <div class="input-group-prepend">
                        <button
                            class="btn btn-outline-secondary"
                            type="button"
                            id="button-addon1">?</button
                        >
                    </div>
                    <input
                        bind:value={payload.host}
                        type="text"
                        class="form-control"
                        placeholder="IP / Hostname"
                    />
                    <div class="input-group-append">
                        <button
                            disabled={payload.host === ""}
                            class:disabled={payload.host === ""}
                            on:click={runTraceroute}
                            class="btn btn-primary"
                            type="button"
                            id="button-addon2">Run Trace Route</button
                        >
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col" />
                <div class="col" />
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
