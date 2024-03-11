package com.bt.busticket.ui.helper;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import com.bt.busticket.R;

public class CustomLoadingDialog {
    private final Dialog dialog;

    public CustomLoadingDialog(Context mContext) {
        var inflater = LayoutInflater.from(mContext);
        var view = inflater.inflate(R.layout.loading_view, null);

        dialog = new Dialog(mContext);
        dialog.setContentView(view);
        dialog.setCancelable(false);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        if (dialog.isShowing())
            dialog.dismiss();
    }
}
