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
    public static final Pattern NO_PATTERN = Pattern.compile("^e\\d{3}$");
    public static final Pattern ACCOUNT_PATTERN = Pattern.compile("^[^\\s]{6}$");
    public static final Pattern PASSWORD_PATTERN = Pattern.compile("^[^\\s]{6}$");
    public static final Pattern ID_NO_PATTERN = Pattern.compile(
            "^[1-9]\\d{5}(18|19|20|(3\\d))\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$");
    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
    public static final Pattern PHONE_PATTERN = Pattern.compile("1\\d{10}");
}
