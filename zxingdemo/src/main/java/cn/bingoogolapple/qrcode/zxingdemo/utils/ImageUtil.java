package cn.bingoogolapple.qrcode.zxingdemo.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class ImageUtil {

    public static byte[] getBytes(Bitmap bitmap){
        //实例化字节数组输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);//压缩位图
        //创建分配字节数组
        return baos.toByteArray();
    }

    public static Bitmap getBitmap(byte[] data){
        // 从字节数组解码位图
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }
}
