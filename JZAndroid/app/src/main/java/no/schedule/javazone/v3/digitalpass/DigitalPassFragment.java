package no.schedule.javazone.v3.digitalpass;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.IOException;

import no.schedule.javazone.v3.R;

import static no.schedule.javazone.v3.util.LogUtils.makeLogTag;

public class DigitalPassFragment extends Fragment{

    private static final String TAG = makeLogTag(DigitalPassFragment.class);

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
        final Button button = (Button) getView().findViewById(R.id.scann_button);
        preview = (CameraSourcePreview) getView().findViewById(R.id.firePreview);
        graphicOverlay = (GraphicOverlay) getView().findViewById(R.id.fireFaceOverlay);
        if (cameraSource == null) {
            cameraSource = new CameraSource(this.getActivity(), graphicOverlay);
        }
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cameraSource.setMachineLearningFrameProcessor(new BarcodeScanningProcessor());
                try {
                    Log.d(TAG, "onClick: start camera");
                    preview.start(cameraSource, graphicOverlay);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
