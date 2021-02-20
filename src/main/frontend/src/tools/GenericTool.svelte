<script lang="ts">
    import { createEventDispatcher, onMount } from "svelte";
    import type { ResultTask } from "../ResultTask";
    import { TaskStatus } from "../TaskStatus";
    import { slide } from "svelte/transition";
    import type { OptionMD, ToolMD } from "../Configuration";
    import NumberInput from "../inputs/NumberInput.svelte";
    import BooleanInput from "../inputs/BooleanInput.svelte";
    import StringInput from "../inputs/StringInput.svelte";

    export let tool: ToolMD;
    let payload = {};
    export let p = {};

    $: setPayload(p);

    let oldTool: ToolMD;
    let showOptions: boolean = false;
    let inputField;

    onMount(async () => {
        inputField.focus();
    });

    function setPayload(pp: any) {
        payload = pp;
    }

    function calcPayload(newTool: ToolMD) {
        if (newTool == oldTool) {
            return;
        }

        let newPayload = { type: newTool.name };
        newPayload[newTool.main.name] = "";
        newTool.options.forEach(
            (o: OptionMD) => (newPayload[o.name] = o.defaultValue)
        );
        payload = newPayload;
        oldTool = newTool;
    }
    $: calcPayload(tool);

    const dispatch = createEventDispatcher();

    function runTask() {
        dispatch("createResult", <ResultTask>{
            active: true,
            displayText: tool.displayName + " " + payload[tool.main.name],
            payload: payload,
            url: tool.name,
            mode: tool.name,
            date: new Date(),
            status: TaskStatus.PREPARED,
        });
    }
</script>

<div class="tab-pane">
    <div class="card-body">
        <p class="text-secondary infotext">
            {@html tool.description}
        </p>
        <form
            on:submit|preventDefault={() =>
                payload[tool.main.name] !== "" ? runTask() : null}
        >
            <div class="row">
                <div class="input-group mb-3">
                    <div class="input-group-prepend">
                        <span
                            title={tool.main.description}
                            class="input-group-text bi bi-question-circle"
                            id="{tool.name}-main"
                        />
                    </div>
                    <input
                        bind:value={payload[tool.main.name]}
                        bind:this={inputField}
                        minlength={tool.main.minlength}
                        maxlength={tool.main.maxlength}
                        type="text"
                        class="form-control"
                        placeholder={tool.main.displayName}
                    />
                    <div class="input-group-append">
                        <button
                            disabled={payload[tool.main.name] === ""}
                            class:disabled={payload[tool.main.name] === ""}
                            on:click={runTask}
                            on:submit={runTask}
                            class="btn btn-primary"
                            type="button"
                            id="submit-{tool.name}"
                            >Run {tool.displayName}</button
                        >
                    </div>
                </div>
            </div>
        </form>
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
                {#each tool.options as option}
                    <div class="d-flex flex-wrap p-2">
                        {#if option.type === "number"}
                            <NumberInput
                                md={option}
                                bind:value={payload[option.name]}
                            />
                        {:else if option.type === "boolean"}
                            <BooleanInput
                                md={option}
                                bind:value={payload[option.name]}
                            />
                        {:else if option.type === "string"}
                            <StringInput
                                md={option}
                                bind:value={payload[option.name]}
                            />
                        {:else}
                            No supported type for
                            {option.displayName}
                        {/if}
                    </div>
                {/each}
            </div>
        {/if}
    </div>
</div>
