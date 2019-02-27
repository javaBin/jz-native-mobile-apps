package no.schedule.javazone.v3.digitalpass.stamp;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import no.schedule.javazone.v3.R;

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
        ImageView logo =  view.findViewById(R.id.stampLogo);
        logo.setImageResource(stamp.getImage());

        // Setting description
        TextView description = view.findViewById(R.id.stampDescription);
        description.setText(stamp.getDescription());

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

    public void setStamp(Stamp stamp){
        this.stamp = stamp;
    }

}
