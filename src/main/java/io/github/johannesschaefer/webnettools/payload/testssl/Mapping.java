package io.github.johannesschaefer.webnettools.payload.testssl;

public enum Mapping {
    OPENSSL("openssl"),
    IANA("iana"),
    RFC("rfc"),
    NO_OPENSSL("no-openssl"),
    NO_IANA("no-iana"),
    NO_RFC("no-rfc");

    private final String mode;

    Mapping(String s) {
        mode = s;
    }

    public String toString() {
        return this.mode;
    }
}