package ru.nbelov.farmdisplay;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.VideoCapture;
import androidx.camera.view.CameraView;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EnregistrementVideoStackActivity extends AppCompatActivity {

    public static final SimpleDateFormat DM_HOUR_MINUTE_FORMAT_SECOND = new SimpleDateFormat("YYYY-MM-dd-HH-mm-ss");
    private boolean isRecording = false;
    private CameraView camera_view;
    private String videoDir;

    private void recordVideo(String videoRecordingFilePath) {
        camera_view.startRecording(new File(videoRecordingFilePath), ContextCompat.getMainExecutor(this), new VideoCapture.OnVideoSavedCallback() {
            @Override
            public void onVideoSaved(File file) {
                Toast.makeText(EnregistrementVideoStackActivity.this, "Recording Saved", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int videoCaptureError, String message, Throwable cause) {
                Toast.makeText(EnregistrementVideoStackActivity.this, "Error saving " + message, Toast.LENGTH_SHORT).show();
            }
        });
        Toast.makeText(this, "Recording Started", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_surface);
        camera_view = findViewById(R.id.camera_view);
        videoDir = Environment.getExternalStorageDirectory() + File.separator
                + Environment.DIRECTORY_DCIM + File.separator;
        Button video_record = findViewById(R.id.video_record);
        Calendar calendar = Calendar.getInstance();
        String videoRecordingFilePath = videoDir + DM_HOUR_MINUTE_FORMAT_SECOND.format(calendar.getTime())+".mp4";
        video_record.setOnClickListener((View view) -> {
            if (isRecording) {
                isRecording = false;
                video_record.setText("Record Video");
//                Toast.makeText(this, "Recording Stopped: " + videoRecordingFilePath, Toast.LENGTH_SHORT).show();
                camera_view.stopRecording();
            } else {
                isRecording = true;
                video_record.setText("Stop Recording");
                recordVideo(videoRecordingFilePath);
            }
        });
    }
}