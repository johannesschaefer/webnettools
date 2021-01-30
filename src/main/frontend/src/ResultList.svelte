<script lang="ts">
    import Result from "./Result.svelte";
    import type { ResultTask } from "./ResultTask";
    import { slide } from "svelte/transition";

    let resultList: ResultTask[] = [];

    export function addResult(result: ResultTask) {
        resultList = [result, ...resultList];
    }

    export function removeResult(result: ResultTask) {
        const index = resultList.indexOf(result, 0);
        if (index > -1) {
            resultList.splice(index, 1);
        }
        resultList = resultList;
    }

    export function clearResults() {
        resultList = [];
    }
</script>

{#if resultList.length != 0}
    <div class="card">
        <div class="">
            <div class="accordion" id="resultList">
                {#each resultList as r (r.date)}
                    <div in:slide={{}} out:slide={{}}>
                        <Result
                            bind:result={r}
                            on:remove={(ev) => removeResult(ev.detail)}
                            on:repeat={(ev) => addResult(ev.detail)}
                            on:edit
                        />
                    </div>
                {/each}
            </div>
            <style>
            </style>
        </div>
    </div>
{:else}
    <div class="card text-muted p-2" in:slide={{}} out:slide={{}}>
        No results available. Please start one of the tools.
    </div>
{/if}
