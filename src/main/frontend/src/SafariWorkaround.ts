export function safariWorkaround(node) {
    if (navigator.appVersion.includes("Safari")) {
        node.style.overflow = "hidden";
    }
}