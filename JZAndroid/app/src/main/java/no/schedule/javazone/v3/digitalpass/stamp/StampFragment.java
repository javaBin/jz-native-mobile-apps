package no.schedule.javazone.v3.digitalpass.stamp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import no.schedule.javazone.v3.R;

public class StampFragment extends Fragment {

    private GridView gridview;
    private ImageAdapter logoAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StampFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.digital_pass_stamp_fragment, container, false);
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
                selectLogo(id);
            }
        });

    }

    private void selectLogo(long id){
    }
}
