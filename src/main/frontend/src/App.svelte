<script>
    import Tailwindcss from "./Tailwindcss.svelte";
    import { default as AnsiUp } from "ansi_up";

    let url = "mobile.psi-mt.de";
    let result = "";
    let result2 = "";
    const ansi_up = new AnsiUp();

    async function checkURL() {
        result = "";
        result2 = "";
        const response = await fetch("/testssl", {
            method: "POST",
            body: JSON.stringify({ url: url }),
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
                result += value;
                result2 = ansi_up.ansi_to_html(result);
            }
        }
    }
</script>

<Tailwindcss />
<div class="box-content h-10 p-4 border-4 shadow-xl">
    <form on:submit|preventDefault={checkURL}>
        <input type="text" bind:value={url} placeholder="Enter url to check" />
        <button>Check SSL</button>
    </form>
</div>
<div class="box-content p-4 border-4 shadow-xl">
    <pre>{@html result2}</pre>
</div>
