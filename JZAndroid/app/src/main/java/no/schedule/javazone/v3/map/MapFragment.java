package no.schedule.javazone.v3.map;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;

import no.schedule.javazone.v3.R;

import static no.schedule.javazone.v3.util.LogUtils.makeLogTag;

public class MapFragment extends Fragment {

    private static final String TAG = makeLogTag(MapFragment.class);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.map_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final PhotoView photoView = view.findViewById(R.id.map);
        photoView.setImageResource(R.drawable.map_lumbridge);

        // Modify this if you wish to change the starting scale
        photoView.post(
                new Runnable() {
                    @Override
                    public void run() {
                        photoView.setScale(1f);
                    }
                });
    }
}
