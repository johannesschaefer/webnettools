<script lang="ts">
    import { createEventDispatcher } from "svelte";

    export let mode;

    const dispatch = createEventDispatcher();
    function changeMode(newMode: string) {
        mode = newMode;
        dispatch("modeChanged", mode);
    }
</script>

<!-- svelte-ignore a11y-missing-attribute -->
<ul class="nav nav-tabs" id="myTab" role="tablist">
    <li class="nav-item" role="presentation">
        <a
            class="nav-link"
            class:active={mode === "testssl"}
            on:click={() => changeMode("testssl")}>Test SSL</a
        >
    </li>
    <li class="nav-item" role="presentation">
        <a
            class="nav-link"
            class:active={mode === "ping"}
            on:click={() => changeMode("ping")}>Ping</a
        >
    </li>
    <li class="nav-item" role="presentation">
        <a
            class="nav-link"
            class:active={mode === "traceroute"}
            on:click={() => changeMode("traceroute")}>Trace Route</a
        >
    </li>
</ul>

<style>
    .nav-tabs {
        margin-bottom: 0;
    }
</style>
