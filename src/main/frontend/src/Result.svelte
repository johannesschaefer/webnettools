<script lang="ts">
    import { default as AnsiUp } from "ansi_up";
    import type { ResultTask } from "./ResultTask";
    import { v4 as uuidv4 } from "uuid";
    import { onMount } from "svelte";

    export let result: ResultTask;
    let id: string = uuidv4();
    let collapseId: string = uuidv4();

    let displayText = "";
    let displayTextFormated = "";
    const ansi_up = new AnsiUp();

    onMount(async () => {
        displayText = "";
        displayTextFormated = "";
        const response = await fetch("http://localhost:8080/" + result.url, {
            method: "POST",
            body: JSON.stringify(result.payload),
            headers: {
                "Content-Type": "application/json",
                Accept: "text/plain",
            },
        });

        if (response.ok && response.body !== null) {
            const reader = response.body
                .pipeThrough(new TextDecoderStream())
                .getReader();
            while (true) {
                const { value, done } = await reader.read();
                if (done) break;
                displayText += value;
                displayTextFormated = ansi_up.ansi_to_html(displayText);
            }
        }
    });
</script>

<div class="card">
    <div class="card-header" id="a{id}">
        <h2 class="mb-0">
            <button
                class="btn btn-link btn-block text-left"
                type="button"
                data-toggle="collapse"
                data-target="#a{collapseId}"
                aria-expanded={result.active}
                aria-controls="collapseOne">{result.displayText}</button
            >
        </h2>
    </div>

    <div
        id="a{collapseId}"
        class="collapse show"
        aria-labelledby="a{id}"
        data-parent="#accordionExample"
    >
        <div class="card-body">
            <pre
                style="overflow: scroll">
                {@html displayTextFormated}
            </pre>
        </div>
    </div>
</div>
