package io.tomoto.util;

import java.util.regex.Pattern;

/**
 * All needed regex pattern.
 *
 * @author Tomoto
 * <p>
 * 2020/11/5 19:37
 */
public class RegexUtil {
    public static final Pattern MONTH_PATTERN = Pattern.compile("^20\\d{2}(0[1-9]|1[0-2])$");
    public static final Pattern NO_PATTERN = Pattern.compile("^e\\d{5}$");
    public static final Pattern ACCOUNT_PATTERN = Pattern.compile("^[^\\s]{6}$");
    public static final Pattern PASSWORD_PATTERN = Pattern.compile("^[^\\s]{6}$");
}
