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
    public static final String CHECK_NO_KEY = "check_no";
    public static final String CHECK_INIT_KEY = "check_initial";
    public static final String CHECKS = "checks";
    public static final String NIC_KEY = "nic" ;
    public static final String NARR_KEY = "narr";
    public static final String CHEQUES_KEY = "num_cheques";
    public static final String BANK_CODE_KEY = "bank_code";
    //registration constants
    public static final String NAME_KEY="name";
    public static final String EMAIL_KEY = "email";
    public static final String DEVICE_ID_KEY = "deviceId";

    //login constants
    public static final String USERNAME_KEY="username";
    public static final String PASSWORD_KEY = "password";
    //urls
   public static final String SERVER_URL = "http://amana.paymediasolutions.com";
    //public static final String SERVER_URL = "http://192.168.43.82:8000";
    //routes
    public static final String CASH_DEPOSIT_ROUTE = "/deposit/cash";
    public static final String CHEQUE_DEPOSIT_ROUTE = "/deposit/cheque";
    public static final String CHEQUE_IMAGE_UPLOAD_ROUTE = "/upload/cheque/image";
    public static final String REGISTER_ROUTE="/registerApp";
    public static final String LOGIN_ROUTE="/loginApp";

    public static final String PERSONAL_KEY = "personal";

    //network
    public static final String REQUEST_SUCCESS = "404";



}

