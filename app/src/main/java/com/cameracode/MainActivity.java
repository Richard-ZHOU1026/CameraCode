package com.cameracode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.zxing.qrcode.CreamCode;
import com.zxing.scan.view.CameraCaptureActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private final static int SCANNING_REQUEST_CODE = 1;
    private Bitmap QRCode_NO;
    private Button button,button1,button2,button3;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button  =  (Button) findViewById(R.id.button);
        button1  =  (Button) findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        imageView = findViewById(R.id.image);
        button.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);


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
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, CameraCaptureActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, SCANNING_REQUEST_CODE);
                break;
            case R.id.button1:
                QRCode_NO = CreamCode.createCodeBitmap(450,450,"sinaweibo://userinfo?uid=2568190010", BarcodeFormat.QR_CODE);
                imageView.setImageBitmap(QRCode_NO);
                break;
            case R.id.button2:
                QRCode_NO = CreamCode.creamQRcodeLogo(450,450,"https://www.uc123.com/",getResources(),R.drawable.timg);
                imageView.setImageBitmap(QRCode_NO);
                break;
            case R.id.button3:
                QRCode_NO = CreamCode.createCodeBitmap(500,200,"123465789",BarcodeFormat.CODE_128);
                imageView.setImageBitmap(QRCode_NO);
                break;
            default:break;
        }
    }
}
