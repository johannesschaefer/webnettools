<script lang="ts">
    import { createEventDispatcher } from "svelte";
    import type { ResultTask } from "../ResultTask";
    import { TaskStatus } from "../TaskStatus";

    let payload = { url: "", hints: true, fast: false };

    const dispatch = createEventDispatcher();

    function runTestSSL() {
        dispatch("createResult", <ResultTask>{
            active: true,
            displayText: "Test SSL for " + payload.url,
            payload: payload,
            url: "testssl",
            mode: "testssl",
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
                        bind:value={payload.url}
                        type="text"
                        class="form-control"
                        placeholder="URL / Hostname"
                    />
                    <div class="input-group-append">
                        <button
                            disabled={payload.url === ""}
                            class:disabled={payload.url === ""}
                            on:click={runTestSSL}
                            class="btn btn-primary"
                            type="button"
                            id="button-addon2">Test SSL</button
                        >
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col">
                    <div class="form-check">
                        <input
                            bind:checked={payload.hints}
                            class="form-check-input"
                            type="checkbox"
                            id="flags-hints"
                        />
                        <label class="form-check-label" for="flags-hints">
                            Hints
                        </label>
                    </div>
                </div>
                <div class="col">
                    <div class="form-check">
                        <input
                            bind:checked={payload.fast}
                            class="form-check-input"
                            type="checkbox"
                            id="flags-fast"
                        />
                        <label class="form-check-label" for="flags-fast">
                            Fast
                        </label>
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
