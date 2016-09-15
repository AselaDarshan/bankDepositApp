package com.siplo.banking.bankdepositapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by asela on 9/12/16.
 */
public class InformationDialogFragment extends DialogFragment {
    private TextView dialogMessage;
    /* The activity that creates an instance of this dialog fragment must
         * implement this interface in order to receive event callbacks.
         * Each method passes the DialogFragment in case the host needs to query it. */
    public interface InformationDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);

    }

    // Use this instance of the interface to deliver action events
    InformationDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (InformationDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.information_dialog_layout, null);
        dialogMessage = (TextView) view.findViewById(R.id.information_dialog_message);

        dialogMessage.setText("1,200,000 LKR has been successfully deposited to Account 35327\n\nRef No: 45812");

        builder.setTitle("Deposit Successful").setView(view)
                // Add action buttons

                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        mListener.onDialogPositiveClick(InformationDialogFragment.this);

                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}