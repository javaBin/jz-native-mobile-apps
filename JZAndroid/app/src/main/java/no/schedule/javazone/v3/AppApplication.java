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

package no.schedule.javazone.v3;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDexApplication;

import com.google.android.gms.security.ProviderInstaller;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import no.schedule.javazone.v3.util.AnalyticsHelper;
import no.schedule.javazone.v3.util.SettingsUtils;
import no.schedule.javazone.v3.util.TimeUtils;

import static no.schedule.javazone.v3.util.LogUtils.LOGD;
import static no.schedule.javazone.v3.util.LogUtils.LOGE;
import static no.schedule.javazone.v3.util.LogUtils.LOGW;
import static no.schedule.javazone.v3.util.LogUtils.makeLogTag;

/**
 * {@link Application} used to initialize Analytics. Code initialized in Application
 * classes is rare since this code will be run any time a ContentProvider, Activity, or Service is
 * used by the user or system. Analytics, dependency injection, and multi-dex frameworks are in this
 * very small set of use cases.
 */
public class AppApplication extends MultiDexApplication {

    private static final String TAG = makeLogTag(AppApplication.class);

    /*
    private RefWatcher mRefWatcher;

    public static RefWatcher getRefWatcher(Context context) {
        AppApplication application = (AppApplication) context.getApplicationContext();
        return application.mRefWatcher;
    } */

    @Override
    public void onCreate() {
        super.onCreate();
        /*
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }

         */
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
        // .setDeveloperModeEnabled(BuildConfig.DEBUG)
         //       .build();
        //FirebaseRemoteConfig.getInstance().setConfigSettings(configSettings);
        //FirebaseRemoteConfig.getInstance().setDefaults(R.xml.remote_config_defaults);
       // mRefWatcher = LeakCanary.install(this);

        TimeUtils.setAppStartTime(getApplicationContext(), System.currentTimeMillis());
        // STUP FIREBASE CRASH ANALYTICS

        LOGD(TAG, "Analytics being prepared.");
        AnalyticsHelper.prepareAnalytics(this);
        SettingsUtils.markDeclinedWifiSetup(this, false);

        // Ensure an updated security provider is installed into the system when a new one is
        // available via Google Play services.
        try {
            ProviderInstaller.installIfNeededAsync(getApplicationContext(),
                    new ProviderInstaller.ProviderInstallListener() {
                        @Override
                        public void onProviderInstalled() {
                            LOGW(TAG, "New security provider installed.");
                        }

                        @Override
                        public void onProviderInstallFailed(int errorCode, Intent intent) {
                            LOGE(TAG, "New security provider install failed.");
                            // No notification shown there is no user intervention needed.
                        }
                    });
        } catch (Exception ignorable) {
            LOGE(TAG, "Unknown issue trying to install a new security provider.", ignorable);
        }
    }

}
