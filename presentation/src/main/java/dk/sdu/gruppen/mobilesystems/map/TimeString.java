package dk.sdu.gruppen.mobilesystems.map;

/*
 * LingPipe v. 3.9
 * Copyright (C) 2003-2010 Alias-i
 *
 * This program is licensed under the Alias-i Royalty Free License
 * Version 1 WITHOUT ANY WARRANTY, without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Alias-i
 * Royalty Free License Version 1 for more details.
 *
 * You should have received a copy of the Alias-i Royalty Free License
 * Version 1 along with this program; if not, visit
 * http://alias-i.com/lingpipe/licenses/lingpipe-license-1.txt or contact
 * Alias-i, Inc. at 181 North 11th Street, Suite 401, Brooklyn, NY 11211,
 * +1 (718) 290-9170.
 */

//package com.aliasi.util;

/**
 * Static utility methods for processing strings, characters and
 * string buffers.
 *
 * @author Bob Carpenter
 * @version 4.0.1
 * @see java.lang.Character
 * @see java.lang.String
 * @see java.lang.StringBuilder
 * @since LingPipe1.0
 */


public class TimeString {
    /**
     * Takes a time in milliseconds and returns an hours, minutes and
     * seconds representation.  Fractional ms times are rounded down.
     * Leading zeros and all-zero slots are removed.  A table of input
     * and output examples follows.
     * <p>
     * <p>Recall that 1 second = 1000 milliseconds.
     * <p>
     * <table border='1' cellpadding='5'>
     * <tr><td><i>Input ms</i></td><td><i>Output String</i></td></tr>
     * <tr><td>0</td><td><code>:00</code></td></tr>
     * <tr><td>999</td><td><code>:00</code></td></tr>
     * <tr><td>1001</td><td><code>:01</code></td></tr>
     * <tr><td>32,000</td><td><code>:32</code></td></tr>
     * <tr><td>61,000</td><td><code>1:01</code></td></tr>
     * <tr><td>11,523,000</td><td><code>3:12:03</code></td></tr>
     * </table>
     *
     * @param ms Time in milliseconds.
     * @return String-based representation of time in hours, minutes
     * and second format.
     */
    public static String msToString(long ms) {
        long totalSecs = ms / 1000;
        long hours = (totalSecs / 3600);
        long mins = (totalSecs / 60) % 60;
        long secs = totalSecs % 60;
        String minsString = (mins == 0)
                ? "00"
                : ((mins < 10)
                ? "0" + mins
                : "" + mins);
        String secsString = (secs == 0)
                ? "00"
                : ((secs < 10)
                ? "0" + secs
                : "" + secs);
        if (hours > 0)
            return hours + ":" + minsString + ":" + secsString;
        else if (mins > 0)
            return mins + ":" + secsString;
        else return ":" + secsString;
    }

}
