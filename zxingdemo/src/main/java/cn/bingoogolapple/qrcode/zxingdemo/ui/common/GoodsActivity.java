package cn.bingoogolapple.qrcode.zxingdemo.ui.common;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.apache.commons.lang3.StringUtils;
import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;
import cn.bingoogolapple.qrcode.zxingdemo.DTO.GoodsDTO;
import cn.bingoogolapple.qrcode.zxingdemo.MyApplication;
import cn.bingoogolapple.qrcode.zxingdemo.R;
import cn.bingoogolapple.qrcode.zxingdemo.constant.CommonConstant;
import cn.bingoogolapple.qrcode.zxingdemo.ui.list.GoodsItemListActivity;
import cn.bingoogolapple.qrcode.zxingdemo.utils.FileUtil;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static cn.bingoogolapple.qrcode.zxingdemo.constant.CommonConstant.CURRENT_GOODS_NUM;
import static cn.bingoogolapple.qrcode.zxingdemo.constant.CommonConstant.CURRENT_IMAGE_PATH;
import static cn.bingoogolapple.qrcode.zxingdemo.constant.CommonConstant.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;
import static cn.bingoogolapple.qrcode.zxingdemo.constant.CommonConstant.REQUEST_TAKE_PHOTO;

public class GoodsActivity extends AppCompatActivity implements QRCodeView.Delegate, EasyPermissions.PermissionCallbacks {
    private static final String TAG = GoodsActivity.class.getSimpleName();
    private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 666;

    private Map<String, GoodsDTO> goodsMap = null;

    private String mCurrentImagePath = null;

    private String mCurrentGoodsNum;

    // 商品编号编辑框
    private EditText goodsNumView;

    // 商品货号编辑框
    private EditText goodsShortNumView;

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

    public static void actionStart(Context context, String goodsNum) {
        Intent intent = new Intent(context, GoodsActivity.class);
        intent.putExtra(CURRENT_GOODS_NUM, goodsNum);
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
                if (!preCheck()) {
                    return false;
                }

                dispatchTakePictureIntent();
                return true;
            }
        });

        final Intent bigImageIntent = new Intent(this, GoodsImageActivity.class);


        // 添加单击事件，若存在图片，则放大
        goodsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!preCheck()) {
                    return;
                }

//                if (StringUtils.isNotBlank(mCurrentImagePath) && StringUtils.isNotBlank(mCurrentGoodsNum)) {
//                    Intent photoIntent =new Intent(Intent.ACTION_VIEW);
//                    File photoFile = new File(mCurrentImagePath);
//                    Uri imageUri = FileProvider.getUriForFile(v.getContext(), "cn.bingoogolapple.qrcode.zxingdemo.fileprovider", photoFile);
//                    Log.e(TAG, "FileProvider生成的文件格式名为: " + imageUri.toString());
//                    photoIntent.setDataAndType(imageUri,"image/*");
//                    photoIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                    startActivity(photoIntent);
//                }
                if (StringUtils.isNotBlank(mCurrentImagePath) && StringUtils.isNotBlank(mCurrentGoodsNum)) {
                    bigImageIntent.putExtra(CURRENT_GOODS_NUM, mCurrentGoodsNum);
                    bigImageIntent.putExtra(CURRENT_IMAGE_PATH, mCurrentImagePath);
                    startActivity(bigImageIntent);
                }

            }
        });

        btnGoodsSaveTextView.setOnClickListener(v -> {
            if (!preCheck()) {
                return;
            }

            GoodsDTO goods = view2Data();
            // 按下保存按钮，将数据塞入到application context的map中
            goodsMap.put(goods.getNum(), goods);
            FileUtil.dumpsGoodsData(CommonConstant.PERSISTENCE_FILE_NAME, goodsMap);

            //查询库，查看是否存在，润存在则，更新；反之新增
            boolean flag = goods.saveOrUpdate("num = ?", goods.getNum());
            if (!flag) {
                Toast.makeText(this,
                        String.format("此商品添加或修改失败，商品编号为: {}", goods.getNum()),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            // 添加成功，跳转到主页面
            GoodsItemListActivity.actionStart(this, goods.getNum());
        });

    }

    private boolean preCheck() {
        Log.i(TAG, "当前商品号为: " + mCurrentGoodsNum);
        if (StringUtils.isBlank(mCurrentGoodsNum)) {
            Toast.makeText(this, "请先输入商品编号", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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
        goodsNumView = (EditText)findViewById(R.id.goodsNum);
        goodsNumView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // 失去焦点，回填mCurrentGoodsNum信息
                if (!hasFocus) {
                    goodsNumView.setText(StringUtils.trim(goodsNumView.getText().toString()));
                    mCurrentGoodsNum = goodsNumView.getText().toString();
                }
            }
        });

        goodsShortNumView = (EditText)findViewById(R.id.goodsShortNum);
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

    private static String generateGoodsNum() {
        List<String> goodsNumList = new ArrayList<>(2);
        goodsNumList.add("697");
        Random random = new Random();
        long randomLongValue = random.nextInt(1000000000);
        String goodsNumPart2 = String.format("%9d", randomLongValue);
        goodsNumList.add(goodsNumPart2);
        return StringUtils.join(goodsNumList, "");
    }

    private void getGoodsNum() {
        // 获取传递的商品编号
        Intent goodsIntent = getIntent();
        String goodsNum = goodsIntent.getStringExtra(CURRENT_GOODS_NUM);
        mCurrentGoodsNum = goodsNum;

        // 若手动录入，则不存在当前货物一维码编号，手动录入货物一维码编号
        if (StringUtils.isBlank(mCurrentGoodsNum)) {
            // 允许更改商品编号
            goodsNumView.setEnabled(true);

            // 自动生成商品编号
            String generatorGoodsNum = null;
            while (StringUtils.isBlank(generatorGoodsNum) || goodsMap.containsKey(generatorGoodsNum)) {
                generatorGoodsNum = generateGoodsNum();
            }
            mCurrentGoodsNum = generatorGoodsNum;
            goodsNumView.setText(mCurrentGoodsNum);
            return;
        }

        goodsNumView.setText(goodsNum);
        // 数据库中查询
        List<GoodsDTO> goodsDTOList = LitePal.where("num = ? ", goodsNum).find(GoodsDTO.class);
        if (goodsDTOList != null && goodsDTOList.size() > 0) {
            GoodsDTO goods = goodsDTOList.get(0);
            Log.e(TAG, "已存储此商品信息");
            assert goods != null;
            if (StringUtils.isBlank(goods.getShortNum())) {
                goods.setShortNum(GoodsDTO.defaultShortNum(goods.getNum()));
            }
            goodsShortNumView.setText(goods.getShortNum());
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
                photoFile = FileUtil.getDownloadStorage4Image(goodsNumView.getText().toString());

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

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return super.onKeyLongPress(keyCode, event);
    }


    private GoodsDTO view2Data() {
        GoodsDTO goods = new GoodsDTO();
        String num = goodsNumView.getText().toString();
        String shortNum = goodsShortNumView.getText().toString();
        if (StringUtils.isBlank(shortNum)) {
            shortNum = GoodsDTO.defaultShortNum(num);
        }
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

        goods.setNum(num);
        goods.setShortNum(shortNum);
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