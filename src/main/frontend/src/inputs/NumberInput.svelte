<script lang="ts">
    import { tooltips } from "../tooltips";

    export let value: number;
    export let name: string;
    export let label: string;
    export let min: number;
    export let max: number;
    export let step: number;
    export let description: string = "";
</script>

<label for={name}> {label ? label : name} </label>
<div class="input-group mb-3">
    <div class="input-group-prepend">
        <i
            use:tooltips
            data-toggle="tooltip"
            data-placement="top"
            title={description}
            class="input-group-text bi bi-question-circle pl-2"
            style="color: #495057"
        />
    </div>
    <input
        type="number"
        {min}
        {max}
        {step}
        class="form-control"
        placeholder={label ? label : name}
        aria-label={label ? label : name}
        aria-describedby={name}
        bind:value
    />
</div>
