package no.schedule.javazone.v3.digitalpass.stamp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Stamp> mStamps;

    public ImageAdapter(Context c) {
        mContext = c;
        mStamps = readStamps("stamps.csv");
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
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(250, 150));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        if(mStamps.get(position).isTagged()){
            int color = Color.parseColor("#AE6118");
            imageView.setColorFilter(color);
            Log.d("ImageAdapter", "Setting color");
        } else {
            int color = Color.parseColor("#000000");
            imageView.setColorFilter(color);
            Log.d("ImageAdapter", "Setting color");
        }

        imageView.setImageResource(mStamps.get(position).getImage());

        return imageView;
    }

    private ArrayList<Stamp> readStamps(String file){
        ArrayList<Stamp> stamps = new ArrayList<Stamp>();
        try {
            CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(mContext.getAssets().open(file))));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                int imageId = mContext.getResources().getIdentifier(nextLine[0],"drawable", mContext.getPackageName());
                String name = nextLine[1];
                String code = nextLine[4];
                Stamp stamp = new Stamp(imageId, name, nextLine[2], nextLine[3], code);
                SharedPreferences sharedPref = mContext.getSharedPreferences("StampPref", Context.MODE_PRIVATE);
                String savedCode = sharedPref.getString(name, null);
                if (savedCode != null && code.equals(savedCode)) {
                    stamp.setTagged(true);
                }
                stamps.add(stamp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "The specified stamps file was not found", Toast.LENGTH_SHORT).show();
        }

        return stamps;
    }

}
