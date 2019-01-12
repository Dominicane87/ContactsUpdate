package com.vlavladada.hw2112;

import android.app.Application;

import com.vlavladada.hw2112.Data.StoreProvider;

public class App extends Application {

    private static App instance;
    public App(){
        super();
        instance = this;
    }
    public static App get(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        StoreProvider.getInstance().setContext(this);
    }
}
