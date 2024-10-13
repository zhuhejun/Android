package com.example.new4;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class PictureActivity extends AppCompatActivity {

    private String bigImage;
    ImageView ivCommodity;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);


        //不显示顶部状态栏
        if(Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }

        Bundle bundle=this.getIntent().getExtras();
        bigImage= bundle.getString("bigImage");
        ivCommodity=findViewById(R.id.iv_commodity);

        if (isFinishing()) {
            //不加载
        }else if (!bigImage.equals("111")) {
            Glide.with(this)
                    .load(bigImage)
                    .into(ivCommodity);
        } else {
            // 处理无图片数据时的逻辑，比如显示默认图片
            ivCommodity.setImageResource(R.drawable.open2);
        }

        ivCommodity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

    }

    //重写系统返回
    @Override
    public void onBackPressed() {
        super.onBackPressed(); // 保持系统返回逻辑
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out); // 添加动画
    }
}
