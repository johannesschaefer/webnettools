package io.github.johannesschaefer.webnettools.payload.testssl;

import io.github.johannesschaefer.webnettools.annotation.*;
import io.github.johannesschaefer.webnettools.payload.Payload;
import lombok.Data;

@Data
@Tool(name="testssl", displayName="Test SSL", cmd="testssl.sh", description="Check the support of SSL/TLS of any server. Uses the <a href=\"https://testssl.sh\">testssl.sh</a> tooling.")
@Group(name=TestSSLPayload.GROUP_GENERAL)
@Group(name=TestSSLPayload.GROUP_CHECKS, description= "By default, testssl.sh run everything, except cipher-per-proto and grease")
@Group(name=TestSSLPayload.GROUP_TUNING)
@Group(name=TestSSLPayload.GROUP_OUTPUT)
public class TestSSLPayload implements Payload {
    public static final String GROUP_GENERAL = "General options";
    public static final String GROUP_CHECKS = "Single checks";
    public static final String GROUP_TUNING = "Tuning / connect options";
    public static final String GROUP_OUTPUT = "Output options";

    @MainParameter(displayName ="URL / Hostname", description="URI can be a hostname, an IPv4 or IPv6 address (restriction see below) or an URL. IPv6 addresses need to be in square brackets. For any given parameter port 443 is assumed unless specified by appending a colon and a port number. The only preceding protocol specifier allowed is https. You need to be aware that checks for an IP address might not hit the vhost you want. DNS resolution (A/AAAA record) is being performed unless you have an /etc/hosts entry for the hostname.")
    private String url;

    @FixedParam(param = "--warnings")
    private String warnings = "batch";

    @EnumParam(displayName = "Mode", param = "--mode", description = "Mass testing to be done serial (default) or parallel", group = TestSSLPayload.GROUP_GENERAL)
    private TestSSLMode mode = TestSSLMode.SERIAL;
    @EnumParam(displayName = "Start TLS", param = "--starttls", description = "Does a run against a STARTTLS enabled service which is one of ftp, smtp, lmtp, pop3, imap, xmpp, xmpp-server, telnet, ldap, nntp, postgres, mysql", group = TestSSLPayload.GROUP_GENERAL)
    private TestSSLStartTLS startTls;
    @StringParam(displayName = "XMPP Host", param = "--xmpphost", description = "For STARTTLS xmpp or xmpp-server checks it supplies the domainname (like SNI)", group = TestSSLPayload.GROUP_GENERAL)
    private String xmpphost;
    @StringParam(displayName = "MX", param = "--mx", description = "Tests MX records from high to low priority (STARTTLS, port 25)", group = TestSSLPayload.GROUP_GENERAL)
    private String mx;
    @NumberParam(displayName = "Connect Timeout", param = "--connect-timeout", description = "useful to avoid hangers. Max <seconds> to wait for the TCP socket connect to return", min = 0, max = 1000, group = TestSSLPayload.GROUP_GENERAL)
    private Integer connectTimeout;
    @NumberParam(displayName = "Openssl Timeout", param = "--openssl-timeout", description = "useful to avoid hangers. Max <seconds> to wait before openssl connect will be terminated", min = 0, max = 1000, group = TestSSLPayload.GROUP_GENERAL)
    private Integer opensslTimeout;

