package no.schedule.javazone.v3.digitalpass;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import no.schedule.javazone.v3.R;
import no.schedule.javazone.v3.digitalpass.pass.PassFragment;
import no.schedule.javazone.v3.digitalpass.stamp.StampFragment;
import no.schedule.javazone.v3.info.BaseInfoFragment;

import static no.schedule.javazone.v3.util.LogUtils.LOGD;
import static no.schedule.javazone.v3.util.LogUtils.LOGE;
import static no.schedule.javazone.v3.util.LogUtils.makeLogTag;

public class DigitalPassViewPagerAdapter extends FragmentPagerAdapter {

    private final static String TAG = makeLogTag(DigitalPassViewPagerAdapter.class);

    private final static int NUM_PAGES = 2;
    private final static int PASS_INDEX = 0;
    private final static int STAMP_INDEX = 1;

    private Context mContext;

    private Fragment[] mFragments;

    private FragmentManager mFragmentManager;

    public DigitalPassViewPagerAdapter(Context context, FragmentManager fm){
        super(fm);
        mContext = context;
        mFragmentManager = fm;
    }

    @Override
    public Fragment getItem(int position) {
        LOGD(TAG, "Creating fragment #" + position);

        // Reuse cached fragment if present
        if (mFragments != null && mFragments.length > position && mFragments[position] != null) {
            return mFragments[position];
        }

        if (mFragments == null) {
            mFragments = new Fragment[getCount()];
        }

        switch (position) {
            case PASS_INDEX:
                mFragments[position] = new PassFragment();
                break;
            case STAMP_INDEX:
                mFragments[position] = new StampFragment();
                break;
        }

        return mFragments[position];
    }

    @Override
    public int getCount() { return NUM_PAGES; }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0:
                return mContext.getResources().getString(R.string.title_digital_pass);
            case 1:
                return mContext.getResources().getString(R.string.title_stamp);
            default:
                return mContext.getResources().getString(R.string.title_digital_pass);
        }
    }

    public Fragment[] getFragments() {
        if (mFragments == null) {
            // Force creating the fragments
            int count = getCount();
            for (int i = 0; i < count; i++) {
                getItem(i);
            }
        }
        return mFragments;
    }

    public void updatePass(){
        Fragment passFragment = getItem(PASS_INDEX);
    }

    /**
     * When the device changes orientation, the {@link Fragment}s are recreated
     * by the system, and they have the same tag ids as the ones previously used. Therefore, this
     * sets the cached fragments to the ones recreated by the system. This must be called before any
     * call to {@link #getItem(int)} (note that when fragments are
     * recreated after orientation change, the {@link FragmentPagerAdapter} doesn't call {@link
     * #getItem(int)}.)
     *
     * @param tags the tags of the retained {@link Fragment}s. Ignored if null
     *             or empty.
     */
    public void setRetainedFragmentsTags(String[] tags) {
        if (tags != null && tags.length > 0) {
            mFragments = new BaseInfoFragment[tags.length];
            for (int i = 0; i < tags.length; i++) {
                BaseInfoFragment fragment =
                        (BaseInfoFragment) mFragmentManager.findFragmentByTag(tags[i]);
                mFragments[i] = fragment;
                if (fragment == null) {
                    LOGE(TAG, "Fragment with existing tag " + tags[i] + " not found!");
                    // No retained fragment (this happens if the fragment hadn't been shown before,
                    // because the tag on it would have been null in that case), so instantiate it
                    getItem(i);
                }
            }
        }
    }
}
