package no.schedule.javazone.v3.digitalpass.stamp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import no.schedule.javazone.v3.R;
import no.schedule.javazone.v3.digitalpass.camera.CameraActivity;

import static no.schedule.javazone.v3.util.LogUtils.makeLogTag;

public class StampDialogFragment extends DialogFragment {
    private static final String TAG = makeLogTag(StampDialogFragment.class);
    private Stamp stamp;

    public StampDialogFragment() {
    }

    public static StampDialogFragment newInstance() {
        StampDialogFragment frag = new StampDialogFragment();
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.digital_pass_stamp_dialog_fragment, container, false);

        // Setting logo
        ImageView logo =  view.findViewById(R.id.dialog_stamp_logo);
        logo.setImageResource(stamp.getImage());

        // Setting description
        TextView description = view.findViewById(R.id.dialog_stamp_description);
        description.setText(stamp.getDescription());

        final Button button = view.findViewById(R.id.dialog_scan_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(
                        new Intent(getActivity(), CameraActivity.class), CameraActivity.BARCODE_REQUEST);
            }
        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.WRAP_CONTENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CameraActivity.BARCODE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                String barcode = data.getStringExtra("barcode");
                Log.d("barcode", barcode);
                if(barcode.equals(stamp.getQrCode())) {
                    stamp.setTagged(true);
                    StampListFragment slf = (StampListFragment) getTargetFragment();
                    slf.refreshList();
                    SharedPreferences sharedPref = getActivity().getSharedPreferences("StampPref", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(stamp.getName(), barcode);
                    editor.commit();
                }else{
                    Context context = getActivity();
                    CharSequence text = "Wrong QR code.";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        }
    }

    public void setStamp(Stamp stamp){
        this.stamp = stamp;
    }

}