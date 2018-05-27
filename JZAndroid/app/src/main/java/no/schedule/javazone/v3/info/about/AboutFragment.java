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
package no.schedule.javazone.v3.info.about;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import no.schedule.javazone.v3.BuildConfig;
import no.schedule.javazone.v3.R;
import no.schedule.javazone.v3.info.BaseInfoFragment;
import no.schedule.javazone.v3.info.CollapsibleCard;
import no.schedule.javazone.v3.util.AboutUtils;

import static no.schedule.javazone.v3.util.LogUtils.LOGE;
import static no.schedule.javazone.v3.util.LogUtils.makeLogTag;

public class AboutFragment extends BaseInfoFragment<AboutInfo> {
    private static final String TAG = makeLogTag(AboutFragment.class);

    private AboutInfo mAboutInfo;

    private CollapsibleCard mAdditionalInfoCard;
    private CollapsibleCard mAboutJavaZoneCard;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.info_about_frag, container, false);
        mAdditionalInfoCard = (CollapsibleCard) root.findViewById(R.id.additionalInfo);
        mAboutJavaZoneCard = (CollapsibleCard) root.findViewById(R.id.aboutConference);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Links
        view.findViewById(R.id.terms_of_service_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent termsLink = new Intent(Intent.ACTION_VIEW);
                termsLink.setData(Uri.parse(getResources()
                        .getString(R.string.about_terms_url)));
                startActivity(termsLink);
            }
        });
//        view.findViewById(R.id.privacy_policy_link).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent privacyPolicyLink = new Intent(Intent.ACTION_VIEW);
//                privacyPolicyLink.setData(Uri.parse(getResources()
//                        .getString(R.string.about_privacy_policy_url)));
//                startActivity(privacyPolicyLink);
//            }
//        });
        view.findViewById(R.id.open_source_licenses_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutUtils.showOpenSourceLicenses(getActivity());
            }
        });

        // Version
        TextView appVersion = (TextView) view.findViewById(R.id.app_version);
        appVersion.setText(getResources()
                .getString(R.string.about_app_version, BuildConfig.VERSION_NAME));

    }

    @Override
    public String getTitle(@NonNull Resources resources) {
        return resources.getString(R.string.title_about);
    }

    @Override
    public void updateInfo(AboutInfo info) {
        mAboutInfo = info;
    }

    @Override
    protected void showInfo() {
        if (mAboutInfo != null) {
            mAdditionalInfoCard.setCardDescription(mAboutInfo.getAdditionalInfoDescription());
            mAboutJavaZoneCard.setCardDescription(mAboutInfo.getAboutJavaZoneDescription());
        } else {
            LOGE(TAG, "AboutInfo should not be null.");
        }
    }
}
