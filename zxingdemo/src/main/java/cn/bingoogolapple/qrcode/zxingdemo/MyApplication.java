package cn.bingoogolapple.qrcode.zxingdemo;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import cn.bingoogolapple.qrcode.zxingdemo.DTO.GoodsDTO;
import cn.bingoogolapple.qrcode.zxingdemo.constant.CommonConstant;
import cn.bingoogolapple.qrcode.zxingdemo.utils.FileUtil;

import static cn.bingoogolapple.qrcode.zxingdemo.constant.CommonConstant.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;

public class MyApplication extends Application {

    private static final String TAG = MyApplication.class.getSimpleName();

    // 商品类别字典，id作为key，类别详情作为value
    public Map<String, GoodsDTO> goodsMap = new HashMap<>(1000);


    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    /**
     * 加载/data/store/goods.csv数据，数据格式按照GoodsDTO中定义
     * @return
     */
    public boolean loadsGoodsData() {
        return false;
    }

    public static Context getInstance() {
        return mContext;
    }

}
