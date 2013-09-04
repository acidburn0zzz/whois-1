package net.ripe.db.whois.logsearch.logformat;

import org.junit.Test;

import static net.ripe.db.whois.logsearch.logformat.LoggedUpdate.Type;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class DailyLogEntryTest {
    @Test(expected = IllegalArgumentException.class)
    public void parse_empty() {
        DailyLogEntry.parse("", "", Type.DAILY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parse_invalid() {
        DailyLogEntry.parse("/", "", Type.DAILY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parse_no_date_folder() {
        DailyLogEntry.parse("/101010.update", "", Type.DAILY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parse_no_update_folder() {
        DailyLogEntry.parse("20130312/", "", Type.DAILY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parse_no_filename() {
        DailyLogEntry.parse("20130312/101010.update", "", Type.DAILY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parse_invalid_filename() {
        DailyLogEntry.parse("20130312/101010.update/filename", "", Type.DAILY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parse_audit() {
        final DailyLogEntry dailyLogEntry = new DailyLogEntry("/some/ignored/path/prefix/20130312/101010.update/000.audit.xml.gz", "20130312");
    }

    @Test
    public void parse_update() {
        final DailyLogEntry dailyLogEntry = new DailyLogEntry("20130312/101010.update/001.msg-in.txt.gz", "20130312");
        assertThat(dailyLogEntry.getUpdateId(), is("20130312/101010.update/001.msg-in.txt.gz"));
        assertThat(dailyLogEntry.getDate(), is("20130312"));
    }

    @Test
    public void parse_ack() {
        final DailyLogEntry dailyLogEntry = new DailyLogEntry("20130312/101010.update/002.ack.txt.gz", "20130312");
        assertThat(dailyLogEntry.getUpdateId(), is("20130312/101010.update/002.ack.txt.gz"));
        assertThat(dailyLogEntry.getDate(), is("20130312"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parse_notification() {
        final DailyLogEntry dailyLogEntry = new DailyLogEntry("20130312/101010.update/666.msg-out.txt.gz", "20130312");
    }
}