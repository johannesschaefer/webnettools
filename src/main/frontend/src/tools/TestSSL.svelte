<script lang="ts">
    import { createEventDispatcher } from "svelte";
    import type { ResultTask } from "../ResultTask";

    let testSSLOption = { url: "", hints: true, fast: false };

    const dispatch = createEventDispatcher();

    function runTestSSL() {
        dispatch("createResult", <ResultTask>{
            active: true,
            displayText: "new tasks " + testSSLOption.url,
            payload: testSSLOption,
            url: "testssl",
            mode: "testssl",
        });
    }
</script>

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
                            id="flags-hints"
                        />
                        <label class="form-check-label" for="flags-hints">
                            Hints
                        </label>
                    </div>
                </div>
                <div class="col">
                    <div class="form-check">
                        <input
                            bind:checked={testSSLOption.fast}
                            class="form-check-input"
                            type="checkbox"
                            id="flags-fast"
                        />
                        <label class="form-check-label" for="flags-fast">
                            Fast
                        </label>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>

<style>
    .tab-pane {
        border-left: 1px solid #ddd;
        border-right: 1px solid #ddd;
        border-bottom: 1px solid #ddd;
        border-radius: 0px 0px 5px 5px;
        padding: 10px;
    }
</style>
