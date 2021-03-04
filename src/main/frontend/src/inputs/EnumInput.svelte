<script lang="ts">
    import type { EnumOptionMD } from "../Configuration";

    export let value: string = null;
    export let md: EnumOptionMD;
</script>

<div>
    <label for={md.name}> {md.displayName ? md.displayName : md.name} </label>
    <div class="input-group mb-3">
        <div class="input-group-prepend">
            <i
                title={md.description}
                class="input-group-text bi bi-question-circle pl-2"
                style="color: #495057"
            />
        </div>
        <select
            class="form-control"
            style="width: 8em;"
            bind:value
            id={md.name}
        >
            <option value={null} selected disabled hidden />
            {#each Object.entries(md.values) as [value, label]}
                <option {value}>{label}</option>
            {/each}
        </select>
    </div>
</div>
