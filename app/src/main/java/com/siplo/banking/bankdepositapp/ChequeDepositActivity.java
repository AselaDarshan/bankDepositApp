package com.siplo.banking.bankdepositapp;

import android.accounts.Account;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Dimension;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v7.widget.CardView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.R.attr.description;
import static android.R.attr.id;
import static android.R.attr.orientation;
import static android.R.attr.phoneNumber;
import static android.R.attr.value;
import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.view.inputmethod.EditorInfo.IME_ACTION_UNSPECIFIED;
import static java.security.AccessController.getContext;

import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;

public class ChequeDepositActivity extends AppCompatActivity implements InformationDialogFragment.InformationDialogListener {

    private final int REQUEST_IMAGE_CAPTURE_FRONT = 0;
    private final int REQUEST_IMAGE_CAPTURE_BACK =1;

    private Bitmap bitmap;
    private Bitmap bitmap1;
    private IWoyouService woyouService;
    private ICallback callback = null;


    private String UPLOAD_URL;
    private ServiceConnection connService = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

            woyouService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            woyouService = IWoyouService.Stub.asInterface(service);

        }
    };

    private EditText mAccountView;
    private EditText mAmountView;
    private EditText mMobileView;
    private EditText mRefNoView;
    private EditText mCheckNoView;
    private ImageView mFrontImageView;
    private ImageView mBackImageView;
    private ImageView mImageView;




    private String accountNo;
    private String amount;
    private String mobile;
    private String refNo;
    private String checkNo;

    LayoutInflater inflater;
    LinearLayout formContainer;
    private ArrayList<LinearLayout> chequeList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheque_deposit);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button nextCheque = (Button) findViewById(R.id.next_cheque);

         nextCheque.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 initializeChequeDepositFrom();
             }
         });
        mAccountView = (EditText)findViewById(R.id.accountNo);

        mMobileView = (EditText)findViewById(R.id.mobile);
        mRefNoView = (EditText)findViewById(R.id.refNo);

        mobileNumberValidation();


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

        //show back icon in titile bar
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        chequeList = new ArrayList<>();
        initializeChequeDepositFrom();
    }


    public void captureFront(View view){
        dispatchTakePictureIntent(REQUEST_IMAGE_CAPTURE_FRONT);
    }
    public void captureBack(View view){
        dispatchTakePictureIntent(REQUEST_IMAGE_CAPTURE_BACK);
    }
    public void proceedCashDeposit(View view){
        uploadImages();
       /* if(validateInputs()){
            Log.d("cheque_deposit","precessing deposit");
            sendDataToServer();
            //uploadImage();
        }*/

    }


    private void uploadImages(){
        int size = chequeList.size();
        for (int i =0;i<size;i++){

            uploadImage(i);
        }
    }

    private void uploadImage(final int i){
        //Showing the progress dialog
        Bitmap bip = null;
        final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.SERVER_URL+Constants.CHEQUE_IMAGE_UPLOAD_ROUTE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Toast.makeText(ChequeDepositActivity.this, s , Toast.LENGTH_LONG).show();
                        Log.d("error1",s);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(ChequeDepositActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                        Log.d("error2",volleyError.getMessage().toString());
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String


                //Creating parameters
                Map<String,String> params = imagepacking(i);

                //Adding parameters


                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }
    public Map<String,String>imagepacking(int i){

        Map<String,String> params = new Hashtable<String, String>();




            String KEY_NAME =((TextInputLayout)(chequeList.get(i).getChildAt(1))).getEditText().getText().toString();


            Bitmap front = ((BitmapDrawable)((ImageView) ((LinearLayout) ((LinearLayout)((LinearLayout)(chequeList.get(i).getChildAt(3))).getChildAt(1)).getChildAt(0)).getChildAt(0)).getDrawable()).getBitmap();
           Bitmap Back = ((BitmapDrawable)((ImageView) ((LinearLayout) ((LinearLayout)((LinearLayout)(chequeList.get(i).getChildAt(3))).getChildAt(1)).getChildAt(1)).getChildAt(0)).getDrawable()).getBitmap();

            String KEY_IMAGE_FRONT = getStringImage(front);
            String KEY_IMAGE_BACK = getStringImage(Back);
            params.put("REF_NO",mRefNoView.getText().toString());
            params.put("CHE_NO",KEY_NAME);
            params.put("FRONT", KEY_IMAGE_FRONT);
        params.put("BACK", KEY_IMAGE_BACK);
            //params.put(KEY_NAME+"-1", KEY_IMAGE_BACK);




        return params;
    }
    public String getStringImage(Bitmap bmp){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
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

    private void sendDataToServer(){


        JSONArray depositCheckDatas = new JSONArray();
        JSONObject depositData = new JSONObject();
        JSONObject checks = new JSONObject();
        int size = chequeList.size();


            try{
                depositData.put(Constants.ACCOUNT_NO_KEY,accountNo);
                depositData.put(Constants.MOBILE_KEY,mobile);
                depositData.put(Constants.REF_NO_KEY,refNo);
                for (int i =0;i<size;i++){
                    JSONObject tempCheckData = new JSONObject();

                    tempCheckData.put(Constants.AMOUNT_KEY,Double.parseDouble(((TextInputLayout)(chequeList.get(i).getChildAt(2))).getEditText().getText().toString().replaceAll("[$, LKR]", "")) );

                    tempCheckData.put(Constants.CHECK_NO_KEY,((TextInputLayout)(chequeList.get(i).getChildAt(1))).getEditText().getText());


                    depositCheckDatas.put(tempCheckData);


                }
                checks.put(Constants.CHECK_INIT_KEY,depositData);
                checks.put(Constants.CHECKS,depositCheckDatas);
                }catch (JSONException e){
                Log.e("cash_deposit","jason error: "+e);
            }



        ServerCommunicationIntentService.sendPostRequest(this, checks.toString(),Constants.SERVER_URL+Constants.CHEQUE_DEPOSIT_ROUTE);


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
        //amount =  mAmountView.getText().toString();
        mobile = mMobileView.getText().toString();
        refNo = mRefNoView.getText().toString();
        //checkNo = mCheckNoView.getText().toString();
        // Reset errors.
        mAccountView.setError(null);
       // mAmountView.setError(null);
        mMobileView.setError(null);
        mRefNoView.setError(null);
        //mCheckNoView.setError(null);
        // Reset errors.
        mAccountView.setError(null);
        //mAmountView.setError(null);
        mMobileView.setError(null);
        mRefNoView.setError(null);




        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(accountNo)) {
            mAccountView.setError(getString(R.string.accountNo_empty));
            focusView = mAccountView;
            cancel = true;

        }

        else if (TextUtils.isEmpty(mobile)) {
            mMobileView.setError(getString(R.string.mobile_empty));
            focusView = mMobileView;
            cancel = true;

        }

        int size = chequeList.size();
        for(int i = 0; i<size; i++){
            mAmountView = ((TextInputLayout)(chequeList.get(i).getChildAt(2))).getEditText();
            mCheckNoView = ((TextInputLayout)(chequeList.get(i).getChildAt(1))).getEditText();
            amount = mAmountView.getText().toString();
           // Log.d("amount value",amount);
            checkNo = mCheckNoView.getText().toString();
            mCheckNoView.setError(null);
            mAmountView.setError(null);
            if (TextUtils.isEmpty(amount)) {
                mAmountView.setError(getString(R.string.amount_empty));
                focusView = mAmountView;
                cancel = true;
               // Log.d("amout val",amount);
            }
            else if(TextUtils.isEmpty(checkNo)){
                mCheckNoView.setError("Please Enter Check Number");
                focusView = mCheckNoView;
                cancel = true;
            }

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

                    //showTransactionCompleteDialog();

                    printReceipt();
                    showTransactionCompleteDialog();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    };

    private void showTransactionCompleteDialog(){
        new AlertDialog.Builder(this)
                .setTitle("Cheque Deposit Successful")
                .setMessage(amount+" LKR has been successfully deposited to Account "+accountNo+"\n" +
                        "\n"
                        )
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }

        return  super.onOptionsItemSelected(item);
    }

    private void initializeChequeDepositFrom(){

     LinearLayout cheque = (LinearLayout)findViewById(R.id.cheque);
       /*  mAccountView = (EditText)findViewById(R.id.accountNo);
        mAmountView =(EditText)findViewById(R.id.amount);
        mMobileView = (EditText)findViewById(R.id.mobile);
        mRefNoView = (EditText)findViewById(R.id.refNo);
        mCheckNoView = (EditText)findViewById(R.id.checkNo);

        mFrontImageView=(ImageView) findViewById(R.id.chequeImage);
        mBackImageView=(ImageView) findViewById(R.id.chequeImageBack);
        //accountNumberDropDown = (Spinner) findViewById(R.id.accountsSpinner);
        //accountNameText = (EditText)findViewById(R.id.accountNameText);
        //accountText = (EditText)findViewById(R.id.accountNumberText);
        //final ArrayList<Account> accounts = Account.getAccounts(this);
        /*final List<String> list2 = new ArrayList<String>();
        list2.add("Registered Accounts");
        for(Account account:accounts){
            list2.add(account.accountNumber);
        }

        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, list2);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountNumberDropDown.setAdapter(dataAdapter2);
        */
        addCheque(cheque);
        /*
        accountText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String accountNumber = accountText.getText().toString();
                if(!list2.contains(accountNumber)){
                    if(accountNumber.length()>=8){
                        accountText.setEnabled(false);

                        showProgress(true);
                        JSONObject data = new JSONObject();


                        try{
                            data.put(Constants.ACCOUNT_NO_KEY,accountNumber);

                        }catch (JSONException e){
                            Log.e("cash_deposit","jason error: "+e);
                        }
                        LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(
                                mMessageReceiver,
                                mStatusIntentFilter);
                        ServerCommunicationIntentService.sendPostRequest(getBaseContext(),data.toString(),Constants.SERVER_URL+Constants.FIND_ACCOUNT_NAME_ROUTE);

                    }

                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        accountNumberDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {

                int item = accountNumberDropDown.getSelectedItemPosition();
                if(item!=0) {//because placeholder is the first item
                    item-=1;
                    accountNumber = accounts.get(item).accountNumber;
                    accountText.setText(accountNumber);
                    accountName = accounts.get(item).accountHolderName;

                    accountNameText.setText(accountName);
                    accountNumberDropDown.setSelection(0);
                }

            }
            public void onNothingSelected(AdapterView<?> arg0) { }
        });

        createButton = (Button)findViewById(R.id.createButton);
        createButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                if(accountText.getText().toString()!="") {
                    accountName = accountNameText.getText().toString();
                    accountNumber = accountText.getText().toString();
                    JSONObject dataList = new JSONObject();
                    int count=0;
                    for(LinearLayout cheque:chequeList){
                        count++;
                        JSONObject data = new JSONObject();
                        Log.d("cheque",((EditText)cheque.getChildAt(1)).getText().toString());
                        try {
                            if(!(((EditText)cheque.getChildAt(4)).getText().toString().equals(""))){
                                double value = Double.parseDouble(((EditText)cheque.getChildAt(4)).getText().toString().replaceAll("[$, LKR]", ""));
                                amount+=value;
                                data.put("Value",value);
                                data.put("Bank",((EditText)cheque.getChildAt(1)).getText().toString());
                                data.put("ChequeNumber",((EditText)cheque.getChildAt(3)).getText().toString());
                                data.put("Branch",((EditText)cheque.getChildAt(2)).getText().toString());

                                ImageView imageView = ((ImageView)cheque.getChildAt(5));
                                imageView.buildDrawingCache();
                                Bitmap bm = imageView.getDrawingCache();


                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                                byte[] b = baos.toByteArray();


                                String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

                                data.put("Image",encodedImage);

                                dataList.put(String.valueOf(count),data);
                            }
                            else{
                                showErrorDialog("Cheque Details are Incomplete","Please fill details of cheque.");
                                break;

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }


                    description=dataList.toString();

                    sendDataToServer();

                }
                else {
                    showErrorDialog("No Account is Selected","Please enter the account number or choose from registered account list");
                }


            }
        });
        */

    }
    int chequeCount=1;
    private void addCheque(final LinearLayout chequeContent){

        final int index = chequeCount;
        final CardView card = new CardView(this);
        LinearLayout cheque =new LinearLayout(this);

        cheque.setOrientation(LinearLayout.VERTICAL);
//      cheque.setId(chequeCount);
        final TextView chequeIndex = new TextView(this);
        chequeIndex.setText( "Cheque "+String.valueOf(chequeCount));
        chequeCount++;
        cheque.addView(chequeIndex);

        // add delete button

        //checkno

        TextInputLayout checkly = new TextInputLayout(this);
        checkly.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));

        EditText chechno = new EditText(this);
        //chechno.setId(R.id.checkNo);
        chechno.setHint("cheque no");
        //chechno.setImeActionLabel("@string/action_sign_in_short",R.id.checkNo);
        chechno.setImeOptions(IME_ACTION_UNSPECIFIED);
        chechno.setInputType(InputType.TYPE_CLASS_NUMBER);
        chechno.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        chechno.setMaxLines(1);
        chechno.setFilters(new InputFilter[] {new InputFilter.LengthFilter(6)});
        checkly.addView(chechno);
        cheque.addView(checkly);



        TextInputLayout amountly = new TextInputLayout(this);
        amountly.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));

        final EditText amount = new EditText(this);
        amount.setHint("amount");
        amount.setImeOptions(IME_ACTION_UNSPECIFIED);
        amount.setInputType(InputType.TYPE_CLASS_NUMBER);
        amount.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        //amount.setMaxLines(1);

        amount.addTextChangedListener(new TextWatcher(){
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
                    amount.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[^\\d]", "");

                    double parsed = Double.parseDouble(cleanString);
                    String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

                    formatted=formatted.replaceAll("[^\\d.,]", "")+" LKR";
                    current = formatted;
                    amount.setText(formatted);
                    amount.setSelection(formatted.length()-4);

                    amount.addTextChangedListener(this);
                }
            }
        });

        //amount.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2)});
        amountly.addView(amount);
        cheque.addView(amountly);


        LinearLayout photely = new LinearLayout(this);
        photely.setOrientation(LinearLayout.VERTICAL);
        photely.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView photoOfCheck = new TextView(this);
        photoOfCheck.setText("Photos of cheque");
        photoOfCheck.setTextColor(getResources().getColor(R.color.colorAccent));
        photoOfCheck.setTextIsSelectable(false);
        photoOfCheck.setPadding(0,10,0,5);
        photoOfCheck.setTextSize(TypedValue.COMPLEX_UNIT_PX,18);
        photoOfCheck.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));




        LinearLayout photohorz = new LinearLayout(this);
        photohorz.setOrientation(LinearLayout.HORIZONTAL);
        photohorz.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));

        //RelativeLayout photoR = new RelativeLayout(this);


        LinearLayout photovertleft = new LinearLayout(this);
        photovertleft.setOrientation(LinearLayout.VERTICAL);
        photovertleft.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT,0.5f));



        final ImageView mFrontImage = new ImageView(this);
        mFrontImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,80));
        //photolef.setId(R.id.chequeImage);
        /*mFrontImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureFront(v);
            }
        });
*/
        photovertleft.addView(mFrontImage);

        Button button_front = new Button(this);
        button_front.setText("Front");
        button_front.setTextColor(Color.parseColor("#ffffff"));
        button_front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureFront(v);
                mFrontImageView = mFrontImage;
            }
        });
        photovertleft.addView(button_front);

        LinearLayout photovertright = new LinearLayout(this);
        photovertright.setOrientation(LinearLayout.VERTICAL);
        photovertright.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT,0.5f));



        final ImageView mBackImage = new ImageView(this);
        mBackImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,80));
        //photoriht.setId(R.id.chequeImageBack);
        /*mBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureBack(v);
            }
        });*/
        photovertright.addView(mBackImage);

        Button button_back = new Button(this);
        button_back.setText("Back");
        button_back.setTextColor(Color.parseColor("#ffffff"));
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureBack(v);
                mBackImageView = mBackImage;
            }
        });
        photovertright.addView(button_back);
        //photoR.addView(photovertleft);
      //photoR.addView(photovertright);
        photohorz.addView(photovertleft);
        photohorz.addView(photovertright);
        //photohorz.addView(photovertleft);
       // photohorz.addView(photoriht);

        photely.addView(photoOfCheck);
        photely.addView(photohorz);

        cheque.addView(photely);
        Button delete = new Button(this);
        delete.setText("Delete");
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card.removeAllViews();
                getRemoveCheck(chequeIndex);

            }
        });
        cheque.addView(delete);

        View line = new View(this);
        ViewGroup.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        param.height = 2;
        line.setLayoutParams(param);
        line.setBackgroundColor(Color.parseColor("#000000"));
        cheque.addView(line);

        /*
        EditText bank = new EditText(this);
        bank.setHint("Bank");

        cheque.addView(bank);

        EditText branchCode = new EditText(this);
        branchCode.setHint("Branch Code");
        branchCode.setInputType(InputType.TYPE_CLASS_NUMBER);
        cheque.addView(branchCode);

        EditText chequeNumber= new EditText(this);
        chequeNumber.setHint("Cheque Number");
        chequeNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
        cheque.addView(chequeNumber);*/
