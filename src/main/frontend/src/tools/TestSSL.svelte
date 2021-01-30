<script lang="ts">
    import { createEventDispatcher } from "svelte";
    import type { ResultTask } from "../ResultTask";
    import { TaskStatus } from "../TaskStatus";
    import { slide } from "svelte/transition";

    export let payload = { url: "", hints: true, fast: false };
    let showOptions = false;

    const dispatch = createEventDispatcher();

    function runTask() {
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
        <form
            on:submit|preventDefault={() =>
                payload.url !== "" ? runTask() : null}
        >
            <div class="row">
                <div class="input-group mb-3">
                    <div class="input-group-prepend">
                        <span class="input-group-text" id="testssl-url">
                            ?
                        </span>
                    </div>
                    <!-- svelte-ignore a11y-autofocus -->
                    <input
                        bind:value={payload.url}
                        autofocus
                        type="text"
                        class="form-control"
                        placeholder="URL / Hostname"
                    />
                    <div class="input-group-append">
                        <button
                            disabled={payload.url === ""}
                            class:disabled={payload.url === ""}
                            on:click={runTask}
                            class="btn btn-primary"
                            type="button"
                            id="submit-test-ssl">Test SSL</button
                        >
                    </div>
                </div>
            </div>
            <div
                class="row text-secondary"
                on:click={() => {
                    showOptions = !showOptions;
                }}
                style="font-size: 0.9em;"
            >
                <i
                    class="bi"
                    class:bi-caret-right-fill={!showOptions}
                    class:bi-caret-down-fill={showOptions}
                    style="margin-right: 1em"
                />
                Options
            </div>

            {#if showOptions}
                <div class="row" in:slide={{}} out:slide={{}}>
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
            {/if}
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
