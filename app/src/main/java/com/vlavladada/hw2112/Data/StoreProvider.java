package com.vlavladada.hw2112.Data;

import android.content.Context;

public class StoreProvider {
    private Context context;
    private static final String SP_AUTH = "SP_AUTH";
    private static final String TOKEN_KEY = "TOKEN";
    private static final StoreProvider ourInstance = new StoreProvider();

    public static StoreProvider getInstance() {
        return ourInstance;
    }

    private StoreProvider() {

    }

    public void setContext(Context context){
        this.context = context;
    }

    public void saveToken(String token){
        context.getSharedPreferences(SP_AUTH, Context.MODE_PRIVATE)
                .edit()
                .putString(TOKEN_KEY,token)
                .apply();
    }

    public String getToken(){
        return context.getSharedPreferences(SP_AUTH,Context.MODE_PRIVATE)
                .getString(TOKEN_KEY,null);
    }

    public void clearToken(){
        context.getSharedPreferences(SP_AUTH,Context.MODE_PRIVATE)
                .edit()
                .remove(TOKEN_KEY)
                .apply();
    }
}
