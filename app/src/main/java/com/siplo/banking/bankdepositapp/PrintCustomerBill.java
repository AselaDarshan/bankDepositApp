package com.siplo.banking.bankdepositapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import woyou.aidlservice.jiuiv5.ICallback;

public class PrintCustomerBill extends AppCompatActivity {
    private  String refNo;
    private String amount;
    private String accountNo;
    private String nic;
    private String mobile;
    private  String numOfCheque;
    private  String bankCode;
    private String narr;
    private  boolean cashTransaction;
    Button print_bill_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_customer_bill);
        final Intent intent = getIntent();
        amount = intent.getStringExtra(Constants.AMOUNT_KEY);
        nic = intent.getStringExtra(Constants.NIC_KEY);
        mobile = intent.getStringExtra(Constants.MOBILE_KEY);
        refNo = intent.getStringExtra(Constants.REF_NO_KEY);
        accountNo = intent.getStringExtra(Constants.ACCOUNT_NO_KEY);
        narr = intent.getStringExtra(Constants.NARR_KEY);
        Log.d(Constants.AMOUNT_KEY,amount);
        print_bill_btn = (Button) findViewById(R.id.print_button);
        print_bill_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cashTransaction){
                    printReceiptCash();
                }
                else {
                    bankCode = intent.getStringExtra(Constants.BANK_CODE_KEY);
                    numOfCheque = intent.getStringExtra(Constants.CHEQUES_KEY);
                    printReceiptCheques();
                }
                Intent intent1 = new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(intent1);
            }
        });


    }
    private void   printReceiptCash(){
        ICallback callback = null;
        SharedPreferences prefs = getSharedPreferences(Constants.PERSONAL_KEY, MODE_PRIVATE);
        String name = prefs.getString(Constants.NAME_KEY,"no_user");//"No name defined" is the default value.
        String mobile = prefs.getString(Constants.MOBILE_KEY,"000000000");
        WoyouPrinter woyouPrinter = WoyouPrinter.getInstance();
        woyouPrinter.initPrinter(getApplicationContext());
        woyouPrinter.print("\nTrans : Cash Deposit"+
                "\nReference No : "+this.refNo+
                " \nAmount : "+this.amount+"  LKR "+
                "\nNarration: "+this.narr+
                "\n"+

                "\nAccount No: "+this.accountNo+

                "\nNIC: "+nic+
                " \nMobile No : "+this.mobile+
                "\n"+

                "\nCollector :"+name+"("+mobile+")"+"\n\nCheque deposit and collections "+"\nare subject to realize and for"+"\nany clarification contact\n" +
                "Samara weera\n" +
                "(071 589 4578/ ID: 148458)\n\n "+
                "............\n"+
                "    sign",callback);

    }
    private void   printReceiptCheques(){
        SharedPreferences prefs = getSharedPreferences(Constants.PERSONAL_KEY, MODE_PRIVATE);

        String name = prefs.getString(Constants.NAME_KEY,"no_user");//"No name defined" is the default value.
        String mobile = prefs.getString(Constants.MOBILE_KEY,"000000000");
        ICallback callback = null;
        WoyouPrinter woyouPrinter = WoyouPrinter.getInstance();
        woyouPrinter.initPrinter(getApplicationContext());

        woyouPrinter.print(
                "\nTrans : Cheque Deposit "+
                        "\nReference No : "+this.refNo+
                        "\nNumber Of Cheques :"+numOfCheque+
                        "\nAmount of Cheques :"+amount+" LKR"+
                        "\nNarration: "+this.narr+
                        "\n"+
                        " \nAccount No: "+this.accountNo+
                        "\nBank Code: "+bankCode+
                        "\nNIC: "+nic+
                        " \nMobile No : "+this.mobile+
                        "\n"+


                        "\nCollector :"+name+"("+mobile+")"+"\n\nCheque deposit and collections "+"\nare subject to realize and for"+"\nany clarification contact\n" +
                        "Samara weera\n" +
                        "(071 589 4578/ ID: 148458)\n "+
                        "............"+
                        "\n   sign ",callback);


        Log.d("printcall:",""+amount);

    }
}
