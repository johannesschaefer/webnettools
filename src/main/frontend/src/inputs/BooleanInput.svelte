<script lang="ts">
    import { tooltips } from "../tooltips";

    export let value: boolean;
    export let name: string;
    export let label: string;
    export let description: string = "";
</script>

<div class="form-check">
    <input
        bind:checked={value}
        class="form-check-input"
        type="checkbox"
        id={name}
    />
    <label class="form-check-label" for={name}> {label ? label : name} </label>
    <i
        use:tooltips
        data-toggle="tooltip"
        data-placement="top"
        title={description}
        class="bi bi-question-circle pl-2"
        style="color: #495057"
    />
</div>
