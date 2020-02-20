package com.haeseong5.android.linememo;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ApplicationClass extends Application {

    public static Realm relalm;

    //앱에서 Realm을 사용하기 전에 Realm을 초기화해야합니다. 이 작업은 한 번만 수행하면됩니다.
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().name("myrealm.realm").build();
        Realm.setDefaultConfiguration(config);
    }
}
