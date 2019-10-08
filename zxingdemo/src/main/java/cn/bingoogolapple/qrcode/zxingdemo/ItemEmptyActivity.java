package cn.bingoogolapple.qrcode.zxingdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class ItemEmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_empty);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
    }
}
