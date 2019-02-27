package com.cameracode;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.ylzn.scanlibrary.zxing.qrcode.CreamCode;
import com.ylzn.scanlibrary.zxing.scan.view.BaseColorActivity;
import com.ylzn.scanlibrary.zxing.scan.view.CameraCaptureActivity;
import com.ylzn.scanlibrary.zxing.utils.CheckPermissionUtils;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.os.Build.VERSION.SDK;

public class MainActivity extends BaseColorActivity implements View.OnClickListener,EasyPermissions.PermissionCallbacks{

    private final static int SCANNING_REQUEST_CODE = 1;
    private Bitmap QRCode_NO;
    private Button button,button1,button2,button3;
    private ImageView imageView;


    public static final String P1 = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String P2 = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String P3 = Manifest.permission.CAMERA;


    /**
     * 请求CAMERA权限码
     */
    public static final int REQUEST_CAMERA_PERM = 101;

    @Override
    protected int getLayoutRsId() {
        return R.layout.activity_main;
    }

    @Override
    protected void setUI() {
        button  =  (Button) findViewById(R.id.button);
        button1  =  (Button) findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        imageView = findViewById(R.id.image);
        button.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);

        CheckPermissionUtils.initPermission(this,this);

    }

    @Override
    protected void initData() {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNING_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String barcode = bundle.getString("barcode");
                    Log.e("xxxx",barcode);
                    if(null != barcode && !"".equals(barcode)) {
                        try {

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                cameraTask();
                break;
            case R.id.button1:

                QRCode_NO = CreamCode.createCodeBitmap(450,450,"https://www.uc123.com/", "QR_CODE");
                imageView.setImageBitmap(QRCode_NO);


                break;
            case R.id.button2:
                QRCode_NO = CreamCode.creamQRcodeLogo(450,450,"https://www.uc123.com/",getResources(),R.drawable.timg);
                imageView.setImageBitmap(QRCode_NO);
                break;
            case R.id.button3:
                QRCode_NO = CreamCode.createCodeBitmap(500,200,"123465789","CODE_128");
                imageView.setImageBitmap(QRCode_NO);
                break;
            default:break;
        }
    }








    @AfterPermissionGranted(REQUEST_CAMERA_PERM)
    public void cameraTask() {
        if (EasyPermissions.hasPermissions(this, P1,P2,P3)) {
            // Have permission, do the thing!

            Intent intent = new Intent();
            intent.setClass(MainActivity.this, CameraCaptureActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, SCANNING_REQUEST_CODE);

        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, "需要请求camera权限",
                    REQUEST_CAMERA_PERM, P1,P2,P3);
        }
    }




    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this, "当前App需要申请camera权限,需要打开设置页面么?")
                    .setTitle("权限申请")
                    .setPositiveButton("确认")
                    .setNegativeButton("取消", null /* click listener */)
                    .setRequestCode(REQUEST_CAMERA_PERM)
                    .build()
                    .show();
        }
    }


    /**
     * EsayPermissions接管权限处理逻辑
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);

    }

}
