package io.github.johannesschaefer.webnettools.payload.nmap;

public enum VerboseMode {
    SINGLE("-v"), DOUBLE("-vv");

    private final String mode;

    VerboseMode(String s) {
        mode = s;
    }

    public String toString() {
        return this.mode;
    }
}