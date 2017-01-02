package com.siplo.banking.bankdepositapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import woyou.aidlservice.jiuiv5.ICallback;

public class cashDepositActivity extends AppCompatActivity {

    private EditText mAccountView;
    private EditText mAmountView;
    private EditText mMobileView;
    private EditText mRefNoView;

    private View mProgressView;
    private View mFormView;

    private String accountNo;
    private String amount;
    private String mobile;
    private String refNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_deposit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mAccountView = (EditText)findViewById(R.id.accountNo);
        mAmountView =(EditText)findViewById(R.id.amount);
        mMobileView = (EditText)findViewById(R.id.mobile);
        mRefNoView = (EditText)findViewById(R.id.refNo);

        mFormView = findViewById(R.id.deposit_form);
        mProgressView = findViewById(R.id.login_progress);
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
            depositData.put(Constants.AMOUNT_KEY,amount);
            depositData.put(Constants.MOBILE_KEY,mobile);
            depositData.put(Constants.REF_NO_KEY,refNo);
        }catch (JSONException e){
            Log.e("cash_deposit","jason error: "+e);
        }

       ServerCommunicationIntentService.sendPostRequest(this, depositData.toString(),Constants.SERVER_URL+Constants.CASH_DEPOSIT_ROUTE);
    }

    public boolean validateInputs(){
        // Store values at the time of the login attempt.
        accountNo = mAccountView.getText().toString();
        amount =  mAmountView.getText().toString();
        mobile = mMobileView.getText().toString();
        refNo = mRefNoView.getText().toString();
        // Reset errors.
        mAccountView.setError(null);
        mAmountView.setError(null);
        mMobileView.setError(null);
        mRefNoView.setError(null);




        boolean cancel = false;
        View focusView = null;

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

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(!isFinishing()) {
                String response = intent.getStringExtra(Constants.EXTENDED_DATA_STATUS);
                try {
                    JSONObject jObject = new JSONObject(response);
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

    private void   printReceipt(){
        ICallback callback = null;
        WoyouPrinter woyouPrinter = WoyouPrinter.getInstance();
        woyouPrinter.initPrinter(getApplicationContext());
        woyouPrinter.print("Cash Deposit\nAmount:", callback);
    }
}
