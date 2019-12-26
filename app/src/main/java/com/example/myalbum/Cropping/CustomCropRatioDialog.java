package com.example.myalbum.Cropping;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myalbum.AlbumsActivity.HomeActivity;
import com.example.myalbum.R;

public class CustomCropRatioDialog {
    private AlertDialog dialog = null;
    private int oldWidth;
    private int oldHeight;
    View DialogView;
    EditText height;
    EditText width;
    CroppingActivity main;
    public static CustomCropRatioDialog newInstance(CroppingActivity activity, int oldWidth, int oldHeight)
    {
        CustomCropRatioDialog CustomDialog = new CustomCropRatioDialog();
        CustomDialog.oldWidth = oldWidth;
        CustomDialog.oldHeight = oldHeight;
        CustomDialog.main = activity;
        return CustomDialog;
    }

    public void show()
    {
        if (dialog == null)
        {
            DialogView = main.getLayoutInflater().inflate(R.layout.crop_custom_ratio_dialog_layout, null);
            width = DialogView.findViewById(R.id.widthEditText);
            width.setText(Integer.toString(oldWidth));
            height = DialogView.findViewById(R.id.heightEditText);
            height.setText(Integer.toString(oldHeight));

            dialog = new AlertDialog.Builder(main)
                    .setView(DialogView)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Bundle bundle =new Bundle();

                            int newWidth = Integer.parseInt(width.getText().toString());
                            int newHeight = Integer.parseInt(height.getText().toString());

                            bundle.putInt("width", newWidth);
                            bundle.putInt("height", newHeight);
                            if (newHeight <=0 || newWidth <=0)
                            {
                                Toast.makeText(main,"Input value wrong, width or height must be positive",Toast.LENGTH_LONG).show();
                                dialogInterface.dismiss();
                                return;
                            }
                            main.onMessageToActivity("custom ratio",bundle);

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create();
        }
        dialog.show();
    }

}
