package no.schedule.javazone.v3.ui.widget;


import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.widget.Checkable;

/**
 * An extension of {@link FloatingActionButton} which implements {@link Checkable}.
 */
public class CheckableFloatingActionButton extends FloatingActionButton implements Checkable {

    private boolean mChecked;

    private static final int[] CHECKED_STATE_SET = { android.R.attr.state_checked };

    public CheckableFloatingActionButton(Context context) {
        super(context);
    }

    public CheckableFloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckableFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void setChecked(boolean checked) {
        if (checked == mChecked) return;
        mChecked = checked;
        refreshDrawableState();
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }
}

