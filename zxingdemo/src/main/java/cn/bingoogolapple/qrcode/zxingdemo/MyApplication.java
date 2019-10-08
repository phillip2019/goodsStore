package cn.bingoogolapple.qrcode.zxingdemo;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import org.litepal.LitePal;

import java.util.HashMap;
import java.util.Map;

import cn.bingoogolapple.qrcode.zxingdemo.DTO.GoodsDTO;
import cn.bingoogolapple.qrcode.zxingdemo.constant.CommonConstant;
import cn.bingoogolapple.qrcode.zxingdemo.utils.FileUtil;

public class MyApplication extends Application {

    private static final String TAG = MyApplication.class.getSimpleName();

    // 商品类别字典，id作为key，类别详情作为value
    public Map<String, GoodsDTO> goodsMap = new HashMap<>(1000);

    public static final String preInsertGoodsTpl = "insert into ";

    public static final String insertGoodsTpl = "";


    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        LitePal.initialize(this);

        loadExternalStorageData();
    }

    /**
     * 载入磁盘文件的数据
     */
    public void loadExternalStorageData() {

        // Permission has already been granted
        try {
            // 启动时，拿文件中的编号数据，去数据库中查找，若未找到，则插入
//            goodsMap = FileUtil.loadGoodsData(CommonConstant.PERSISTENCE_FILE_NAME);
            goodsMap = FileUtil.loadGoodsDataByDB();
            Log.i(TAG, "从数据库中读取成功!");
        } catch (Exception e) {
            Log.e(TAG, "加载文件数据出错，请检查文件", e);
        }

//        try {
//            batchInsert2DB();
//        } catch (Exception e) {
//            Log.e(TAG, "loadExternalStorageData: batch insert goods to db", e);
////            SQLiteDatabase db = LitePal.getDatabase();
//            //清空goods表
//            LitePal.deleteAll(GoodsDTO.class, "1 = ?" , "1");
//        }
    }

    public void batchInsert2DB() {
        String sql = new StringBuilder()
                .append("INSERT INTO GoodsDTO(num, shortNum, category, ")
                .append(" name, counts, tradePrice, retailPrice1, ")
                .append(" retailPrice2, retailPrice3, imagePath, ")
                .append(" remark ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,")
                .append(" ?)")
                .toString();

        SQLiteDatabase db = LitePal.getDatabase();
        db.beginTransaction();
        GoodsDTO goodsDTO = null;
        for (Map.Entry<String, GoodsDTO> entry: goodsMap.entrySet()) {
            goodsDTO = entry.getValue();
            SQLiteStatement stmt = db.compileStatement(sql);
            stmt.bindString(1, goodsDTO.getNum());
            stmt.bindString(2, goodsDTO.getShortNum());
            stmt.bindString(3, goodsDTO.getCategory());
            stmt.bindString(4, goodsDTO.getName());
            stmt.bindLong(5, goodsDTO.getCounts());
            stmt.bindDouble(6, goodsDTO.getTradePrice());
            stmt.bindDouble(7, goodsDTO.getRetailPrice1());
            stmt.bindDouble(8, goodsDTO.getRetailPrice2());
            stmt.bindDouble(9, goodsDTO.getRetailPrice3());
            stmt.bindString(10, goodsDTO.getImagePath());
            stmt.bindString(11, goodsDTO.getRemark());
            stmt.execute();
            stmt.clearBindings();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public static Context getInstance() {
        return mContext;
    }

}
