package com.csipsimple.ui.account;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.csipsimple.R;
import com.csipsimple.ui.prefs.SessionManager;
import com.csipsimple.utils.Has256;
import com.csipsimple.utils.Log;

import java.security.SignatureException;
import java.util.Objects;

/**
 * Created by Kien Shinichi on 9/29/2016.
 */

public class CustomDialogPassword extends Dialog {
    public Activity activity;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String FINALPASSWORD = "finalPass";
    private SessionManager session;
    private Dialog dialog;

    public CustomDialogPassword(Activity a) {
        super(a);
        activity = a;
    }

    public void showDialog(){
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_password);
        final Button dialogButton = (Button) dialog.findViewById(R.id.btn_login);

        session = new SessionManager(activity);
        sharedPreferences = activity.getSharedPreferences(activity.getPackageName(), 0);
        editor = sharedPreferences.edit();

        final TextView txt_password = (TextView) dialog.findViewById(R.id.txt_password);

        if (sharedPreferences.getString(FINALPASSWORD, null) == null) {
            dialogButton.setText("Đặt mật khẩu");
            txt_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(txt_password.getText().toString().trim() == "") {
                        dialogButton.setVisibility(View.GONE);
                    } else {
                        dialogButton.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        final TextView txt_error = (TextView) dialog.findViewById(R.id.txt_error);

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String pass = null;
                try {
                    pass = Has256.sha256Digest(txt_password.getText().toString());
                } catch (SignatureException e) {
                    e.printStackTrace();
                }

                if (sharedPreferences.getString(FINALPASSWORD, null) == null) {
                    editor.putString(FINALPASSWORD, pass);
                    editor.apply();
                    Log.e(FINALPASSWORD, sharedPreferences.getString(FINALPASSWORD, null));
                }
                if (sharedPreferences.getString(FINALPASSWORD, null) != null) {
                    String finalPass = sharedPreferences.getString(FINALPASSWORD, null);
                    if (Objects.equals(finalPass, pass)) {
                        session.createLoginSession(pass);
//                        dialog.cancel();
//                        dialog.dismiss();
                        dialog.hide();
                        Log.e("FUCK", "true");
                    } else {
                        Log.e("FUCK", "false");
                        txt_password.setText("");
                        txt_error.setVisibility(View.VISIBLE);
                        txt_error.setText("Lỗi, vui lòng thử lại.");
                    }
                }
            }
        });

        dialog.show();

    }

    private void savePassword(String password) {
        String has = null;

        try {
            has = Has256.sha256Digest(password);
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        SharedPreferences settings = activity.getApplicationContext().getSharedPreferences(activity.getPackageName(), 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("password", has);
        editor.apply();
    }

    private String getPassword() {
        String pass = null;
        SharedPreferences settings = activity.getApplicationContext().getSharedPreferences(activity.getPackageName(), 0);
        pass = settings.getString("password", null);

        return pass;
    }
}
