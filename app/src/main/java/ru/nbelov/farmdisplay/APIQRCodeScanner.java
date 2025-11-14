package ru.nbelov.farmdisplay;

import android.content.Intent;
import android.os.Bundle;

import androidx.camera.view.PreviewView;
import androidx.viewbinding.ViewBinding;

import ru.nbelov.farmdisplay.Scanner.AbstractGoodScannerActivity;
import ru.nbelov.farmdisplay.Scanner.GraphicOverlay;
import ru.nbelov.farmdisplay.databinding.ActivityQrscanfindapiBinding;

public class APIQRCodeScanner extends AbstractGoodScannerActivity {

    ActivityQrscanfindapiBinding activityQrscanfindapiBinding = null;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(getBinding().getRoot());
        initScanner();
    }

    @Override
    public ViewBinding getBinding() {
        if (activityQrscanfindapiBinding == null) {
            activityQrscanfindapiBinding = ActivityQrscanfindapiBinding.inflate(getLayoutInflater());
        }
        return activityQrscanfindapiBinding;
    }

    @Override
    public PreviewView getPreviewView() {
        return activityQrscanfindapiBinding.previewView;
    }

    @Override
    public GraphicOverlay getGraphicOverlay() {
        return activityQrscanfindapiBinding.graphicOverlay;
    }

    @Override
    public void sendScannedCode(String code, String codetype) {
        Intent intent = new Intent();
        intent.putExtra("SCAN_RESULT", code);
        setResult(RESULT_OK, intent);
        finish();
    }
}
