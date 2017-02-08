package com.siplo.banking.bankdepositapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import woyou.aidlservice.jiuiv5.ICallback;

public class cashDepositActivity extends AppCompatActivity {

    private EditText mAccountView;
    private EditText mAmountView;
    private EditText mMobileView;
    private TextView mRefNoView;
    private EditText mNicView;
    private EditText mNarrView;
    //private EditText mBankCodeView;

    private View mProgressView;
    private View mFormView;

    private String accountNo;
    private String amount;
    private String mobile;
    private String refNo;
    private String nic;
    private String narr;
    //private String bankCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_deposit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mAccountView = (EditText)findViewById(R.id.accountNo);
        mAmountView =(EditText)findViewById(R.id.amount);
        mMobileView = (EditText)findViewById(R.id.mobile);
        mRefNoView = (TextView) findViewById(R.id.refNo);
        mNicView = (EditText) findViewById(R.id.nic);
        mNarrView = (EditText) findViewById(R.id.narr);
        mFormView = findViewById(R.id.deposit_form);
        mProgressView = findViewById(R.id.login_progress);
        //mBankCodeView = (EditText) findViewById(R.id.bankCode);

        mobileNumberValidation();
        currencyFormatValidation();
        //nicFormatValidation();
        AutoGenerateTransactionId();
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        // The filter's action is BROADCAST_ACTION
        IntentFilter mStatusIntentFilter = new IntentFilter(
                Constants.BROADCAST_ACTION);

        // Adds a data filter for the HTTP scheme
       // mStatusIntentFilter.addDataScheme("http");

        // Instantiates a new DownloadStateReceiver

