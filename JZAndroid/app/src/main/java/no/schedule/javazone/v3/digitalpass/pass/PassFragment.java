package no.schedule.javazone.v3.digitalpass.pass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import net.glxn.qrgen.android.QRCode;

import no.schedule.javazone.v3.R;
import no.schedule.javazone.v3.digitalpass.camera.CameraActivity;
import no.schedule.javazone.v3.digitalpass.camera.CameraSource;
import no.schedule.javazone.v3.digitalpass.camera.CameraSourcePreview;
import no.schedule.javazone.v3.digitalpass.camera.GraphicOverlay;

import static no.schedule.javazone.v3.util.LogUtils.makeLogTag;

public class PassFragment extends Fragment{

    private static final String TAG = makeLogTag(PassFragment.class);

    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;
    private PassFragment pf = this;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.digital_pass_fragment, container, false);
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
        final Button button = (Button) getView().findViewById(R.id.scan_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    startActivityForResult(
                            new Intent(getActivity(), CameraActivity.class),
                            CameraActivity.BARCODE_REQUEST);
                }
        });
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String barcode = sharedPref.getString(getString(R.string.myqr), null);
        if (barcode != null){
            Bitmap myBitmap = QRCode.from(barcode).bitmap();
            ImageView myImage = (ImageView) getView().findViewById(R.id.my_qr);
            myImage.setImageBitmap(myBitmap);
        }
    }

    public void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == CameraActivity.BARCODE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                String barcode = data.getStringExtra("barcode");
                Log.d("barcode", barcode);

                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.myqr), barcode);
                editor.commit();
                Bitmap myBitmap = QRCode.from(barcode).bitmap();
                ImageView myImage = (ImageView) getView().findViewById(R.id.my_qr);
                myImage.setImageBitmap(myBitmap);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }

}
