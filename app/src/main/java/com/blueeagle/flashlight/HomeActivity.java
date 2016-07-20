package com.blueeagle.flashlight;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class HomeActivity extends AppCompatActivity {

    private TextView tvLightStatus;
    private RelativeLayout rtlTurnOnOffFlashlight;
    private RadioGroup rbgMode;
    private RadioButton rbFlash;
    private RadioButton rbScreen;
    private RadioButton rbTwinkle;
    private RadioButton rbSOS;
    private LinearLayout twinkleSetting;
    private TextView tvTwinkleMessage;
    private SeekBar sbTwinkleTime;
    private ImageView imvLightBuld;

    private boolean IsSupportFlash = false;
    private boolean IsFlashlightOn = false;
    private Camera camera;
    private SharePreManager sharePreManager;

    private static final int SCREEN_LIGHT_REQUEST_CODE = 1;
    private static final int FLASH_MODE = 1;
    private static final int SCREEN_MODE = 2;
    private static final int TWINKLE_MODE = 3;
    private static final int SOS_MODE = 4;
    private int currentRdbID = -1;
    private int delay = 100;

    private BlinkThread blinkThread;

    private Animation fadeIn;
    private Animation fadeOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide notificationbar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_home);

        initView();

        // Change font
        final Typeface regular = Typeface.createFromAsset(getAssets(), "montserrat.ttf");
        FontHelper.changeFont((ViewGroup) findViewById(R.id.home), regular);

        // Check flash support
        IsSupportFlash = checkFlashSupport();

        // Set listener
        setListener();

        sharePreManager = new SharePreManager(this);

        Log.d("S", "onCreateView");
    }

    public void setListener() {
        rtlTurnOnOffFlashlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom);
                rtlTurnOnOffFlashlight.startAnimation(animation);

                if (IsFlashlightOn) {
                    TurnOffFlashlight();
                } else {
                    int MODE = getCurrentMode();
                    TurnOnFlashlight(MODE);
                }
            }
        });

        rbgMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int MODE = getCurrentMode();
                ChangeFlashlightMode(MODE);

                try {
                    if (currentRdbID != -1)
                        ((RadioButton) findViewById(currentRdbID)).setTextColor(Color.WHITE);

                    currentRdbID = rbgMode.getCheckedRadioButtonId();
                    ((RadioButton) findViewById(currentRdbID)).setTextColor(Color.parseColor("#212121"));
                } catch (Exception e) {
                }
            }
        });

        sbTwinkleTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                delay = progress;
                DecimalFormat format = new DecimalFormat("#.#");
                tvTwinkleMessage.setText("Blinking time is " + format.format(delay / 1000f) + "s");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public int getCurrentMode() {
        int id = rbgMode.getCheckedRadioButtonId();
        int re = 0;

        switch (id) {
            case R.id.rbFlash:
                re = FLASH_MODE;
                break;

            case R.id.rbScreen:
                re = SCREEN_MODE;
                break;

            case R.id.rbTwinkle:
                re = TWINKLE_MODE;
                break;

            case R.id.rbSOS:
                re = SOS_MODE;
                break;
        }

        return re;
    }

    public void ChangeFlashlightMode(int flashMode) {

        // Show / hide twinkle setting
        showHideTwinkleSetting(flashMode);

        // Check flash support && flashlight status
        if (!IsFlashlightOn && IsSupportFlash) {
            showCustomToast("Please turn on flashlight before");
            return;
        }

        if (!IsSupportFlash)
            showCustomToast("Your device does not support flash, SCREEN MODE is on");

        switch (flashMode) {
            case FLASH_MODE:
                Log.i("MODE", "Change to flash");
                ChangeToFlashMode();
                break;

            case SCREEN_MODE:
                Log.i("MODE", "Change to SCREEN");
                ChangeToScreenMode();
                break;

            case TWINKLE_MODE:
                Log.i("MODE", "Change to TWINKLE");
                ChangeToTwinkleMode();
                break;

            case SOS_MODE:
                Log.i("MODE", "Change to SOS");
                ChangeToSOSMode();
                break;
        }
    }

    public void showHideTwinkleSetting(int flashMode) {

        AnimationSet animation = new AnimationSet(false); //change to false

        if (flashMode != TWINKLE_MODE) {

            // Fade out
            fadeOut = new AlphaAnimation(1, 0);
            fadeOut.setInterpolator(new AccelerateInterpolator());
            fadeOut.setDuration(300);

            // Add animation
            animation.addAnimation(fadeOut);
            twinkleSetting.setAnimation(animation);

            twinkleSetting.setVisibility(LinearLayout.GONE);
        } else {

            // Show twinkle setting
            twinkleSetting.setVisibility(LinearLayout.VISIBLE);

            // Fade in
            fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator());
            fadeIn.setDuration(1000);

            // Add animation
            animation.addAnimation(fadeIn);
            twinkleSetting.setAnimation(animation);

            // Set progress time
            delay = sbTwinkleTime.getProgress();
            //sbTwinkleTime.setProgress(delay);
        }
    }

    private void ChangeToSOSMode() {
        BlinkRunning = false;
        TurnOnSOSMode();
    }

    private void ChangeToTwinkleMode() {
        BlinkRunning = false;
        TurnOnTwinkleMode();
    }

    private void ChangeToScreenMode() {
        BlinkRunning = false;
        TurnOffFlash();
        TurnOnScreenMode();
    }

    private void ChangeToFlashMode() {
        BlinkRunning = false;
        TurnOnFlashMode();
    }

    public void TurnOnFlashlight(int mode) {

        // Stop blink thread if this is exist
        BlinkRunning = false;

        if (!IsSupportFlash)
            showCustomToast("Your device does not support flash, SCREEN MODE is on");

        switch (mode) {
            case FLASH_MODE:
                TurnOnFlashMode();
                break;

            case SCREEN_MODE:
                TurnOnScreenMode();
                break;

            case TWINKLE_MODE:
                TurnOnTwinkleMode();
                break;

            case SOS_MODE:
                TurnOnSOSMode();
                break;
        }

        IsFlashlightOn = true;
        tvLightStatus.setText("Flashlight on");
        imvLightBuld.setImageResource(R.drawable.light_off);
    }

    public boolean BlinkRunning = false;

    public void TurnOnFlashMode() {
        if (IsSupportFlash) {
            TurnOnFlash();
        } else {
            TurnOnScreenMode();
        }
    }

    public void TurnOnScreenMode() {
        // Turn off flash
        TurnOffFlashlight();

        Intent intent = new Intent(HomeActivity.this, WhiteActivity.class);
        startActivityForResult(intent, SCREEN_LIGHT_REQUEST_CODE);
    }

    public void TurnOnSOSMode() {

        if (IsSupportFlash) {
            delay = 300;
            TurnOnFlash();
            BlinkRunning = true;

            if (blinkThread != null)
                blinkThread.interrupt();

            blinkThread = new BlinkThread();
            blinkThread.start();
        } else {
            TurnOnScreenMode();
        }
    }

    public void TurnOnTwinkleMode() {

        if (IsSupportFlash) {
            TurnOnFlash();
            BlinkRunning = true;

            if (blinkThread != null)
                blinkThread.interrupt();

            blinkThread = new BlinkThread();
            blinkThread.start();
        } else {
            TurnOnScreenMode();
        }
    }

    public void TurnOnFlash() {
        if (camera == null)
            camera = Camera.open();

        if (camera != null) {
            Parameters p = camera.getParameters();
            p.setFlashMode(Parameters.FLASH_MODE_ON);
            camera.setParameters(p);
            camera.startPreview();
        }
    }

    public void TurnOffFlash() {
        try {
            if (camera != null) {
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        } catch (Exception e) {
            Log.e("TURN OFF CAMERA", e.getMessage());
        }
    }

    public void TurnOffFlashlight() {
        // Stop all blink thread if this is exist
        BlinkRunning = false;

        if (IsSupportFlash)
            TurnOffFlash();

        IsFlashlightOn = false;
        tvLightStatus.setText("Flashlight off");
        imvLightBuld.setImageResource(R.drawable.light_on);
    }

    public boolean checkFlashSupport() {
        return this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    private void initView() {
        tvLightStatus = (TextView) findViewById(R.id.tvLightStatus);
        rtlTurnOnOffFlashlight = (RelativeLayout) findViewById(R.id.btnTurnOnOffLight);

        rbgMode = (RadioGroup) findViewById(R.id.rbgMode);
        rbFlash = (RadioButton) findViewById(R.id.rbFlash);
        rbScreen = (RadioButton) findViewById(R.id.rbScreen);
        rbTwinkle = (RadioButton) findViewById(R.id.rbTwinkle);
        rbSOS = (RadioButton) findViewById(R.id.rbSOS);

        sbTwinkleTime = (SeekBar) findViewById(R.id.sbTwinkleTime);
        tvTwinkleMessage = (TextView) findViewById(R.id.tvTwinkleMessage);
        twinkleSetting = (LinearLayout) findViewById(R.id.twinkleSettings);

        imvLightBuld = (ImageView) findViewById(R.id.imvLightBuld);

        rbFlash.setChecked(true);
        currentRdbID = R.id.rbFlash;
        ((RadioButton) findViewById(currentRdbID)).setTextColor(Color.parseColor("#212121"));
    }

    public void showCustomToast(String message) {

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_vew,
                (ViewGroup) findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 140);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SCREEN_LIGHT_REQUEST_CODE:
                TurnOffFlashlight();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (!IsFlashlightOn || sharePreManager.isDontRemind())
            super.onBackPressed();
        else {
            // Show dialog
            new AlertDialog.Builder(this)
                    .setTitle("Warning")
                    .setMessage("If you press back button, a flashlight will be off. Press the home button to make flashlight is on")
                    .setPositiveButton("DISCARD", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            sharePreManager.setRemindValue(sharePreManager.getRemindValue() + 1);
                            finish();
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    })
                    .show();
        }
    }

    @Override
    protected void onDestroy() {
        BlinkRunning = false;
        if (blinkThread != null)
            blinkThread.interrupt();

        TurnOffFlash();

        super.onDestroy();
    }

    public class BlinkThread extends Thread {
        @Override
        public void run() {

            try {
                if (camera == null) {
                    TurnOnFlash();
                }

                Parameters parameters;
                String FlashMode;
                while (BlinkRunning) {
                    parameters = camera.getParameters();
                    FlashMode = parameters.getFlashMode();

                    // Chuyen doi giua cac trang thai Flash
                    if (FlashMode.equals(Parameters.FLASH_MODE_ON))
                        parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
                    else
                        parameters.setFlashMode(Parameters.FLASH_MODE_ON);

                    camera.setParameters(parameters);
                    sleep(delay);
                }
            } catch (Exception e) {
                //Log.e("BLINK_THREAD", e.getMessage());
            }
        }
    }
}
