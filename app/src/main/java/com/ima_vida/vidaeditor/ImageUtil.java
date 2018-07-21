package com.ima_vida.vidaeditor;

import static android.support.v4.view.ViewCompat.LAYER_TYPE_SOFTWARE;
import static android.support.v4.view.ViewCompat.setLayerType;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
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

    /**
     * 改变图像的颜色
     * @param inBitmap 传入的图像
     * @param tintColor 改变的颜色
     * @return 改变后的图像
     */
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

    /**
     * 以文件名保存图片
     * @param bitmap 图片
     * @param fileName 文件名
     * @param callBack 得到图片后的callback
     */
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

    /**
     * 按高度等比例改变图像大小
     * @param bitmap 图片
     * @param newHeight 新的高度
     * @return 改变后的图片
     */
    public static Bitmap setBitmapHeight(Bitmap bitmap, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float newWidth = ((float)newHeight/height)*width;
        float scaleWidth = newWidth / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }

    /**
     * 按宽度等比例改变图像大小
     * @param bitmap 图片
     * @param newWidth 新的高度
     * @return 改变后的图片
     */
    public static Bitmap setBitmapWidth(Bitmap bitmap, int newWidth) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float newHeight = ((float)newWidth/width)*width;
        float scaleWidth = ((float)newWidth) / width;
        float scaleHeight = newHeight / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }

    /**
     * 将图像应用于蛋上
     * @param vidaBitmap：蛋蛋图像
     * @param picBitmap：具体图片
     * @return
     */
    public static Bitmap changeVidaPic(Bitmap vidaBitmap,Bitmap picBitmap){
        Bitmap outBitmap = Bitmap.createBitmap(vidaBitmap.getWidth(),vidaBitmap.getHeight(),Config.ARGB_8888);
        Canvas canvas = new Canvas(outBitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        canvas.drawBitmap(vidaBitmap,0,0,paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(picBitmap,0,0,paint);
        return outBitmap;
    }
}