    @BooleanParam(displayName ="Each Cipher", param="--each-cipher", group = TestSSLPayload.GROUP_CHECKS, description="checks each local cipher remotely")
    private Boolean eachCipher;
    @BooleanParam(displayName ="Cipher per Proto", param="--cipher-per-proto", group = TestSSLPayload.GROUP_CHECKS, description="checks those per protocol")
    private Boolean cipherPerProto;
    @BooleanParam(displayName ="Standard", param="--standard", group = TestSSLPayload.GROUP_CHECKS, description="tests certain lists of cipher suites by strength")
    private Boolean standard;
    @BooleanParam(displayName ="Forward security", param="--fs", group = TestSSLPayload.GROUP_CHECKS, description="checks forward secrecy settings")
    private Boolean fs;
    @BooleanParam(displayName = "protocols", param = "--protocols", group = TestSSLPayload.GROUP_CHECKS, description = "checks TLS/SSL protocols (including SPDY/HTTP2)")
    private Boolean protocols;
    @BooleanParam(displayName = "grease", param = "--grease", group = TestSSLPayload.GROUP_CHECKS, description = "tests several server implementation bugs like GREASE and size limitations")
    private Boolean grease;
    @BooleanParam(displayName = "server-defaults", param = "--server-defaults", group = TestSSLPayload.GROUP_CHECKS, description = "displays the server's default picks and certificate info")
    private Boolean serverDefaults;
    @BooleanParam(displayName = "server-preference", param = "--server-preference", group = TestSSLPayload.GROUP_CHECKS, description = "displays the server's picks: protocol+cipher")
    private Boolean serverPreference;
    @StringParam(displayName = "single cipher pattern", param = "--single-cipher", group = TestSSLPayload.GROUP_CHECKS, description = "tests matched <pattern> of ciphers (if <pattern> not a number: word match)")
    private String singleCipher;
    @BooleanParam(displayName = "client-simulation", param = "--client-simulation", group = TestSSLPayload.GROUP_CHECKS, description = "test client simulations, see which client negotiates with cipher and protocol")
    private Boolean clientSimulation;
    @BooleanParam(displayName = "header", param = "--header", group = TestSSLPayload.GROUP_CHECKS, description = "tests HSTS, HPKP, server/app banner, security headers, cookie, reverse proxy, IPv4 address")
    private Boolean header;
    @BooleanParam(displayName = "vulnerable", param = "--vulnerable", group = TestSSLPayload.GROUP_CHECKS, description = "tests all (of the following) vulnerabilities (if applicable)")
    private Boolean vulnerable;
    @BooleanParam(displayName = "heartbleed", param = "--heartbleed", group = TestSSLPayload.GROUP_CHECKS, description = "tests for Heartbleed vulnerability")
    private Boolean heartbleed;
    @BooleanParam(displayName = "ccs-injection", param = "--ccs-injection", group = TestSSLPayload.GROUP_CHECKS, description = "tests for CCS injection vulnerability")
    private Boolean ccsInjection;
    @BooleanParam(displayName = "ticketbleed", param = "--ticketbleed", group = TestSSLPayload.GROUP_CHECKS, description = "tests for Ticketbleed vulnerability in BigIP loadbalancers")
    private Boolean ticketbleed;
    @BooleanParam(displayName = "robot", param = "--robot", group = TestSSLPayload.GROUP_CHECKS, description = "tests for Return of Bleichenbacher's Oracle Threat (ROBOT) vulnerability")
    private Boolean robot;
    @BooleanParam(displayName = "starttls-injection", param = "--starttls-injection", group = TestSSLPayload.GROUP_CHECKS, description = "tests for STARTTLS injection issues")
    private Boolean starttlsInjection;
    @BooleanParam(displayName = "renegotiation", param = "--renegotiation", group = TestSSLPayload.GROUP_CHECKS, description = "tests for renegotiation vulnerabilities")
    private Boolean renegotiation;
    @BooleanParam(displayName = "compression", param = "--compression", group = TestSSLPayload.GROUP_CHECKS, description = "tests for CRIME vulnerability (TLS compression issue)")
    private Boolean compression;
    @BooleanParam(displayName = "breach", param = "--breach", group = TestSSLPayload.GROUP_CHECKS, description = "tests for BREACH vulnerability (HTTP compression issue)")
    private Boolean breach;
    @BooleanParam(displayName = "poodle", param = "--poodle", group = TestSSLPayload.GROUP_CHECKS, description = "tests for POODLE (SSL) vulnerability")
    private Boolean poodle;
    @BooleanParam(displayName = "tls-fallback", param = "--tls-fallback", group = TestSSLPayload.GROUP_CHECKS, description = "checks TLS_FALLBACK_SCSV mitigation")
    private Boolean tlsFallback;
    @BooleanParam(displayName = "sweet32", param = "--sweet32", group = TestSSLPayload.GROUP_CHECKS, description = "tests 64 bit block ciphers (3DES, RC2 and IDEA): SWEET32 vulnerability")
    private Boolean sweet32;
    @BooleanParam(displayName = "beast", param = "--beast", group = TestSSLPayload.GROUP_CHECKS, description = "tests for BEAST vulnerability")
    private Boolean beast;
    @BooleanParam(displayName = "lucky13", param = "--lucky13", group = TestSSLPayload.GROUP_CHECKS, description = "tests for LUCKY13")
    private Boolean lucky13;
    @BooleanParam(displayName = "winshock", param = "--winshock", group = TestSSLPayload.GROUP_CHECKS, description = "tests for winshock vulnerability")
    private Boolean winshock;
    @BooleanParam(displayName = "freak", param = "--freak", group = TestSSLPayload.GROUP_CHECKS, description = "tests for FREAK vulnerability")
    private Boolean freak;
    @BooleanParam(displayName = "logjam", param = "--logjam", group = TestSSLPayload.GROUP_CHECKS, description = "tests for LOGJAM vulnerability")
    private Boolean logjam;
    @BooleanParam(displayName = "drown", param = "--drown", group = TestSSLPayload.GROUP_CHECKS, description = "tests for DROWN vulnerability")
    private Boolean drown;
    @BooleanParam(displayName = "rc4", param = "--rc4", group = TestSSLPayload.GROUP_CHECKS, description = "which RC4 ciphers are being offered?")
    private Boolean rc4;

