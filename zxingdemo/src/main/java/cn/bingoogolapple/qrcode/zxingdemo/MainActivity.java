package cn.bingoogolapple.qrcode.zxingdemo;

import android.Manifest;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.litepal.LitePal;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zxingdemo.DTO.GoodsDTO;
import cn.bingoogolapple.qrcode.zxingdemo.constant.CommonConstant;
import cn.bingoogolapple.qrcode.zxingdemo.ui.common.GoodsActivity;
import cn.bingoogolapple.qrcode.zxingdemo.ui.list.GoodsItemListActivity;
import cn.bingoogolapple.qrcode.zxingdemo.ui.scan.GoodsScanActivity;
import cn.bingoogolapple.qrcode.zxingdemo.utils.FileUtil;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private static final int REQUEST_CODE_QRCODE_PERMISSIONS = 1;

    private static final String TAG = MainActivity.class.getSimpleName();

    private EditText searchGoodsInfoText;

    private EditText searchGoodsNumOrNameInfoText;

    private TextView totalGoodsInfoTextView;

    private TextView btnScanGoodsInfoTextView;

    private ImageButton btnSearchGoodsInfo;

    private ImageButton btnSearchNumOrNameGoodsInfo;

    private TextView goodsListTextView;


    private MyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        BGAQRCodeUtil.setDebug(false);

        init();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        // 读取数据库中项目条目
        int size = LitePal.count(GoodsDTO.class);
        totalGoodsInfoTextView.setText(String.format("目前库中共存在%d 种商品", size));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mScanGoodsInfo:
                startActivity(new Intent(this, GoodsScanActivity.class));
                break;
            case R.id.mInputGoodsInfoByHand:
                GoodsActivity.actionStart(this, null);
            default:
                break;
        }
        return true;
    }

    /**
     * 读取文件数据，加载系统数据
     */
    public void init() {
        app = (MyApplication) getApplication();

        searchGoodsInfoText = (EditText)findViewById(R.id.searchGoodsInfo);

        searchGoodsNumOrNameInfoText = (EditText)findViewById(R.id.searchNumOrNameGoodsInfo);

        totalGoodsInfoTextView = findViewById(R.id.totalGoodsInfo);



        btnSearchGoodsInfo = findViewById(R.id.btnSearchGoodsInfo);

        btnSearchNumOrNameGoodsInfo = findViewById(R.id.btnSearchNumOrNameGoodsInfo);

        btnSearchNumOrNameGoodsInfo.setOnClickListener(v -> searchGoodsNumOrNameInfo(v));

        btnScanGoodsInfoTextView = findViewById(R.id.scanGoodsInfo);
        btnScanGoodsInfoTextView.setOnClickListener(v -> GoodsScanActivity.actionStart(v.getContext()));


        btnSearchGoodsInfo.setOnClickListener(v -> searchGoodsInfo(v));

        goodsListTextView = findViewById(R.id.goodsList);
        goodsListTextView.setOnClickListener(v -> GoodsItemListActivity.actionStart(v.getContext(), null));

    }

    public String matchGoodsNum(String searchGoodsNum) {
        Iterator<Map.Entry<String, GoodsDTO>> it = app.goodsMap.entrySet().iterator();
        Map.Entry<String, GoodsDTO> goodsEntry;
        GoodsDTO goods;
        String goodsNum;
        String goodsShortNum;
        while (it.hasNext()) {
            goodsEntry = it.next();
            goodsNum = goodsEntry.getKey();
            goods = goodsEntry.getValue();
            goodsShortNum = GoodsDTO.defaultShortNum(goodsNum);
            if (StringUtils.equals(goodsShortNum, searchGoodsNum)) {
                return goodsNum;
            }
            if (StringUtils.equals(goods.getShortNum(), searchGoodsNum)) {
                return goodsNum;
            }
        }
        return null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestCodeQRCodePermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        switch (requestCode) {
            case REQUEST_CODE_QRCODE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (perms.size() > 0
                        && perms.contains(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    MyApplication app = (MyApplication) getApplication();
                    // Permission has already been granted
                    try {
                        app.goodsMap = FileUtil.loadGoodsData(CommonConstant.PERSISTENCE_FILE_NAME);
                    } catch (Exception e) {
                        Log.e(TAG, "加载文件数据出错，请检查文件", e);
                    }

                } else {
                    Toast.makeText(this, "拒绝授予权限，无法保存", Toast.LENGTH_SHORT).show();
//                    Log.e(TAG, "拒绝授予权限，无法保存");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
            break;
        default:
                break;
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }

    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    private void requestCodeQRCodePermissions() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, };
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "扫描二维码需要打开相机和散光灯的权限", REQUEST_CODE_QRCODE_PERMISSIONS, perms);
        }
    }

    public void searchGoodsInfo(View view) {
        String goodsNum = searchGoodsInfoText.getText().toString().trim();
        if (StringUtils.isBlank(goodsNum)) {
            Toast.makeText(MainActivity.this, "商品编号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String matchGoodsNum = matchGoodsNum(goodsNum);
        if (matchGoodsNum == null) {
            Toast.makeText(MainActivity.this, "库中不存在此商品信息，请先录入", Toast.LENGTH_SHORT).show();
            searchGoodsInfoText.setText("");
            return;
        }
        GoodsActivity.actionStart(this, matchGoodsNum);
    }

    public void searchGoodsNumOrNameInfo(View view) {
        String goodsNumOrName = searchGoodsNumOrNameInfoText.getText().toString().trim();
        if (StringUtils.isBlank(goodsNumOrName)) {
            Toast.makeText(MainActivity.this, "商品编号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        GoodsItemListActivity.actionStart(this, goodsNumOrName);
    }
}
