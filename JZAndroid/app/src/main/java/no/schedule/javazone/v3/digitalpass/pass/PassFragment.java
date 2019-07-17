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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVReader;

import net.glxn.qrgen.android.QRCode;
import net.glxn.qrgen.core.scheme.VCard;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import no.schedule.javazone.v3.R;
import no.schedule.javazone.v3.digitalpass.DigitalPassActivity;
import no.schedule.javazone.v3.digitalpass.camera.CameraActivity;
import no.schedule.javazone.v3.digitalpass.stamp.Stamp;

import static no.schedule.javazone.v3.util.LogUtils.makeLogTag;

public class PassFragment extends Fragment{

    private static final String TAG = makeLogTag(PassFragment.class);
    ProgressBar pb;
    TextView ptv;

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
        int[] numbers = readStamps("stamps.csv");
        pb.setMax(numbers[0]);
        pb.setProgress(numbers[1]);

        ptv.setText(numbers[1] + " of " + numbers[0] + " scanned.");

        if (numbers[0] == numbers[1]){
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

    private int[] readStamps(String file){
        ArrayList<Stamp> stamps = new ArrayList<Stamp>();
        Context mContext = getContext();
        int [] numbers = new int[2];
        try {
            CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(mContext.getAssets().open(file))));
            String[] nextLine;
            int count = 0;
            while ((nextLine = reader.readNext()) != null) {
                String name = nextLine[1];
                String code = nextLine[4];
                SharedPreferences sharedPref = mContext.getSharedPreferences("StampPref", Context.MODE_PRIVATE);
                String savedCode = sharedPref.getString(name, null);
                if (savedCode != null && code.equals(savedCode)) {
                    numbers[1] += 1;
                }
                numbers[0] += 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "The specified stamps file was not found", Toast.LENGTH_SHORT).show();
        }
        return numbers;
    }

    private void noBarcode (){
        ImageView myImage = getView().findViewById(R.id.my_qr);
        myImage.setImageDrawable(getResources().getDrawable(R.drawable.qr_code));
        final Button button = getView().findViewById(R.id.scan_button);
        ((DigitalPassActivity) getActivity()).setRegistered(false);
        button.setText("Scan QR Code");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    startActivityForResult(
                            new Intent(getActivity(), CameraActivity.class),
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
        editor.remove(getString(R.string.myqr));
        editor.commit();
        sharedPref = getActivity().getSharedPreferences("StampPref", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.clear().commit();
        noBarcode();
    }
}


