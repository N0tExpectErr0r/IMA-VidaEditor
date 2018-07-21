package com.ima_vida.vidaeditor;

import static android.graphics.Color.rgb;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.util.Date;

public class ColorActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private Bitmap mOriBitmap;
    private ImageView mIvVida;
    private TextView mTvRed;
    private TextView mTvGreen;
    private TextView mTvBlue;
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
        Bitmap saveBitmap = ImageUtil.handleBitmapColor(mOriBitmap, color);
        Date date = new Date();
        ImageUtil.saveBitmap(saveBitmap, "vida_" + date.getTime() + ".png",
                new PictureSaveCallBack() {
                    @Override
                    public void onSaved(final String path) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ColorActivity.this, "图片已成功保存至" + path,
                                        Toast.LENGTH_SHORT).show();
                                canSave = true;
                            }
                        });

                    }
                });
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
