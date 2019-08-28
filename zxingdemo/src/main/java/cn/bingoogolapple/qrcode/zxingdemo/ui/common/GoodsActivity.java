package cn.bingoogolapple.qrcode.zxingdemo.ui.common;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;
import cn.bingoogolapple.qrcode.zxingdemo.DTO.GoodsDTO;
import cn.bingoogolapple.qrcode.zxingdemo.MyApplication;
import cn.bingoogolapple.qrcode.zxingdemo.R;
import cn.bingoogolapple.qrcode.zxingdemo.constant.CommonConstant;
import cn.bingoogolapple.qrcode.zxingdemo.utils.FileUtil;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static cn.bingoogolapple.qrcode.zxingdemo.constant.CommonConstant.CURRENT_GOODS_ID;
import static cn.bingoogolapple.qrcode.zxingdemo.constant.CommonConstant.CURRENT_IMAGE_PATH;
import static cn.bingoogolapple.qrcode.zxingdemo.constant.CommonConstant.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;
import static cn.bingoogolapple.qrcode.zxingdemo.constant.CommonConstant.REQUEST_TAKE_PHOTO;

public class GoodsActivity extends AppCompatActivity implements QRCodeView.Delegate, EasyPermissions.PermissionCallbacks {
    private static final String TAG = GoodsActivity.class.getSimpleName();
    private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 666;

    private Map<String, GoodsDTO> goodsMap = null;

    private String mCurrentImagePath = null;

    private String mCurrentGoodsId;

    // 商品编号编辑框
    private EditText goodsIdView;

    private EditText goodsCategoryView;

    private EditText goodsNameView;

    private EditText goodsTradePriceView;

    private EditText goodsInventoryCountsView;

    private EditText goodsRetailPrice1View;

    private EditText goodsRetailPrice2View;

    private EditText goodsRetailPrice3View;

    private ImageView goodsImageView;

    private EditText goodsRemark;

    private TextView btnGoodsSaveTextView;

    private ZXingView mZXingView;

