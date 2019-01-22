package no.schedule.javazone.v3.digitalpass;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

import no.schedule.javazone.v3.R;
import no.schedule.javazone.v3.ui.BaseActivity;

import static no.schedule.javazone.v3.util.LogUtils.makeLogTag;

public class CameraActivity extends BaseActivity {
    private static final String TAG = makeLogTag(CameraActivity.class);

    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;

    private DigitalPassFragment dpf = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: camera");
        setContentView(R.layout.camera_fragment);
        setFullscreenLayout();

        preview = (CameraSourcePreview) findViewById(R.id.firePreview);
        graphicOverlay = (GraphicOverlay) findViewById(R.id.fireFaceOverlay);
        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
        }
        cameraSource.setMachineLearningFrameProcessor(new BarcodeScanningProcessor(this, dpf));
        try {
            Log.d(TAG, "onClick: start camera");
            preview.start(cameraSource, graphicOverlay);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
}