    @BooleanParam(displayName = "fast", param = "--fast", group = TestSSLPayload.GROUP_TUNING, description = "omits some checks: using openssl for all ciphers (-e), show only first preferred cipher.")
    private Boolean fast;
    @BooleanParam(displayName = "full", param = "--full", group = TestSSLPayload.GROUP_TUNING, description = "includes tests for implementation bugs and cipher per protocol (could disappear)")
    private Boolean full;
    @BooleanParam(displayName = "bugs", param = "--bugs", group = TestSSLPayload.GROUP_TUNING, description = "enables the \"-bugs\" option of s_client, needed e.g. for some buggy F5s")
    private Boolean bugs;
    @BooleanParam(displayName = "assume-http", param = "--assume-http", group = TestSSLPayload.GROUP_TUNING, description = "if protocol check fails it assumes HTTP protocol and enforces HTTP checks")
    private Boolean assumeHttp;
    @BooleanParam(displayName = "ssl-native", param = "--ssl-native", group = TestSSLPayload.GROUP_TUNING, description = "fallback to checks with OpenSSL where sockets are normally used")
    private Boolean sslNative;
    @StringParam(displayName = "proxy", param = "--proxy", group = TestSSLPayload.GROUP_TUNING, description = "(experimental) proxy connects via <host:port>, auto: values from $env ($http(s)_proxy)")
    private String proxy;
    @BooleanParam(displayName = "6", param = "-6", group = TestSSLPayload.GROUP_TUNING, description = "also use IPv6. Works only with supporting OpenSSL version and IPv6 connectivity")
    private Boolean six;
    @StringParam(displayName = "ip", param = "--ip", group = TestSSLPayload.GROUP_TUNING, description = "a) tests the supplied <ip> v4 or v6 address instead of resolving host(s) in URI\nb) arg \"one\" means: just test the first DNS returns (useful for multiple IPs)")
    private String ip;
    @EnumParam(displayName = "nodns", param = "--nodns", group = TestSSLPayload.GROUP_TUNING, description = "if \"none\": do not try any DNS lookups, \"min\" queries A, AAAA and MX records")
    private NoDns nodns;
    @BooleanParam(displayName = "sneaky", param = "--sneaky", group = TestSSLPayload.GROUP_TUNING, description = "leave less traces in target logs: user agent, referer")
    private Boolean sneaky;
    @StringParam(displayName = "user-agent", param = "--user-agent", group = TestSSLPayload.GROUP_TUNING, description = "set a custom user agent instead of the standard user agent")
    private String userAgent;
    @BooleanParam(displayName = "ids-friendly", param = "--ids-friendly", group = TestSSLPayload.GROUP_TUNING, description = "skips a few vulnerability checks which may cause IDSs to block the scanning IP")
    private Boolean idsFriendly;
    @BooleanParam(displayName = "phone-out", param = "--phone-out", group = TestSSLPayload.GROUP_TUNING, description = "allow to contact external servers for CRL download and querying OCSP responder")
    private Boolean phoneOut;
    @StringParam(displayName = "basicauth", param = "--basicauth", group = TestSSLPayload.GROUP_TUNING, description = "provide HTTP basic auth information <user:pass>")
    private String basicauth;
    @StringParam(displayName = "reqheader", param = "--reqheader", group = TestSSLPayload.GROUP_TUNING, description = "add custom http request headers")
    private String reqheader;
    @ServerParam(param = "--add-ca", handler = TestSSLAddCaHandler.class)
    private String addCa;
    @FileParam(displayName = "CA file", param = "--add-ca", accept = "*.pem", group = TestSSLPayload.GROUP_TUNING, description = "CA files enables test against additional CAs" )
    private String addCaFile;

    @BooleanParam(displayName ="Quiet", param="--quiet", group = TestSSLPayload.GROUP_OUTPUT, description="Normally testssl.sh displays a banner on stdout with several version information, usage rights and a warning. This option suppresses it. Please note that by choosing this option you acknowledge usage terms and the warning normally appearing in the banner.")
    private Boolean quiet;
    @BooleanParam(displayName ="wide", param="--wide", group = TestSSLPayload.GROUP_OUTPUT, description="wide output for tests like RC4, BEAST. FS also with hexcode, kx, strength, RFC name")
    private Boolean wide;
    @BooleanParam(displayName ="show-each", param="--show-each", group = TestSSLPayload.GROUP_OUTPUT, description="for wide outputs: display all ciphers tested -- not only succeeded ones")
    private Boolean showEach;
    @EnumParam(displayName = "mapping", param = "--mapping", group = TestSSLPayload.GROUP_OUTPUT, description = "openssl: use the OpenSSL cipher suite name as the primary name cipher suite name form (default)\n-> use the IANA/(RFC) cipher suite name as the primary name cipher suite name form\n-> don't display the OpenSSL cipher suite name, display IANA/(RFC) names only\n-> don't display the IANA/(RFC) cipher suite name, display OpenSSL names only")
    private Mapping mapping;
    @EnumParam(displayName = "color", param = "--color", group = TestSSLPayload.GROUP_OUTPUT, description = "0: no escape or other codes,  1: b/w escape codes,  2: color (default), 3: extra color (color all ciphers)")
    private Color color;
    @BooleanParam(displayName ="colorblind", param="--colorblind", group = TestSSLPayload.GROUP_OUTPUT, description="swap green and blue in the output")
    private Boolean colorblind;
    @BooleanParam(displayName ="disable-rating", param="--disable-rating", group = TestSSLPayload.GROUP_OUTPUT, description="Explicitly disables the rating output")
    private Boolean disableRating;

    @Override
    public String getCacheString() {
        return url;
    }
}