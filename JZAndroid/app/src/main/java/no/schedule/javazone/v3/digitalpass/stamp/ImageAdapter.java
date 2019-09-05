package no.schedule.javazone.v3.digitalpass.stamp;

import android.content.Context;
import android.graphics.Color;

import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.caverock.androidsvg.SVG;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.util.ArrayList;

import no.schedule.javazone.v3.R;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Stamp> mStamps;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();

    public ImageAdapter(Context c) {
        mContext = c;
        mStamps = new ArrayList<>();
        DatabaseReference ref = database.getReference("partners");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Stamp stamp = snapshot.getValue(Stamp.class);
                    Log.d("read stamps", stamp.getName());
                    Log.d("ImageAdapter", "LogoUrl_png " + stamp.getLogoUrl_png());
                    Log.d("ImageAdapter", "LogoUrl " + stamp.getLogoUrl());
                    mStamps.add(stamp);
                    Log.d("ImageAdapter", "Stamp count: " + mStamps.size());
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FirebaseError", "The read failed: " + databaseError.getCode());
            }
        });
    }

    public int getCount() {
        return mStamps.size();
    }

    public Stamp getItem(int position) {
        return mStamps.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView mImageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            mImageView = new ImageView(mContext);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(250, 150));
            mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mImageView.setPadding(8, 8, 8, 8);
        } else {
            mImageView = (ImageView) convertView;
        }

        GenericRequestBuilder<Uri, InputStream, SVG, PictureDrawable> requestBuilder = Glide.with(mContext)
                .using(Glide.buildStreamModelLoader(Uri.class, mContext), InputStream.class)
                .from(Uri.class)
                .as(SVG.class)
                .transcode(new SvgDrawableTranscoder(), PictureDrawable.class)
                .sourceEncoder(new StreamEncoder())
                .cacheDecoder(new FileToStreamDecoder<SVG>(new SvgDecoder()))
                .decoder(new SvgDecoder())
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.circle_border)
                .animate(android.R.anim.fade_in)
                .listener(new SvgSoftwareLayerSetter<Uri>());

        Uri uri = Uri.parse(mStamps.get(position).getLogoUrl());
        requestBuilder
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                // SVG cannot be serialized so it's not worth to cache it
                .load(uri)
                .into(mImageView);

        if(mStamps.get(position).name.equals("Ambita AS")) {
            mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            mImageView.setAdjustViewBounds(true);
        }

        if (mStamps.get(position).isTagged()) {
            mImageView.setBackgroundColor(Color.parseColor("#f5ad42"));
            Log.d("ImageAdapter", "Setting color");
        } else {
            mImageView.setBackgroundColor(Color.parseColor("white"));
            Log.d("ImageAdapter", "Setting color");
        }

        return mImageView;
    }
}
