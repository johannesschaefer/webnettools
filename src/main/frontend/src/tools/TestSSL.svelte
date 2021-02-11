<script lang="ts">
    import { createEventDispatcher, onMount } from "svelte";
    import type { ResultTask } from "../ResultTask";
    import { TaskStatus } from "../TaskStatus";
    import { slide } from "svelte/transition";
    import { tooltips } from "../tooltips";

    export let payload = {
        type: "testssl",
        url: "",
        hints: true,
        quiet: false,
    };
    export const name = "testssl";

    let showOptions: boolean = false;
    let inputField;

    onMount(async () => {
        inputField.focus();
    });

    const dispatch = createEventDispatcher();

    function runTask() {
        dispatch("createResult", <ResultTask>{
            active: true,
            displayText: "Test SSL for " + payload.url,
            payload: payload,
            url: "testssl",
            mode: "testssl",
            date: new Date(),
            status: TaskStatus.PREPARED,
        });
    }
</script>

<div class="tab-pane">
    <div class="card-body">
        <p class="text-secondary infotext">
            Check the support of SSL/TLS of any server. Uses the
            <a href="https://testssl.sh">testssl.sh</a> tooling.
        </p>
        <form
            on:submit|preventDefault={() =>
                payload.url !== "" ? runTask() : null}
        >
            <div class="row">
                <div class="input-group mb-3">
                    <div class="input-group-prepend">
                        <span
                            use:tooltips
                            data-toggle="tooltip"
                            title="URI can be a hostname, an IPv4 or IPv6 address (restriction see below) or an URL. IPv6 addresses need to be in square brackets. For any given parameter port 443 is assumed unless specified by appending a colon and a port number. The only preceding protocol specifier allowed is https. You need to be aware that checks for an IP address might not hit the vhost you want. DNS resolution (A/AAAA record) is being performed unless you have an /etc/hosts entry for the hostname."
                            class="input-group-text bi bi-question-circle"
                            id="testssl-url"
                        />
                    </div>
                    <input
                        bind:value={payload.url}
                        bind:this={inputField}
                        type="text"
                        class="form-control"
                        placeholder="URL / Hostname"
                    />
                    <div class="input-group-append">
                        <button
                            disabled={payload.url === ""}
                            class:disabled={payload.url === ""}
                            on:click={runTask}
                            class="btn btn-primary"
                            type="button"
                            id="submit-test-ssl">Test SSL</button
                        >
                    </div>
                </div>
            </div>
            <div
                class="row text-secondary"
                on:click={() => {
                    showOptions = !showOptions;
                }}
                style="font-size: 0.9em;"
            >
                <i
                    class="bi"
                    class:bi-caret-right-fill={!showOptions}
                    class:bi-caret-down-fill={showOptions}
                    style="margin-right: 1em"
                />
                Options
            </div>

            {#if showOptions}
                <div class="row" in:slide={{}} out:slide={{}}>
                    <div class="col">
                        <div class="form-check">
                            <input
                                bind:checked={payload.hints}
                                class="form-check-input"
                                type="checkbox"
                                id="flags-hints"
                            />
                            <label class="form-check-label" for="flags-hints">
                                Hints
                            </label>
                            <i
                                use:tooltips
                                data-toggle="tooltip"
                                data-placement="top"
                                title="This option is not in use yet. This option is meant to give hints how to fix a finding or at least a help to improve something. GIVE_HINTS is the environment variable for this."
                                class="bi bi-question-circle pl-2"
                                style="color: #495057"
                            />
                        </div>
                    </div>
                    <div class="col">
                        <div class="form-check">
                            <input
                                bind:checked={payload.quiet}
                                class="form-check-input"
                                type="checkbox"
                                id="flags-quiet"
                            />
                            <label class="form-check-label" for="flags-quiet">
                                Quiet
                            </label>
                            <i
                                use:tooltips
                                data-toggle="tooltip"
                                data-placement="top"
                                title="Normally testssl.sh displays a banner on stdout with several version information, usage rights and a warning. This option suppresses it. Please note that by choosing this option you acknowledge usage terms and the warning normally appearing in the banner."
                                class="bi bi-question-circle pl-2"
                                style="color: #495057"
                            />
                        </div>
                    </div>
                </div>
            {/if}
        </form>
    </div>
</div>
