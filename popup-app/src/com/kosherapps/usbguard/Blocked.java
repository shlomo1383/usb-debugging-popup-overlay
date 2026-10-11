package com.kosherapps.usbguard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;

public class Blocked extends Activity {

    private static final String TITLE = "Allow USB debugging?";
    private static final String FINGERPRINT = "7B:E2:4C:91:08:D5:3A:6F:B7:1E:C4:92:5D:A8:30:E6";
    private static final String MESSAGE =
            "The computer's RSA key fingerprint is:\n" + FINGERPRINT;
    private static final String DISABLED_LABEL = "Always allow from this computer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        // Build the checkbox using the SAME themed context the dialog uses,
        // not the activity's own (translucent) theme -- otherwise its label
        // text inherits the wrong color and can render invisible, same as
        // the original "Always allow from this computer" row does.
        ContextThemeWrapper dialogCtx = new ContextThemeWrapper(this,
                android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);

        // Plain default CheckBox, no custom icon, no margin offset.
        // Position is left exactly as Android places it by default --
        // fine-tune this value directly once the project is in a repo
        // with a proper layout preview instead of guessing through
        // screenshots round-trip.
        CheckBox box = new CheckBox(dialogCtx);
        box.setText(DISABLED_LABEL);
        box.setChecked(false);
        // fully normal/checkable -- only the Allow button is inert

        final AlertDialog dialog = new AlertDialog.Builder(dialogCtx)
                .setTitle(TITLE)
                .setMessage(MESSAGE)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Allow", null)
                .create();
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        dialog.setView(box, pad, 0, pad, 0);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d2) {
                Button allow = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                allow.setEnabled(false);
                allow.setClickable(false);
                allow.setAlpha(0.4f);
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface d3) {
                finish();
            }
        });
        dialog.show();
    }
}
