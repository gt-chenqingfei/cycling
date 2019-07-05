package com.beastbikes.framework.business;

import java.util.Locale;

/**
 * The context of business transaction
 *
 * @author johnson
 */
public interface BusinessContext {

    /**
     * Returns the error message of the specified error code
     *
     * @param errorCode The error code
     * @return A detail message that described the specified error code
     */
    public String getErrorMessage(int errorCode);

    /**
     * Returns the error message of the specified error code in the specified
     * locale language
     *
     * @param locale    The locale
     * @param errorCode The error code
     * @return A detail message that described the specified error code in the
     * specified locale
     */
    public String getErrorMessage(Locale locale, int errorCode);

}
