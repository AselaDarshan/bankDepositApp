package com.siplo.banking.bankdepositapp;

/**
 * Created by asela on 9/14/16.
 */
public final class Constants {

    // Defines a custom Intent action
    public static final String BROADCAST_ACTION =
            "com.siplo.banking.bankdepositapp.BROADCAST";

    // Defines the key for the status "extra" in an Intent
    public static final String EXTENDED_DATA_STATUS =
            "com.siplo.banking.bankdepositapp.STATUS";

    public static final String COMMUNICATION_ERROR = "com.siplo.banking.bankdepositapp.communication.error";

    //cash deposit constants
    public static final String ACCOUNT_NO_KEY= "account_no";
    public static final String AMOUNT_KEY = "amount";
    public static final String MOBILE_KEY="mobile";
    public static final String REF_NO_KEY = "ref_no";

    //urls
    public static final String SERVER_URL = "http://192.168.8.102:8000";
    public static final String CASH_DEPOSIT_ROUTE = "/deposit/cash";

}
