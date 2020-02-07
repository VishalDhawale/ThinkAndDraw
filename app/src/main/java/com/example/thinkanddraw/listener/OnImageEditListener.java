package com.example.thinkanddraw.listener;

import android.graphics.Bitmap;

public interface OnImageEditListener {

    void onHighlightImage(Bitmap bitmap);

    void onRemove();
}
