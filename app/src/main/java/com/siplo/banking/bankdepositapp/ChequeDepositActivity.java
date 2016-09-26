package com.siplo.banking.bankdepositapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class ChequeDepositActivity extends AppCompatActivity implements InformationDialogFragment.InformationDialogListener {

    private final int REQUEST_IMAGE_CAPTURE_FRONT = 0;
    private final int REQUEST_IMAGE_CAPTURE_BACK =1;

    private EditText mAccountView;
    private EditText mAmountView;
    private EditText mMobileView;
    private EditText mRefNoView;
    private ImageView mFrontImageView;
    private ImageView mBackImageView;


    private String accountNo;
    private String amount;
    private String mobile;
    private String refNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheque_deposit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mAccountView = (EditText)findViewById(R.id.accountNo);
        mAmountView =(EditText)findViewById(R.id.amount);
        mMobileView = (EditText)findViewById(R.id.mobile);
        mRefNoView = (EditText)findViewById(R.id.refNo);

        mFrontImageView=(ImageView) findViewById(R.id.chequeImage);
        mBackImageView=(ImageView) findViewById(R.id.chequeImageBack);
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
//                dispatchTakePictureIntent();
//            }
//        });


        // The filter's action is BROADCAST_ACTION
        IntentFilter mStatusIntentFilter = new IntentFilter(
                Constants.BROADCAST_ACTION);


        // Registers the DownloadStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver,
                mStatusIntentFilter);
    }
    public void captureFront(View view){
        dispatchTakePictureIntent(REQUEST_IMAGE_CAPTURE_FRONT);
    }
    public void captureBack(View view){
        dispatchTakePictureIntent(REQUEST_IMAGE_CAPTURE_BACK);
    }
    public void proceedCashDeposit(View view){

        if(validateInputs()){
            Log.d("cheque_deposit","precessing deposit");
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
    public void captureImage(View view){

       // dispatchTakePictureIntent();
    }
    //static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent(int imageCaptureSide) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {


            startActivityForResult(takePictureIntent, imageCaptureSide);
//            Log.d("cheque","capturing");
        }
        else{
//            Log.d("cheque","capture faild");
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == REQUEST_IMAGE_CAPTURE_BACK ||requestCode == REQUEST_IMAGE_CAPTURE_FRONT)  && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();


            Bitmap imageBitmap = (Bitmap) extras.get("data");
            if(requestCode==REQUEST_IMAGE_CAPTURE_FRONT){
                mFrontImageView.setImageBitmap(imageBitmap);
                mFrontImageView.setRotation(90);
            }
            else{
                mBackImageView.setImageBitmap(imageBitmap);
                mBackImageView.setRotation(90);
            }
        }
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

    private void showTransactionCompleteDialog(String refNo){
        new AlertDialog.Builder(this)
                .setTitle("Cheque Deposit Successful")
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
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        finish();
    }
}
