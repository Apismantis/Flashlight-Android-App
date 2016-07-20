package com.blueeagle.flashlight;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {

    private SharePreManager sharePreManager;
    private ViewPager slider;
    private LinearLayout layoutDots;
    private Button btnNext;
    private ViewPaperAdapter adapter;
    private int[] layouts;
    private TextView[] dots;
    private int currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide notificationbar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_welcome);

        // Launch home activity if not first time
        sharePreManager = new SharePreManager(this);
        if (!sharePreManager.isFirstLaunch()) {
            launchHomeActivity();
        }

        // Save first time launch
        //sharePreManager.SetFirstTimeLaunch(true);

        // init view
        initView();

        // Set listner
        setListener();

        layouts = new int[]{
                R.layout.layout_welcome_1,
                R.layout.layout_welcome_1,
                R.layout.layout_welcome_1,
                R.layout.layout_welcome_1
        };

        adapter = new ViewPaperAdapter(this, layouts);
        slider.setAdapter(adapter);
        slider.addOnPageChangeListener(onPageChangeListener);

        addBottomDots(currentPage);
    }

    public void launchHomeActivity() {
        Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public void initView() {
        slider = (ViewPager) findViewById(R.id.slider);
        layoutDots = (LinearLayout) findViewById(R.id.layoutDots);
        btnNext = (Button) findViewById(R.id.btnNext);
    }

    public void setListener() {

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPage++;

                if (currentPage == layouts.length)
                    launchHomeActivity();

                currentPage = currentPage % layouts.length;
                slider.setCurrentItem(currentPage);
            }
        });
    }

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            addBottomDots(position);
            //currentPage = position;

            if (currentPage == layouts.length - 1)
                btnNext.setText("GET STATER");
            else
                btnNext.setText("NEXT");
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            Log.d("AB", "Page scroll");
        }
    };

    public void addBottomDots(int position) {
        dots = new TextView[layouts.length];
        layoutDots.removeAllViews();

        for (int i = 0; i < layouts.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextColor(getResources().getColor(R.color.dot_dark));
            dots[i].setTextSize(35);
            layoutDots.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(getResources().getColor(R.color.dot_light));
    }
}
