package cn.bingoogolapple.qrcode.zxingdemo.ui.common;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.net.URI;

import cn.bingoogolapple.qrcode.zxingdemo.R;

import static cn.bingoogolapple.qrcode.zxingdemo.constant.CommonConstant.CURRENT_GOODS_NUM;
import static cn.bingoogolapple.qrcode.zxingdemo.constant.CommonConstant.CURRENT_IMAGE_PATH;

public class GoodsImageActivity extends AppCompatActivity {
    private static final String TAG = GoodsImageActivity.class.getSimpleName();

    private PhotoView bigGoodsImageView;

    private String goodsNum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设置为横屏
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_goods_image);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        init();

        loadLocalImage();

        addOnClick();
    }

    public void init() {
        bigGoodsImageView = findViewById(R.id.bigGoodsImage);
    }

    private void loadLocalImage() {
        Intent intent = getIntent();
        String imageFilePath = intent.getStringExtra(CURRENT_IMAGE_PATH);
        goodsNum = intent.getStringExtra(CURRENT_GOODS_NUM);
        File imageFile = new File(imageFilePath);
        try {
            //加载图片
            bigGoodsImageView.setImageURI(Uri.fromFile(imageFile));
//            Glide.with(this).load(Uri.fromFile(imageFile)).into(bigGoodsImageView);
        } catch (Exception e) {
            Log.e(TAG, "加载图片异常", e);
            // 跳回原页面，销毁，避免老是存入栈中
            finish();
        }
    }



    private void addOnClick() {
//        bigGoodsImageView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (v.getId() == R.id.bigGoodsImage) {
//                    GoodsImageActivity.this.finish();
//                }
//                return true;
//            }
//        });
    }


    /**
     * 获取屏幕宽度(px)
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }


}