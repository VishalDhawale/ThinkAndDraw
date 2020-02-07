package com.example.thinkanddraw.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.thinkanddraw.R;
import com.example.thinkanddraw.custom_view.MyFreeDrwable;
import com.example.thinkanddraw.listener.OnImageEditListener;
import com.example.thinkanddraw.util.Util;

import java.io.File;

public class EditImageDialogFragment extends DialogFragment implements View.OnClickListener {
    private Dialog mDialog;
    private Button btnSave;
    private Button btnUndo;
    private MyFreeDrwable myDrawable;
    private Button btnRedPaint;
    private Button btnGreenPaint;
    private Button btnBluePaint;
    private OnImageEditListener highlightListener;
    private File file;

    public void setImage(File file) {
        this.file = file;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDialog = new Dialog(getActivity());
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.image_edit_dialog);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.show();
        init();
        return mDialog;
    }

    private void init() {
        initView();
        initListener();
        initData();
    }

    private void initListener() {
        btnSave.setOnClickListener(this);
        btnUndo.setOnClickListener(this);
        btnRedPaint.setOnClickListener(this);
        btnGreenPaint.setOnClickListener(this);
        btnBluePaint.setOnClickListener(this);
    }


    @SuppressLint("NewApi")
    private void initData() {
        myDrawable.setBackground(new BitmapDrawable(Util.getBitMapFromFile(file)));
    }

    private void initView() {
        btnSave = mDialog.findViewById(R.id.btn_capture_edited_image);
        btnUndo = mDialog.findViewById(R.id.btn_undo_edited_image);
        btnRedPaint = mDialog.findViewById(R.id.btn_paint_r_edited_image);
        btnGreenPaint = mDialog.findViewById(R.id.btn_paint_g_edited_image);
        btnBluePaint = mDialog.findViewById(R.id.btn_paint_b_edited_image);
        myDrawable = mDialog.findViewById(R.id.my_image_editor);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_capture_edited_image:
                myDrawable.captureChanges();
                Bitmap newBitmap = myDrawable.getBitmap();
                highlightListener.onHighlightImage(newBitmap);
                mDialog.dismiss();
                break;
            case R.id.btn_undo_edited_image:
                myDrawable.undoLastChange();
                break;
            case R.id.btn_paint_r_edited_image:
                myDrawable.setBrushColourToRed();
                break;
            case R.id.btn_paint_g_edited_image:
                myDrawable.setBrushColourToGreen();
                break;
            case R.id.btn_paint_b_edited_image:
                myDrawable.setBrushColourToBlue();
                break;
        }
    }

    public void setOnImageEditListener(OnImageEditListener highlightListener) {
        this.highlightListener = highlightListener;
    }
}
