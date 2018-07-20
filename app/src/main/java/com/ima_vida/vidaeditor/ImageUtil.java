package com.ima_vida.vidaeditor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @AUTHOR nullptr
 * @DATE 创建时间: 2018/7/20
 * @DESCRIPTION
 */
public class ImageUtil {

    private static MediaScannerConnection sMediaScannerConnection;

    public static Bitmap handleBitmapColor(Bitmap inBitmap, int tintColor) {
        if (inBitmap == null) {
            return null;
        }
        Bitmap outBitmap = Bitmap.createBitmap(inBitmap.getWidth(), inBitmap.getHeight(), inBitmap.getConfig());
        Canvas canvas = new Canvas(outBitmap);
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(inBitmap, 0, 0, paint);
        return outBitmap;
    }

    public static void saveBitmap(final Bitmap bitmap, final String fileName,
            final PictureSaveCallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String subFloder = Environment.getExternalStorageDirectory().getPath() + "/vida/";
                File floder = new File(subFloder);
                if (!floder.exists()) {
                    floder.mkdirs();
                }
                File myCaptureFile = new File(subFloder, fileName);
                if (!myCaptureFile.exists()) {
                    try {
                        myCaptureFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("saveBitmap: ", e.getMessage());
                    }
                }
                try {
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
                    bitmap.compress(CompressFormat.PNG, 80, bos);
                    bos.flush();
                    bos.close();

                    callBack.onSaved(subFloder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public static Bitmap setBitmapSize(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }
}
