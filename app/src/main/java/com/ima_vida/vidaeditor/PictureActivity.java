package com.ima_vida.vidaeditor;

import static android.graphics.Color.rgb;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;

public class PictureActivity extends AppCompatActivity{

    public static final int PICK_PHOTO = 1;
    public static final int CROP_PHOTO = 2;
    private static final String IMAGE_FILE_LOCATION = "file:///" + Environment.getExternalStorageDirectory().getPath() + "/temp.jpg";
    private Uri imageUri = Uri.parse(IMAGE_FILE_LOCATION);
    private boolean isJPG = true;
    private ImageView mIvVida;
    private FloatingActionButton mFabChoose;
    private boolean canSave = true;
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        mIvVida = findViewById(R.id.iv_vida_picture);
        mFabChoose = findViewById(R.id.fab_choose);

        //等到ImageView初始化完再进行
        mIvVida.post(new Runnable() {
            @Override
            public void run() {
                Bitmap tempBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.vida);
                Bitmap vidaBitmap = ImageUtil.setBitmapWidth(tempBitmap, mIvVida.getMeasuredWidth());
                mIvVida.setImageBitmap(vidaBitmap);
                mBitmap = vidaBitmap;
            }
        });
        initToolbar();

        mFabChoose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击按钮选择图片
                selectPicFromLocal();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pic, menu);
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
     * 保存图片
     */
    private void savePicture() {
        if (!canSave) {
            return;
        }
        canSave = false;
        Date date = new Date();
        final CompressFormat type = isJPG?CompressFormat.JPEG:CompressFormat.PNG;
        String fileName = "vida_" + date.getTime() + (isJPG?".jpg":".png");
        ImageUtil.saveBitmapToGallery(mBitmap, fileName,
            new PictureSaveCallBack() {
                @Override
                public void onSaved(final File file) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PictureActivity.this, "图片已成功保存至" + file,
                                    Toast.LENGTH_SHORT).show();
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                    Uri.fromFile(file)));
                            canSave = true;
                        }
                    });

                }
            },type);
    }

    /**
     * 从本地选择图片
     */
    private void selectPicFromLocal() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, PICK_PHOTO);
    }

    /**
     * 打开Activity回调
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_PHOTO: // 如果是直接从相册获取
                    startPicCrop(data.getData());//拿到所选图片的Uri
                    break;
                case CROP_PHOTO: // 取得裁剪后的图片
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        onChooseBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * 打开图片裁剪
     * @param uri：图片uri
     */
    public void startPicCrop(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        //该参数可以不设定用来规定裁剪区的宽高比
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        //是否返回bitmap对象
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());//输出图片的格式
        startActivityForResult(intent, CROP_PHOTO);
    }

    /**
     * 初始化Toolbar
     */
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("蛋蛋底图编辑器");
        actionBar.setSubtitle("请选择图片后点击保存");
        if (actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * 选择照片后的处理
     */
    private void onChooseBitmap(Bitmap bitmap) {
        Bitmap tempBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.vida);
        Bitmap vidaBitmap = ImageUtil.setBitmapWidth(tempBitmap, mIvVida.getMeasuredWidth());
        Bitmap picBitmap = ImageUtil.setBitmapWidth(bitmap, mIvVida.getMeasuredWidth());
        mBitmap = ImageUtil.changeVidaPic(vidaBitmap, picBitmap);
        mIvVida.setImageBitmap(mBitmap);
    }

}
