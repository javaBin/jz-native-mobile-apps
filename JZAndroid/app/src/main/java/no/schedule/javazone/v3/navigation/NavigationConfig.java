/*
 * Copyright (c) 2016 Google Inc.
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

package no.schedule.javazone.v3.navigation;

import java.util.ArrayList;
import java.util.List;

import no.schedule.javazone.v3.navigation.NavigationModel.NavigationItemEnum;

/**
 * Configuration file for items to show in the {@link AppNavigationView}. This is used by the {@link
 * NavigationModel}.
 */
public class NavigationConfig {

    public final static NavigationItemEnum[] ITEMS = new NavigationItemEnum[]{
            NavigationItemEnum.SCHEDULE,
            NavigationItemEnum.MY_SCHEDULE,
            NavigationItemEnum.DIGITAL_PASS,
//            NavigationItemEnum.FEED,
//            NavigationItemEnum.MAP,
            NavigationItemEnum.INFO,
    };

    private static NavigationItemEnum[] concatenateItems(NavigationItemEnum[] first,
            NavigationItemEnum[] second) {
        NavigationItemEnum[] items = new NavigationItemEnum[first.length + second.length];
        for (int i = 0; i < first.length; i++) {
            items[i] = first[i];
        }
        for (int i = 0; i < second.length; i++) {
            items[first.length + i] = second[i];
        }
        return items;
    }

    public static NavigationItemEnum[] appendItem(NavigationItemEnum[] first,
            NavigationItemEnum second) {
        return concatenateItems(first, new NavigationItemEnum[]{second});
    }

    public static NavigationItemEnum[] filterOutItemsDisabledInBuildConfig(
            NavigationItemEnum[] items) {
        List<NavigationItemEnum> enabledItems = new ArrayList<>();
        for (int i = 0; i < items.length; i++) {
            boolean includeItem = true;
            switch (items[i]) {
                case SCHEDULE:
                    includeItem = true;
                    break;
                case MY_SCHEDULE:
                    includeItem = true;
                    break;
                case DIGITAL_PASS:
                    includeItem = true;
                    break;
//                case FEED:
//                    includeItem = BuildConfig.ENABLE_FEED_IN_NAVIGATION;
//                    break;
//                case MAP:
//                    includeItem = true;
//                    break;
                case INFO:
                    includeItem = true;
                    break;
            }

            if (includeItem) {
                enabledItems.add(items[i]);
            }
        }
        return enabledItems.toArray(new NavigationItemEnum[enabledItems.size()]);
    }

}
