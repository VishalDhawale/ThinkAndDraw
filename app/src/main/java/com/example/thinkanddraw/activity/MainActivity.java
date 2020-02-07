package com.example.thinkanddraw.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.thinkanddraw.fragment.CameraDialogFragment;
import com.example.thinkanddraw.listener.OnImageEditListener;
import com.example.thinkanddraw.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnImageEditListener {

    private int PICK_IMAGE_REQUEST = 1;
    private ImageView ivImageView;
    private Button selectImage;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        initView();
        initListener();
    }

    private void initListener() {
        selectImage.setOnClickListener(this);
        ivImageView.setOnClickListener(this);
    }

    private void initView() {
        selectImage = findViewById(R.id.btn_upload_image);
        ivImageView = findViewById(R.id.iv_view_image);
    }

    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                file = convertToFile(bitmap);
                ivImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File convertToFile(Bitmap bitmap) {
        //create a file to write bitmap data
        try {
            File storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            file = File.createTempFile(
                    "temp_file",
                    ".jpg",
                    storageDir
            );
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();
            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    private void openCaptureImageDialog() {
        FragmentManager fm = this.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        CameraDialogFragment frag = new CameraDialogFragment();
        frag.addOnSignatureCompleteListener(this, file);
        frag.show(ft, CameraDialogFragment.class.getSimpleName());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_upload_image:
                chooseImage();
                break;

            case R.id.iv_view_image:
                openCaptureImageDialog();
                break;
        }
    }

    @Override
    public void onHighlightImage(Bitmap bitmap) {
        ivImageView.setImageBitmap(bitmap);
        file = convertToFile(bitmap);
    }

    @Override
    public void onRemove() {
        file.delete();
        ivImageView.setImageBitmap(null);
        ivImageView.setBackgroundResource(R.color.white);
    }
}
