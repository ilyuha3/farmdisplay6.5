package ru.nbelov.farmdisplay;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class ValidatePermissionsActivity extends AppCompatActivity {
    public static final int MY_PERMISSIONS_REQUEST = 1;
    public static final int MY_PERMISSIONS_REQUEST_NO_MICRO = 2;

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (!validate(this)) {
            renderActivityPermissions();
        } else {
            Intent intent = new Intent(this, FindAPIActivity.class);
            finishActivity(0);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    public static boolean validate(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    private void renderActivityPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            LinearLayout ll = findViewById(R.id.isAccessWiFiState);
            ll.setVisibility(View.VISIBLE);
        } else {
            LinearLayout ll = findViewById(R.id.isAccessWiFiState);
            ll.setVisibility(View.GONE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            LinearLayout ll = findViewById(R.id.isAccessInternet);
            ll.setVisibility(View.VISIBLE);
        } else {
            LinearLayout ll = findViewById(R.id.isAccessInternet);
            ll.setVisibility(View.GONE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
            LinearLayout ll = findViewById(R.id.isAccessVibrate);
            ll.setVisibility(View.VISIBLE);
        } else {
            LinearLayout ll = findViewById(R.id.isAccessVibrate);
            ll.setVisibility(View.GONE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            LinearLayout ll = findViewById(R.id.isAccessNetworkState);
            ll.setVisibility(View.VISIBLE);
        } else {
            LinearLayout ll = findViewById(R.id.isAccessNetworkState);
            ll.setVisibility(View.GONE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            LinearLayout ll = findViewById(R.id.isAccessCamera);
            ll.setVisibility(View.VISIBLE);
        } else {
            LinearLayout ll = findViewById(R.id.isAccessCamera);
            ll.setVisibility(View.GONE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            LinearLayout ll = findViewById(R.id.isAccessCamera);
            ll.setVisibility(View.VISIBLE);
        } else {
            LinearLayout ll = findViewById(R.id.isAccessCamera);
            ll.setVisibility(View.GONE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            LinearLayout ll = findViewById(R.id.isAccessCamera);
            ll.setVisibility(View.VISIBLE);
        } else {
            LinearLayout ll = findViewById(R.id.isAccessCamera);
            ll.setVisibility(View.GONE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            LinearLayout ll = findViewById(R.id.isAccessCamera);
            ll.setVisibility(View.VISIBLE);
        } else {
            LinearLayout ll = findViewById(R.id.isAccessCamera);
            ll.setVisibility(View.GONE);
        }
    }

    public void getAccess(View view) {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.WAKE_LOCK,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.VIBRATE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.BLUETOOTH
                },
                MY_PERMISSIONS_REQUEST);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);
        renderActivityPermissions();
    }
}
