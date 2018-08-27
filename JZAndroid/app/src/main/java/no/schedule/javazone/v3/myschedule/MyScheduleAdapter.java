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
package no.schedule.javazone.v3.myschedule;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.doist.recyclerviewext.sticky_headers.StickyHeaders;
import no.schedule.javazone.v3.Config;
import no.schedule.javazone.v3.R;
import no.schedule.javazone.v3.model.ScheduleItem;
import no.schedule.javazone.v3.schedule.NonSessionItemViewHolder;
import no.schedule.javazone.v3.schedule.SessionItemViewHolder;
import no.schedule.javazone.v3.ui.UIUtils;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static no.schedule.javazone.v3.schedule.ScheduleItemViewHolder.SessionTimeFormat.SPAN;

class MyScheduleAdapter extends Adapter<ViewHolder> implements StickyHeaders, StickyHeaders.ViewSetup {

    private static final int VIEW_TYPE_SESSION = 0;
    private static final int VIEW_TYPE_NON_SESSION = 1;
    private static final int VIEW_TYPE_SPACER = 2;
    private static final int VIEW_TYPE_DAY_HEADER = 3;
    private static final int VIEW_TYPE_MESSAGE_CARD = 4;
    private static final int PAYLOAD_TAG_META = 7;

    private static final List<DaySeparator> DAY_SEPARATORS;

    static {

        DAY_SEPARATORS = new ArrayList<>(Config.CONFERENCE_DAYS.length + 1);

        for (int i = 0; i < Config.CONFERENCE_DAYS.length; i++) {
            DAY_SEPARATORS.add(new DaySeparator(i, Config.CONFERENCE_DAYS[i][0]));
        }
    }

    private final List<Object> mItems = new ArrayList<>();
    private final Callbacks mCallbacks;
    private final float stuckHeaderElevation;

    private Context mContext;

    interface Callbacks extends SessionItemViewHolder.Callbacks {

        /**
         * @param conferenceDay the conference day for the clicked header, where 0 is the first day
         */
        void onAddEventsClicked(int conferenceDay);
    }

    MyScheduleAdapter(Context context, Callbacks callbacks) {
        mContext = context;
        mCallbacks = callbacks;
        stuckHeaderElevation = context.getResources().getDimension(R.dimen.card_elevation);
        setHasStableIds(true);
        setItems(null); // build the initial list of items
    }

    void setItems(List<ScheduleItem> items) {
        List<Object> newData = new ArrayList<>();

        int day = 0;
        if (items != null && !items.isEmpty()) {
            // Add the items to our list, interleaving separators as we go
            long separatorTime = DAY_SEPARATORS.get(day).mStartTime;
            for (ScheduleItem item : items) {
                // We need to iterate here in case the first day is empty
                while (item.startTime >= separatorTime && day < DAY_SEPARATORS.size()) {
                    // add the separator first
                    newData.add(new SeparatorSpacer());
                    newData.add(DAY_SEPARATORS.get(day));
                    day++;
                    if (day >= DAY_SEPARATORS.size()) {
                        // run the list to the end
                        separatorTime = Long.MAX_VALUE;
                    } else {
                        separatorTime = DAY_SEPARATORS.get(day).mStartTime;
                    }
                }
                // Add the item
                newData.add(item);
            }
        }

        // Add any remaining separators
        for (; day < DAY_SEPARATORS.size(); day++) {
            newData.add(new SeparatorSpacer());
            newData.add(DAY_SEPARATORS.get(day));
        }

        DiffUtil.DiffResult diff = DiffUtil.calculateDiff(new MyIoDiff(mItems, newData));
        mItems.clear();
        mItems.addAll(newData);
        diff.dispatchUpdatesTo(this);
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public long getItemId(int position) {
        Object item = mItems.get(position);
        if (item instanceof ScheduleItem) {
            return item.hashCode();
        }
        if (item instanceof DaySeparator) {
            return ((DaySeparator) item).mStartTime;
        }
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        Object item = mItems.get(position);
        if (item instanceof ScheduleItem) {
            if (((ScheduleItem) item).type == ScheduleItem.BREAK) {
                return VIEW_TYPE_NON_SESSION;
            }
            return VIEW_TYPE_SESSION;
        }
        if (item instanceof SeparatorSpacer) {
            return VIEW_TYPE_SPACER;
        }
        if (item instanceof DaySeparator) {
            return VIEW_TYPE_DAY_HEADER;
        }

        return RecyclerView.INVALID_TYPE;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_SESSION:
                return SessionItemViewHolder.newInstance(parent, mCallbacks, SPAN);
            case VIEW_TYPE_NON_SESSION:
                return NonSessionItemViewHolder.newInstance(parent);
            case VIEW_TYPE_SPACER:
                return SeparatorSpacerViewHolder.newInstance(parent);
            case VIEW_TYPE_DAY_HEADER:
                return DaySeparatorViewHolder.newInstance(parent, mCallbacks);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Object item = mItems.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_SESSION:
                ((SessionItemViewHolder) holder).bind((ScheduleItem)item);
                break;
            case VIEW_TYPE_NON_SESSION:
                ((NonSessionItemViewHolder) holder).bind((ScheduleItem) item);
                break;
            case VIEW_TYPE_DAY_HEADER:
                ((DaySeparatorViewHolder) holder).bind((DaySeparator) item);
                break;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
            onBindViewHolder(holder, position);
    }

    @Override
    public boolean isStickyHeader(int position) {
        return getItemViewType(position) == VIEW_TYPE_DAY_HEADER;
    }

    @Override
    public void setupStickyHeaderView(View view) {
        view.setTranslationZ(stuckHeaderElevation);
    }

    @Override
    public void teardownStickyHeaderView(View view) {
        view.setTranslationZ(0f);
    }

    /**
     * Return the position of the first item that has not finished.
     */
    int findPositionForTime(final long time) {
        for (int i = 0; i < mItems.size(); i++) {
            Object item = mItems.get(i);
            if (item instanceof ScheduleItem) {
                if (((ScheduleItem) item).endTime > time) {
                    return i;
                }
            } else if (item instanceof DaySeparator) {
                if (((DaySeparator) item).mStartTime > time) {
                    return i;
                }
            }
        }
        return RecyclerView.NO_POSITION;
    }

    private static class DaySeparatorViewHolder extends ViewHolder {

        private final TextView mDateText;
        private final Button mAddEventsButton;
        private final Callbacks mCallbacks;

        private static final StringBuilder FORMAT_STRINGBUILDER = new StringBuilder();

        static DaySeparatorViewHolder newInstance(ViewGroup parent, Callbacks callbacks) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.my_schedule_list_item_day_separator, parent, false);
            return new DaySeparatorViewHolder(itemView, callbacks);
        }

