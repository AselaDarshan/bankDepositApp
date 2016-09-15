package com.siplo.banking.bankdepositapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import org.w3c.dom.Text;

public class ChequeDepositActivity extends AppCompatActivity implements InformationDialogFragment.InformationDialogListener {

    private EditText mAccountView;
    private EditText mAmountView;
    private EditText mMobileView;
    private EditText mRefNoView;
    private ImageView mImageView;
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

        mImageView=(ImageView) findViewById(R.id.chequeImage);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                dispatchTakePictureIntent();
            }
        });
    }

    public void proceedCashDeposit(View view){

        if(validateInputs()){
            DialogFragment newFragment = new InformationDialogFragment();
            newFragment.show(getSupportFragmentManager(), "missiles");
        }

    }
    public void captureImage(View view){

        dispatchTakePictureIntent();
    }
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//            Log.d("cheque","capturing");
        }
        else{
//            Log.d("cheque","capture faild");
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
            mImageView.setRotation(90);
        }
    }

    public boolean validateInputs(){
        // Store values at the time of the login attempt.
        String account = mAccountView.getText().toString();

        // Reset errors.
        mAccountView.setError(null);
        mAmountView.setError(null);
        mMobileView.setError(null);
        mRefNoView.setError(null);




        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(account)) {
            mAccountView.setError(getString(R.string.accountNo_empty));
            focusView = mAccountView;
            cancel = true;

        }
        if(cancel) {
            focusView.requestFocus();
            return false;
        }
        return true;
    }
    private void showTransactionCompleteDilaog(){
        new AlertDialog.Builder(this)
                .setTitle("Deposit Successful")
                .setMessage("1,200,000 LKR has been successfully deposited to Account 35327\n" +
                        "\n" +
                        "Ref No: 45812")
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
