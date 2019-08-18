package no.schedule.javazone.v3.digitalpass.stamp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.caverock.androidsvg.SVG;

import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import no.schedule.javazone.v3.R;
import no.schedule.javazone.v3.digitalpass.camera.CameraActivity;
import no.schedule.javazone.v3.map.MapActivity;
import no.schedule.javazone.v3.util.FirebaseRemoteConfigUtil;

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
        setStyle(R.style.FullScreenDialogStyle, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.digital_pass_stamp_dialog_fragment, container, false);

        // Setting logo
        ImageView logo = view.findViewById(R.id.dialog_stamp_logo);

        GenericRequestBuilder<Uri, InputStream, SVG, PictureDrawable> requestBuilder = Glide.with(getContext())
                .using(Glide.buildStreamModelLoader(Uri.class, getContext()), InputStream.class)
                .from(Uri.class)
                .as(SVG.class)
                .transcode(new SvgDrawableTranscoder(), PictureDrawable.class)
                .sourceEncoder(new StreamEncoder())
                .cacheDecoder(new FileToStreamDecoder<SVG>(new SvgDecoder()))
                .decoder(new SvgDecoder())
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.jzappsplash)
                .animate(android.R.anim.fade_in)
                .listener(new SvgSoftwareLayerSetter<Uri>());

        Uri uri = Uri.parse(stamp.getLogoUrl());
        requestBuilder
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                // SVG cannot be serialized so it's not worth to cache it
                .load(uri)
                .into(logo);

        if(stamp.name.equals("Ambita AS")) {
            logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
            logo.setAdjustViewBounds(true);
        }

        // Setting description
        TextView url = view.findViewById(R.id.dialog_stamp_url);
        String urlText = String.format("<a href=\"%s\">Visit %s</a>", stamp.getHomepageUrl(), stamp.getName());
        url.setText(Html.fromHtml(urlText));
        url.setMovementMethod(LinkMovementMethod.getInstance());

        final Button scanButton = view.findViewById(R.id.dialog_scan_button);
        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(
                        new Intent(getActivity(), CameraActivity.class), CameraActivity.BARCODE_REQUEST);
            }
        });


//        final Button mapButton = view.findViewById(R.id.dialog_map_button);
//        mapButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), MapActivity.class), MapActivity.MARKER_REQUEST);
//            }
//        });


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
                String verificationKey;
                try {
                    verificationKey = stamp.generateVerificationKey(FirebaseRemoteConfigUtil.getRemoteConfigSequence("partners"));
                } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                    Log.d("StampDialogFragment", e.getMessage());
                    return;
                }
                String barcode = data.getStringExtra("barcode");
                Log.d("barcode", barcode);
                if (barcode.equals(verificationKey)) {
                    stamp.setTagged(true);
                    StampListFragment slf = (StampListFragment) getTargetFragment();
                    slf.refreshList();
                    SharedPreferences sharedPref = getActivity().getSharedPreferences("StampPref", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(stamp.getName(), barcode);
                    editor.commit();
                } else {
                    Context context = getActivity();
                    CharSequence text = "Wrong QR code.";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        }
    }

    public void setStamp(Stamp stamp) {
        this.stamp = stamp;
    }
}