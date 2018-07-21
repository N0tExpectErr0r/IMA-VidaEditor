package com.ima_vida.vidaeditor;

import static android.graphics.Color.rgb;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.Date;

public class ColorActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private Bitmap mOriBitmap;
    private ImageView mIvVida;
    private TextView mTvRed;
    private TextView mTvGreen;
    private TextView mTvBlue;
    private boolean isJPG = true;
    private boolean canSave = true;
    private int red = 0;
    private int green = 211;
    private int blue = 254;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color);
        mIvVida = findViewById(R.id.iv_vida);

        SeekBar sbRed = findViewById(R.id.sb_red);
        SeekBar sbGreen = findViewById(R.id.sb_green);
        SeekBar sbBlue = findViewById(R.id.sb_blue);

        mTvRed = findViewById(R.id.tv_red);
        mTvGreen = findViewById(R.id.tv_green);
        mTvBlue = findViewById(R.id.tv_blue);

        sbRed.setOnSeekBarChangeListener(this);
        sbGreen.setOnSeekBarChangeListener(this);
        sbBlue.setOnSeekBarChangeListener(this);

        initToolbar();

        //ImageView初始化完成后，加载图片
        mIvVida.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.vida);
                mOriBitmap = ImageUtil.setBitmapHeight(bitmap, mIvVida.getMeasuredHeight());
                int color = rgb(red, green, blue);
                mIvVida.setImageBitmap(ImageUtil.handleBitmapColor(mOriBitmap, color));
            }
        });

    }

    /**
     * 保存图片
     */
    private void savePicture() {
        if (!canSave) {
            return;
        }
        canSave = false;
        int color = rgb(red, green, blue);
        CompressFormat type = isJPG? CompressFormat.JPEG:CompressFormat.PNG;
        Bitmap saveBitmap = ImageUtil.handleBitmapColor(mOriBitmap, color);
        Date date = new Date();
        String fileName = "vida_" + date.getTime() + (isJPG?".jpg":".png");
        ImageUtil.saveBitmapToGallery(saveBitmap, fileName,
                new PictureSaveCallBack() {
                    @Override
                    public void onSaved(final File file) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ColorActivity.this, "图片已成功保存至" + file.getAbsolutePath(),
                                        Toast.LENGTH_SHORT).show();
                                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                        Uri.fromFile(file)));
                                canSave = true;
                            }
                        });

                    }
                },type);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_color, menu);
        return true;
    }

    /**
     * 点击Toolbar菜单监听
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_save:
                savePicture();
                break;
            case R.id.ic_about:
                new AboutDialog(this).show();
                break;
            case R.id.home:
                finish();
                break;
            case R.id.ic_mode:
                if (isJPG){
                    isJPG = false;
                    item.setTitle("PNG");
                }else {
                    isJPG = true;
                    item.setTitle("JPG");
                }
                break;
        }
        return true;
    }

    /**
     * 初始化Toolbar
     */
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("蛋蛋颜色编辑器");
        actionBar.setSubtitle("请调整好颜色后点击保存");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * SeekBar滑动监听
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.sb_red:
                red = progress;
                mTvRed.setText(progress + "");
                break;
            case R.id.sb_green:
                green = progress;
                mTvGreen.setText(progress + "");
                break;
            case R.id.sb_blue:
                blue = progress;
                mTvBlue.setText(progress + "");
                break;
            default:
                break;
        }
        int color = rgb(red, green, blue);
        mIvVida.setImageBitmap(ImageUtil.handleBitmapColor(mOriBitmap, color));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }




}
