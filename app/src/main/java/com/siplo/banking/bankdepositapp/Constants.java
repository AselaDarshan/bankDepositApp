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

    //registration constants
    public static final String NAME_KEY="name";
    public static final String EMAIL_KEY = "email";
    public static final String DEVICE_ID_KEY = "deviceId";

    //login constants
    public static final String USERNAME_KEY="username";
    public static final String PASSWORD_KEY = "password";
    //urls
    public static final String SERVER_URL = "http://paymediasolutions.com";
    //routes
    public static final String CASH_DEPOSIT_ROUTE = "/deposit/cash";
    public static final String REGISTER_ROUTE="/registerApp";
    public static final String LOGIN_ROUTE="/loginApp";

}

