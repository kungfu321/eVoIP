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
import com.csipsimple.utils.Has256;

import java.security.SignatureException;

/**
 * Created by Kien Shinichi on 9/29/2016.
 */

public class CustomDialogPassword extends Dialog {
    public Activity activity;

    public CustomDialogPassword(Activity a) {
        super(a);
        activity = a;
    }

    public void showDialog(){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_password);
        Button dialogButton = (Button) dialog.findViewById(R.id.btn_login);

        final TextView txt_password = (TextView) dialog.findViewById(R.id.txt_password);
        txt_password.setText("");
        final TextView txt_confirm = (TextView) dialog.findViewById(R.id.txt_confirm);
        txt_confirm.setText("");

        if (getPassword() == null) {
            txt_confirm.setVisibility(View.VISIBLE);
            dialogButton.setText("Đặt mật khẩu");
        }
        final TextView txt_error = (TextView) dialog.findViewById(R.id.txt_error);

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt_password.getText().toString().equals(getPassword())) {
                    dialog.dismiss();
                } else {
                    txt_password.setText("");
                    txt_error.setVisibility(View.VISIBLE);
                    txt_error.setText("Mật khẩu không đúng, vui lòng thử lại.");
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
