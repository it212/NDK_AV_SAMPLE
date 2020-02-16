package com.devyk.myplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.devyk.crash_module.Crash;
import com.devyk.crash_module.inter.JavaCrashUtils;
import com.devyk.player_common.Constants;
import com.devyk.player_common.PlayerManager;
import com.devyk.player_common.callback.OnPreparedListener;
import com.devyk.player_common.play.YKPlayer;

import java.io.File;

import static android.content.ContentValues.TAG;
import static com.devyk.player_common.Constants.JavaPath;
import static com.devyk.player_common.Constants.nativePath;

public class MainActivity extends AppCompatActivity implements OnPreparedListener {

    private YKPlayer mYKPlayer;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(getApplicationContext(), "当前 FFmpeg 版本:" + PlayerManager.getInstance().getFFmpegVersion(), Toast.LENGTH_SHORT).show();

        SurfaceView mSurView = findViewById(R.id.sf_player);

        mYKPlayer = new YKPlayer();

        mYKPlayer.setSurfaceView(mSurView);
        PlayerManager.getInstance().setOnPreparedListener(this);


        if (!new File(nativePath).exists()) {
            new File(nativePath).mkdirs();
        }
        if (!new File(JavaPath).exists()) {
            new File(JavaPath).mkdirs();
        }


        new Crash.CrashBuild(getApplicationContext())
                .nativeCrashPath(nativePath)
                .javaCrashPath(JavaPath, new JavaCrashUtils.OnCrashListener() {
                    @Override
                    public void onCrash(String crashInfo, Throwable e) {
                        Log.d(TAG, crashInfo);
                    }
                })
                .build();

    }


    @Override
    protected void onResume() {
        super.onResume();
        mYKPlayer.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mYKPlayer.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mYKPlayer.release();
    }


    /**
     * 拉流
     * @param view
     */
    public void pull(View view) {
        mYKPlayer.setDataSource(Constants.HUNAN_PATH);
        if (mYKPlayer.isPlayer()) {
            Toast.makeText(getApplicationContext(), "正在播放!", Toast.LENGTH_SHORT).show();
            return;
        }
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("通过 RTMP 拉取网络音视频流...");
        mProgressDialog.setTitle("提示");
        mProgressDialog.show();
        mYKPlayer.prepare();
    }


    /**
     * Http 拉流
     * @param view
     */
    public void http(View view) {
        mYKPlayer.setDataSource(Constants.HTTP_PATH);
        if (mYKPlayer.isPlayer()) {
            Toast.makeText(getApplicationContext(), "正在播放!", Toast.LENGTH_SHORT).show();
            return;
        }
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("通过 HTTP 拉取网络音视频流...");
        mProgressDialog.setTitle("提示");
        mProgressDialog.show();
        mYKPlayer.prepare();
    }


    /**
     * 本地文件播放
     * @param view
     */
    public void local_play(View view) {
        mYKPlayer.setDataSource(Constants.LOCAL_FILE);
        if (mYKPlayer.isPlayer()) {
            Toast.makeText(getApplicationContext(), "正在播放!", Toast.LENGTH_SHORT).show();
            return;
        }
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("正在播放本地 MP4 文件...");
        mProgressDialog.setTitle("提示");
        mProgressDialog.show();
        mYKPlayer.prepare();
    }

    public void url_mp4(View view) {
        mYKPlayer.setDataSource(Constants.MP4_PLAY);
        if (mYKPlayer.isPlayer()) {
            Toast.makeText(getApplicationContext(), "正在播放!", Toast.LENGTH_SHORT).show();
            return;
        }
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("正在播放网络 MP4 文件...");
        mProgressDialog.setTitle("提示");
        mProgressDialog.show();
        mYKPlayer.prepare();
    }

    /**
     * 停止拉流
     * @param view
     */
    public void stop(View view) {
        mYKPlayer.stop();
    }

    /**
     * 恢复
     * @param view
     */
    public void restart(View view) {
        mYKPlayer.onRestart();
    }

    /**
     * 销毁资源
     * @param view
     */
    public void release(View view) {
        mYKPlayer.release();
    }
    /**
     * JNI 回调会执行这里
     */
    @Override
    public void onPrepared() {
        //数据准备好了，可以开始播放
//        mYKPlayer.start();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "准备好了，开始播放", Toast.LENGTH_SHORT).show();
                mProgressDialog.cancel();
            }
        });

        mYKPlayer.start();
    }


    /**
     * JNI 回调会执行这里
     */
    @Override
    public void onError(final String errorText) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.cancel();
                Toast.makeText(getApplicationContext(), "播放出错了😢," + errorText, Toast.LENGTH_SHORT).show();
            }
        });
    }


}