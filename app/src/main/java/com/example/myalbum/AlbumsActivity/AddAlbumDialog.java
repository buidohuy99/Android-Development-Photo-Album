package com.example.myalbum.AlbumsActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.example.myalbum.R;
import com.example.myalbum.utilities.BCrypt;
import com.example.myalbum.utilities.UtilityGlobals;

public class AddAlbumDialog {

    private String title;
    private HomeActivity activity;
    private AlertDialog dialog = null;
    private AlertDialog alertError = null;
    private View DialogView = null;
    private boolean usePassword = false;

    private EditText albumNameField ;
    private EditText albumPasswordField;
    private View albumPasswordArea;
    private CheckBox albumPasswordCheckbox;

    private AddAlbumDialog() {

    }

    //New Instance
    public static AddAlbumDialog newInstance(HomeActivity activity, String title){
        AddAlbumDialog myDialog = new AddAlbumDialog();
        myDialog.activity = activity;
        myDialog.title = title;
        myDialog.alertError = new AlertDialog.Builder(activity)
                .setTitle("Huhu có lỗi rồi")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        return myDialog;
    }

    public void show() {
        if(dialog == null) {
            //Make View
            DialogView = activity.getLayoutInflater().inflate(R.layout.addalbumdialog_layout, null);
            albumNameField = DialogView.findViewById(R.id.albumNameField);
            albumPasswordField = DialogView.findViewById(R.id.albumPasswordField);
            albumPasswordArea = DialogView.findViewById(R.id.albumPasswordArea);
            albumPasswordCheckbox = DialogView.findViewById(R.id.albumPasswordCheck);

            //Set password checkbox
            albumPasswordCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    albumPasswordArea.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                }
            });

            //Make dialog
            dialog = new AlertDialog.Builder(activity)
                    .setTitle(title)
                    .setView(DialogView)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(
                            albumNameField.getText().toString().replaceAll("\\ ","").equals("")
                                    ||
                            (albumPasswordArea.getVisibility() == View.VISIBLE &&
                            albumPasswordField.getText().toString().replaceAll("\\ ", "").equals(""))
                            ){
                                dialog.dismiss();
                                alertError.show();
                                return;
                            }
                            if(albumPasswordArea.getVisibility() == View.VISIBLE) usePassword = true;
                            else usePassword = false;
                            //Execute thread to hash passw
                            (new HashPassword()).execute(albumPasswordField.getText().toString(), albumNameField.getText().toString());
                        }
                    })
                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            albumNameField.setText("");
                            albumPasswordField.setText("");
                            albumPasswordCheckbox.setChecked(false);
                            dialog.dismiss();
                        }
                    })
                    .create();
        }
        dialog.show();
    }

    private class HashPassword extends AsyncTask<String, Void, String[]>{

        @Override
        protected String[] doInBackground(String... strings) {
            String password = strings[0];
            String albumName = strings[1];

            String generatedHash = BCrypt.hashpw(password, BCrypt.gensalt());

            return new String[]{generatedHash, albumName};
        }

        @Override
        protected void onPostExecute(String[] s) {
            super.onPostExecute(s);
            Bundle bd = new Bundle();
            bd.putString("albumName", s[1]);
            bd.putString("albumPassword", !usePassword ? null : s[0]);
            albumNameField.setText("");
            albumPasswordField.setText("");
            albumPasswordCheckbox.setChecked(false);
            activity.onMessageToActivity(UtilityGlobals.ADD_ALBUM_DIALOG, bd);
        }
    }
}