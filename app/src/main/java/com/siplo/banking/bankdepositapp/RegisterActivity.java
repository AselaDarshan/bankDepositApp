package com.siplo.banking.bankdepositapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private EditText mNameView;
    private EditText mAccountNoView;
    private EditText mMobileView;
    private EditText mEmailView;

    private String name;
    private String accountNo;
    private String mobile;
    private String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAccountNoView = (EditText)findViewById(R.id.accountNo);
        mNameView =(EditText)findViewById(R.id.name);
        mMobileView = (EditText)findViewById(R.id.mobile);
        mEmailView = (EditText)findViewById(R.id.email);


        // The filter's action is BROADCAST_ACTION
        IntentFilter mStatusIntentFilter = new IntentFilter(
                Constants.BROADCAST_ACTION);
        // Registers the messageReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver,
                mStatusIntentFilter);


    }
    public void register(View view){

        name = mNameView.getText().toString();
        accountNo = mAccountNoView.getText().toString();
        mobile = mMobileView.getText().toString();
        email = mEmailView.getText().toString();

        sendDataToServer();
        //saveRegisteredState();

//        Intent intent = new Intent(this, HomeActivity.class);
//        startActivity(intent);
//        finish();
    }
    private void sendDataToServer(){
        JSONObject registrationData = new JSONObject();
        try{
            registrationData.put(Constants.ACCOUNT_NO_KEY,accountNo);
            registrationData.put(Constants.NAME_KEY,name);
            registrationData.put(Constants.MOBILE_KEY,mobile);
            registrationData.put(Constants.EMAIL_KEY,email);
        }catch (JSONException e){
            Log.e("registration","jason error: "+e);
        }

        ServerCommunicationIntentService.sendPostRequest(this, registrationData.toString(),Constants.SERVER_URL+Constants.REGISTER_ROUTE);
    }

    private void registrationSucceed(){
        Parameters.currentUser = name;
        saveRegisteredState();
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
    private void saveRegisteredState(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isRegistered",true);
        editor.apply();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(!isFinishing()) {
                String response = intent.getStringExtra(Constants.EXTENDED_DATA_STATUS);
                try {
                    JSONObject jObject = new JSONObject(response);
                   // showTransactionCompleteDialog(jObject.getString(Constants.REF_NO_KEY));
                    if(jObject.getBoolean("success")){
                        registrationSucceed();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    };


}
