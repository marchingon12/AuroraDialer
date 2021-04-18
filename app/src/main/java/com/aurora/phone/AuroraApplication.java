/*
 * Aurora Store
 * Copyright (C) 2019, Rahul Kumar Patel <whyorean@gmail.com>
 *
 * Aurora Store is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Aurora Store is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Aurora Store.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package com.aurora.phone;

import android.app.Application;
import android.telecom.Call;

import com.aurora.contact.entity.ContactBundle;
import com.aurora.phone.events.Event;
import com.aurora.phone.events.RxBus;

import io.reactivex.plugins.RxJavaPlugins;

public class AuroraApplication extends Application {

    public static Call call;
    private static RxBus rxBus = null;

    public static RxBus getRxBus() {
        return rxBus;
    }

    public static void rxNotify(Event event) {
        rxBus.getBus().accept(event);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        rxBus = new RxBus();
        RxJavaPlugins.setErrorHandler(err ->{
            err.printStackTrace();
        });
    }
}
