package no.schedule.javazone.v3.digitalpass.camera;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;

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

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    public static final int BARCODE_REQUEST = 0;

    private static final String TAG = makeLogTag(PassFragment.class);
    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;
    private int requestCode;

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        intent.putExtra("requestCode", requestCode);
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestCode = getIntent().getIntExtra("requestCode", -1);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != this.getPackageManager().PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }else{
            setupCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch(requestCode){
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    setupCamera();
                } else {
                    finish();
                }
            }
        }
    }

    private void setupCamera(){
        setContentView(R.layout.activity_camera);

        preview = (CameraSourcePreview) findViewById(R.id.firePreview);
        graphicOverlay = (GraphicOverlay) findViewById(R.id.fireFaceOverlay);
        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
        }
        cameraSource.setMachineLearningFrameProcessor(new BarcodeScanningProcessor(this, requestCode));
        try {
            Log.d(TAG, "onClick: start camera");
            preview.start(cameraSource, graphicOverlay);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onQrScanned(FirebaseVisionBarcode.ContactInfo contactInfo){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("requestCode", requestCode);
        //switch (requestCode){
        //    case BARCODE_REQUEST:
        //        resultIntent.putExtra("barcode", )
        //}
        //extras.putSerializable("contactInfo", contactInfo);
        resultIntent.putExtra("name", contactInfo.getName().getFormattedName());
        resultIntent.putExtra("email", contactInfo.getEmails().get(0).getAddress());
        //resultIntent.putExtra("address", contactInfo.getAddresses().get(0).getAddressLines());
        //resultIntent.putExtra("company", contactInfo.getOrganization());
        //resultIntent.putExtra("phone", contactInfo.getPhones().get(0).getNumber());
        //resultIntent.putExtra("url", contactInfo.getUrls()[0]);
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
