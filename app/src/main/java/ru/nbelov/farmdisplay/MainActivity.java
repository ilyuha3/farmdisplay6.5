package ru.nbelov.farmdisplay;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import ru.nbelov.farmdisplay.dbapi.GetDisplayNumberAPI;
import ru.nbelov.farmdisplay.dbapi.SyncAPI;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    public static final SimpleDateFormat DM_HOUR_MINUTE_FORMAT_SECOND = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    private static int SYNC_NOTHING = 0;
    private static int SYNC_LEFT_RIGHT = 1;
    private static int SYNC_FULLSCREEN = 2;

    private static int START_RECORD = 1;
    private static int STOP_RECORD = 2;

    private TextView displayLbl;
    private WebView left;
    private WebView right;
    private WebView fullscreen;
    private Timer mTimer;
    private boolean isLoading = false;
    private LinearLayout twosideslayout;
    private TextToSpeech engine;
    private boolean ttsEnabled = false;
    private MediaRecorder recorder;
    private boolean recording = false;
    private String videoDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoDir = Environment.getExternalStorageDirectory() + File.separator
                + Environment.DIRECTORY_DCIM + File.separator;
//        initRecorder();
        engine = new TextToSpeech(this, this);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        displayLbl = findViewById(R.id.displaylbl);
        left = findViewById(R.id.left);
        right = findViewById(R.id.right);
        fullscreen = findViewById(R.id.fullscreen);
        CustomWebViewClient customWebViewClient = new CustomWebViewClient();
        fullscreen.setWebViewClient(customWebViewClient);
        left.setWebViewClient(customWebViewClient);
        right.setWebViewClient(customWebViewClient);
        twosideslayout = findViewById(R.id.twosideslayout);
        left.setVisibility(View.GONE);
        right.setVisibility(View.GONE);
        fullscreen.setVisibility(View.GONE);
        displayLbl.setVisibility(View.VISIBLE);
        displayLbl.setText("Идет соединение...");
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int dpi = (int)(metrics.density * 160f);
        int width = display.getWidth();
        int height = display.getHeight();
        MainActivity ctx = this;
        GetDisplayNumberAPI getDisplayNumberAPI = new GetDisplayNumberAPI(ctx, android_id, width, height, dpi, displayId -> {
            displayLbl.setText("Номер дисплея " + displayId);
            if (mTimer != null) {
                mTimer.cancel();
            }
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mTimer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            if (isLoading) {
                                return;
                            }
                            isLoading = true;
                            SyncAPI syncAPI = new SyncAPI(ctx, android_id, width, height, dpi, (htmlLeft, htmlRight, htmlFull, voicePrice, startStopRecord, signal) -> {
                                isLoading = false;
                               if (signal == SYNC_NOTHING) {
                                   return;
                               } else if (signal == SYNC_LEFT_RIGHT) {
                                   displayLbl.setVisibility(View.GONE);
                                   twosideslayout.setVisibility(View.VISIBLE);
                                   left.setVisibility(View.VISIBLE);
                                   right.setVisibility(View.VISIBLE);
                                   fullscreen.setVisibility(View.GONE);
                                   left.loadDataWithBaseURL(null, htmlLeft, "text/html", "utf-8", null);
                                   right.loadDataWithBaseURL(null, htmlRight, "text/html", "utf-8", null);
                               } else if (signal == SYNC_FULLSCREEN) {
                                   displayLbl.setVisibility(View.GONE);
                                   twosideslayout.setVisibility(View.GONE);
                                   fullscreen.setVisibility(View.VISIBLE);
                                   fullscreen.loadDataWithBaseURL(null, htmlFull, "text/html", "utf-8", null);
                               }
                               if (voicePrice != null && !voicePrice.isEmpty() && ttsEnabled) {
                                   engine.speak(voicePrice, TextToSpeech.QUEUE_FLUSH, null, null);
                               }
                               if (startStopRecord == START_RECORD) {
//                                   if (recording) {
//                                       recorder.stop();
//                                   }
//                                   Calendar calendar = Calendar.getInstance();
//                                   Log.i("VIDEO", videoDir + DM_HOUR_MINUTE_FORMAT_SECOND.format(calendar.getTime())+".mp4");
//                                   recorder.setOutputFile(videoDir + DM_HOUR_MINUTE_FORMAT_SECOND.format(calendar.getTime())+".mp4");
//                                   recorder.start();
//                                   recording = true;
                               } else if (startStopRecord == STOP_RECORD) {
                                   if (recording) {
                                       recorder.stop();
                                       recording = false;
                                   }
                               }
                            },errorMessage -> {
                                isLoading = false;
                            });
                            syncAPI.execute();
                        }
                    }, 3000, 3000);
                }
            }, 4000);
        }, errorMessage -> {
            AlertDialog.show(ctx, "Ошибка соединения с базой данных: " + errorMessage, () -> {
            }, null);
        });
        getDisplayNumberAPI.execute();
    }

    private void initRecorder() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        CamcorderProfile cpHigh = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        recorder.setProfile(cpHigh);
        Calendar calendar = Calendar.getInstance();
        recorder.setOutputFile(videoDir + DM_HOUR_MINUTE_FORMAT_SECOND.format(calendar.getTime())+".mp4");
        recorder.setMaxDuration(50000); // 50 seconds
        recorder.setMaxFileSize(5000000); // Approximately 5 megabytes
        try {
            recorder.prepare();
            recorder.start();
            recording = true;
        } catch (Exception ex) {
            Log.e("CAMERA", ex.getLocalizedMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            //Setting speech Language
            if (engine.isLanguageAvailable(new Locale(Locale.getDefault().getLanguage()))
                    == TextToSpeech.LANG_AVAILABLE) {
                engine.setLanguage(new Locale(Locale.getDefault().getLanguage()));
            } else {
                engine.setLanguage(Locale.US);
            }
            engine.setPitch(1.0f);
            //engine.setSpeechRate(0.9f);
            ttsEnabled = true;
        }
    }
}
