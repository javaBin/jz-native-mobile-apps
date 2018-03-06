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
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;

import java.util.List;

import no.schedule.javazone.v3.model.ScheduleItem;
import no.schedule.javazone.v3.myschedule.MyScheduleContract.MySchedulePresenter;
import no.schedule.javazone.v3.myschedule.MyScheduleContract.MyScheduleView;
import no.schedule.javazone.v3.util.CursorModelLoader;

public class MySchedulePresenterImpl implements MySchedulePresenter {
    private static final int LOADER_SCHEDULE = 1;
    private static final int LOADER_BLOCKS = 2;

    private Context mContext;
    private MyScheduleView mView;
    private MyScheduleModel mModel;

    MySchedulePresenterImpl(Context context, MyScheduleView view) {
        mContext = context;
        mView = view;
        mModel = new MyScheduleModel();
    }

    @Override
    public void initModel(LoaderManager loaderManager) {
        loaderManager.initLoader(LOADER_SCHEDULE, null, mSessionsLoaderCallbacks);
        loaderManager.initLoader(LOADER_BLOCKS, null, mBlocksLoaderCallbacks);
    }

    @Override
    public void refreshUI(LoaderManager loaderManager) {
        loaderManager.restartLoader(LOADER_SCHEDULE, null, mSessionsLoaderCallbacks);
        loaderManager.restartLoader(LOADER_BLOCKS, null, mBlocksLoaderCallbacks);
    }

    // -- LoaderCallbacks implementations

    private LoaderCallbacks<List<ScheduleItem>> mSessionsLoaderCallbacks =
            new LoaderCallbacks<List<ScheduleItem>>() {

        @Override
        public Loader<List<ScheduleItem>> onCreateLoader(int id, Bundle args) {
            return new CursorModelLoader<>(mContext, new MyScheduleCursorTransform());
        }

        @Override
        public void onLoadFinished(Loader<List<ScheduleItem>> loader, List<ScheduleItem> data) {
            mModel.setSessionItems(data);
            mView.onScheduleLoaded(mModel);
        }

        @Override
        public void onLoaderReset(Loader<List<ScheduleItem>> loader) {
            mModel.setSessionItems(null);
            mView.onScheduleLoaded(mModel);
        }
    };


    private LoaderCallbacks<List<ScheduleItem>> mBlocksLoaderCallbacks =
            new LoaderCallbacks<List<ScheduleItem>>() {

                @Override
                public Loader<List<ScheduleItem>> onCreateLoader(int id, Bundle args) {
                    return new CursorModelLoader<>(mContext, new MyScheduleBlocksCursorTransform());
                }

                @Override
                public void onLoadFinished(Loader<List<ScheduleItem>> loader, List<ScheduleItem> data) {
                    mModel.setBlockItems(data);
                    mView.onScheduleLoaded(mModel);
                }

                @Override
                public void onLoaderReset(Loader<List<ScheduleItem>> loader) {
                    mModel.setBlockItems(null);
                    mView.onScheduleLoaded(mModel);
                }
            };
}
