package no.schedule.javazone.v3.digitalpass.pass;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opencsv.CSVReader;

import net.glxn.qrgen.android.QRCode;
import net.glxn.qrgen.core.scheme.VCard;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Collection;

import no.schedule.javazone.v3.R;
import no.schedule.javazone.v3.digitalpass.DigitalPassActivity;
import no.schedule.javazone.v3.digitalpass.camera.CameraActivity;
import no.schedule.javazone.v3.digitalpass.stamp.Stamp;

import static no.schedule.javazone.v3.util.LogUtils.makeLogTag;

public class PassFragment extends Fragment{

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();

    private static final String TAG = makeLogTag(PassFragment.class);
    ProgressBar pb;
    TextView ptv;
    private int[] counts = new int[2];

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
        String name = sharedPref.getString("name", null);

        if (name != null){
            VCard contactInfo = new VCard(name)
                    .setEmail(sharedPref.getString("email", null))
                    .setAddress(sharedPref.getString("address", null))
                    .setCompany(sharedPref.getString("company", null))
                    .setPhoneNumber(sharedPref.getString("number", null))
                    .setWebsite(sharedPref.getString("url", null));
            hasBarcode(contactInfo);
        } else {
            noBarcode();
        }

        pb = view.findViewById(R.id.progress_circular);
        ptv = view.findViewById(R.id.progress_text);
    }

    @Override
    public void onStart(){
        super.onStart();
        getStampProgress();
    }

    public void setProgressText(){
        pb.setMax(counts[0]);
        pb.setProgress(counts[1]);

        ptv.setText(counts[1] + " of " + counts[0] + " scanned.");

        Log.d("Counts", "counts[1] = " + counts[1] + ", counts[0] = " + counts[0]);

        if (counts[0] == counts[1]){
            Context context = getActivity();
            CharSequence text = "All stamped";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            ptv.setText("All stamped.");
        }
    }

    public void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == CameraActivity.BARCODE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                String name = data.getStringExtra("name");
                String email = data.getStringExtra("email");
                String address = data.getStringExtra("address");
                String company = data.getStringExtra("company");
                String number = data.getStringExtra("number");
                String url = data.getStringExtra("url");
                VCard contactInfo = new VCard(name)
                        .setEmail(email)
                        .setAddress(address)
                        .setCompany(company)
                        .setPhoneNumber(number)
                        .setWebsite(url);

                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("name", name);
                editor.putString("email", email);
                editor.putString("address", address);
                editor.putString("company", company);
                editor.putString("number", number);
                editor.putString("url", url);
                editor.commit();
                hasBarcode(contactInfo);
            }
        }
    }

    private void getStampProgress(){
        DatabaseReference ref = database.getReference("partners");
        final Context mContext = getContext();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                counts[0] = 0;
                counts[1] = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Stamp stamp = snapshot.getValue(Stamp.class);
                    counts[0]+= 1;
                    //if code is in sharedpreference
                    SharedPreferences sharedPref = mContext.getSharedPreferences("StampPref", Context.MODE_PRIVATE);
                    String savedCode = sharedPref.getString(stamp.getName(), null);
                    try {
                        if (savedCode != null && stamp.generateVerificationKey("tQMHgyouAYrOPACRDcEC").equals(savedCode)) {
                            counts[1] += 1;
                        }
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (InvalidKeySpecException e) {
                        e.printStackTrace();
                    }
                }
                Log.d("Counts", "counts[1] = " + counts[1] + ", counts[0] = " + counts[0]);
                setProgressText();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FirebaseError", "The read failed: " + databaseError.getCode());
            }
        });

    }

    private void noBarcode (){
        ImageView myImage = getView().findViewById(R.id.my_qr);
        myImage.setImageDrawable(getResources().getDrawable(R.drawable.qr_code));
        final Button button = getView().findViewById(R.id.scan_button);
        ((DigitalPassActivity) getActivity()).setRegistered(false);
        button.setText("Scan QR Code");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CameraActivity.class);
                intent.putExtra("requestCode", CameraActivity.BARCODE_REQUEST);
                    startActivityForResult(
                            intent,
                            CameraActivity.BARCODE_REQUEST);
                }
        });
    }

    private void hasBarcode (VCard contactInfo){
        //VCard contactInfo =
        Bitmap myBitmap = QRCode.from(contactInfo).bitmap();
        ImageView myImage = getView().findViewById(R.id.my_qr);
        myImage.setImageBitmap(myBitmap);
        ((DigitalPassActivity) getActivity()).setRegistered(true);
        final Button button = getView().findViewById(R.id.scan_button);
        button.setText("Delete QR Code");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.app_name);
                builder.setMessage("Are you sure you want to deregister? This will delete all registered stamps.");
                //builder.setIcon(R.drawable.ic_launcher);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        deleteQR();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    private void deleteQR(){
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //editor.remove(getString(R.string.myqr));
        editor.clear();
        editor.commit();
        sharedPref = getActivity().getSharedPreferences("StampPref", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.clear().commit();
        noBarcode();
        getStampProgress();
    }
}


