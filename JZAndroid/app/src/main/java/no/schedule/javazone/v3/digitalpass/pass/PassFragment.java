package no.schedule.javazone.v3.digitalpass;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;

import net.glxn.qrgen.android.QRCode;

import java.io.IOException;

import no.schedule.javazone.v3.R;

import static no.schedule.javazone.v3.util.LogUtils.makeLogTag;

public class DigitalPassFragment extends Fragment{

    private static final String TAG = makeLogTag(DigitalPassFragment.class);
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;

    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_digital_pass, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View header = view.findViewById(R.id.header_anim);
        if (header instanceof ImageView) {
            AnimatedVectorDrawable avd = (AnimatedVectorDrawable) ContextCompat.getDrawable(
                    getContext(), R.drawable.avd_header_my_io);
            ((ImageView) header).setImageDrawable(avd);
            avd.start();
        }
        final Button button = (Button) getView().findViewById(R.id.scann_button);
        /*
        preview = (CameraSourcePreview) getView().findViewById(R.id.firePreview);
        graphicOverlay = (GraphicOverlay) getView().findViewById(R.id.fireFaceOverlay);
        if (cameraSource == null) {
            cameraSource = new CameraSource(this.getActivity(), graphicOverlay);
        }
        cameraSource.setMachineLearningFrameProcessor(new BarcodeScanningProcessor(this));
        try {
            Log.d(TAG, "onClick: start camera");
            preview.start(cameraSource, graphicOverlay);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        //onQrScanned(null);
        /*
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                        != getContext().getPackageManager().PERMISSION_GRANTED) {
                    // Permission is not granted
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                }else{
                    cameraSource.setMachineLearningFrameProcessor(new BarcodeScanningProcessor());
                    try {
                        Log.d(TAG, "onClick: start camera");
                        preview.start(cameraSource, graphicOverlay);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        */
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }

    public void onQrScanned(FirebaseVisionBarcode barcode) {
        Log.d("Barcode", barcode.getDisplayValue());
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.myqr), barcode.getDisplayValue());
        editor.commit();

        Bitmap myBitmap = QRCode.from(barcode.getDisplayValue()).bitmap();
        ImageView myImage = (ImageView) getView().findViewById(R.id.my_qr);
        myImage.setImageBitmap(myBitmap);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == getContext().getPackageManager().PERMISSION_GRANTED) {
                    cameraSource.setMachineLearningFrameProcessor(new BarcodeScanningProcessor(this));
                    try {
                        Log.d(TAG, "onClick: start camera");
                        preview.start(cameraSource, graphicOverlay);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return;
            }
        }
    }
}
