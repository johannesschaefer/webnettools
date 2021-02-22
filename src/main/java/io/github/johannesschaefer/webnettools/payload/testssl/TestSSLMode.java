package io.github.johannesschaefer.webnettools.payload.testssl;

public enum TestSSLMode {
    SERIAL("serial"), PARALLEL("parallel");

    private final String mode;

    TestSSLMode(String s) {
        mode = s;
    }

    public String toString() {
        return this.mode;
    }
}