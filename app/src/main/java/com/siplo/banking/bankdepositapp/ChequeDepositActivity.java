package com.siplo.banking.bankdepositapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import woyou.aidlservice.jiuiv5.ICallback;

public class ChequeDepositActivity extends AppCompatActivity implements InformationDialogFragment.InformationDialogListener {

    private final int REQUEST_IMAGE_CAPTURE_FRONT = 0;
    private final int REQUEST_IMAGE_CAPTURE_BACK =1;

    private EditText mAccountView;
    private EditText mAmountView;
    private EditText mMobileView;
    private EditText mRefNoView;
    private ImageView mFrontImageView;
    private ImageView mBackImageView;
    private ImageView mImageView;




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

        ServerCommunicationIntentService.sendPostRequest(this, depositData.toString(),Constants.SERVER_URL+Constants.CHEQUE_DEPOSIT_ROUTE);
    }
    public void captureImage(View view){

       // dispatchTakePictureIntent();
    }
    //create file for image capturing
    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
    //static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent(int imageCaptureSide) {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            // Create the File where the photo should go
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//
//            }
//            // Continue only if the File was successfully created
//            if (photoFile != null) {
//                Uri photoURI = FileProvider.getUriForFile(this,
//                        "com.example.android.fileprovider",
//                        photoFile);
//                Log.d("File Capture",photoURI.toString());
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(takePictureIntent, imageCaptureSide);
//            }
//        }

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

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();

            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            Log.d("Capture",encoded);
            if(requestCode==REQUEST_IMAGE_CAPTURE_FRONT){
//                mImageView = mFrontImageView;
//                setPic();

                mFrontImageView.setImageBitmap(imageBitmap);
                mFrontImageView.setRotation(90);
            }
            else{
//                mImageView = mBackImageView;
//                setPic();
                mBackImageView.setImageBitmap(imageBitmap);
                mBackImageView.setRotation(90);
            }
        }
    }
//set currently captured photo in image view
private void setPic() {
    // Get the dimensions of the View
    int targetW = mImageView.getWidth();
    int targetH = mImageView.getHeight();

    // Get the dimensions of the bitmap
    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
    bmOptions.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
    int photoW = bmOptions.outWidth;
    int photoH = bmOptions.outHeight;

    // Determine how much to scale down the image
    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

    // Decode the image file into a Bitmap sized to fill the View
    bmOptions.inJustDecodeBounds = false;
    bmOptions.inSampleSize = scaleFactor;
    bmOptions.inPurgeable = true;

    Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
    mImageView.setImageBitmap(bitmap);
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
                    printReceipt();
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

    private void   printReceipt(){
        ICallback callback = null;
        WoyouPrinter woyouPrinter = WoyouPrinter.getInstance();
        woyouPrinter.initPrinter(getApplicationContext());
        woyouPrinter.print("\nTransaction Type : Cheque Deposit \nAmount : "+this.amount+" : \nAccount No: "+this.accountNo+" \nMobile No : "+this.mobile+"\nReference No : "+this.refNo,callback);
    }
}
