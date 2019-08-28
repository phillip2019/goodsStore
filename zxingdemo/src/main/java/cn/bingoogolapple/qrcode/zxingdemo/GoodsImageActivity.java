package cn.bingoogolapple.qrcode.zxingdemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

import static cn.bingoogolapple.qrcode.zxingdemo.constant.CommonConstant.CURRENT_GOODS_ID;
import static cn.bingoogolapple.qrcode.zxingdemo.constant.CommonConstant.CURRENT_IMAGE_PATH;

public class GoodsImageActivity extends AppCompatActivity {
    private static final String TAG = GoodsImageActivity.class.getSimpleName();

    private int mLastFirstPostion;
    private int mLastFirstTop;
    private int touchSlop;

    private ImageView bigGoodsImageView;

    private String goodsId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_image);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        init();

        loadLocalImage();

        addOnClick();
    }

    public void init() {
        bigGoodsImageView = (ImageView)findViewById(R.id.bigGoodsImage);
    }

    private void loadLocalImage() {
        Intent intent = getIntent();
        String imageFilePath = intent.getStringExtra(CURRENT_IMAGE_PATH);
        goodsId = intent.getStringExtra(CURRENT_GOODS_ID);
        File imageFile = new File(imageFilePath);
        try {
            //加载图片
            Glide.with(this).load(Uri.fromFile(imageFile)).into(bigGoodsImageView);
        } catch (Exception e) {
            Log.e(TAG, "加载图片异常", e);
//            Intent goodsIntent = new Intent(this, GoodsActivity.class);
//            goodsIntent.putExtra(CURRENT_GOODS_ID, goodsId);
            // 跳回原页面，销毁，避免老是存入栈中
            finish();
        }
    }



    private void addOnClick() {
//        final Intent goodsIntent = new Intent();
        bigGoodsImageView.setOnTouchListener((v, event) -> {
            if (v.getId() == R.id.bigGoodsImage) {
//                goodsIntent.putExtra(CURRENT_GOODS_ID, goodsId);
//                startActivity(goodsIntent);
                finish();
            }
            return true;
        });
    }


}