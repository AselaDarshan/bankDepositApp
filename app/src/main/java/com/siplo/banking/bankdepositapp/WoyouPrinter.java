package com.siplo.banking.bankdepositapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;

public class WoyouPrinter {
	
	private static final WoyouPrinter printer = new WoyouPrinter();
	private IWoyouService woyouService = null;
	
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

	private WoyouPrinter(){}
	
	
	public static WoyouPrinter getInstance() {
		return printer;
	}
	
	public void initPrinter(Context context){
		Intent intent=new Intent();
		intent.setPackage("woyou.aidlservice.jiuiv5");
		intent.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
		context.startService(intent);
		context.bindService(intent, connService, Context.BIND_AUTO_CREATE);		
	}

	public IWoyouService getWoyouService(){
		return this.woyouService;
	}
	
	public void print(final String msg, final ICallback callback){
		if(woyouService != null){
			ThreadPoolManager.getInstance().executeTask(new Runnable() {
				@Override
				public void run() {
					try {
						woyouService.printTextWithFont("Amana Bank\n", "", 50, callback);
						woyouService.lineWrap(2, callback);
//						woyouService.printTextWithFont(msg, "", 24, callback);

						for(int i=0; i<4; i++){
							woyouService.printTextWithFont(msg, "", 24, callback);
						}
//						int[] width = new int[] { 10, 6, 6, 8 };
//						int[] align = new int[] { 0, 2, 2, 2 };
//						String[] text = new String[4];
//						woyouService.printColumnsText(text, width, new int[] { 1, 2, 2, 2 }, callback);
						woyouService.lineWrap(4, callback);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
}
