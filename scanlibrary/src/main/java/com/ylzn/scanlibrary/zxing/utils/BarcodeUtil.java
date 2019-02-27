package com.ylzn.scanlibrary.zxing.utils;


import android.content.Intent;

import com.google.zxing.BarcodeFormat;
import com.ylzn.scanlibrary.zxing.qrcode.CreamCode;

/**
 * @author Richard_zhou
 * @date 2019/2/25
 * Describe:
 */
public class   BarcodeUtil {

//    /** Aztec 2D barcode format. */
//    AZTEC,
//
//    /** CODABAR 1D format. */
//    CODABAR,
//
//    /** Code 39 1D format. */
//    CODE_39,
//
//    /** Code 93 1D format. */
//    CODE_93,
//
//    /** Code 128 1D format. */
//    CODE_128,
//
//    /** Data Matrix 2D barcode format. */
//    DATA_MATRIX,
//
//    /** EAN-8 1D format. */
//    EAN_8,
//
//    /** EAN-13 1D format. */
//    EAN_13,
//
//    /** ITF (Interleaved Two of Five) 1D format. */
//    ITF,
//
//    /** MaxiCode 2D barcode format. */
//    MAXICODE,
//
//    /** PDF417 format. */
//    PDF_417,
//
//    /** QR Code 2D barcode format. */
//    QR_CODE,
//
//    /** RSS 14 */
//    RSS_14,
//
//    /** RSS EXPANDED */
//    RSS_EXPANDED,
//
//    /** UPC-A 1D format. */
//    UPC_A,
//
//    /** UPC-E 1D format. */
//    UPC_E,
//
//    /** UPC/EAN extension format. Not a stand-alone format. */
//    UPC_EAN_EXTENSION

    public static BarcodeFormat getBarcodeFormat(String formatType){
        BarcodeFormat format = null;
        if("AZTEC".equals(formatType)){
            format = BarcodeFormat.AZTEC;

        }else if("CODABAR".equals(formatType)) {
            format = BarcodeFormat.CODABAR;

        }else if("CODE_39".equals(formatType)) {
            format = BarcodeFormat.CODE_39;

        }else if("CODE_93".equals(formatType)) {
            format = BarcodeFormat.CODE_93;

        }else if("CODE_128".equals(formatType)) {
            format = BarcodeFormat.CODE_128;

        }else if("DATA_MATRIX".equals(formatType)) {
            format = BarcodeFormat.DATA_MATRIX;

        }else if("EAN_8".equals(formatType)) {
            format = BarcodeFormat.EAN_8;

        }else if("EAN_13".equals(formatType)) {
            format = BarcodeFormat.EAN_13;

        }else if("ITF".equals(formatType)) {
            format = BarcodeFormat.ITF;
        }else if("QR_CODE".equals(formatType)) {
            format = BarcodeFormat.QR_CODE;
        }

        return format;
    }


/*    *//** Aztec 2D barcode format. *//*
    public static BarcodeFormat AZTEC = BarcodeFormat.AZTEC;

    *//** CODABAR 1D format. *//*
    public static BarcodeFormat CODABAR = BarcodeFormat.CODABAR;

    *//** Code 39 1D format. *//*
    public static BarcodeFormat CODE_39 = BarcodeFormat.CODE_39;

    *//** Code 93 1D format. *//*
    public static BarcodeFormat CODE_93 = BarcodeFormat.CODE_93;

    *//** Code 128 1D format. *//*
    public static BarcodeFormat CODE_128 = BarcodeFormat.CODE_128;

    *//** Data Matrix 2D barcode format. *//*
    public static BarcodeFormat DATA_MATRIX = BarcodeFormat.DATA_MATRIX;

    *//** EAN-8 1D format. *//*
    public static BarcodeFormat EAN_8 = BarcodeFormat.EAN_8;

    *//** EAN-13 1D format. *//*
    public static BarcodeFormat EAN_13 = BarcodeFormat.EAN_13;

    *//** ITF (Interleaved Two of Five) 1D format. *//*
    public static BarcodeFormat ITF = BarcodeFormat.ITF;

    *//** MaxiCode 2D barcode format. *//*
    public static BarcodeFormat MAXICODE = BarcodeFormat.MAXICODE;

    *//** PDF417 format. *//*
    public static BarcodeFormat PDF_417 = BarcodeFormat.PDF_417;

    *//** QR Code 2D barcode format. *//*
    public static BarcodeFormat QR_CODE = BarcodeFormat.QR_CODE;

    *//** RSS 14 *//*
    public static BarcodeFormat RSS_14 = BarcodeFormat.RSS_14;

    *//** RSS EXPANDED *//*
    public static BarcodeFormat RSS_EXPANDED = BarcodeFormat.RSS_EXPANDED;

    *//** UPC-A 1D format. *//*
    public static BarcodeFormat UPC_A = BarcodeFormat.UPC_A;

    *//** UPC-E 1D format. *//*
    public static BarcodeFormat UPC_E = BarcodeFormat.UPC_E;

    *//** UPC/EAN extension format. Not a stand-alone format. *//*
    public static BarcodeFormat UPC_EAN_EXTENSION = BarcodeFormat.UPC_EAN_EXTENSION;*/
}
