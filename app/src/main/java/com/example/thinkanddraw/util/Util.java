package com.example.thinkanddraw.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

public class Util {
    public static Bitmap getBitMapFromFile(File file) {
        if (file != null && file.exists()) {
            return BitmapFactory.decodeFile(file.getAbsolutePath());
        }
        return null;
    }
}
