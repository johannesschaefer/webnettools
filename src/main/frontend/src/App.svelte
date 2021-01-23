<script lang="ts">
    import Navigation from "./Navigation.svelte";
    import ResultList from "./ResultList.svelte";
    import type { ResultTask } from "./ResultTask";
    import Ping from "./tools/Ping.svelte";
    import TestSsl from "./tools/TestSSL.svelte";
    import Traceroute from "./tools/Traceroute.svelte";

    let mode = "testssl";

    let resultList2: ResultTask[] = [];

    function modeChanged(event: CustomEvent<string>) {
        mode = event.detail;
    }

    function addResult(event: CustomEvent<ResultTask>) {
        resultList2 = [event.detail, ...resultList2];
        resultList2 = resultList2;
    }
</script>

<h1>Web Net Tools</h1>
<div class="container-fluid">
    <Navigation {mode} on:modeChanged={modeChanged} />
    {#if mode === "testssl"}
        <TestSsl on:createResult={addResult} />
    {:else if mode === "ping"}
        <Ping on:createResult={addResult} />
    {:else if mode === "traceroute"}
        <Traceroute on:createResult={addResult} />
    {/if}
</div>
<div class="container-fluid" style="padding-top: 0.5em">
    <ResultList bind:resultList={resultList2} />
</div>
