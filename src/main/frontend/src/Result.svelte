<script lang="ts">
    import { default as AnsiUp } from "ansi_up";
    import { v4 as uuidv4 } from "uuid";
    import { createEventDispatcher, onMount } from "svelte";
    import type { ResultTask } from "./ResultTask";
    import { TaskStatus } from "./TaskStatus";
    import moment from "moment";
    import { slide } from "svelte/transition";

    const dispatch = createEventDispatcher();

    moment.locale();

    export let result: ResultTask;
    //export let parentList: ResultList;
    let id: string = uuidv4();
    let collapseId: string = uuidv4();

    let displayText = "";
    let displayTextFormated = "";
    const ansi_up = new AnsiUp();

    function edit() {
        dispatch("edit", result);
    }

    function repeat() {
        let newResult: ResultTask = {
            active: true,
            date: new Date(),
            displayText: result.displayText,
            mode: result.mode,
            status: TaskStatus.PREPARED,
            url: result.url,
            payload: result.payload,
        };
        dispatch("repeat", newResult);
    }

    function remove() {
        dispatch("remove", result);
    }

    onMount(async () => {
        result.status = TaskStatus.PREPARED;
        displayText = "";
        displayTextFormated = "";
        try {
            const response = await fetch("/" + result.url, {
                method: "POST",
                body: JSON.stringify(result.payload),
                headers: {
                    "Content-Type": "application/json",
                    Accept: "text/plain",
                },
            });

            if (response.ok && response.body !== null) {
                result.status = TaskStatus.RUNNING;
                const reader = response.body
                    .pipeThrough(new TextDecoderStream())
                    .getReader();
                while (true) {
                    const { value, done } = await reader.read();
                    if (done) break;
                    displayText += value;
                    displayTextFormated = ansi_up.ansi_to_html(displayText);
                }
            }
            if (!response.ok) {
                result.status = TaskStatus.ERROR;
            } else {
                result.status = TaskStatus.SUCCESS;
            }
        } catch (e) {
            result.status = TaskStatus.ERROR;
            displayTextFormated = e;
        } finally {
        }
    });
</script>

<div class="card">
    <div
        on:click={() => (result.active = !result.active)}
        class="card-header d-flex flex-row"
        id="header-{id}"
        class:bg-success={result.status === TaskStatus.SUCCESS}
        class:bg-danger={result.status === TaskStatus.ERROR}
        class:bg-info={result.status === TaskStatus.RUNNING}
        class:bg-secondary={result.status === TaskStatus.PREPARED}
    >
        <div class="p-2 bd-highlight">
            <div class="row">
                <i
                    class="bi"
                    class:bi-caret-right-fill={!result.active}
                    class:bi-caret-down-fill={result.active}
                    style="padding-right: 0.3em"
                />
                {result.displayText}
            </div>
            <div class="row" style="font-size: 0.7em; padding-left: 2em">
                {moment(result.date).format("L")}
                {moment(result.date).format("LTS")}
            </div>
        </div>
        <div
            class="p-2 bd-highlight flex-grow-1 justify-content-end"
            style="margin-left: 1em; margin-top: 0.5em"
        >
            <div
                class:spinner-border={result.status === TaskStatus.RUNNING}
                role="status"
            >
                <span class="sr-only">Loading...</span>
            </div>
        </div>
        <div class="p-2 bd-highlight justify-content-end">
            <button class="btn" on:click|stopPropagation={edit}>
                <i class="bi bi-pencil" />
            </button>
            <button class="btn" on:click|stopPropagation={repeat}>
                <i class="bi bi-arrow-repeat" />
            </button>
            <button class="btn" on:click|stopPropagation={remove}>
                <i class="bi bi-x" />
            </button>
        </div>
    </div>

    {#if result.active}
        <div transition:slide>
            <div class="card-body">
                <pre
                    style="overflow: scroll; max-height: 20em">
                {@html displayTextFormated}
            </pre>
            </div>
        </div>
    {/if}
</div>
