package com.blueeagle.flashlight;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

public class WhiteActivity extends AppCompatActivity {

    private RelativeLayout btnBack;
    private SeekBar sbBrightness;
    private SharePreManager sharePreManager;
    private float bv = 1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide notificationbar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_white);

        // Make screen always is on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sharePreManager = new SharePreManager(this);
        bv = sharePreManager.getBrightnessValue();

        // Make max brightness
        setScreenBrightness(bv);

        btnBack = (RelativeLayout) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        sbBrightness = (SeekBar) findViewById(R.id.sbBrightness);
        sbBrightness.setProgress((int) (bv * 100));

        sbBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                bv = progress / 100f;
                setScreenBrightness(bv);
                sharePreManager.saveBrightness(bv);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void setScreenBrightness(float value) {
        if (value <= 0.01f)
            value = 0.01f;

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = value;
        getWindow().setAttributes(layoutParams);
    }

    @Override
    public void onBackPressed() {
        // Clear screen always is on
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        finish();
        super.onBackPressed();
    }
}
