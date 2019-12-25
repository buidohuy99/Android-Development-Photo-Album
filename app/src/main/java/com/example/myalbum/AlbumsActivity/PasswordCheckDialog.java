package com.example.myalbum.AlbumsActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;

import com.example.myalbum.R;
import com.example.myalbum.utilities.BCrypt;
import com.example.myalbum.utilities.UtilityGlobals;

public class PasswordCheckDialog {

    private String title;
    private Activity activity;
    private AlertDialog dialog = null;
    private View DialogView = null;
    private AlertDialog alertError = null;

    private String comparePW = null;

    private PasswordCheckDialog() {

    }

    //New Instance
    public static PasswordCheckDialog newInstance(Activity activity, String title){
        PasswordCheckDialog myDialog = new PasswordCheckDialog();
        myDialog.activity = activity;
        myDialog.title = title;
        myDialog.alertError = new AlertDialog.Builder(activity)
                .setTitle("Mật khẩu không đúng")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        return myDialog;
    }

    public void setCompare(String truePassword) {
        comparePW = truePassword;
    }

    public void show() {
        if(dialog == null) {
            //Make view
            DialogView = activity.getLayoutInflater().inflate(R.layout.passwordcheck_dialog, null);
            final EditText passwordField = DialogView.findViewById(R.id.passwordCheckField);

            //Make dialog
            dialog = new AlertDialog.Builder(activity)
                    .setTitle(title)
                    .setView(DialogView)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String typed = passwordField.getText().toString();

                            boolean match = BCrypt.checkpw(typed, comparePW);

                            if(!match) {
                                dialog.dismiss();
                                alertError.show();
                                return;
                            }

                            Bundle bd = new Bundle();
                            bd.putBoolean("passwordMatch", match);
                            passwordField.setText("");
                            if(activity.getClass() == HomeActivity.class)
                                ((HomeActivity)activity).onMessageToActivity(UtilityGlobals.PASSWORD_CHECK_DIALOG, bd);
                            else if(activity.getClass() == SearchAlbumActivity.class)
                                ((SearchAlbumActivity)activity).onMessageToActivity(UtilityGlobals.PASSWORD_CHECK_DIALOG, bd);
                        }
                    })
                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
        }
        dialog.show();
    }
}
