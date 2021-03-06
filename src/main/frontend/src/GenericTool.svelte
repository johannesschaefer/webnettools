<script lang="ts">
    import { createEventDispatcher, onMount } from "svelte";
    import { ResultTask } from "./ResultTask";
    import { TaskStatus } from "./TaskStatus";
    import { slide } from "svelte/transition";
    import type { OptionMD, ToolMD } from "./Configuration";
    import NumberInput from "./inputs/NumberInput.svelte";
    import BooleanInput from "./inputs/BooleanInput.svelte";
    import StringInput from "./inputs/StringInput.svelte";
    import EnumInput from "./inputs/EnumInput.svelte";
    import { safariWorkaround } from "./SafariWorkaround";
    import FileInput from "./inputs/FileInput.svelte";

    export let tool: ToolMD;
    let payload = {};
    export let p = {};

    $: setPayload(p);

    let oldTool: ToolMD;
    let showOptions = {};
    let inputField;

    onMount(async () => {
        if (inputField) {
            inputField.focus();
        }
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
        if (newTool.main) {
            newPayload[newTool.main.name] = "";
        }
        newTool.options.forEach(
            (o: OptionMD) => (newPayload[o.name] = o.defaultValue)
        );
        payload = newPayload;
        oldTool = newTool;
    }
    $: calcPayload(tool);

    const dispatch = createEventDispatcher();

    function setUrlParams() {
        let params = JSON.parse(
            JSON.stringify({ tool: tool.name, payload: payload })
        );
        tool.options.forEach((option) => {
            if (option.type === "file") {
                delete params.payload[option.name];
            }
        });
        let dn = tool.displayName;
        if (tool.main) {
            dn += " " + payload[tool.main.name];
        }
        window.history.pushState(
            {},
            dn,
            "?config=" + encodeURIComponent(JSON.stringify(params))
        );
    }

    function clear() {
        let newPayload = { type: tool.name };
        if (tool.main) {
            newPayload[tool.main.name] = "";
        }
        tool.options.forEach(
            (o: OptionMD) => (newPayload[o.name] = o.defaultValue)
        );
        payload = newPayload;
        setUrlParams();
    }

    function runTask() {
        setUrlParams();
        let dn = tool.displayName;
        if (tool.main) {
            dn += " " + payload[tool.main.name];
        }
        dispatch(
            "createResult",
            new ResultTask(
                tool.name,
                dn,
                tool.name,
                payload,
                true,
                TaskStatus.PREPARED,
                new Date()
            )
        );
    }
</script>

<div class="tab-pane">
    <div class="card-body">
        <p class="text-secondary infotext">
            {@html tool.description}
        </p>
        <form
            on:submit|preventDefault={() =>
                tool.main === undefined || payload[tool.main.name] !== ""
                    ? runTask()
                    : null}
        >
            <div class="row">
                {#if tool.main}
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
                        <div class="input-group-append">
                            <button
                                on:click={clear}
                                class="btn btn-light bi bi-x"
                                style="border-color: #ced4da; font-size: 1rem"
                                type="button"
                                title="Clear settings"
                            />
                        </div>
                    </div>
                {:else}
                    <button
                        on:click={runTask}
                        on:submit={runTask}
                        class="btn btn-primary"
                        type="button"
                        id="submit-{tool.name}">Run {tool.displayName}</button
                    >
                {/if}
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
                            {:else if option.type === "file"}
                                <FileInput
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
