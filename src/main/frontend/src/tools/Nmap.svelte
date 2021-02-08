<script lang="ts">
    import { createEventDispatcher, onMount } from "svelte";
    import type { ResultTask } from "../ResultTask";
    import { TaskStatus } from "../TaskStatus";
    import { slide } from "svelte/transition";

    export let payload = { host: "", count: 5, wait: 1 };
    export const name = "nmap";

    let showOptions: boolean = false;
    let inputField;

    onMount(async () => {
        inputField.focus();
    });

    const dispatch = createEventDispatcher();

    function runTask() {
        dispatch("createResult", <ResultTask>{
            active: true,
            displayText: "Nmap " + payload.host,
            payload: payload,
            url: "nmap",
            mode: "nmap",
            date: new Date(),
            status: TaskStatus.PREPARED,
        });
    }
</script>

<div class="tab-pane">
    <div class="card-body">
        <p class="text-secondary infotext">
            <a href="https://nmap.org">Nmap</a> ("Network Mapper") is a free and
            open source utility for network discovery and security auditing.
        </p>
        <form
            on:submit|preventDefault={() =>
                payload.host !== "" ? runTask() : null}
        >
            <div class="row">
                <div class="input-group mb-3">
                    <div class="input-group-prepend">
                        <span
                            class="input-group-text bi bi-question-circle"
                            id="nmap-host"
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
                            id="submit-nmap">Run NMAP</button
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
                <div class="row" in:slide={{}} out:slide={{}} />
            {/if}
        </form>
    </div>
</div>
