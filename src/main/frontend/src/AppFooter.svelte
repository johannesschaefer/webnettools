<script lang="ts">
    import { version } from "../package.json";
    import * as git from "../git.json";
</script>

<footer class="footer">
    <div class="container d-flex justify-content-center align-items-center">
        <small class="px-2">Version: {version}</small>
        <small
            class="px-2 d-none d-sm-inline d-md-inline d-lg-inline d-xl-inline">
            Commit: {git["default"]["git.commit.id.abbrev"]}
        </small>
        <div class="px-2">
            <a
                href="https://github.com/johannesschaefer/webnettools"
                target="_blank">
                <i class="bi bi-github" />
            </a>
        </div>
    </div>
</footer>
