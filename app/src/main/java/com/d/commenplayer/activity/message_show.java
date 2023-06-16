package com.d.commenplayer.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.d.commenplayer.Activity_contorller;
import com.d.commenplayer.MainActivity;
import com.d.commenplayer.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class message_show extends AppCompatActivity {
    private static Activity mContext;
    private TextView tv_msg_zhongduanhao;
    private TextView tv_msg_content;
    private TextView tv_gpsjd_rev;
    private TextView tv_gpswd_rev;
    private TextView tv_time_rev;
    private ImageView tv_img_rev;
    private static long times;
    private static Intent intent;
    private MsgRevDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        String type = intent.getStringExtra("type");
        Log.e("type", type);
        Activity_contorller.add_activities(this);
        dao=new MsgRevDao(message_show.this);
        if(type.equals("短信"))
        {
            setContentView(R.layout.activity_message_show);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        tv_msg_zhongduanhao=findViewById(R.id.tv_msg_zhongduanhao);
        tv_msg_content=findViewById(R.id.tv_msg_content);
        tv_gpsjd_rev=findViewById(R.id.tv_gpsjd_rev);
        tv_gpswd_rev=findViewById(R.id.tv_gpswd_rev);
        tv_time_rev=findViewById(R.id.tv_time_rev);

        String zhongduanhao = intent.getStringExtra("tv_msg_zhongduanhao");

        tv_msg_zhongduanhao.setText(zhongduanhao);
        tv_msg_content.setText(intent.getStringExtra("tv_msg"));
        tv_gpsjd_rev.setText(intent.getStringExtra("tv_gpsjd_rev"));
        tv_gpswd_rev.setText(intent.getStringExtra("tv_gpswd_rev"));
        tv_time_rev.setText(intent.getStringExtra("rili")+intent.getStringExtra("tv_time_rev"));}
        else if(type.equals("图片"))
        {
            setContentView(R.layout.activity_picture_show);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            tv_msg_zhongduanhao=findViewById(R.id.tv_msg_zhongduanhao);
            tv_gpsjd_rev=findViewById(R.id.tv_gpsjd_rev);
            tv_gpswd_rev=findViewById(R.id.tv_gpswd_rev);
            tv_time_rev=findViewById(R.id.tv_time_rev);
            tv_img_rev=findViewById(R.id.recv_picture);

            String zhongduanhao = intent.getStringExtra("tv_msg_zhongduanhao");

            tv_msg_zhongduanhao.setText(zhongduanhao);
            tv_gpsjd_rev.setText(intent.getStringExtra("tv_gpsjd_rev"));
            tv_gpswd_rev.setText(intent.getStringExtra("tv_gpswd_rev"));
            tv_time_rev.setText(intent.getStringExtra("rili")+intent.getStringExtra("tv_time_rev"));
            tv_img_rev.setImageBitmap(SimpleActivity.bitmap);

        }
        else if(type.equals("信箱图片"))
        {
            Log.d("TAG", "onCreate: ");
            setContentView(R.layout.activity_picture_show);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            Button button = findViewById(R.id.saveimg);
            button.setVisibility(View.INVISIBLE);
            tv_msg_zhongduanhao=findViewById(R.id.tv_msg_zhongduanhao);
            tv_gpsjd_rev=findViewById(R.id.tv_gpsjd_rev);
            tv_gpswd_rev=findViewById(R.id.tv_gpswd_rev);
            tv_time_rev=findViewById(R.id.tv_time_rev);
            tv_img_rev=findViewById(R.id.recv_picture);

            String zhongduanhao = intent.getStringExtra("tv_msg_zhongduanhao");

            tv_msg_zhongduanhao.setText(zhongduanhao);
            tv_gpsjd_rev.setText(intent.getStringExtra("tv_gpsjd_rev"));
            tv_gpswd_rev.setText(intent.getStringExtra("tv_gpswd_rev"));
            tv_time_rev.setText(intent.getStringExtra("rili")+"时间:"+intent.getStringExtra("tv_time_rev"));

            String storePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + "test"+intent.getStringExtra("tv_time_rev");
            System.out.println(storePath);
            File appDir = new File(storePath);
            if (!appDir.exists()) {
                appDir.mkdir();
                System.out.println("图片不存在！");
            }
            String fileName = ".jpg";
            File file = new File(appDir, fileName);
            ByteArrayOutputStream baos = null;
            InputStream is = null;
            try {
                is = new FileInputStream(file);
                baos = new ByteArrayOutputStream();
                // 3、操作(分段读取)
                byte[] flush = new byte[1024 * 10];// 缓冲容器
                int len = -1;// 接收长度
                try {
                    while ((len = is.read(flush)) != -1) {
                        // 写出到字节数组中
                        baos.write(flush,0,len);
                    }
                    baos.flush();
                    // 返回回来，上面调用时就有了
                    Bitmap bitmap = Bytes2Bitmap(baos.toByteArray());
                    tv_img_rev.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                // 4、释放资源
                try {
                    if (null != is) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        }
        }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == 216&& event.getAction() == KeyEvent.ACTION_UP)       //取消键
        {
            finish();
        }
        if (event.getKeyCode() == 215&& event.getAction() == KeyEvent.ACTION_UP&&intent.getStringExtra("type").equals("图片"))
        {
                requestPermission(this, SimpleActivity.bitmap);
                MsgInfo msgInfo=new MsgInfo(-1,intent.getStringExtra("data.substring(19,28)"),"图片  "+"时间:"+intent.getStringExtra("tv_time_rev"),
                    "经度:","纬度:",intent.getStringExtra("tv_time_rev"),intent.getStringExtra("rili"),1,1);
                dao.add(msgInfo);
            }

        return super.dispatchKeyEvent(event);
    }

    public static Bitmap Bytes2Bitmap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }


    public void requestPermission(Activity context, Bitmap bitmap) {
        mContext = context;
        /**
         * 添加读写权限
         *      READ_EXTERNAL_STORAGE：读外部存储的权限
         *      WRITE_EXTERNAL_STORAGE：写外部存储的权限
         */
        String[] mPermissionList = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        // 判断有无读写权限
        if (hasPermissions(mContext, mPermissionList)) {
            // 已同意 去保存
            saveImage(context, bitmap);
        } else {
            // 未同意 申请权限
            finish();
        }
    }

    // 是否有对应权限
    public static boolean hasPermissions(Context context, String... perms) {
        // 判断sdk版本
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        for (String perm : perms) {
            boolean hasPerm = (ContextCompat.checkSelfPermission(context, perm) ==
                    PackageManager.PERMISSION_GRANTED);
            if (!hasPerm) {
                return false;
            }
        }
        return true;
    }

    public void saveImage(Context context, Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        saveImageToGallery(context, bitmap);
        Toast.makeText(mContext, "保存图片成功！  "+"保存地址："+getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + "test"+intent.getStringExtra("tv_time_rev"), Toast.LENGTH_SHORT).show();

    }

    public void saveImageToGallery(Context context, Bitmap image) {
        // 首先保存图片
        String storePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + "test"+intent.getStringExtra("tv_time_rev");

        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            // 通过io流的方式来压缩保存图片
            image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}