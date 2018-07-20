package com.ima_vida.vidaeditor;

import static android.graphics.Color.rgb;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private Bitmap mOriBitmap;

    private int red;
    private int green;
    private int blue;

    private ImageView mIvVida;

    private TextView mTvRed;
    private TextView mTvGreen;
    private TextView mTvBlue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        red = 0;
        green = 211;
        blue = 254;
        //ImageView初始化完成后，加载图片
        mIvVida.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.vida);
                mOriBitmap = ImageUtil.setBitmapSize(bitmap,mIvVida.getMeasuredWidth(),mIvVida.getMeasuredHeight());
                int color = rgb(red, green, blue);
                mIvVida.setImageBitmap(ImageUtil.handleBitmapColor(mOriBitmap, color));
            }
        });
    }

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
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
}
