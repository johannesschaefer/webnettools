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
    let statusVisible: boolean = true;

    let displayText = "";
    let displayTextFormated = "";
    let textarea;
    const ansi_up = new AnsiUp();
    const controller = new AbortController();
    const signal = controller.signal;

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
        cancelRequest();
        dispatch("remove", result);
    }

    function cancelRequest() {
        controller.abort();
        result.status = TaskStatus.CANCELED;
    }

    onMount(async () => {
        result.status = TaskStatus.PREPARED;
        displayText = "";
        displayTextFormated = "";
        try {
            const response = await fetch("__URL__" + "tools/" + result.url, {
                method: "POST",
                body: JSON.stringify(result.payload),
                headers: {
                    "Content-Type": "application/json",
                    Accept: "text/plain",
                },
                signal: signal,
            });

            if (response.ok && response.body !== null) {
                result.status = TaskStatus.RUNNING;
                const reader = response.body
                    .pipeThrough(new TextDecoderStream())
                    .getReader();
                while (true && result.status === TaskStatus.RUNNING) {
                    const { value, done } = await reader.read();
                    if (done) break;
                    displayText += value;
                    displayTextFormated = ansi_up.ansi_to_html(displayText);
                    scrollToEnd();
                }
            }
            if (!response.ok) {
                result.status = TaskStatus.ERROR;
            } else {
                result.status = TaskStatus.SUCCESS;
            }
        } catch (e) {
            result.status = TaskStatus.ERROR;
            displayTextFormated += "\n" + e;
        } finally {
        }
    });

    function toggleVisibility() {
        result.active = !result.active;
        scrollToEnd();
    }
    function scrollToEnd() {
        if (textarea !== null) {
            textarea.scrollTop = textarea.scrollHeight;
        }
    }
</script>

<div class="card">
    <div
        on:click={toggleVisibility}
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
            {#if statusVisible}
                <div
                    on:mouseenter={() => (statusVisible = false)}
                    on:mouseleave={() => (statusVisible = true)}
                    class:spinner-border={result.status ===
                        TaskStatus.RUNNING ||
                        result.status === TaskStatus.PREPARED}
                    role="status"
                >
                    <span class="sr-only">Loading...</span>
                </div>
            {:else}
                <i
                    on:mouseenter={() => (statusVisible = false)}
                    on:mouseleave={() => (statusVisible = true)}
                    on:click|stopPropagation={cancelRequest}
                    class="bi bi-x"
                    style="font-size: 1.3em"
                />
            {/if}
        </div>
        <div class="bd-highlight justify-content-end">
            <button class="btn" on:click|stopPropagation={edit}>
                <i class="bi bi-pencil" style="font-size: 1.3em" />
            </button>
            <button class="btn" on:click|stopPropagation={repeat}>
                <i class="bi bi-arrow-repeat" style="font-size: 1.3em" />
            </button>
            <button class="btn" on:click|stopPropagation={remove}>
                <i class="bi bi-x" style="font-size: 1.3em" />
            </button>
        </div>
    </div>

    {#if result.active}
        <div transition:slide={{}} on:introend={scrollToEnd}>
            <div
                class="card-body"
                style="padding-top 0px; padding-bottom: 0px;"
            >
                <pre
                    class="overflow-scroll-gradient"
                    bind:this={textarea}>
                {@html displayTextFormated}
                </pre>
            </div>
        </div>
    {/if}
</div>

<style>
    .overflow-scroll-gradient {
        overflow: scroll;
        max-height: 20em;
        margin-top: 0.5em;
        margin-bottom: 0.5em;
    }
    .overflow-scroll-gradient::before {
        content: "";
        position: absolute;
        width: 100%;
        height: 0.5em;
        background: linear-gradient(
            white,
            rgba(255, 255, 255, 0.001)
        ); /* transparent keyword is broken in Safari */
    }
    .overflow-scroll-gradient::after {
        content: "";
        position: absolute;
        bottom: 0;
        width: 100%;
        height: 0.5em;
        background: linear-gradient(
            rgba(255, 255, 255, 0.001),
            white
        ); /* transparent keyword is broken in Safari */
    }
</style>