/*
        final EditText value = new EditText(this);
        value.setHint("Value");
        value.setInputType(InputType.TYPE_CLASS_NUMBER);
        value.addTextChangedListener(new TextWatcher(){
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
                    value.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[$,. LKR]", "");

                    double parsed = Double.parseDouble(cleanString);
                    String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

                    formatted=formatted.replaceAll("[$]", "")+" LKR";
                    current = formatted;
                    value.setText(formatted);
                    value.setSelection(formatted.length());

                    value.addTextChangedListener(this);
                }
            }
        });
        cheque.addView(value);*/

        /*mFrontImageView = new ImageView(this);

        Button photoButton = new Button(this);
        photoButton.setText("Photo");
        photoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        cheque.addView(mFrontImageView);
        cheque.addView(photoButton);



        final Button addButton = new Button(this);
        addButton.setText("Add Another Cheque");
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addCheque(chequeContent);
                chequeContent.removeView(addButton);
            }
        });*/
        card.setContentPadding(15, 15, 15, 15);
        card.addView(cheque);
        chequeContent.addView(card);
        //chequeContent.addView(addButton);

        chequeList.add(cheque);

    }
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void   printReceipt(){
        SharedPreferences prefs = getSharedPreferences(Constants.PERSONAL_KEY, MODE_PRIVATE);

        String name = prefs.getString(Constants.NAME_KEY,"no_user");//"No name defined" is the default value.
        String mobile = prefs.getString(Constants.MOBILE_KEY,"000000000");
        double amount =0.0;
        int size = chequeList.size();
        StringBuilder checks = new StringBuilder("\n");
        for(int i = 0; i<size; i++) {
            mCheckNoView = ((TextInputLayout) (chequeList.get(i).getChildAt(1))).getEditText();
            checks.append("\nCheck No :"+mCheckNoView.getText().toString());
            mAmountView = ((TextInputLayout) (chequeList.get(i).getChildAt(2))).getEditText();
            checks.append("\nAmount : "+mAmountView.getText().toString()+"\n");
        }
        ICallback callback = null;
        WoyouPrinter woyouPrinter = WoyouPrinter.getInstance();
        woyouPrinter.initPrinter(getApplicationContext());

        woyouPrinter.print("\nTransaction Type : Cheque Deposit  \nAccount No: "+this.accountNo+""+checks.toString()+" \nMobile No : "+this.mobile+"\nReference No : "+this.refNo+"\ncollector :"+name+"("+mobile+")"+"\n\nCheque deposit and collections "+"\nare subject to realize and for"+"\nany clarification contact\n" +
                "Samaraweera\n" +
                "(071 589 4578/ ID: 148458) ",callback);


        Log.d("printcall:",""+amount);

    }
    public int getRemoveCheck(TextView chequeIndex){
        boolean find = false;
        int size = chequeList.size();
        int index = 0;
        for(int i = 0; i<size;i++){
            if(((TextView)(chequeList.get(i).getChildAt(0))).getText().toString().equals(chequeIndex.getText().toString())){

                index =i ;
                find = true;
            }
            if(find){
                ((TextView)(chequeList.get(i).getChildAt(0))).setText( "Cheque "+String.valueOf(i));

            }


        }
        chequeList.remove(index);
        chequeCount = size;
        return 0;
    }
    class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;

        public  DecimalDigitsInputFilter(int digitsAfterZero) {
            mPattern=Pattern.compile("[0-9]+((\\.[0-9]{0," + (digitsAfterZero-1) + "})?)||(\\.)?");
        }


        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher=mPattern.matcher(dest);
            if(!matcher.matches())
                return "";
            return null;
        }

    }
    class mobileFilter implements InputFilter {

        Pattern mPattern;


        public mobileFilter(){

            mPattern = Pattern.compile("^[0]");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher=mPattern.matcher(dest);
            if(!matcher.matches())
                return "";
            return null;
        }

    }

}
