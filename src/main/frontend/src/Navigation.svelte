<script lang="ts">
    import { createEventDispatcher } from "svelte";
    import type { ToolMD } from "./Configuration";

    export let mode: string;
    export let tools: ToolMD[] = [];

    const dispatch = createEventDispatcher();
    function changeMode(newMode: string) {
        mode = newMode;
        dispatch("modeChanged", mode);
    }
</script>

<ul class="nav nav-tabs" id="myTab" role="tablist">
    {#each tools as tool}
        <li class="nav-item" role="presentation">
            <!-- svelte-ignore a11y-missing-attribute -->
            <a
                class="nav-link"
                class:active={mode === tool.name}
                on:click={() => changeMode(tool.name)}>{tool.displayName}</a
            >
        </li>
    {/each}
</ul>

<style>
    .nav-tabs {
        margin-bottom: 0;
    }
</style>
