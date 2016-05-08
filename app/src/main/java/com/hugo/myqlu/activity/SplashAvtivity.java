package com.hugo.myqlu.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hugo.myqlu.R;
import com.hugo.myqlu.bean.VersionBean;
import com.hugo.myqlu.utils.SpUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;

public class SplashAvtivity extends AppCompatActivity {
    @Bind(R.id.tv_version)
    TextView tv_version;
    private String updateUrl = "http://hunao0221.github.io/myqluupdate.json";
    private Context mContext = this;
    private boolean isFirstIn;
    private String mVersionName;
    private int mVersionCode;
    private VersionBean versionInfo;
    private ProgressBar pbDown;
    private SharedPreferences config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_avtivity);
        ButterKnife.bind(this);
        config = SpUtil.getSp(mContext, "config");
        getVersionInfo();
        if (config.getBoolean("auto_update", true)) {
            checkForUpdate();
        } else {
            handler.sendEmptyMessageDelayed(0, 3000);
        }
    }


    private void checkForUpdate() {
        OkHttpUtils.get().url(updateUrl)
                .build()
                .connTimeOut(2000)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        handler.sendEmptyMessageDelayed(1, 1000);
                    }

                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        versionInfo = gson.fromJson(response, VersionBean.class);
                        hasNewVersion();
                    }
                });
    }

    /**
     * 是否有新版本
     */
    private void hasNewVersion() {
        if (versionInfo != null) {
            int versionCode = versionInfo.getVersionCode();
            if (versionCode > mVersionCode) {
                if (Build.VERSION.SDK_INT >= 23) {
                    int permission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (permission != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
                        return;
                    } else {
                        showUpdateDialog();
                    }
                } else {
                    showUpdateDialog();
                }
            } else {
                handler.sendEmptyMessageDelayed(0, 2000);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 123:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    showUpdateDialog();
                } else {
                    // Permission Denied
                    handler.sendEmptyMessageDelayed(0, 1000);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 检测到更新后，显示弹窗提示下载
     */
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("检测到新版本:" + versionInfo.getVersionName());
        builder.setMessage(versionInfo.getDescription());
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showDownLoadDialog();

                downLoadUpdateFile();
            }
        });
        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                handler.sendEmptyMessageDelayed(0, 1000);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
    }

    /**
     * 下载进度
     */
    private void showDownLoadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Download...");
        View view = View.inflate(mContext, R.layout.dialog_download, null);
        builder.setView(view);
        pbDown = ButterKnife.findById(view, R.id.pb_down);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
    }

    /**
     * 下载更新文件
     */
    private void downLoadUpdateFile() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        OkHttpUtils.get().url(versionInfo.getDownloadUrl())
                .build()
                .connTimeOut(5000)
                .execute(new FileCallBack(path, "myQluUpdate.apk") {
                    @Override
                    public void inProgress(float progress, long total) {
                        System.out.println(total + "------" + progress * total);
                        pbDown.setMax((int) total);
                        pbDown.setProgress((int) (progress * total));
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        handler.sendEmptyMessageDelayed(2, 1000);
                    }

                    @Override
                    public void onResponse(File response) {
                        //下载成功。进入程序安装界面
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setDataAndType(Uri.fromFile(response),
                                "application/vnd.android.package-archive");
                        startActivityForResult(intent, 0);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        enterHome();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 获得本地版本号
     */
    public void getVersionInfo() {
        //首先拿到packageManager对象
        PackageManager packageManager = getPackageManager();
        try {
            //获取PackageInfo
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            //获得版本号
            mVersionName = packageInfo.versionName;
            mVersionCode = packageInfo.versionCode;
            tv_version.setText("version " + mVersionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    break;
                case 2:
                    Toast.makeText(mContext, "下载失败", Toast.LENGTH_SHORT).show();
                    break;
            }
            enterHome();
        }
    };

    /**
     * 首次进入登陆了界面
     * 下次啊直接进入主页
     */
    private void enterHome() {
        config = SpUtil.getSp(mContext, "config");
        isFirstIn = config.getBoolean("isFirstIn", true);
        Intent intent;
        if (isFirstIn) {
            intent = new Intent(mContext, LoginActivity.class);
        } else {
            intent = new Intent(mContext, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
