<script lang="ts">
    import { createEventDispatcher, onMount } from "svelte";
    import type { ResultTask } from "./ResultTask";
    import { TaskStatus } from "./TaskStatus";
    import { slide } from "svelte/transition";
    import type { GroupMD, OptionMD, ToolMD } from "./Configuration";
    import NumberInput from "./inputs/NumberInput.svelte";
    import BooleanInput from "./inputs/BooleanInput.svelte";
    import StringInput from "./inputs/StringInput.svelte";
    import EnumInput from "./inputs/EnumInput.svelte";
    import { safariWorkaround } from "./SafariWorkaround";

    export let tool: ToolMD;
    let payload = {};
    export let p = {};

    $: setPayload(p);

    let oldTool: ToolMD;
    let showOptions = {};
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
        showOptions = {};
        if (newTool.presetPayload !== null) {
            payload = newTool.presetPayload;
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
        let data = { tool: tool.name, payload: payload };
        window.history.pushState(
            {},
            tool.displayName + " " + payload[tool.main.name],
            "?config=" + encodeURIComponent(JSON.stringify(data))
        );
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
        {#each tool.groups as group}
            <div
                class="row text-secondary"
                on:click={() => {
                    showOptions[group.name] = !showOptions[group.name];
                }}
                style="font-size: 0.9em;"
            >
                <i
                    class="bi"
                    class:bi-caret-right-fill={!showOptions[group.name]}
                    class:bi-caret-down-fill={showOptions[group.name]}
                    style="margin-right: 1em"
                />
                {group.name}
                {#if group.description !== ""}
                    <i
                        title={group.description}
                        class="bi-question-circle pl-2"
                        style="color: #495057"
                    />
                {/if}
            </div>

            {#if showOptions[group.name]}
                <div
                    class="row"
                    in:slide={{}}
                    out:slide={{}}
                    use:safariWorkaround
                >
                    {#each tool.options.filter((t) => t.group === group.name) as option}
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
                            {:else if option.type === "enum"}
                                <EnumInput
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
        {/each}
    </div>
</div>
