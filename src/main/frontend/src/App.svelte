<script lang="ts">
    import { default as AnsiUp } from "ansi_up";
    import Result from "./Result.svelte";

    /*
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
*/
    let mode = "testssl";

    let testSSLOption = { url: "", hints: true };

    async function runTestSSL() {
        console.log("runTestSSL", testSSLOption);
        const response = await fetch("/testssl", {
            method: "POST",
            body: JSON.stringify({ url: url }),
            headers: {
                "Content-Type": "application/json",
                Accept: "text/plain",
            },
        });
    }
</script>

<h1>Web Net Tools</h1>
<div class="container-fluid">
    <ul class="nav nav-tabs" id="myTab" role="tablist">
        <li class="nav-item" role="presentation">
            <a
                class="nav-link {mode === 'testssl' ? 'active' : ''}"
                on:click={() => (mode = "testssl")}
                href="#">Test SSL</a
            >
        </li>
        <li class="nav-item" role="presentation">
            <a
                class="nav-link {mode === 'ping' ? 'active' : ''}"
                on:click={() => (mode = "ping")}
                href="#">Ping</a
            >
        </li>
        <li class="nav-item" role="presentation">
            <a
                class="nav-link {mode === 'traceroute' ? 'active' : ''}"
                on:click={() => (mode = "traceroute")}
                href="#">Trace Route</a
            >
        </li>
    </ul>
    {#if mode === "testssl"}
        <div class="tab-pane">
            <div class="card-body">
                <form>
                    <div class="row">
                        <div class="input-group mb-3">
                            <div class="input-group-prepend">
                                <button
                                    class="btn btn-outline-secondary"
                                    type="button"
                                    id="button-addon1">?</button
                                >
                            </div>
                            <input
                                bind:value={testSSLOption.url}
                                type="text"
                                class="form-control"
                                placeholder="URL / Hostname"
                            />
                            <div class="input-group-append">
                                <button
                                    on:click={runTestSSL}
                                    class="btn btn-outline-primary"
                                    type="button"
                                    id="button-addon2">Test SSL</button
                                >
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col">
                            <div class="form-check">
                                <input
                                    bind:checked={testSSLOption.hints}
                                    class="form-check-input"
                                    type="checkbox"
                                    value=""
                                    id="defaultCheck1"
                                />
                                <label
                                    class="form-check-label"
                                    for="defaultCheck1"> Hints </label>
                            </div>
                        </div>
                        <div class="col">
                            <div class="form-check">
                                <input
                                    class="form-check-input"
                                    type="checkbox"
                                    value=""
                                    id="defaultCheck1"
                                />
                                <label
                                    class="form-check-label"
                                    for="defaultCheck1">
                                    Default checkbox
                                </label>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    {/if}
    {#if mode === "ping"}
        <div class="tab-pane">
            <div class="card-body">Ping</div>
        </div>
    {/if}
    {#if mode === "traceroute"}
        <div class="tab-pane">
            <div class="card-body">Trace Route</div>
        </div>
    {/if}
</div>
<Result />

<style>
    .tab-pane {
        border-left: 1px solid #ddd;
        border-right: 1px solid #ddd;
        border-bottom: 1px solid #ddd;
        border-radius: 0px 0px 5px 5px;
        padding: 10px;
    }

    .nav-tabs {
        margin-bottom: 0;
    }
</style>
