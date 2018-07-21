package com.ima_vida.vidaeditor;

import static android.graphics.Color.rgb;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorSpace.Rgb;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private ImageView mIvLogo;
    private Button mBtnColor;
    private Button mBtnPicture;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIvLogo = findViewById(R.id.iv_logo);
        mBtnColor = findViewById(R.id.btn_color);
        mBtnPicture = findViewById(R.id.btn_pic);

        mIvLogo.post(new Runnable() {
            @Override
            public void run() {
                int color = rgb(255,255,255);
                Bitmap logo = BitmapFactory.decodeResource(getResources(),R.drawable.vida);
                logo = ImageUtil.setBitmapWidth(logo,mIvLogo.getMeasuredWidth());
                logo = ImageUtil.handleBitmapColor(logo,color);
                mIvLogo.setImageBitmap(logo);
            }
        });

        mBtnColor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ColorActivity.class);
                startActivity(intent);
            }
        });

        mBtnPicture.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,PictureActivity.class);
                startActivity(intent);
            }
        });

        //申请读写文件权限
        verifyStoragePermissions(this);

    }

    /**
     * 动态申请读写文件权限
     */
    public void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this,
                        "应用需要" + permissions[i] + "权限才可正常运行", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
