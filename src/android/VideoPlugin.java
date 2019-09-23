package AkeVideo;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;


import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import com.iceteck.silicompressorr.SiliCompressor;
import __PACKAGE_NAME__.VideoUpActivity;

import java.io.File;
import java.net.URISyntaxException;


/**
 * This class echoes a string called from JavaScript.
 */
public class VideoPlugin extends CordovaPlugin {

    private CallbackContext callbackContext;

    //必要权限
    private String permissions[]={
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA};
    private static final int TAKE_PIC_PER = 0x0001;//权限请求请求码
    public static final int PERMISSION_DENIED_ERROR = 0x0002;//权限错误返回码

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext =callbackContext;
        if (action.equals("coolMethod")) {

            boolean hasPermission=true;
            for (String permission : permissions) {
                if (!cordova.hasPermission(permission)) {
                    hasPermission = false;
                }
            }
            if (hasPermission){
                toVideo();
            }else {
                cordova.requestPermissions(this,TAKE_PIC_PER,permissions);
            }

            return true;
        }
        return false;
    }

    private void toVideo() {
        Intent intent=new Intent(cordova.getContext(), VideoUpActivity.class);
        cordova.startActivityForResult(VideoPlugin.this,intent,101);
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {

        for (int r : grantResults) {
            if (r== PackageManager.PERMISSION_DENIED){
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR,PERMISSION_DENIED_ERROR));
                return;
            }
        }

        if (requestCode == TAKE_PIC_PER) {
            toVideo();
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode==101){

            String url = intent.getStringExtra("url");
            if (resultCode==202){
                new Thread(() -> {
                    File downloadCacheDirectory = Environment.getExternalStorageDirectory();
                    try {
                        final String callPath = SiliCompressor.with(cordova.getContext()).compressVideo(url,
                                downloadCacheDirectory.getAbsolutePath(), 0,0,2200000);
                        File file=new File(callPath);
                        // 通知图库更新
                        if (Build.VERSION.SDK_INT > 19) {
                            MediaScannerConnection.scanFile(cordova.getActivity(), new String[]{file.getAbsolutePath()}, new String[]{"video/mp4"}, null);
                        } else {
                            cordova.getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,  Uri.fromFile(file )));
                        }
                        cordova.getActivity().runOnUiThread(() -> callbackContext.success(callPath));
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }).start();
            }else if (resultCode==201){
                callbackContext.success(url);
            }else {
                callbackContext.error("访问图片失败");
            }


        }

    }

}

