<script lang="ts">
    import { createEventDispatcher } from "svelte";

    export let mode: string;
    export let availableTools: string[];

    const dispatch = createEventDispatcher();
    function changeMode(newMode: string) {
        mode = newMode;
        dispatch("modeChanged", mode);
    }
</script>

<!-- svelte-ignore a11y-missing-attribute -->
<ul class="nav nav-tabs" id="myTab" role="tablist">
    {#if availableTools.indexOf("testssl") >= 0}
        <li class="nav-item" role="presentation">
            <a
                class="nav-link"
                class:active={mode === "testssl"}
                on:click={() => changeMode("testssl")}>Test SSL</a
            >
        </li>
    {/if}

    {#if availableTools.indexOf("ping") >= 0}
        <li class="nav-item" role="presentation">
            <a
                class="nav-link"
                class:active={mode === "ping"}
                on:click={() => changeMode("ping")}>Ping</a
            >
        </li>
    {/if}

    {#if availableTools.indexOf("nmap") >= 0}
        <li class="nav-item" role="presentation">
            <a
                class="nav-link"
                class:active={mode === "namp"}
                on:click={() => changeMode("nmap")}>Nmap</a
            >
        </li>
    {/if}

    {#if availableTools.indexOf("traceroute") >= 0}
        <li class="nav-item" role="presentation">
            <a
                class="nav-link"
                class:active={mode === "traceroute"}
                on:click={() => changeMode("traceroute")}>Trace Route</a
            >
        </li>
    {/if}
</ul>

<style>
    .nav-tabs {
        margin-bottom: 0;
    }
</style>
