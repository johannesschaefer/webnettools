<script lang="ts">
    import type { FileOptionMD } from "../Configuration";

    export let value: string = null;
    export let md: FileOptionMD;
    let filename: string = "";

    function getFilename(path: string): string {
        return "test";
    }

    function processFile(e) {
        filename = "";
        value = "";
        if (e.target.files.length < 1) {
            return;
        }
        let file: File = e.target.files[0];
        if (file.size > md.maxSize) {
            alert("Maximum file size is " + md.maxSize + " byte.");
            return;
        }
        filename = file.name;
        let reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = (e) => {
            value = "filename:" + filename + ";" + e.target.result.toString();
        };
    }
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
        <div class="custom-file" style="width: 19.3em;">
            <input
                type="file"
                class="custom-file-input"
                id={md.name}
                accept={md.accept}
                on:change={(e) => processFile(e)}
            />
            <label
                class="custom-file-label"
                for={md.name}
                style="overflow: hidden">{filename}</label
            >
        </div>
    </div>
</div>
