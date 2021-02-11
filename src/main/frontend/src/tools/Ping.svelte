<script lang="ts">
    import { createEventDispatcher, onMount } from "svelte";
    import type { ResultTask } from "../ResultTask";
    import { TaskStatus } from "../TaskStatus";
    import { slide } from "svelte/transition";
    import NumberInput from "../inputs/NumberInput.svelte";

    export let payload = { type: "ping", host: "", count: 5, wait: 1 };
    export const name = "ping";

    let showOptions: boolean = false;
    let inputField;

    onMount(async () => {
        inputField.focus();
    });

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
        <p class="text-secondary infotext">Standard Linux Ping tooling.</p>
        <form
            on:submit|preventDefault={() =>
                payload.host !== "" ? runTask() : null}
        >
            <div class="row">
                <div class="input-group mb-3">
                    <div class="input-group-prepend">
                        <span
                            class="input-group-text bi bi-question-circle"
                            id="ping-host"
                        />
                    </div>
                    <input
                        bind:value={payload.host}
                        bind:this={inputField}
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
                        <NumberInput
                            name="count"
                            bind:value={payload.count}
                            label="Count"
                            min={1}
                            max={100}
                            step={1}
                        />
                    </div>
                    <div class="col">
                        <NumberInput
                            name="wait"
                            bind:value={payload.wait}
                            label="Wait"
                            min={1}
                            max={100}
                            step={1}
                        />
                    </div>
                </div>
            {/if}
        </form>
    </div>
</div>
