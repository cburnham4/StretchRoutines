package letshangllc.stretchingroutines.controller.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Locale;

import letshangllc.stretchingroutines.AdsHelper;
import letshangllc.stretchingroutines.R;
import letshangllc.stretchingroutines.model.Data.Routine;
import letshangllc.stretchingroutines.model.JavaObjects.Stretch;
import letshangllc.stretchingroutines.model.api.APIRequests;

public class StretchActivity extends AppCompatActivity {
    private static final String TAG = StretchActivity.class.getSimpleName();

    private TextView tv_stretchName, tv_instructions, tvStretchCount, tv_timer;
    private ImageView img_stretch;
    private ProgressDialog progressDialog;

    private CountDownTimer countDownTimer;
    private int currentStretchIndex;
    private boolean isPaused;

    private Ringtone r;
    private ArrayList<Stretch> stretches;

    private AdsHelper adsHelper;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private long millisUntilFinished = 0;

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (adsHelper != null) {
                adsHelper.refreshAd();
            }
            handler.postDelayed(this, getResources().getInteger(R.integer.ad_refresh_rate));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stretch);
        applySystemBarInsets();

        Intent intent = getIntent();
        Routine routine;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            routine = intent.getParcelableExtra(getString(R.string.routine_index_intent), Routine.class);
        } else {
            //noinspection deprecation
            routine = intent.getParcelableExtra(getString(R.string.routine_index_intent));
        }
        loadStretches(routine);

        findViews();
        currentStretchIndex = 0;

        runAds();
    }

    private void applySystemBarInsets() {
        View root = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            View content = ((ViewGroup) v).getChildAt(0);
            if (content != null) {
                content.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            }
            return windowInsets;
        });
    }

    private void loadStretches(Routine routine) {
        progressDialog = ProgressDialog.show(this, "Loading Stretches", "Please wait...", true);
        APIRequests.getStretches(routine, stretches -> {
            StretchActivity.this.stretches = stretches;
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            startStretches();
        });
    }

    private void findViews() {
        tv_stretchName = findViewById(R.id.tv_stretchName);
        tv_instructions = findViewById(R.id.tv_instructions);
        img_stretch = findViewById(R.id.img_stretchPhoto);
        tv_timer = findViewById(R.id.tv_timer);
        tvStretchCount = findViewById(R.id.tvStretchCount);
    }

    private void startStretches() {
        if (stretches == null || currentStretchIndex >= stretches.size()) {
            finish();
            return;
        }
        isPaused = false;
        Stretch stretch = stretches.get(currentStretchIndex);
        tv_stretchName.setText(stretch.getName());
        tv_instructions.setText(stretch.getInstructions());
        String stretchCount = (currentStretchIndex + 1) + " / " + stretches.size();
        tvStretchCount.setText(stretchCount);

        Glide.with(this).load(stretch.downloadURL).into(img_stretch);

        startSwitchCountdown(stretch.time * 1000);
    }

    public void nextExerciseOnClick(View view) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        currentStretchIndex++;
        startStretches();
    }

    private void startSwitchCountdown(final int stretchDuration) {
        countDownTimer = new CountDownTimer(3000, 250) {
            public void onTick(long millisUntilFinished) {
                int time = (int) Math.round(Math.ceil(millisUntilFinished * 0.001f));
                Log.i(TAG, String.format(Locale.getDefault(), "%d", time));
                tv_timer.setText(String.format(Locale.getDefault(), "%d", time));
                StretchActivity.this.millisUntilFinished = millisUntilFinished;
            }

            public void onFinish() {
                startStretchCountdown(stretchDuration);
            }
        };
        countDownTimer.start();
    }

    private void startStretchCountdown(int stretchDuration) {
        countDownTimer = new CountDownTimer(stretchDuration, 250) {
            public void onTick(long millisUntilFinished) {
                int time = Math.round(millisUntilFinished * 0.001f);
                Log.i(TAG, String.format(Locale.getDefault(), "%d", time));
                tv_timer.setText(String.format(Locale.getDefault(), "%d", time));
                StretchActivity.this.millisUntilFinished = millisUntilFinished;
            }

            public void onFinish() {
                playAlarm();
                currentStretchIndex++;
                startStretches();
            }
        };
        countDownTimer.start();
    }

    private void playAlarm() {
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        if (alert == null) {
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null) {
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }

        try {
            r = RingtoneManager.getRingtone(this, alert);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                if (r != null) {
                    r.stop();
                }
            } catch (Exception e) {
                if (r != null) {
                    r.stop();
                }
            }
        }).start();
    }

    public void pauseExercise(View view) {
        Log.i(TAG, " Pause");
        if (isPaused) {
            countDownTimer.start();
            ((Button) view).setText("PAUSE");
        } else {
            if (countDownTimer != null) {
                ((Button) view).setText("RESUME");
                countDownTimer.cancel();
            }
        }
        isPaused = !isPaused;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (adsHelper != null) {
            adsHelper.onPause();
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adsHelper != null) {
            adsHelper.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable);
        if (adsHelper != null) {
            adsHelper.onDestroy();
        }
        super.onDestroy();
    }

    public void runAds() {
        adsHelper = new AdsHelper(
                getWindow().getDecorView(),
                getString(R.string.admob_id_stretches),
                this
        );
        adsHelper.setUpAds();
        handler.postDelayed(runnable, 100);
    }
}
