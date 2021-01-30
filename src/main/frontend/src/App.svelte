<script lang="ts">
    import AppFooter from "./AppFooter.svelte";
    import Navigation from "./Navigation.svelte";
    import ResultList from "./ResultList.svelte";
    import type { ResultTask } from "./ResultTask";
    import Ping from "./tools/Ping.svelte";
    import TestSsl from "./tools/TestSSL.svelte";
    import Traceroute from "./tools/Traceroute.svelte";

    let currentComponent;
    let mode = "testssl";
    let props;
    let resultListComponent: ResultList;

    function modeChanged(evMode: string) {
        mode = evMode;
        if (mode === "testssl") {
            currentComponent = TestSsl;
        } else if (mode === "ping") {
            currentComponent = Ping;
        } else if (mode === "traceroute") {
            currentComponent = Traceroute;
        }
        props = {};
    }
    modeChanged(mode);

    function addResult(event: CustomEvent<ResultTask>) {
        resultListComponent.addResult(event.detail);
    }

    function edit(result: ResultTask) {
        modeChanged(result.mode);
        props = { payload: result.payload };
    }
</script>

<main role="main" class="container-fluid">
    <h1>
        <i class="bi bi-hdd-network" />
        Web Net Tools
    </h1>
    <div class="container-fluid">
        <Navigation {mode} on:modeChanged={(ev) => modeChanged(ev.detail)} />
        <svelte:component
            this={currentComponent}
            {...props}
            on:createResult={addResult}
        />
    </div>
    <div class="container-fluid" style="padding-top: 0.5em">
        <ResultList
            bind:this={resultListComponent}
            on:edit={(ev) => edit(ev.detail)}
        />
    </div>
</main>

<AppFooter />

<style>
    h1 {
        margin-left: 0.5em;
        margin-top: 0.3em;
    }
</style>
