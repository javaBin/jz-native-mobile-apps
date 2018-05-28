/*
 * Copyright (c) 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package no.schedule.javazone.v3.info.travel;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import no.schedule.javazone.v3.R;
import no.schedule.javazone.v3.info.BaseInfoFragment;
import no.schedule.javazone.v3.info.CollapsibleCard;

import static no.schedule.javazone.v3.util.LogUtils.LOGE;
import static no.schedule.javazone.v3.util.LogUtils.makeLogTag;

public class TravelFragment extends BaseInfoFragment<TravelInfo> {
    private static final String TAG = makeLogTag(TravelFragment.class);

    private TravelInfo mTravelInfo;

    private CollapsibleCard publicTransportationCard;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.info_travel_frag, container, false);
        publicTransportationCard =
                (CollapsibleCard) root.findViewById(R.id.publicTransportationCard);
        return root;
    }

    @Override
    public String getTitle(@NonNull Resources resources) {
        return resources.getString(R.string.title_travel);
    }

    @Override
    public void updateInfo(TravelInfo info) {
        mTravelInfo = info;
    }

    @Override
    protected void showInfo() {
        if (mTravelInfo != null) {
            publicTransportationCard.setCardDescription(mTravelInfo.getPublicTransportationInfo());
        } else {
            LOGE(TAG, "TravelInfo should not be null.");
        }
    }
}
