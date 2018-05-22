/*
 * Copyright 2015 Google Inc. All rights reserved.
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

package no.schedule.javazone.v3.session;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import no.schedule.javazone.v3.Config;
import no.schedule.javazone.v3.archframework.ModelWithLoaderManager;
import no.schedule.javazone.v3.archframework.QueryEnum;
import no.schedule.javazone.v3.archframework.UserActionEnum;
import no.schedule.javazone.v3.feedback.SessionFeedbackActivity;
import no.schedule.javazone.v3.model.ScheduleItem;
import no.schedule.javazone.v3.model.ScheduleItemHelper;
import no.schedule.javazone.v3.provider.ScheduleContract;
import no.schedule.javazone.v3.ui.UIUtils;
import no.schedule.javazone.v3.util.AnalyticsHelper;
import no.schedule.javazone.v3.util.SessionsHelper;
import no.schedule.javazone.v3.util.TimeUtils;

import static no.schedule.javazone.v3.provider.ScheduleContract.*;
import static no.schedule.javazone.v3.util.LogUtils.makeLogTag;

public class SessionDetailModel extends ModelWithLoaderManager<SessionDetailModel.SessionDetailQueryEnum,
        SessionDetailModel.SessionDetailUserActionEnum> {

    protected final static String TAG = makeLogTag(SessionDetailModel.class);

    private final Context mContext;

    private final SessionsHelper mSessionsHelper;

    private String mSessionId;

    private Uri mSessionUri;

    private boolean mSessionLoaded = false;

    private String mTitle;

    private String mSubtitle;

    private boolean mInSchedule;

    private boolean mInScheduleWhenSessionFirstLoaded;

    private int mServerReservationStatus;

    private boolean mIsKeynote;

    private long mSessionStart;

    private long mSessionEnd;

    private String mSessionAbstract;

    private String mUrl = "";

    private String mRoomId;

    private String[] mTags;

    private String mPhotoUrl;

    private boolean mHasLiveStream = false;

    private boolean mLiveStreamVideoWatched = false;

    private boolean mHasFeedback = false;

    private String mRequirements;

    private String mSpeakersNames;

    private int mSessionType;

    // Request pending
    private boolean mReservationPending = false;
    private boolean mReturnPending = false;

    // Reservation status
    private String mReservationStatus;
    private String mReservationResult;

    // Seats available
    private boolean mSeatsAvailable;

    /**
     * Holds a list of links for the session. The first element of the {@code Pair} is the resource
     * id for the string describing the link, the second is the {@code Intent} to launch when
     * selecting the link.
     */
    private List<Pair<Integer, Intent>> mLinks = new ArrayList<>();

    private List<Speaker> mSpeakers = new ArrayList<>();

    private List<ScheduleItem> mRelatedSessions;

    private StringBuilder mBuffer = new StringBuilder();

    public SessionDetailModel(Uri sessionUri, Context context, SessionsHelper sessionsHelper,
            LoaderManager loaderManager) {
        super(SessionDetailQueryEnum.values(), SessionDetailUserActionEnum.values(), loaderManager);
        mContext = context;
        mSessionsHelper = sessionsHelper;
        mSessionUri = sessionUri;
        mSessionId = extractSessionId(sessionUri);
    }

    @Override
    public void requestData(@NonNull SessionDetailQueryEnum query,
            @NonNull DataQueryCallback<SessionDetailQueryEnum> callback) {
        switch (query) {
            default:
                super.requestData(query, callback);
                break;
        }
    }

    public String getSessionId() {
        return mSessionId;
    }

    public String getSessionTitle() {
        return mTitle;
    }

    public String getSessionSubtitle() {
        return mSubtitle;
    }

    public String getSessionUrl() {
        return mUrl;
    }

    public int getSessionTrackColor() {
        return Color.TRANSPARENT;
    }

    public String getSessionAbstract() {
        return mSessionAbstract;
    }

    public boolean getLiveStreamVideoWatched() {
        return mLiveStreamVideoWatched;
    }

    public boolean isSessionOngoing() {
        long currentTimeMillis = TimeUtils.getCurrentTime(mContext);
        return currentTimeMillis > mSessionStart && currentTimeMillis <= mSessionEnd;
    }

    public boolean hasSessionStarted() {
        long currentTimeMillis = TimeUtils.getCurrentTime(mContext);
        return currentTimeMillis > mSessionStart;
    }

    public boolean hasSessionEnded() {
        long currentTimeMillis = TimeUtils.getCurrentTime(mContext);
        return currentTimeMillis > mSessionEnd;
    }

    /**
     * Returns the number of minutes, rounded down, since session has started, or 0 if not started
     * yet.
     */
    public long minutesSinceSessionStarted() {
        if (!hasSessionStarted()) {
            return 0l;
        } else {
            long currentTimeMillis = TimeUtils.getCurrentTime(mContext);
            // Rounded down number of minutes.
            return (currentTimeMillis - mSessionStart) / 60000;
        }
    }

    /**
     * Returns the number of minutes, rounded up, until session stars, or 0 if already started.
     */
    public long minutesUntilSessionStarts() {
        if (hasSessionStarted()) {
            return 0l;
        } else {
            long currentTimeMillis = TimeUtils.getCurrentTime(mContext);
            int minutes = (int) ((mSessionStart - currentTimeMillis) / 60000);
            // Rounded up number of minutes.
            return minutes * 60000 < (mSessionStart - currentTimeMillis) ? minutes + 1 : minutes;
        }
    }

    public long minutesUntilSessionEnds() {
        if (hasSessionEnded()) {
            // If session has ended, return 0 minutes until end of session.
            return 0l;
        } else {
            long currentTimeMillis = TimeUtils.getCurrentTime(mContext);
            int minutes = (int) ((mSessionEnd - currentTimeMillis) / 60000);
            // Rounded up number of minutes.
            return minutes * 60000 < (mSessionEnd - currentTimeMillis) ? minutes + 1 : minutes;
        }
    }

    public boolean isSessionReadyForFeedback() {
        long now = TimeUtils.getCurrentTime(mContext);
        return now > mSessionEnd - SessionDetailConstants.FEEDBACK_MILLIS_BEFORE_SESSION_END_MS;
    }

    public boolean isInSchedule() {
        return mInSchedule;
    }

    public boolean isInScheduleWhenSessionFirstLoaded() {
        return mInScheduleWhenSessionFirstLoaded;
    }

    public boolean isKeynote() {
        return mIsKeynote;
    }

    public boolean hasFeedback() {
        return mHasFeedback;
    }


    public String[] getTags() {
        return mTags;
    }

    public List<Pair<Integer, Intent>> getLinks() {
        return mLinks;
    }

    public List<Speaker> getSpeakers() {
        return mSpeakers;
    }

    public List<ScheduleItem> getRelatedSessions() {
        return mRelatedSessions;
    }

    public boolean hasSummaryContent() {
        return !TextUtils.isEmpty(mTitle)
                || !TextUtils.isEmpty(mSubtitle)
                || !TextUtils.isEmpty(mSessionAbstract);
    }

    @Override
    public boolean readDataFromCursor(Cursor cursor, SessionDetailQueryEnum query) {
        boolean success = false;

        if (cursor != null && cursor.moveToFirst()) {

            if (SessionDetailQueryEnum.SESSIONS == query) {
                readDataFromSessionCursor(cursor);
                mSessionLoaded = true;
                success = true;
            } else if (SessionDetailQueryEnum.SPEAKERS == query) {
                readDataFromSpeakersCursor(cursor);
                success = true;
            }
//            } else if (SessionDetailQueryEnum.FEEDBACK == query) {
//                readDataFromFeedbackCursor(cursor);
//                success = true;
            }


        return success;
    }

    private void readDataFromSessionCursor(Cursor cursor) {
        mTitle = cursor.getString(cursor.getColumnIndex(
                Sessions.SESSION_TITLE));
        mInSchedule = cursor.getInt(cursor.getColumnIndex(
                Sessions.SESSION_IN_MY_SCHEDULE)) != 0;

        if (!mSessionLoaded) {
            mInScheduleWhenSessionFirstLoaded = mInSchedule;
        }

        String tagsString = cursor
                .getString(cursor.getColumnIndex(Sessions.SESSION_TAGS));
        if (tagsString != null) {
            mIsKeynote = tagsString.contains(Config.Tags.SPECIAL_KEYNOTE);
            mTags = tagsString.split(",");
        }

        mSessionStart = cursor
                .getLong(cursor.getColumnIndex(Sessions.SESSION_START));
        mSessionEnd = cursor.getLong(cursor.getColumnIndex(Sessions.SESSION_END));

        mRoomId = cursor.getString(cursor.getColumnIndex(Sessions.ROOM_ID));

        mSessionAbstract = cursor
                .getString(cursor.getColumnIndex(Sessions.SESSION_ABSTRACT));

        mSpeakersNames = cursor
                .getString(cursor.getColumnIndex(Sessions.SESSION_SPEAKER_NAMES));

        mRequirements = cursor
                .getString(cursor.getColumnIndex(Sessions.SESSION_REQUIREMENTS));

        formatSubtitle();
    }

    public int getSessionType() {
        return mSessionType;
    }

    @VisibleForTesting
    public void formatSubtitle() {
        mSubtitle = UIUtils.formatSessionSubtitle(mSessionStart, mSessionEnd, mRoomId, mBuffer,
                mContext);
    }

    public Intent getFeedbackIntent() {
        return new Intent(Intent.ACTION_VIEW, mSessionUri, mContext,
                SessionFeedbackActivity.class);
    }

    private void readDataFromFeedbackCursor(Cursor cursor) {
        mHasFeedback = cursor.getCount() > 0;
    }

    private void readDataFromSpeakersCursor(Cursor cursor) {
        mSpeakers.clear();

        // Not using while(cursor.moveToNext()) because it would lead to issues when writing tests.
        // Either we would mock cursor.moveToNext() to return true and the test would have infinite
        // loop, or we would mock cursor.moveToNext() to return false, and the test would be for an
        // empty cursor.
        int count = cursor.getCount();
        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            final String speakerName =
                    cursor.getString(cursor.getColumnIndex(Speakers.SPEAKER_NAME));
            if (TextUtils.isEmpty(speakerName)) {
                continue;
            }

            final String speakerImageUrl = cursor.getString(
                    cursor.getColumnIndex(Speakers.PICTURE_URL));
            final String speakerCompany = cursor.getString(
                    cursor.getColumnIndex(Speakers.SPEAKER_COMPANY));
            final String speakerUrl = cursor.getString(
                    cursor.getColumnIndex(Speakers.SPEAKER_URL));
            final String speakerTwitterUrl = cursor.getString(
                    cursor.getColumnIndex(Speakers.SPEAKER_TWITTER_URL));
            final String speakerAbstract = cursor.getString(
                    cursor.getColumnIndex(Speakers.SPEAKER_ABSTRACT));

            mSpeakers.add(new Speaker(speakerName, speakerImageUrl, speakerCompany, speakerUrl,speakerTwitterUrl, speakerAbstract));
        }
    }

    private void readDataFromRelatedSessionsCursor(Cursor cursor) {
        mRelatedSessions = ScheduleItemHelper.cursorToItems(cursor, mContext);
    }

    @Override
    public Loader<Cursor> createCursorLoader(SessionDetailQueryEnum query, Bundle args) {
        CursorLoader loader = null;
        if (query == null) {
            return loader;
        }
        switch (query) {
            case SESSIONS:
                loader = getCursorLoaderInstance(mContext, mSessionUri,
                        SessionDetailQueryEnum.SESSIONS.getProjection(), null, null, null);
                break;
            case SPEAKERS:
                Uri speakersUri = getSpeakersDirUri(mSessionId);
                loader = getCursorLoaderInstance(mContext, speakersUri,
                        SessionDetailQueryEnum.SPEAKERS.getProjection(), null, null,
                        Speakers.DEFAULT_SORT);
//                break;
//            case FEEDBACK:
//                Uri feedbackUri = getFeedbackUri(mSessionId);
//                loader = getCursorLoaderInstance(mContext, feedbackUri,
//                        SessionDetailQueryEnum.FEEDBACK.getProjection(), null, null, null);
//                break;
        }
        return loader;
    }

    @VisibleForTesting
    public CursorLoader getCursorLoaderInstance(Context context, Uri uri, String[] projection,
            String selection, String[] selectionArgs, String sortOrder) {
        return new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
    }

    @VisibleForTesting
    public Uri getFeedbackUri(String sessionId) {
        return null;
        // return Feedback.buildFeedbackUri(sessionId);
    }

    @VisibleForTesting
    public Uri getSpeakersDirUri(String sessionId) {
        return Sessions.buildSpeakersDirUri(sessionId);
    }

    @VisibleForTesting
    public String extractSessionId(Uri uri) {
        return Sessions.getSessionId(uri);
    }

    @Override
    public void processUserAction(SessionDetailUserActionEnum action, @Nullable Bundle args,
            UserActionCallback<SessionDetailUserActionEnum> callback) {
        switch (action) {
            case STAR:
            case UNSTAR:
                mInSchedule = action == SessionDetailUserActionEnum.STAR;
                setSessionBookmark(mSessionId, mInSchedule, mTitle);
                callback.onModelUpdated(this, action);
                break;
            case SHOW_MAP:
                // ANALYTICS EVENT: Click on Map action in Session Details page.
                // Contains: Session title/subtitle
                sendAnalyticsEvent("Session", "Map", mTitle);
                callback.onModelUpdated(this, action);
                break;
            case SHOW_SHARE:
                // ANALYTICS EVENT: Share a session.
                // Contains: Session title.
                sendAnalyticsEvent("Session", "Shared", mTitle);
                callback.onModelUpdated(this, action);
                break;
            case GIVE_FEEDBACK:
                // ANALYTICS EVENT: Click on the "send feedback" action in Session Details.
                // Contains: The session title.
                sendAnalyticsEvent("Session", "Feedback", getSessionTitle());
                callback.onModelUpdated(this, action);
                break;
            case EXTENDED:
                // ANALYTICS EVENT: Click on the extended session link in Session Details.
                sendAnalyticsEvent("Session", "Extended Session", getSessionTitle());
                callback.onModelUpdated(this, action);
                break;
            case STAR_RELATED:
            case UNSTAR_RELATED:
                String sessionId = args == null ? null : args.getString(Sessions.SESSION_ID);
                if (!TextUtils.isEmpty(sessionId)) {
                    boolean inSchedule = action == SessionDetailUserActionEnum.STAR_RELATED;
                    setSessionBookmark(sessionId, inSchedule, mTitle);
                    for (ScheduleItem item : mRelatedSessions) {
                        if (TextUtils.equals(sessionId, item.sessionId)) {
                            item.inSchedule = inSchedule;
                            break;
                        }
                    }
                    callback.onModelUpdated(this, action);
                }
                break;
            default:
                callback.onError(action);
        }
    }

    private void setSessionBookmark(String sessionId, boolean bookmarked, String title) {
        Uri sessionUri = Sessions.buildSessionUri(sessionId);
        mSessionsHelper.setSessionStarred(sessionUri, bookmarked, title);
    }

    @VisibleForTesting
    public void sendAnalyticsEvent(String category, String action, String label) {
        AnalyticsHelper.sendEvent(category, action, label);
    }

    @Override
    public void cleanUp() {
    }

    public enum SessionDetailQueryEnum implements QueryEnum {
        SESSIONS(0, new String[]{Sessions.SESSION_START,
                Sessions.SESSION_END,
                Sessions.SESSION_LEVEL,
                Sessions.SESSION_TITLE,
                Sessions.SESSION_ABSTRACT,
                Sessions.SESSION_REQUIREMENTS,
                Sessions.SESSION_IN_MY_SCHEDULE,
                Sessions.ROOM_ID,
                Rooms.ROOM_NAME,
                Sessions.SESSION_TAGS,
                Sessions.SESSION_CONFERENCE,
                Sessions.SESSION_SPEAKER_NAMES}),
        SPEAKERS(1, new String[]{
            ScheduleContract.Speakers.SPEAKER_NAME,
                ScheduleContract.Speakers.PICTURE_URL,
                ScheduleContract.Speakers.SPEAKER_COMPANY,
                ScheduleContract.Speakers.SPEAKER_ABSTRACT,
                ScheduleContract.Speakers.SPEAKER_URL,
                ScheduleContract.Speakers.SPEAKER_TWITTER_URL});
       // FEEDBACK(2, new String[]{ScheduleContract.Feedback.SESSION_ID}),

        private int id;

        private String[] projection;

        SessionDetailQueryEnum(int id, String[] projection) {
            this.id = id;
            this.projection = projection;
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public String[] getProjection() {
            return projection;
        }

    }

    public enum SessionDetailUserActionEnum implements UserActionEnum {
        STAR(1),
        UNSTAR(2),
        SHOW_MAP(3),
        SHOW_SHARE(4),
        GIVE_FEEDBACK(5),
        EXTENDED(6),
        STAR_RELATED(7),
        UNSTAR_RELATED(8),
        RESERVE(9),
        RETURN(10); // Cancel reservation
        private int id;

        SessionDetailUserActionEnum(int id) {
            this.id = id;
        }

        @Override
        public int getId() {
            return id;
        }

    }

    public static class Speaker {

        private String mName;

        private String mImageUrl;

        private String mCompany;

        private String mUrl;

        private String mTwitterUrl;

        private String mAbstract;

        public Speaker(String name, String imageUrl, String company, String url,
                String twitterUrl, String anAbstract) {
            mName = name;
            mImageUrl = imageUrl;
            mCompany = company;
            mUrl = url;
            mTwitterUrl = twitterUrl;
            mAbstract = anAbstract;
        }

        public String getName() {
            return mName;
        }

        public String getImageUrl() {
            return mImageUrl;
        }

        public String getCompany() {
            return mCompany;
        }

        public String getUrl() {
            return mUrl;
        }

        public String getTwitterUrl() {
            return mTwitterUrl;
        }

        public String getAbstract() {
            return mAbstract;
        }
    }
}
