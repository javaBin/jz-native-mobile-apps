package no.schedule.javazone.v3.digitalpass.camera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

import no.schedule.javazone.v3.R;
import no.schedule.javazone.v3.digitalpass.pass.PassFragment;
import no.schedule.javazone.v3.ui.BaseActivity;

import static no.schedule.javazone.v3.util.LogUtils.makeLogTag;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class CameraActivity extends BaseActivity {

    private static final String TAG = makeLogTag(PassFragment.class);
    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera);

        preview = (CameraSourcePreview) findViewById(R.id.firePreview);
        graphicOverlay = (GraphicOverlay) findViewById(R.id.fireFaceOverlay);
        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
        }
        cameraSource.setMachineLearningFrameProcessor(new BarcodeScanningProcessor(this));
        try {
            Log.d(TAG, "onClick: start camera");
            preview.start(cameraSource, graphicOverlay);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onQrScanned(String barcode){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("barcode", barcode);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }
}
