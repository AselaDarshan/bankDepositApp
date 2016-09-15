package com.siplo.banking.bankdepositapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
    public void loadCashDeposit(View view){
        Intent intent = new Intent(this, cashDepositActivity.class);
        startActivity(intent);
    }
    public void loadChequeDeposit(View view){
        Intent intent = new Intent(this, ChequeDepositActivity.class);
        startActivity(intent);
    }
}
