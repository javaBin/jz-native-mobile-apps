package no.schedule.javazone.v3.digitalpass;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import no.schedule.javazone.v3.R;

public class DigitalPassFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_digital_pass, container, false);
    }
}
