/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package no.schedule.javazone.v3.schedule;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;

import no.schedule.javazone.v3.R;
import no.schedule.javazone.v3.model.ScheduleItem;
import no.schedule.javazone.v3.util.TimeUtils;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * A {@link ViewHolder} modeling Sessions.
 */
public class SessionItemViewHolder extends ScheduleItemViewHolder
        implements DividerDecoration.Divided {

    private final TextView mTitle;
    private final TextView mSubTitle;
    private final TextView mDescription;
    private final ImageButton mBookmark;
    private final Button mRate;

    private final Callbacks mCallbacks;
    @Nullable
    private ScheduleItem mSession;

    private SessionItemViewHolder(View itemView, Callbacks callbacks,
                                  SessionTimeFormat timeFormat) {
        super(itemView, timeFormat);
        mCallbacks = callbacks;
        mTitle = (TextView) itemView.findViewById(R.id.slot_title);
        mSubTitle = (TextView) itemView.findViewById(R.id.speaker_names);
        mDescription = (TextView) itemView.findViewById(R.id.slot_description);
        mBookmark = (ImageButton) itemView.findViewById(R.id.bookmark);
       // mLiveNow = itemView.findViewById(R.id.live_now_badge);
        mRate = (Button) itemView.findViewById(R.id.give_feedback_button);

        itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallbacks == null || mSession == null) {
                    return;
                }
                mCallbacks.onSessionClicked(mSession.sessionId);
            }
        });
        mRate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCallbacks == null || mSession == null) {
                    return;
                }
                mCallbacks.onFeedbackClicked(mSession.sessionId, mSession.title);
            }
        });
        mBookmark.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (mCallbacks == null || mSession == null) {
                    return;
                }
                Resources res = view.getResources();
                // Note: contentDescription is set based on the previous inSchedule state.
                mBookmark.setContentDescription(mSession.inSchedule ?
                        res.getString(R.string.add_bookmark) :
                        res.getString(R.string.remove_bookmark));
                mBookmark.setActivated(!mBookmark.isActivated());
                mCallbacks.onBookmarkClicked(mSession.sessionId, mSession.inSchedule);
            }
        });
//        mTagClick = new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Tag tag = (Tag) v.getTag(R.id.key_session_tag);
//                if (tag == null || mCallbacks == null) {
//                    return;
//                }
//                mCallbacks.onTagClicked(tag);
//            }
//        };
    }

    public static SessionItemViewHolder newInstance(ViewGroup parent, Callbacks callbacks,
                                                    SessionTimeFormat timeFormat) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.schedule_session_item, parent, false);
        return new SessionItemViewHolder(itemView, callbacks, timeFormat);
    }


    public void bind(@NonNull ScheduleItem item) {
        if (item.type != ScheduleItem.SESSION) {
            return;
        }
        mSession = item;
        final Context context = itemView.getContext();

        mTitle.setText(item.title);
        setSpeakerNames(mSubTitle, item);
        setDescription(mDescription, item);

        boolean isLivestreamed = item.isKeynote()
                || (item.flags & ScheduleItem.FLAG_HAS_LIVESTREAM) != 0;
        final long now = TimeUtils.getCurrentTime(context);
        final boolean streamingNow = isLivestreamed && item.startTime <= now && now <= item.endTime;

        if (mCallbacks.bookmarkingEnabled() && !item.isKeynote()) {
            mBookmark.setVisibility(VISIBLE);
            // activated is proxy for in-schedule
            mBookmark.setActivated(item.inSchedule);
        } else {
            mBookmark.setVisibility(GONE);
        }

        boolean showFeedback = mCallbacks.feedbackEnabled()
                && (now >= item.endTime && !item.hasGivenFeedback);
        mRate.setVisibility(showFeedback ? VISIBLE : GONE);
    }

    public interface Callbacks {
        /**
         * @param sessionId The ID of the session
         */
        void onSessionClicked(String sessionId);

        /**
         * @return true if bookmark icons should be shown
         */
        boolean bookmarkingEnabled();

        /**
         * @param sessionId    The ID of the session
         * @param isInSchedule Whether the session is bookmarked in the backing data
         */
        void onBookmarkClicked(String sessionId, boolean isInSchedule);

        /**
         * @return true if feedback buttons can be shown
         */
        boolean feedbackEnabled();

        /**
         * @param sessionId    The ID of the session
         * @param sessionTitle The title of the session
         */
        void onFeedbackClicked(String sessionId, String sessionTitle);

        /**
         * @param tag The tag that was clicked
         */
    }
}
