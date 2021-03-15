package io.github.johannesschaefer.webnettools.payload.speedtest;

public enum Unit {
    BPS("bps"),
    KBPS("kbps"),
    MBPS("Mbps"),
    GBPS("Gbps"),
    B_S("B/s"),
    KB_S("kB/s"),
    MB_S("MB/s"),
    GB_S("GB/s"),
    KIBPS("kibps"),
    MIBPS("Mibps"),
    GIBPS("Gibps"),
    KIB_S("kiB/s"),
    MIB_S("MiB/s"),
    GIB_S("GiB/s"),
    AUTO_BINARY_BITS("auto-binary-bits"),
    AUTO_BINARY_BYTES("auto-binary-bytes"),
    AUTO_DECIMAL_BITS("auto-decimal-bits"),
    AUTO_DECIMAL_BYTES("auto-decimal-bytes");

    private final String mode;

    Unit(String s) {
        mode = s;
    }

    public String toString() {
        return this.mode;
    }
}