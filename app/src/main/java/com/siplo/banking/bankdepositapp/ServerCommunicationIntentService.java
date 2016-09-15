package com.siplo.banking.bankdepositapp;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.provider.SyncStateContract;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ServerCommunicationIntentService extends IntentService {



    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_POST = "com.siplo.banking.bankdepositapp.action.POST";
    private static final String ACTION_FOO = "com.siplo.banking.bankdepositapp.action.FOO";
    private static final String ACTION_BAZ = "com.siplo.banking.bankdepositapp.action.BAZ";

    // TODO: Rename parameters
    private static final String PARAM_DATA = "com.siplo.banking.bankdepositapp.extra.DATA";
    private static final String PARAM_URL = "com.siplo.banking.bankdepositapp.extra.URL";
    private static final String EXTRA_PARAM1 = "com.siplo.banking.bankdepositapp.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.siplo.banking.bankdepositapp.extra.PARAM2";



    public ServerCommunicationIntentService() {
        super("ServerCommunicationIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */

    public static void sendPostRequest(Context context, String data,String url) {
        Intent intent = new Intent(context, ServerCommunicationIntentService.class);
        intent.setAction(ACTION_POST);
        intent.putExtra(PARAM_DATA, data);
        intent.putExtra(PARAM_URL,url);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("communication_service","onHandelCalled");
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_POST.equals(action)) {
                handleActionHttpPostRequest(intent.getStringExtra(PARAM_DATA),intent.getStringExtra(PARAM_URL));
            }

        }
    }
    private void handleActionHttpPostRequest(final String data,String url) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                Log.d("communication_service","http response: "+response);
                broadcast(response);
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                Log.d("communication_service","http error: "+error.toString());
                broadcast(Constants.COMMUNICATION_ERROR);
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("Data", data); //Add the data you'd like to send to the server.
                return MyData;
            }
        };



    // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    private void broadcast(String status ){
         /*
     * Creates a new Intent containing a Uri object
     * BROADCAST_ACTION is a custom Intent action
     */
        Intent localIntent =
                new Intent(Constants.BROADCAST_ACTION)
                        // Puts the status into the Intent
                        .putExtra(Constants.EXTENDED_DATA_STATUS, status);
        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

    }

}

