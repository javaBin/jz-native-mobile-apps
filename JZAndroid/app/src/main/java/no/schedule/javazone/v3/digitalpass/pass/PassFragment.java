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

import static no.schedule.javazone.v3.util.LogUtils.makeLogTag;

public class PassFragment extends Fragment{

    private static final String TAG = makeLogTag(PassFragment.class);
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;

    static final int BARCODE_REQUEST = 0;

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

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String barcode = sharedPref.getString(getString(R.string.myqr), null);
        if (barcode != null){
            hasBarcode(barcode);
        } else {
            noBarcode();

        }
    }

    public void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == BARCODE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                String barcode = data.getStringExtra("barcode");
                Log.d("barcode", barcode);

                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.myqr), barcode);
                editor.commit();
                hasBarcode(barcode);
            }
        }
    }

    private void noBarcode (){
        ImageView myImage = getView().findViewById(R.id.my_qr);
        myImage.setImageDrawable(getResources().getDrawable(R.drawable.qr_code));
        final Button button = getView().findViewById(R.id.scan_button);
        button.setText("Scan QR Code");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA)
                        != getContext().getPackageManager().PERMISSION_GRANTED) {
                    // Permission is not granted
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{android.Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                }else{
                    startActivityForResult(
                            new Intent(getActivity(), CameraActivity.class),
                            BARCODE_REQUEST);
                }
            }
        });
    }
    private void hasBarcode (String barcode){
        Bitmap myBitmap = QRCode.from(barcode).bitmap();
        ImageView myImage = getView().findViewById(R.id.my_qr);
        myImage.setImageBitmap(myBitmap);
        final Button button = getView().findViewById(R.id.scan_button);
        button.setText("Delete QR Code");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.remove(getString(R.string.myqr));
                editor.commit();
                noBarcode();
            }
        });
    }

}