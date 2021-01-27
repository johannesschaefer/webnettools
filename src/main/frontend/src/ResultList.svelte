<script lang="ts">
    import Result from "./Result.svelte";
    import type { ResultTask } from "./ResultTask";

    export let resultList: ResultTask[];
    let id: string = "resultList";
</script>

{#if resultList.length != 0}
    <div class="card">
        <div class="">
            <div class="accordion" {id}>
                {#each resultList as r (r.date)}
                    <Result bind:result={r} />
                {/each}
            </div>
            <style>
            </style>
        </div>
    </div>
{:else}
    <div class="card text-muted p-2">
        No results available. Please start one of the tools.
    </div>
{/if}