        // Registers the DownloadStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver,
                mStatusIntentFilter);


        //show back icon in titile bar
        //Toolbar toolbar1 = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    public void proceedCashDeposit(View view){
        if(validateInputs()){
            showProgress(true);
            Log.d("cash_deposit","processing deposit");
            sendDataToServer();

        }

    }

    private void sendDataToServer(){
        JSONObject depositData = new JSONObject();
        try{
            depositData.put(Constants.ACCOUNT_NO_KEY,accountNo);
            //depositData.put(Constants.BANK_CODE_KEY,bankCode);
            depositData.put(Constants.AMOUNT_KEY,amount);
            depositData.put(Constants.MOBILE_KEY,mobile);
            depositData.put(Constants.REF_NO_KEY,refNo);
            depositData.put(Constants.NIC_KEY,nic);
            depositData.put(Constants.NARR_KEY,narr);

        }catch (JSONException e){
            Log.e("cash_deposit","jason error: "+e);
        }

       ServerCommunicationIntentService.sendPostRequest(this, depositData.toString(),Constants.SERVER_URL+Constants.CASH_DEPOSIT_ROUTE);
    }

    public boolean validateInputs(){
        // Store values at the time of the login attempt.
        accountNo = mAccountView.getText().toString();

        amount =  Double.parseDouble(mAmountView.getText().toString().replaceAll("[$, LKR]", ""))+"";
        Log.d("Amount : ",amount);
        //Double.parseDouble(((EditText)cheque.getChildAt(4)).getText().toString().replaceAll("[$, LKR]", ""));
        mobile = mMobileView.getText().toString();

        nic = mNicView.getText().toString();
        narr = mNarrView.getText().toString();
        //bankCode = mBankCodeView.getText().toString();
        // Reset errors.
        mAccountView.setError(null);
        mAmountView.setError(null);
        mMobileView.setError(null);

        mNicView.setError(null);
        mNarrView.setError(null);
        //mBankCodeView.setError(null);



        boolean cancel = false;
        View focusView = null;
        if(!TextUtils.isEmpty(amount)){
            amount = amount.replaceAll("[$, LKR]", "");
        }
        if (TextUtils.isEmpty(accountNo)) {
            mAccountView.setError(getString(R.string.accountNo_empty));
            focusView = mAccountView;
            cancel = true;

        }
        else if (TextUtils.isEmpty(amount)) {
            mAmountView.setError(getString(R.string.amount_empty));
            focusView = mAmountView;
            cancel = true;

        }
        else if (TextUtils.isEmpty(mobile)) {
            mMobileView.setError(getString(R.string.mobile_empty));
            focusView = mMobileView;
            cancel = true;

        }
//        else if (TextUtils.isEmpty(bankCode)) {
//            mBankCodeView.setError("Please Enter Bank Code");
//            focusView = mBankCodeView;
//            cancel = true;
//
//        }
//        else if (TextUtils.isEmpty(refNo)) {
//            mRefNoView.setError(getString(R.string.accountNo_empty));
//            focusView = mRefNoView;
//            cancel = true;
//
//        }
        if(cancel) {
            focusView.requestFocus();
            return false;
        }
        return true;
    }
    private void AutoGenerateTransactionId(){

                    int time = (int) (System.currentTimeMillis());
                    refNo = Integer.toString(time);
                    mRefNoView.setText("ReferenceNo: "+ refNo); ;




    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(!isFinishing()) {
                String response = intent.getStringExtra(Constants.EXTENDED_DATA_STATUS);
                try {
                    JSONObject jObject = new JSONObject(response);
                    printReceipt();
                    printReceipt();
                    showTransactionCompleteDialog(jObject.getString(Constants.REF_NO_KEY));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    };
//    // Broadcast receiver for receiving status updates from the IntentService
//    private class ResponseReceiver extends BroadcastReceiver
//    {
//        // Prevents instantiation
//        private ResponseReceiver() {
//        }
//        // Called when the BroadcastReceiver gets an Intent it's registered to receive
//
//        public void onReceive(Context context, Intent intent) {
//
//        /*
//         * Handle Intents here.
//         */
////            showProgress(false);
////            DialogFragment newFragment = new InformationDialogFragment();
////
////
////            newFragment.show(getSupportFragmentManager(), "asdf");
//            if(!isFinishing()) {
//                if(intent==null){
//                    Log.d("cash_deposit","intent is null");
//                }
//                showTransactionCompleteDialog(intent.getStringExtra(Constants.EXTENDED_DATA_STATUS));
//            }
//        }
//    }

    private void showTransactionCompleteDialog(String refNo){
        new AlertDialog.Builder(this)
                .setTitle("Cash Deposit Successful")
                .setMessage(amount+" LKR has been successfully deposited to Account "+accountNo+"\n" +
                        "\n" +
                        "Ref No: "+refNo)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete

                        dialog.dismiss();
                        finish();
                    }
                })

                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    private void   printReceipt(){
        ICallback callback = null;
        SharedPreferences prefs = getSharedPreferences(Constants.PERSONAL_KEY, MODE_PRIVATE);
        String name = prefs.getString(Constants.NAME_KEY,"no_user");//"No name defined" is the default value.
        String mobile = prefs.getString(Constants.MOBILE_KEY,"000000000");
        WoyouPrinter woyouPrinter = WoyouPrinter.getInstance();
        woyouPrinter.initPrinter(getApplicationContext());
        woyouPrinter.print("\nTransaction Type : Cash Deposit"+
                "\nReference No : "+this.refNo+
                " \nAmount : "+this.amount+
                "\n"+

                " LKR \nAccount No: "+this.accountNo+

                "\nNIC: "+nic+
                " \nMobile No : "+this.mobile+
                "\n"+

                "\ncollector :"+name+"("+mobile+")"+"\n\nCheque deposit and collections "+"\nare subject to realize and for"+"\nany clarification contact\n" +
                "Samaraweera\n" +
                "(071 589 4578/ ID: 148458)\n "+
                "sign :...........\n"+
                "----------------------",callback);

    }
    public void currencyFormatValidation(){

        mAmountView.addTextChangedListener(new TextWatcher(){
            DecimalFormat dec = new DecimalFormat("0.00");
            @Override
            public void afterTextChanged(Editable arg0) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            private String current = "";
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals(current)){
                    mAmountView.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[$,. LKR]", "");

                    double parsed = Double.parseDouble(cleanString);
                    String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

                    formatted=formatted.replaceAll("[$]", "")+" LKR";
                    current = formatted;
                    mAmountView.setText(formatted);
                    mAmountView.setSelection(formatted.length()-4);

                    mAmountView.addTextChangedListener(this);
                }
            }
        });
    }
    public void nicFormatValidation(){

        mNicView.addTextChangedListener(new TextWatcher(){
            //DecimalFormat dec = new DecimalFormat("0.00");
            @Override
            public void afterTextChanged(Editable arg0) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            private String current = "";
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals(current)){
                    mNicView.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[A-Z,a-z]", "");

                    current = cleanString+"V";
                    mNicView.setText(current);
                    mNicView.setSelection(current.length()-1);

                    mNicView.addTextChangedListener(this);
                }
            }
        });
    }
    private void mobileNumberValidation(){

        mMobileView.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count){
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after){
            }
            @Override
            public void afterTextChanged(Editable s)
            {
                if(s.length() == 1){
                    Pattern mPattern = Pattern.compile("^[0]");
                    String text = s.toString();
                    Matcher matcher = mPattern.matcher(s.toString());
                    if (!matcher.matches())
                    {
                        // dont know what to place

                        mMobileView.setText("");
                        mMobileView.setError("Invalid Number (format :07X XXXXXXX)");

                    }

                }
                if(s.length() == 2){
                    Pattern mPattern = Pattern.compile("^[0][7]");
                    String text = s.toString();
                    Matcher matcher = mPattern.matcher(s.toString());
                    if (!matcher.matches())
                    {
                        // dont know what to place

                        mMobileView.setText(text.substring(0,1));
                        mMobileView.setSelection(1);
                        mMobileView.setError("Invalid Number (format :07X XXXXXXX)");

                    }

                }
                if(s.length() == 3){
                    Pattern mPattern = Pattern.compile("^[0][7][0125678]");
                    String text = s.toString();
                    Matcher matcher = mPattern.matcher(s.toString());
                    if (!matcher.matches())
                    {
                        // dont know what to place

                        mMobileView.setText(text.substring(0,2));
                        mMobileView.setSelection(2);
                        mMobileView.setError("Invalid Number (format :07X XXXXXXX)");

                    }

                }
                if(s.length() <= 10 && s.length() > 3){
                    Pattern mPattern = Pattern.compile("^[0][7][1250678](\\d)*$");
                    String text = s.toString();
                    Matcher matcher = mPattern.matcher(s.toString());
                    if (!matcher.matches())
                    {
                        // dont know what to place




                    }

                }


            }
        });
    }
}
