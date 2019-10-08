package cn.bingoogolapple.qrcode.zxingdemo.constant;

import java.io.Serializable;

public class CommonConstant implements Serializable {
    /**
     * 持久化到磁盘的文件名
     */
    public static final String PERSISTENCE_FILE_NAME = "store.csv";

    public static final String APP_DIRECTORY = "com.wuyan.store";

    public static final String APP_IMAGE_DIRECTORY = "images";

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 666;

    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 667;

    public static final int REQUEST_TAKE_PHOTO = 668;

    /**
     * 请求照相机权限
     */
    public static final int REQUEST_IMAGE_CAPTURE = 1;

    public static final String CURRENT_IMAGE_PATH = "cn.bingoogolapple.qrcode.currentImagePath";

    public static final String CURRENT_GOODS_NUM = "cn.bingoogolapple.qrcode.currentGoodsNum";

    public static final String SEARCH_GOODS_NUM_OR_NAME = "cn.bingoogolapple.qrcode.searchGoodsNumOrName";

}
