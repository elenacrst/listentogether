package com.elena.listentogether.ui.custom.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.AppCompatEditText;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.elena.listentogether.R;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class SearchDialog {

    private Dialog dialog;

    public void showDialog(Activity activity, SearchCallback searchCallback, String hint){
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow()
                .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.dialog_search);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.TOP;
        wlp.width = MATCH_PARENT;
        window.setAttributes(wlp);
        AppCompatEditText editText = dialog.findViewById(R.id.edit_search);
        editText.setHint(hint);
        dialog.findViewById(R.id.btn_dialog_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText() != null){
                    String searchQuery = editText.getText().toString();
                    searchCallback.onSearch(searchQuery);
                }

            }
        });
        dialog.show();
    }

    public void dismiss(){
        if (dialog != null){
            dialog.dismiss();
        }
    }

}