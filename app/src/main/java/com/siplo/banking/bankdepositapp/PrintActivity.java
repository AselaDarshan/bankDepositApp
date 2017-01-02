package com.siplo.banking.bankdepositapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;

public class PrintActivity extends AppCompatActivity {

    private IWoyouService woyouService;
    private ICallback callback = null;
    private ServiceConnection connService = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

            woyouService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            woyouService = IWoyouService.Stub.asInterface(service);
            setButtonEnable(true);
        }
    };

    private Button printButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initPrinting();
        printButton = (Button) findViewById(R.id.printButton);
        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testPrint();
            }
        });
    }

    private void initPrinting(){

        Intent intent=new Intent();
        intent.setPackage("woyou.aidlservice.jiuiv5");
        intent.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
        startService(intent);
        bindService(intent, connService, Context.BIND_AUTO_CREATE);
    }

    private void setButtonEnable(boolean flag){

    }

    private void testPrint(){
        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                try {
                    woyouService.printText("There were  ",callback);
                    for(int i=24; i<=48; i+=6){
                        woyouService.printTextWithFont("Sunmi", "", i, callback);
                    }
                    for(int i=48; i>=12; i-=2){
                        woyouService.printTextWithFont("Sunmi", "", i, callback);
                    }
                    woyouService.lineWrap(1, callback);
                    woyouService.printTextWithFont("ABCDEFGHIJKLMNOPQRSTUVWXYZ01234\n","",30, callback);
                    woyouService.printTextWithFont("abcdefghijklmnopqrstuvwxyz56789\n","",30, callback);
                    woyouService.printText("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789\n", callback);
                    woyouService.printText("abcdefghijklmnopqrstuvwxyz0123456789\n",callback);
                    woyouService.lineWrap(4, callback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