        private DaySeparatorViewHolder(View itemView, Callbacks callbacks) {
            super(itemView);
            mCallbacks = callbacks;
            mDateText = (TextView) itemView.findViewById(R.id.text);
            mAddEventsButton = (Button) itemView.findViewById(R.id.add_events);
        }

        private void bind(final DaySeparator separator) {
            mDateText.setText(UIUtils.formatDaySeparator(itemView.getContext(),
                    FORMAT_STRINGBUILDER, separator.mStartTime));
            mAddEventsButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallbacks != null && separator.mDay >= 0) {
                        mCallbacks.onAddEventsClicked(separator.mDay);
                    }
                }
            });
            mAddEventsButton.setVisibility(separator.mDay >= 0 ? VISIBLE : GONE);
        }
    }

    private static class DaySeparator {
        private final int mDay;
        private final long mStartTime;

        DaySeparator(int day, long startTime) {
            mDay = day;
            mStartTime = startTime;
        }
    }

    private static class SeparatorSpacer { }

    private static class SeparatorSpacerViewHolder extends ViewHolder {

        private SeparatorSpacerViewHolder(View itemView) {
            super(itemView);
        }

        static SeparatorSpacerViewHolder newInstance(@NonNull ViewGroup parent) {
            return new SeparatorSpacerViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.spacer, parent, false));
        }
    }

    private static class MyIoDiff extends DiffUtil.Callback {

        private final List<Object> oldItems;
        private final List<Object> newItems;

        MyIoDiff(List<Object> oldItems, List<Object> newItems) {
            this.oldItems = oldItems;
            this.newItems = newItems;
        }

        @Override
        public int getOldListSize() {
            return oldItems.size();
        }

        @Override
        public int getNewListSize() {
            return newItems.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            Object oldItem = oldItems.get(oldItemPosition);
            Object newItem = newItems.get(newItemPosition);
            if (oldItem instanceof SeparatorSpacer && newItem instanceof SeparatorSpacer) {
                return true;
            } else if (oldItem instanceof DaySeparator && newItem instanceof DaySeparator) {
                return ((DaySeparator) oldItem).mDay == ((DaySeparator) newItem).mDay;
            } else if ((oldItem instanceof ScheduleItem && newItem instanceof ScheduleItem)) {
                return oldItem.equals(newItem);
            }
            return false;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return true; // all items are stateless
        }
    }
}
