<script lang="ts">
    import Result from "./Result.svelte";
    import type { ResultTask } from "./ResultTask";

    export let resultList: ResultTask[];
</script>

{#if resultList.length != 0}
    <div class="card">
        <div class="">
            <div class="accordion" id="accordionExample">
                {#each resultList as r}
                    <Result bind:result={r} />
                {/each}
            </div>
            <style>
            </style>
        </div>
    </div>
{:else}
    <div class="card">Please add an new entry.</div>
{/if}
