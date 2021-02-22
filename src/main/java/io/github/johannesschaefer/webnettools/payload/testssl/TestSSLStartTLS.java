package io.github.johannesschaefer.webnettools.payload.testssl;

public enum TestSSLStartTLS {
    FTP("ftp"),
    SMTP("smtp"),
    LMTP("lmtp"),
    POP3("pop3"),
    IMAP("imap"),
    XMPP("xmpp"),
    XMPP_SERVER("xmpp-server"),
    TELNET("telnet"),
    LDAP("ldap"),
    NNTP("nntp"),
    POSTGRES("postgres"),
    MYSQL("mysql");

    private final String mode;

    TestSSLStartTLS(String s) {
        mode = s;
    }

    public String toString() {
        return this.mode;
    }
}