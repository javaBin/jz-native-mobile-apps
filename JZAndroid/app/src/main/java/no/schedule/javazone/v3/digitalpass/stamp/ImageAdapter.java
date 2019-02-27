package no.schedule.javazone.v3.digitalpass.stamp;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import no.schedule.javazone.v3.R;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mStamps.length;
    }

    public Stamp getItem(int position) {
        return mStamps[position];
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

        imageView.setImageResource(mStamps[position].getImage());

        return imageView;
    }


    private Stamp[] mStamps = {
            new Stamp(R.drawable.vaadin, "vaadin", "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest", "url"),
            new Stamp(R.drawable.miles, "miles", "test", "url"),
            new Stamp(R.drawable.seven_n, "7N", "test", "url"),
            new Stamp(R.drawable.vaadin, "vaadin", "test", "url"),
            new Stamp(R.drawable.miles, "miles", "test", "url"),
            new Stamp(R.drawable.seven_n, "7N", "test", "url"),
            new Stamp(R.drawable.vaadin, "vaadin", "test", "url"),
            new Stamp(R.drawable.miles, "miles", "test", "url"),
            new Stamp(R.drawable.seven_n, "7N", "test", "url"),
            new Stamp(R.drawable.vaadin, "vaadin", "test", "url"),
            new Stamp(R.drawable.miles, "miles", "test", "url"),
            new Stamp(R.drawable.seven_n, "7N", "test", "url"),
            new Stamp(R.drawable.vaadin, "vaadin", "test", "url"),
            new Stamp(R.drawable.miles, "miles", "test", "url"),
            new Stamp(R.drawable.seven_n, "7N", "test", "url"),
            new Stamp(R.drawable.vaadin, "vaadin", "test", "url"),
            new Stamp(R.drawable.miles, "miles", "test", "url"),
            new Stamp(R.drawable.seven_n, "7N", "test", "url"),
            new Stamp(R.drawable.vaadin, "vaadin", "test", "url"),
            new Stamp(R.drawable.miles, "miles", "test", "url"),
            new Stamp(R.drawable.seven_n, "7N", "test", "url"),
            new Stamp(R.drawable.miles, "miles", "test", "url"),
            new Stamp(R.drawable.seven_n, "7N", "test", "url"),
            new Stamp(R.drawable.miles, "miles", "test", "url"),
            new Stamp(R.drawable.seven_n, "7N", "test", "url"),
            new Stamp(R.drawable.vaadin, "vaadin", "test", "url"),
            new Stamp(R.drawable.miles, "miles", "test", "url"),
            new Stamp(R.drawable.seven_n, "7N", "test", "url"),
            new Stamp(R.drawable.miles, "miles", "test", "url"),
            new Stamp(R.drawable.seven_n, "7N", "test", "url"),
            new Stamp(R.drawable.miles, "miles", "test", "url"),
            new Stamp(R.drawable.seven_n, "7N", "test", "url"),
            new Stamp(R.drawable.miles, "miles", "test", "url"),
            new Stamp(R.drawable.seven_n, "7N", "test", "url"),
            new Stamp(R.drawable.miles, "miles", "test", "url"),
            new Stamp(R.drawable.seven_n, "7N", "test", "url"),
            new Stamp(R.drawable.miles, "miles", "test", "url"),
            new Stamp(R.drawable.seven_n, "7N", "test", "url"),
            new Stamp(R.drawable.vaadin, "vaadin", "test", "url"),
            new Stamp(R.drawable.miles, "miles", "test", "url"),
            new Stamp(R.drawable.seven_n, "7N", "test", "url"),
            new Stamp(R.drawable.vaadin, "vaadin", "test", "url"),
            new Stamp(R.drawable.miles, "miles", "test", "url"),
            new Stamp(R.drawable.seven_n, "7N", "test", "url"),
            new Stamp(R.drawable.vaadin, "vaadin", "test", "url"),
            new Stamp(R.drawable.miles, "miles", "test", "url"),
            new Stamp(R.drawable.seven_n, "7N", "test", "url"),
            new Stamp(R.drawable.vaadin, "vaadin", "test", "url"),
            new Stamp(R.drawable.miles, "miles", "test", "url"),
            new Stamp(R.drawable.seven_n, "7N", "test", "url"),
            new Stamp(R.drawable.vaadin, "vaadin", "test", "url"),
            new Stamp(R.drawable.miles, "miles", "test", "url"),
            new Stamp(R.drawable.seven_n, "7N", "test", "url"),
            new Stamp(R.drawable.vaadin, "vaadin", "test", "url"),
            new Stamp(R.drawable.miles, "miles", "test", "url"),
            new Stamp(R.drawable.seven_n, "7N", "test", "url"),
            new Stamp(R.drawable.miles, "miles", "test", "url"),
            new Stamp(R.drawable.seven_n, "7N", "test", "url"),
    };
}
