package io.github.johannesschaefer.webnettools.payload.testssl;

public enum Color {
    _0("0"),
    _1("1"),
    _2("2"),
    _3("3");

    private final String mode;

    Color(String s) {
        mode = s;
    }

    public String toString() {
        return this.mode;
    }
}