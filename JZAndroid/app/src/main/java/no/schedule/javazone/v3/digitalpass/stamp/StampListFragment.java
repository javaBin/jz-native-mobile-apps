package no.schedule.javazone.v3.digitalpass.stamp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import no.schedule.javazone.v3.R;
import no.schedule.javazone.v3.digitalpass.DigitalPassActivity;
import no.schedule.javazone.v3.digitalpass.camera.CameraActivity;
import no.schedule.javazone.v3.util.FirebaseRemoteConfigUtil;

public class StampListFragment extends Fragment {

    static int DIALOG_STAMP = 10;

    private GridView gridview;
    private ImageAdapter logoAdapter;

    private Button button;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StampListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.digital_pass_stamp_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logoAdapter = new ImageAdapter(getContext());

        gridview = view.findViewById(R.id.gridview);
        gridview.setAdapter(logoAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                showStampDialog(position);
            }
        });

        button = view.findViewById(R.id.stamp_list_scan_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(
                        new Intent(getActivity(), CameraActivity.class), CameraActivity.BARCODE_REQUEST);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        if (((DigitalPassActivity)getActivity()).isRegistered()){
            button.setEnabled(true);
        } else {
            button.setEnabled(false);
        }
    }


    public void showStampDialog(int position) {
        FragmentManager fm = getFragmentManager();
        StampDialogFragment stampDialog = StampDialogFragment.newInstance();
        stampDialog.setTargetFragment(this, DIALOG_STAMP);
        stampDialog.setStamp(logoAdapter.getItem(position));
        stampDialog.show(fm, "stamp_dialog");
    }

    public void refreshList(){
        logoAdapter.notifyDataSetChanged();
    }
  
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CameraActivity.BARCODE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                String barcode = data.getStringExtra("barcode");
                String salt = FirebaseRemoteConfigUtil.getRemoteConfigSequence("partners");

                Log.d("barcode", barcode);
                for (int i = 0; i < logoAdapter.getCount(); i++) {
                    Stamp stamp = logoAdapter.getItem(i);

                    String verificationKey;
                    try{
                        verificationKey = stamp.generateVerificationKey(salt);
                    }catch( NoSuchAlgorithmException | InvalidKeySpecException e){
                        Log.d("StampDialogFragment", e.getMessage());
                        return;
                    }
                    if (barcode.equals(verificationKey)) {
                        stamp.setTagged(true);
                        this.refreshList();
                        SharedPreferences sharedPref = getActivity().getSharedPreferences("StampPref", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(stamp.getName(), barcode);
                        editor.commit();
                        Context context = getActivity();
                        CharSequence text = "QR code for " + stamp.getName() + " scanned.";
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                        return;
                    }
                }
            }else{
                Context context = getActivity();
                CharSequence text = "Error scanning QR code";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                return;
            }
        }
        Context context = getActivity();
        CharSequence text = "QR code not valid.";
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

}
