package no.schedule.javazone.v3.digitalpass;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import no.schedule.javazone.v3.R;
import no.schedule.javazone.v3.util.AnalyticsHelper;

public class DigitalPassPagerFragment extends Fragment {
    private static final String DIGITAL_PASS_TAB_FRAGMENTS_TAGS = "digitalpass_tab_fragments_tags";

    private static final String CURRENT_DIGITAL_PASS_TAB_FRAGMENT_POSITION = "current_digitalpass_fragments_position";

    private ViewPager mViewPager;
    private DigitalPassViewPagerAdapter mViewPagerAdapter;
    private TabLayout mTabLayout;
    private int mCurrentPage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.digital_pass_pager_frag, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String[] digitalPassTabFragmentTags = null;
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(DIGITAL_PASS_TAB_FRAGMENTS_TAGS)) {
                digitalPassTabFragmentTags = savedInstanceState.getStringArray(
                        DIGITAL_PASS_TAB_FRAGMENTS_TAGS);
            }
            if (savedInstanceState.containsKey(CURRENT_DIGITAL_PASS_TAB_FRAGMENT_POSITION)) {
                mCurrentPage = savedInstanceState.getInt(
                        CURRENT_DIGITAL_PASS_TAB_FRAGMENT_POSITION);
            }
        }
        mViewPager = view.findViewById(R.id.view_pager);
        mViewPagerAdapter = new DigitalPassViewPagerAdapter(getContext(),
                getChildFragmentManager());
        mViewPagerAdapter.setRetainedFragmentsTags(digitalPassTabFragmentTags);
        mViewPager.setAdapter(mViewPagerAdapter);
        mTabLayout = view.findViewById(R.id.sliding_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabTextColors(getResources().getColor(R.color.app_white), getResources().getColor(R.color.jz_orange));

        String currentLabel = (String) mTabLayout.getTabAt(mViewPager.getCurrentItem()).getText();
        AnalyticsHelper.sendScreenView("DigitalPass: " + currentLabel, getActivity());

        // Add a listener for any reselection events
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(final TabLayout.Tab tab) {
                AnalyticsHelper.sendScreenView("DigitalPass: " + tab.getText().toString(), getActivity());
            }

            @Override
            public void onTabUnselected(final TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(final TabLayout.Tab tab) {
            }
        });
        mViewPager.setPageMargin(getResources()
                .getDimensionPixelSize(R.dimen.my_schedule_page_margin));
        mViewPager.setPageMarginDrawable(R.drawable.page_margin);
        View header = view.findViewById(R.id.header_anim);
        if (header instanceof ImageView) {
            AnimatedVectorDrawable avd = (AnimatedVectorDrawable) ContextCompat.getDrawable(
                    getContext(), R.drawable.avd_header_info);
            ((ImageView) header).setImageDrawable(avd);
            avd.start();
        }

        setCurrentPage();
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        /*
        if (mViewPagerAdapter != null && mViewPagerAdapter.getFragments() != null) {
            Fragment[] digitalPassFragments = mViewPagerAdapter.getFragments();
            String[] tags = new String[digitalPassFragments.length];
            for (int i = 0; i < tags.length; i++) {
                tags[i] = digitalPassFragments[i].getTag();
            }
            outState.putStringArray(DIGITAL_PASS_TAB_FRAGMENTS_TAGS, tags);
            outState.putInt(CURRENT_DIGITAL_PASS_TAB_FRAGMENT_POSITION, mViewPager.getCurrentItem());
        }
        */
    }

    private void setCurrentPage() {
        if (mViewPager != null) {
            mViewPager.setCurrentItem(mCurrentPage);
        }
    }
}
