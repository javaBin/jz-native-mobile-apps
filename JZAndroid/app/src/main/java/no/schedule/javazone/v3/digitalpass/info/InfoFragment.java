package no.schedule.javazone.v3.digitalpass.info;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import no.schedule.javazone.v3.R;

import static no.schedule.javazone.v3.util.LogUtils.makeLogTag;

public class InfoFragment extends Fragment {
    private static final String TAG = makeLogTag(InfoFragment.class);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.digital_pass_info_fragment, container, false);
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
    }
}
