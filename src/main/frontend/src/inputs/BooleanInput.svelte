<script lang="ts">
    import type { BooleanOptionMD } from "../Configuration";

    export let value: boolean = null;
    export let md: BooleanOptionMD;
    let values = [
        { value: true, label: md.labelTrue },
        { value: false, label: md.labelFalse },
    ];
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
            {#each values as v}
                <option value={v.value}>{v.label}</option>
            {/each}
        </select>
    </div>
</div>
