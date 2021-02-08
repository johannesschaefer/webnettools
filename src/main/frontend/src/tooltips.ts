export function tooltips(node) {
    window.$(function () {
        window.$('[data-toggle="tooltip"]').tooltip();
    });
    return {
        destroy() {
            console.log("destroy");
        },
    };
}