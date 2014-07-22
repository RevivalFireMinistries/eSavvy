package za.org.rfm.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Russel.Mupfumira
 * Date: 2014/07/18
 * Time: 11:35 AM
 */
public class EmailFormatValidator {
    private Pattern pattern;
    private Matcher matcher;

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public EmailFormatValidator() {
        pattern = Pattern.compile(EMAIL_PATTERN);
    }

    public boolean validate(final String email) {

        matcher = pattern.matcher(email);
        return matcher.matches();

    }
}
