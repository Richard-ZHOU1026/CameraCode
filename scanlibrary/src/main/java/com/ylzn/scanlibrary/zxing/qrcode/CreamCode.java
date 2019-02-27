package com.ylzn.scanlibrary.zxing.qrcode;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.ylzn.scanlibrary.zxing.utils.BarcodeUtil;

import java.util.Hashtable;

public class CreamCode{

    //二维码或条形码
    public static Bitmap createCodeBitmap(int QRCODE_WIDTH,int QRCODE_HEIGHT,String content,String formatType) {
        //设置打印类型
        BarcodeFormat format = BarcodeUtil.getBarcodeFormat(formatType);
        // 用于设置QR二维码参数
        Hashtable<EncodeHintType, Object> qrParam = new Hashtable<EncodeHintType, Object>();
        // 设置QR二维码的纠错级别——这里选择最高H级别
        /**
         *
            ERROR_CORRECTION	容错率，指定容错等级，例如二维码中使用的ErrorCorrectionLevel, Aztec使用Integer
            分为四个等级：L/M/Q/H, 等级越高，容错率越高，识别速度降低。例如一个角被损坏，容错率高的也许能够识别出来。通常为H

            CHARACTER_SET	编码集
            DATA_MATRIX_SHAPE	指定生成的数据矩阵的形状，类型为SymbolShapeHint
            MARGIN	生成条码的时候使用，指定边距，单位像素，受格式的影响。类型Integer, 或String代表的数字类型，默认为4, 实际效果并不是填写的值，一般默认值就行
            PDF417_COMPACT	指定是否使用PDF417紧凑模式（具体含义不懂）类型Boolean
            PDF417_COMPACTION	指定PDF417的紧凑类型
            PDF417_DIMENSIONS	指定PDF417的最大最小行列数
            AZTEC_LAYERS	aztec编码相关，不理解
            QR_VERSION	指定二维码版本，版本越高越复杂，反而不容易解析

            适用于二维码的有：ERROR_CORRECTION, CHARACTER_SET, MARGIN, QR_VERSION.

         */
        qrParam.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        // 设置编码方式
        qrParam.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        // 参数顺序分别为：编码内容，编码类型，生成图片宽度，生成图片高度，设置参数
        //MultiFormatWriter设置二维码的边距
        //生成图形编码的方式
        //BitMatrix	二维码的描述对象

        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
                    format, QRCODE_WIDTH, QRCODE_HEIGHT, qrParam);
            // 开始利用二维码数据创建Bitmap图片，分别设为黑白两色
            int widthPix = bitMatrix.getWidth();
            int heightPix = bitMatrix.getHeight();
            int[] data = new int[widthPix * heightPix];

            for (int y = 0; y < heightPix; y++) {
                for (int x = 0; x < widthPix; x++) {
                    if (bitMatrix.get(x, y))
                        data[y * widthPix + x] = 0xff000000;// 黑色
                    else
                        data[y * widthPix + x] = -1;// -1 相当于0xffffffff 白色
                }
            }

            // 创建一张bitmap图片，采用最高的图片效果ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(widthPix, heightPix, Bitmap.Config.ARGB_8888);
            // 将上面的二维码颜色数组传入，生成图片颜色
            bitmap.setPixels(data, 0, widthPix, 0, 0, widthPix, heightPix);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static Bitmap creamQRcodeLogo(int QRCODE_WIDTH,int QRCODE_HEIGHT,String content,Resources res,int id) {

            Bitmap qr = null;
            Bitmap logo =null;
            if(QRCODE_WIDTH != 0 && QRCODE_HEIGHT != 0 && content != null ) {
                qr = createCodeBitmap(QRCODE_WIDTH,QRCODE_HEIGHT,content,"QR_CODE");
            }
            if(id != 0) {
                logo = BitmapFactory.decodeResource(res,id);
            }
            if(qr != null){
                //获取图片的宽高
                int srcWidth = qr.getWidth();
                int srcHeight = qr.getHeight();
                int logoWidth = logo.getWidth();
                int logoHeight = logo.getHeight();

                //logo大小为二维码整体大小的1/5
                float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
                try {
                    Canvas canvas = new Canvas(qr);
                    canvas.drawBitmap(qr, 0, 0, null);
                    canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
                    canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);
                    canvas.save();
                    canvas.restore();
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
            return qr;
    }



}
