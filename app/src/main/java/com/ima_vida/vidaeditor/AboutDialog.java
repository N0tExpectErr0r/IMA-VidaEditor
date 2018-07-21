package com.ima_vida.vidaeditor;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

/**
 * @AUTHOR nullptr
 * @DATE 创建时间: 2018/7/21
 * @DESCRIPTION
 */
public class AboutDialog extends AlertDialog.Builder {
    private String aboutMessage = "IMA蛋协2018\n"
            + "梦想就像一只蛋，从外而内打破是压力，从内而外打破，就是生命。";

    public AboutDialog(final Context context) {
        super(context);
        setTitle("关于我们");
        setMessage(aboutMessage);
        setPositiveButton("加入我们", new DialogInterface.OnClickListener(){
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
                    context.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(context, "未安装手机QQ或安装的版本不支持",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        setIcon(R.mipmap.ic_launcher);
    }
}
