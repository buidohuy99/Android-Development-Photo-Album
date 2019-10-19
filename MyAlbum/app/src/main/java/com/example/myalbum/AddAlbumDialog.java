package com.example.myalbum;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class AddAlbumDialog {

    private String title;
    private HomeActivity activity;
    private AlertDialog dialog = null;
    private View DialogView = null;

    //New Instance
    public static AddAlbumDialog newInstance(HomeActivity activity, String title){
        AddAlbumDialog myDialog = new AddAlbumDialog();
        myDialog.activity = activity;
        myDialog.title = title;

        return myDialog;
    }

    public void show() {
        if(dialog == null) {
            //Make View
            DialogView = new EditText(activity);
            //Make dialog
            dialog = new AlertDialog.Builder(activity)
                    .setTitle(title)
                    .setView(DialogView)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Bundle bd = new Bundle();
                            bd.putString("albumName", ((EditText) DialogView).getText().toString());
                            ((EditText) DialogView).setText("");
                            activity.onMessageToActivity("Add Album Dialog", bd);
                        }
                    })
                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((EditText) DialogView).setText("");
                            dialog.dismiss();
                        }
                    })
                    .create();
        }
       dialog.show();
    }
}