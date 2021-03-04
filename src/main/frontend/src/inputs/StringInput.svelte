<script lang="ts">
    import { afterUpdate, beforeUpdate } from "svelte";
    import type { StringOptionMD } from "../Configuration";

    export let value: string = null;
    export let md: StringOptionMD;
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
        <input
            type="text"
            minlength={md.minlength}
            maxlength={md.maxlength}
            class="form-control"
            placeholder={md.displayName ? md.displayName : md.name}
            aria-label={md.displayName ? md.displayName : md.name}
            aria-describedby={md.name}
            bind:value
            style="width: 8em;"
            id={md.name}
        />
    </div>
</div>
