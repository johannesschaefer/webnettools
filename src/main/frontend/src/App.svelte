<script lang="ts">
    import { onMount } from "svelte";
    import { fade } from "svelte/transition";
    import AppFooter from "./AppFooter.svelte";
    import type { Configuration } from "./Configuration";
    import Navigation from "./Navigation.svelte";
    import ResultList from "./ResultList.svelte";
    import type { ResultTask } from "./ResultTask";
    import Ping from "./tools/Ping.svelte";
    import TestSsl from "./tools/TestSSL.svelte";
    import Traceroute from "./tools/Traceroute.svelte";

    enum Status {
        LOADING,
        READY,
        ERROR,
    }

    let currentComponent;
    let currentComponentProps;
    let currentComponentInstance;
    let resultListComponent: ResultList;
    let config: Configuration = { availableTools: [] };
    let status: Status = Status.LOADING;
    let errorMsg: string = "";

    onMount(async () => {
        try {
            const response = await fetch("__URL__" + "config");
            if (response.ok && response.body !== null) {
                config = ((await response.json()) as unknown) as Configuration;
                if (config.availableTools.length > 0) {
                    modeChanged(config.availableTools[0]);
                }

                status = Status.READY;
            }
            if (!response.ok) {
                status = Status.ERROR;
                errorMsg = await response.text();
            }
        } catch (e) {
            status = Status.ERROR;
            errorMsg = e;
        }
    });

    function modeChanged(mode: string) {
        if (mode === "testssl") {
            currentComponent = TestSsl;
        } else if (mode === "ping") {
            currentComponent = Ping;
        } else if (mode === "traceroute") {
            currentComponent = Traceroute;
        }
        currentComponentProps = {};
    }

    function addResult(event: CustomEvent<ResultTask>) {
        resultListComponent.addResult(event.detail);
    }

    function edit(result: ResultTask) {
        modeChanged(result.mode);
        currentComponentProps = { payload: result.payload };
    }
</script>

<main role="main" class="container-fluid">
    <h1>
        <i class="bi bi-hdd-network" />
        Web Net Tools
    </h1>

    {#if status === Status.READY}
        <div transition:fade={{ delay: 300, duration: 300 }}>
            <div class="container-fluid">
                <Navigation
                    availableTools={config.availableTools}
                    mode={currentComponentInstance !== undefined
                        ? currentComponentInstance.name
                        : ""}
                    on:modeChanged={(ev) => modeChanged(ev.detail)}
                />
                <svelte:component
                    this={currentComponent}
                    bind:this={currentComponentInstance}
                    {...currentComponentProps}
                    on:createResult={addResult}
                />
            </div>
            <div class="container-fluid" style="padding-top: 0.5em">
                <ResultList
                    bind:this={resultListComponent}
                    on:edit={(ev) => edit(ev.detail)}
                />
            </div>
        </div>
    {:else if status === Status.LOADING}
        <div transition:fade={{ delay: 0, duration: 300 }}>
            <div class="d-flex justify-content-center">
                <div
                    class="spinner-border"
                    style="width: 3rem; height: 3rem; margin: 3rem;"
                    role="status"
                >
                    <span class="sr-only">Loading...</span>
                </div>
            </div>
        </div>
    {:else}
        <div transition:fade={{ delay: 300, duration: 300 }}>
            <div
                class="alert alert-danger"
                style="margin-top: 2rem;"
                role="alert"
            >
                {errorMsg}
            </div>
        </div>
    {/if}
</main>

<AppFooter />

<style>
    h1 {
        margin-left: 0.5em;
        margin-top: 0.3em;
    }
</style>
