package com.ima_vida.vidaeditor;

import static android.graphics.Color.rgb;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private Bitmap mOriBitmap;
    private ImageView mIvVida;
    private TextView mTvRed;
    private TextView mTvGreen;
    private TextView mTvBlue;
    private boolean canSave = true;
    private int red = 0;
    private int green = 211;
    private int blue = 254;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

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

        initToolbar(getString(R.string.app_name));

        //ImageView初始化完成后，加载图片
        mIvVida.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.vida);
                mOriBitmap = ImageUtil.setBitmapSize(bitmap, mIvVida.getMeasuredHeight());
                int color = rgb(red, green, blue);
                mIvVida.setImageBitmap(ImageUtil.handleBitmapColor(mOriBitmap, color));
            }
        });

        //申请读写文件权限
        verifyStoragePermissions(this);

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
                                Toast.makeText(MainActivity.this, "图片已成功保存至" + path,
                                        Toast.LENGTH_SHORT).show();
                                canSave = true;
                            }
                        });

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
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
                String aboutMessage = "IMA蛋协2018\n"
                        + "梦想就像一只蛋，从外而内打破是压力，从内而外打破，就是生命。";
                new Builder(this)
                        .setTitle("关于我们")
                        .setMessage(aboutMessage)
                        .setPositiveButton("加入我们", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //打开加群界面
                                //key通过http://qun.qq.com/join.html网站获得，不同key对应不同QQ群
                                String key = "rauaMTu4_vQKVaoFZ-_Be5Xv0dglOg91";
                                String uriStr = "mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%"
                                        + "2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D"
                                        + key;
                                Intent intent = new Intent();
                                intent.setData(Uri.parse(uriStr));
                                try {
                                    startActivity(intent);
                                } catch (Exception e) {
                                    Toast.makeText(MainActivity.this, "未安装手机QQ或安装的版本不支持",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setIcon(R.mipmap.ic_launcher)
                        .show();
                break;
        }
        return true;
    }

    /**
     * 初始化Toolbar
     */
    private void initToolbar(CharSequence title) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(title);
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
