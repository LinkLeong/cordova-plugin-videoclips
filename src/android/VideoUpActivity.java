package __PACKAGE_NAME__;


import android.app.Activity;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import __PACKAGE_NAME__.R;

import com.cjt2325.cameralibrary.JCameraView;
import com.cjt2325.cameralibrary.listener.ClickListener;
import com.cjt2325.cameralibrary.listener.JCameraListener;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class VideoUpActivity extends Activity {

    private JCameraView jCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_up);

        jCameraView = (JCameraView) findViewById(R.id.jcameraview);

        //设置视频保存路径
        jCameraView.setSaveVideoPath(Environment.getExternalStorageDirectory()
                + File.separator + Environment.DIRECTORY_DCIM);

        //JCameraView监听
        jCameraView.setJCameraLisenter(new JCameraListener() {
            @Override
            public void captureSuccess(Bitmap bitmap) {
                //获取图片bitmap
                Log.i("JCameraView", "bitmap = " + bitmap.getWidth());
                new Thread(() -> {
                    try {
                        final String imgPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+
                                "BITMAP_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".png";
                        final File file;
                        try {
                            file = new File(imgPath);
                            file.createNewFile();
                            FileOutputStream out = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            out.flush();
                            out.close();

//                                MediaStore.Images.Media.insertImage(getContentResolver(),
//                                        file.getAbsolutePath(), file.getName(), null);

                            // 通知图库更新
                            if (Build.VERSION.SDK_INT > 19) {
                                MediaScannerConnection.scanFile(VideoUpActivity.this, new String[]{file.getAbsolutePath()}, new String[]{"image/jpeg"}, null);
                            } else {
                                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,  Uri.fromFile(file )));
                            }

                        } catch (Exception e) {
                            Log.d("addImgCacheError", e.toString());
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                File file1 = new File(imgPath);
                                if (file1.exists()){
                                    Intent intent = new Intent();
                                    intent.putExtra("url",imgPath);
                                    VideoUpActivity.this.setResult(201,intent);
                                    finish();
                                }

                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }

            @Override
            public void recordSuccess(final String url, Bitmap firstFrame) {

                Intent intent = new Intent();
                intent.putExtra("url",url);
                VideoUpActivity.this.setResult(202,intent);
                finish();


            }



        });
        jCameraView.setLeftClickListener(() -> {
            Intent intent = new Intent();
            VideoUpActivity.this.setResult(301,intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        VideoUpActivity.this.setResult(301,intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        jCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        jCameraView.onPause();
    }

}

