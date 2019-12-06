package com.example.myalbum.AlbumsActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.myalbum.R;
import com.example.myalbum.utilities.UtilityGlobals;

public class AddAlbumDialog {

    private String title;
    private HomeActivity activity;
    private AlertDialog dialog = null;
    private AlertDialog alertError = null;
    private View DialogView = null;

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
            final EditText albumNameField = DialogView.findViewById(R.id.albumNameField);
            //Make dialog
            dialog = new AlertDialog.Builder(activity)
                    .setTitle(title)
                    .setView(DialogView)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(albumNameField.getText().toString().replaceAll("\\ ","").equals("")) {
                                dialog.dismiss();
                                alertError.show();
                                return;
                            }
                            Bundle bd = new Bundle();
                            bd.putString("albumName", albumNameField.getText().toString());
                            albumNameField.setText("");
                            activity.onMessageToActivity(UtilityGlobals.ADD_ALBUM_DIALOG, bd);
                        }
                    })
                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            albumNameField.setText("");
                            dialog.dismiss();
                        }
                    })
                    .create();
        }
        dialog.show();
    }
}