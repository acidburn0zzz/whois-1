package net.ripe.db.whois.api.rest;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/*
 * This test checks the shell script behaviour that filters passwords from urls.:
 *
 *   /usr/bin/strip_passwords_from_access_log
 *
 *    while read line ; do
 *     echo "$line" | sed -E 's/password=[^& \t\n"]* /password=filtered-out/g'
 *   done
 */
public class UrlPasswordReplacementTest {
    public static final String PASSWORD_PATTERN = "password=[^& \\t\\n\"]*";
    private final String PASSWORD_REPLACEMENT = "password=filtered";

    @Test
    public void encoded_ampersand_single_and_double_quote() {

        // (" is encoded to %22, ' to %27, & to %26, check this guide: http://www.w3schools.com/tags/ref_urlencode.asp)

        final String encoded_ampersand_single_and_double_quote =
                "2001:67c:2e8:9::c100:14e6 rest-prepdev.db.ripe.net - - [28/Apr/2015:10:38:44 +0200] " +
                        "\"GET /favicon.ico HTTP/1.1\" 400 226 " +
                        "\"https://rest-prepdev.db.ripe.net/ripe/mntner/TPOLYCHNIA-MNT.json?password=test*%22%27%26@-ab\" " +
                        "\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.90 Safari/537.36\"";

        final String filtered = "2001:67c:2e8:9::c100:14e6 rest-prepdev.db.ripe.net - - [28/Apr/2015:10:38:44 +0200] " +
                "\"GET /favicon.ico HTTP/1.1\" 400 226 " +
                "\"https://rest-prepdev.db.ripe.net/ripe/mntner/TPOLYCHNIA-MNT.json?password=filtered\" " +
                "\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.90 Safari/537.36\"";

        assertThat(encoded_ampersand_single_and_double_quote.replaceAll(PASSWORD_PATTERN, PASSWORD_REPLACEMENT), is(filtered));
    }

    @Test
    public void http11_header_in_url() {

        final String http11_header_in_url =
                "2001:67c:2e8:9::c100:14e6 rest-prepdev.db.ripe.net - - [28/Apr/2015:11:46:46 +0200] " +
                        "\"GET /ripe/mntner/TPOLYCHNIA-MNT.json?password=test HTTP/1.1\" 200 478 " +
                        "\"-\" \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.90 Safari/537.36\"";

        assertThat(http11_header_in_url.replaceAll(PASSWORD_PATTERN, PASSWORD_REPLACEMENT),
                containsString("2001:67c:2e8:9::c100:14e6 rest-prepdev.db.ripe.net - - [28/Apr/2015:11:46:46 +0200] " +
                        "\"GET /ripe/mntner/TPOLYCHNIA-MNT.json?password=filtered HTTP/1.1\" 200 478 " +
                        "\"-\" \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.90 Safari/537.36\""));
    }

    @Test
    public void special_chars_not_encoded() {
        assertThat("https://rest-prepdev.db.ripe.net/ripe/mntner/TPOLYCHNIA-MNT.json?password=test$#@!%^*-ab".
                        replaceAll(PASSWORD_PATTERN, PASSWORD_REPLACEMENT),
                containsString("https://rest-prepdev.db.ripe.net/ripe/mntner/TPOLYCHNIA-MNT.json?password=filtered"));
    }

    @Test
    public void password_first_and_last() {
        assertThat("https://rest-prepdev.db.ripe.net/ripe/mntner/TPOLYCHNIA-MNT.json?password=bla&param=param1&password=bla".
                        replaceAll(PASSWORD_PATTERN, PASSWORD_REPLACEMENT),
                containsString("https://rest-prepdev.db.ripe.net/ripe/mntner/TPOLYCHNIA-MNT.json?password=filtered&param=param1&password=filtered"));
    }

    @Test
    public void password_in_the_middle() {
        assertThat("https://rest-prepdev.db.ripe.net/ripe/mntner/TPOLYCHNIA-MNT.json?param=3&password=bla&param=param1&password=bla&param=a".
                        replaceAll(PASSWORD_PATTERN, PASSWORD_REPLACEMENT),
                containsString("https://rest-prepdev.db.ripe.net/ripe/mntner/TPOLYCHNIA-MNT.json?param=3&password=filtered&param=param1&password=filtered&param=a"));
    }

    @Test
    public void multiple_passwords_in_a_row() {
        assertThat("https://rest-prepdev.db.ripe.net/ripe/mntner/TPOLYCHNIA-MNT.json?password=bla&password=bla&password=bla".
                        replaceAll(PASSWORD_PATTERN, PASSWORD_REPLACEMENT),
                containsString("https://rest-prepdev.db.ripe.net/ripe/mntner/TPOLYCHNIA-MNT.json?password=filtered&password=filtered&password=filtered"));
    }
}