    public static void actionStart(Context context, String goodsId) {
        Intent intent = new Intent(context, GoodsActivity.class);
        intent.putExtra(CURRENT_GOODS_ID, goodsId);
        context.startActivity(intent);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        init();

        // 长按，开启后置摄像头拍照
        goodsImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (v.getId() == R.id.goodsImage) {
                    dispatchTakePictureIntent();
                } else {
                    return false;
                }
                return true;
            }
        });

        final Intent bigImageIntent = new Intent(this, GoodsImageActivity.class);
        // 单击，若存在图片，则放大
        goodsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "单击了图片信息" + mCurrentImagePath);
                if (StringUtils.isNotBlank(mCurrentImagePath)) {
                    bigImageIntent.putExtra(CURRENT_GOODS_ID, mCurrentGoodsId);
                    bigImageIntent.putExtra(CURRENT_IMAGE_PATH, mCurrentImagePath);
                    startActivity(bigImageIntent);
                }

            }
        });
        btnGoodsSaveTextView.setOnClickListener(v -> {
            GoodsDTO goods = view2Data();
            // 按下保存按钮，将数据塞入到application context的map中
            goodsMap.put(goods.getId(), goods);
            FileUtil.dumpsGoodsData(CommonConstant.PERSISTENCE_FILE_NAME, goodsMap);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        // 初始化数据
        getGoodsNum();
    }

    // 初始化
    private void init() {
        // 得到相应控件引用
        goodsIdView = (EditText)findViewById(R.id.goodsId);
        goodsCategoryView = (EditText)findViewById(R.id.goodsCategory);
        goodsNameView = (EditText)findViewById(R.id.goodsName);
        goodsTradePriceView = (EditText)findViewById(R.id.tradePrice);
        goodsInventoryCountsView = (EditText)findViewById(R.id.inventoryCounts);
        goodsRetailPrice1View = (EditText)findViewById(R.id.retailPrice1);
        goodsRetailPrice2View = (EditText)findViewById(R.id.retailPrice2);
        goodsRetailPrice3View = (EditText)findViewById(R.id.retailPrice3);
        goodsImageView = (ImageView)findViewById(R.id.goodsImage);
        goodsRemark = (EditText)findViewById(R.id.remark);
        btnGoodsSaveTextView = (TextView)findViewById(R.id.btnGoodsSave);

        MyApplication app = (MyApplication) getApplication();
        goodsMap = app.goodsMap;
    }

    private void getGoodsNum() {
        // 获取传递的商品编号
        Intent goodsIntent = getIntent();
        String goodsNum = goodsIntent.getStringExtra(CURRENT_GOODS_ID);
        mCurrentGoodsId = goodsNum;
        goodsIdView.setText(goodsNum);

        if (goodsMap.containsKey(goodsNum)) {
            Log.e(TAG, "已存储此商品信息");
            GoodsDTO goods = goodsMap.get(goodsNum);
            assert goods != null;
            goodsCategoryView.setText(goods.getCategory());
            goodsNameView.setText(goods.getName());
            goodsTradePriceView.setText(String.valueOf(goods.getTradePrice()));
            goodsInventoryCountsView.setText(String.valueOf(goods.getCounts()));
            goodsRetailPrice1View.setText(String.valueOf(goods.getRetailPrice1()));
            goodsRetailPrice2View.setText(String.valueOf(goods.getRetailPrice2()));
            goodsRetailPrice3View.setText(String.valueOf(goods.getRetailPrice3()));
            if (StringUtils.isNotBlank(goods.getImagePath())) {
                mCurrentImagePath = goods.getImagePath();
                Log.e(TAG, "加载的图片路径为:" + goods.getImagePath());
                FileUtil.setImage4ViewBitmap(goodsImageView, goods.getImagePath());
            }
            goodsRemark.setText(goods.getRemark());
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent();
        takePictureIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        takePictureIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = FileUtil.getDownloadStorage4Image(goodsIdView.getText().toString());

                // 获取当前图像路径
                mCurrentImagePath = photoFile.getAbsolutePath();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(TAG, "创建共享存储图片文件失败", ex);
                mCurrentImagePath = null;
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri imageUri = FileProvider.getUriForFile(this, "cn.bingoogolapple.qrcode.zxingdemo.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        requestWriteExternalStoragePermissions();
    }

    @Override
    protected void onStop() {
        if (mZXingView != null) {
            mZXingView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mZXingView != null) {
            mZXingView.onDestroy(); // 销毁二维码扫描控件
        }
        super.onDestroy();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        Log.i(TAG, "result:" + result);
        setTitle("扫描结果为：" + result);
        vibrate();

        if (mZXingView != null) {
            mZXingView.startSpot(); // 开始识别
        }
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
        // 这里是通过修改提示文案来展示环境是否过暗的状态，接入方也可以根据 isDark 的值来实现其他交互效果
        String tipText = mZXingView.getScanBoxView().getTipText();
        String ambientBrightnessTip = "\n环境过暗，请打开闪光灯";
        if (isDark) {
            if (!tipText.contains(ambientBrightnessTip)) {
                mZXingView.getScanBoxView().setTipText(tipText + ambientBrightnessTip);
            }
        } else {
            if (tipText.contains(ambientBrightnessTip)) {
                tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip));
                mZXingView.getScanBoxView().setTipText(tipText);
            }
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");
    }

    public boolean onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGoodsSave:
                GoodsDTO goods = view2Data();

                // 按下保存按钮，将数据塞入到application context的map中
                goodsMap.put(goods.getId(), goods);
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                } else {
                    // Permission has already been granted
                    FileUtil.dumpsGoodsData(CommonConstant.PERSISTENCE_FILE_NAME, goodsMap);
                }
                break;
            case R.id.goodsImage:
                Log.e(TAG, "单击了图片信息");
                Intent bigImageIntent = new Intent(this, GoodsImageActivity.class);
                bigImageIntent.putExtra(CURRENT_GOODS_ID, mCurrentGoodsId);
                bigImageIntent.putExtra(CURRENT_IMAGE_PATH, mCurrentImagePath);
                startActivity(bigImageIntent);
                break;
        default:
            break;
        }
        return true;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return super.onKeyLongPress(keyCode, event);
    }


    private GoodsDTO view2Data() {
        GoodsDTO goods = new GoodsDTO();
        String id = goodsIdView.getText().toString();
        String category = goodsCategoryView.getText().toString();
        String name = goodsNameView.getText().toString();
        Integer counts = 0;
        try {
            counts = Integer.valueOf(goodsInventoryCountsView.getText().toString());
        } catch (Exception e) {
            Log.e(TAG, "将字符串转换成数字异常");
        }
        Double tradePrice = getEditTextViewValue(goodsTradePriceView);
        Double retailPrice1 = getEditTextViewValue(goodsRetailPrice1View);
        Double retailPrice2 = getEditTextViewValue(goodsRetailPrice2View);
        Double retailPrice3 = getEditTextViewValue(goodsRetailPrice3View);
        goods.setImagePath("");
        if (mCurrentImagePath != null) {
            goods.setImagePath(mCurrentImagePath);
        }
        String remark = goodsRemark.getText().toString();


        goods.setId(id);
        goods.setCategory(category);
        goods.setName(name);
        goods.setCounts(counts);
        goods.setTradePrice(tradePrice);
        goods.setRetailPrice1(retailPrice1);
        goods.setRetailPrice2(retailPrice2);
        goods.setRetailPrice3(retailPrice3);
        goods.setRemark(remark);

        return goods;
    }

    private Double getEditTextViewValue(EditText editText) {
        Double v = 0.0;
        try {
            v = Double.valueOf(editText.getText().toString());
        } catch (Exception e) {
            Log.e(TAG, "将字符串转换成double异常");
        }
        return v;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            // 判断mCurrentImagePath是否有值，若有值，加载缩略图
            if (mCurrentImagePath != null) {
                FileUtil.setImage4ViewBitmap(goodsImageView, mCurrentImagePath);
            }
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)
    private void requestWriteExternalStoragePermissions() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "此应用需要持久化存储，请授权写磁盘权限", MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE, perms);
        }
    }
}