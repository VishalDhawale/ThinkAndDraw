package com.example.thinkanddraw.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.thinkanddraw.R;
import com.example.thinkanddraw.activity.MainActivity;
import com.example.thinkanddraw.listener.OnImageEditListener;
import com.example.thinkanddraw.util.Util;

import java.io.File;

public class CameraDialogFragment extends DialogFragment implements View.OnClickListener {
    private OnImageEditListener listener;
    private Dialog mDialog;
    private Button btnCancle;
    private Button btnDelete;
    private ImageView ivImage;
    private RelativeLayout llContainer;
    private ScaleGestureDetector zoomDetector;
    Matrix matrix = new Matrix();
    private GestureDetector panDetector;
    private int ZOOM_IN_COUNT = 0;
    private MainActivity mAct;
    private Button btnEdit;
    private File file;

    public void addOnSignatureCompleteListener(MainActivity mAct, File file) {
        this.listener = mAct;
        this.mAct = mAct;
        this.file = file;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDialog = new Dialog(getActivity());
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.image_preview_dialog);
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

    private void initData() {
        setImage();
    }

    private void initListener() {
        btnCancle.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
    }

    private void setImage() {
        ivImage.setImageBitmap(Util.getBitMapFromFile(file));
        initGestures();
    }

    private void initView() {
        btnCancle = mDialog.findViewById(R.id.btn_cancle);
        btnEdit = mDialog.findViewById(R.id.btn_edit_image);
        btnDelete = mDialog.findViewById(R.id.btn_delete);
        ivImage = mDialog.findViewById(R.id.iv_preview_image_dialog);
        llContainer = mDialog.findViewById(R.id.ll_image_container);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancle:
                mDialog.dismiss();
                break;
            case R.id.btn_delete:
                showConfirmDiscardImageAlart();
                break;
            case R.id.btn_edit_image:
                onEditClick();
                break;
        }
    }

    private void onEditClick() {
        EditImageDialogFragment fragment = new EditImageDialogFragment();
        fragment.setImage(file);
        fragment.setOnImageEditListener(listener);
        fragment.show(mAct.getSupportFragmentManager(), "Editor");
        mDialog.dismiss();
    }

    private void initGestures() {
        llContainer.setOnTouchListener(new MyOnTouchListner());
        zoomDetector = new ScaleGestureDetector(getActivity(), new MyScaleGestureListener());
        panDetector = new GestureDetector(getActivity(), new MySimpleGestureDetector());
    }

    private class MyOnTouchListner implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (event.getPointerCount() > 1)
                    zoomDetector.onTouchEvent(event);
                else
                    panDetector.onTouchEvent(event);
            }
            return true;
        }
    }

    private class MyScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            try {
                float scale = detector.getScaleFactor();
                scale = Math.max(0.1f, Math.min(scale, 5.0f));
                if (scale > 0.0 && ZOOM_IN_COUNT < 50) {
                    ZOOM_IN_COUNT++;
                    ivImage.setScaleType(ImageView.ScaleType.MATRIX);
                    boolean b1 = matrix.postScale(scale, scale);
                    ivImage.setImageMatrix(matrix);
                } else if (ZOOM_IN_COUNT > -20 && ZOOM_IN_COUNT != 0) {
                    ZOOM_IN_COUNT--;
                    ivImage.setScaleType(ImageView.ScaleType.MATRIX);
                    boolean b1 = matrix.postScale(scale, scale);
                    ivImage.setImageMatrix(matrix);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    private class MySimpleGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            distanceX = distanceX > 40 || distanceX < -40 ? 0 : distanceX;
            distanceY = distanceY > 40 || distanceY < -40 ? 0 : distanceY;
            matrix.postTranslate(distanceX * -1, distanceY * -1);
            ivImage.setImageMatrix(matrix);
            return true;
        }
    }

    private void showConfirmDiscardImageAlart() {
        AlertDialog.Builder alertDialog;
        alertDialog = new AlertDialog.Builder(mAct);
        alertDialog.setTitle("Please Confirm");
        alertDialog.setMessage("You really want to discard this image?");
        alertDialog.setCancelable(false);
        alertDialog.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onRemove();
                dialog.dismiss();
                mDialog.dismiss();
            }
        });

        alertDialog.setPositiveButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.create();
        alertDialog.show();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}