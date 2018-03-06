/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.schedule.javazone.v3.schedule;

import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewSwitcher;

import io.doist.recyclerviewext.sticky_headers.StickyHeadersLinearLayoutManager;
import no.schedule.javazone.v3.Config;
import no.schedule.javazone.v3.R;
import no.schedule.javazone.v3.archframework.UpdatableView;
import no.schedule.javazone.v3.provider.ScheduleContract;
import no.schedule.javazone.v3.util.SessionsHelper;
import no.schedule.javazone.v3.util.TimeUtils;

/**
 * This is used by the {@link android.support.v4.view.ViewPager} used by the narrow layout in {@link
 * ScheduleActivity}. It is a {@link ListFragment} that shows schedule items for a day, using
 * {@link ScheduleDayAdapter} as its data source.
 */
public class ScheduleSingleDayFragment extends Fragment
        implements UpdatableView<ScheduleModel, ScheduleModel.MyScheduleQueryEnum, ScheduleModel.MyScheduleUserActionEnum>,
        LoaderCallbacks<Cursor>, SessionItemViewHolder.Callbacks {
    /**
     * This is 1 for the first day of the conference, 2 for the second, and so on, and {@link
     * ScheduleModel#PRE_CONFERENCE_DAY_ID} for the preconference day
     */
    private int mDayId = 1;
    private ViewSwitcher mLoadingSwitcher;
    private RecyclerView mRecyclerView;
    private ScheduleDayAdapter mViewAdapter;
    private UserActionListener<ScheduleModel.MyScheduleUserActionEnum> mListener;
    private boolean mScheduleLoaded = false;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.schedule_singleday_frag, container, false);
        mLoadingSwitcher = (ViewSwitcher) view;
        mRecyclerView = (RecyclerView) view.findViewById(android.R.id.list);
        mRecyclerView.addItemDecoration(new DividerDecoration(getContext()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(
                new StickyHeadersLinearLayoutManager<ScheduleDayAdapter>(getContext()));
        mViewAdapter = new ScheduleDayAdapter(getContext(), this, true);
        mRecyclerView.setAdapter(mViewAdapter);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (mScheduleLoaded) {
            showSchedule();
        }
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mListener != null) {
            mListener.onUserAction(ScheduleModel.MyScheduleUserActionEnum.RELOAD_DATA, null);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    @Override
    public void displayData(ScheduleModel model, ScheduleModel.MyScheduleQueryEnum query) {
        switch (query) {
            case SCHEDULE:
                updateSchedule(model);
                break;
        }
    }

    @Override
    public void displayErrorMessage(ScheduleModel.MyScheduleQueryEnum query) {
        // Not showing any error
    }

    @Override
    public void displayUserActionResult(ScheduleModel model, ScheduleModel.MyScheduleUserActionEnum userAction,
            boolean success) {
        switch (userAction) {
            case RELOAD_DATA:
            case SESSION_STAR:
            case SESSION_UNSTAR:
                updateSchedule(model);
                break;
            case SESSION_SLOT:
                break;
            case FEEDBACK:
                break;
        }
    }

    private void updateSchedule(ScheduleModel model) {
        showSchedule();
        mViewAdapter.updateItems(model.getConferenceDataForDay(mDayId));
        mScheduleLoaded = true;

        if (isShowingCurrentDay()) {
            LinearLayoutManager lm = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            if (lm.findFirstVisibleItemPosition() <= 0) {
                // If we're showing the current day and we're still showing the first pos, move
                // to the current time slot
                moveToCurrentTimeSlot(false);
            }
        }
    }

    public void resetListPosition() {
        if (isShowingCurrentDay()) {
            moveToCurrentTimeSlot(true);
        } else {
            // Else scroll to the first item
            mRecyclerView.smoothScrollToPosition(0);
        }
    }

    @Override
    public Uri getDataUri(ScheduleModel.MyScheduleQueryEnum query) {
        // Not used by the model
        return null;
    }

    @Override
    public void addListener(UserActionListener<ScheduleModel.MyScheduleUserActionEnum> listener) {
        mListener = listener;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(final Loader<Cursor> loader) {
    }

    // -- Adapter callbacks

    @Override
    public void onSessionClicked(String sessionId) {
        Bundle args = new Bundle();
        Uri sessionUri = ScheduleContract.Sessions.buildSessionUri(sessionId);
        args.putString(ScheduleModel.SESSION_URL_KEY, sessionUri.toString());
        mListener.onUserAction(ScheduleModel.MyScheduleUserActionEnum.SESSION_SLOT, args);

        startActivity(new Intent(Intent.ACTION_VIEW, sessionUri));
    }

    @Override
    public boolean bookmarkingEnabled() {
        return true;
    }

    @Override
    public void onBookmarkClicked(String sessionId, boolean isInSchedule) {
        ScheduleModel.MyScheduleUserActionEnum action = isInSchedule
                ? ScheduleModel.MyScheduleUserActionEnum.SESSION_UNSTAR
                : ScheduleModel.MyScheduleUserActionEnum.SESSION_STAR;
        Bundle args = new Bundle();
        args.putString(ScheduleModel.SESSION_ID_KEY, sessionId);
        mListener.onUserAction(action, args);
        SessionsHelper.showBookmarkClickedHint(getActivity().findViewById(android.R.id.content),
                !isInSchedule);
    }

    @Override
    public boolean feedbackEnabled() {
        return false;
    }

    @Override
    public void onFeedbackClicked(String sessionId, String sessionTitle) {
        Bundle args = new Bundle();
        args.putString(ScheduleModel.SESSION_ID_KEY, sessionId);
        args.putString(ScheduleModel.SESSION_TITLE_KEY, sessionTitle);
        mListener.onUserAction(ScheduleModel.MyScheduleUserActionEnum.FEEDBACK, args);
       // SessionFeedbackActivity.launchFeedback(getContext(), sessionId);
    }

    private void initViews() {
        mDayId = getArguments().getInt(ScheduleActivity.ARG_CONFERENCE_DAY_INDEX, 0);

        // Set id to list view, so it can be referred to from tests
        TypedArray ids = getResources().obtainTypedArray(R.array.myschedule_listview_ids);
        int listViewId = ids.getResourceId(mDayId, 0);
        ids.recycle();
        mRecyclerView.setId(listViewId);
    }

    private void moveToCurrentTimeSlot(boolean animate) {
        final long now = TimeUtils.getCurrentTime(getContext());
        final int pos = mViewAdapter.findTimeHeaderPositionForTime(now);
        if (pos >= 0) {
            if (animate) {
                mRecyclerView.smoothScrollToPosition(pos);
            } else {
                LinearLayoutManager lm = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                lm.scrollToPositionWithOffset(pos,
                        getResources().getDimensionPixelSize(R.dimen.spacing_normal));
            }
        }
    }

    private boolean isShowingCurrentDay() {
        final long now = TimeUtils.getCurrentTime(getContext());
        return mDayId > 0 && now >= Config.CONFERENCE_DAYS[mDayId - 1][0]
                && now <= Config.CONFERENCE_DAYS[mDayId - 1][1];
    }

    private void showSchedule() {
        if (mLoadingSwitcher != null) {
            mLoadingSwitcher.setDisplayedChild(1);
        }
    }
}
