package com.app.appsinrek.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.app.appsinrek.models.User;
import com.app.appsinrek.phonenumberui.AppConstant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;


public class StorageUtil {

    private SharedPreferences preferences;
    private final Context context;
    private final String userInfo = "userInfo";
    private final String userAuthToken = "userAuthTonek";

    public StorageUtil(Context context) {
        this.context = context;
    }

    public SharedPreferences getSharedPreferences() {
        String STORAGE = AppConstant.sharedPrefName;
        return context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
    }

    public void writeUserInfo(User modelUser) {
        preferences = getSharedPreferences();
        Gson gson = new Gson();
        String json = gson.toJson(modelUser);
        preferences.edit().putString(userInfo, json).apply();
    }
    public User readUserInfo() {
        preferences = getSharedPreferences();
        Gson gson = new Gson();
        String json = preferences.getString(userInfo, null);
        if (json != null) {
            Log.e(userInfo, json);
        }else{
            return null;
        }
        Type type = new TypeToken<User>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void writeUserAuthToken(String auth_token) {
        preferences = getSharedPreferences();
        preferences.edit().putString(userAuthToken, auth_token).apply();
    }

    public String readUserAuthToken() {
        preferences = getSharedPreferences();
        String auth_token = preferences.getString(userAuthToken, null);
        if (auth_token == null) {
            return null;
        }
        return auth_token;
    }

    public void clear() {
        preferences = getSharedPreferences();
        preferences.edit().clear().apply();
    }

    public void rememberMe(boolean b,String email, String pass) {
        preferences = getSharedPreferences();
        if(b) {
            preferences.edit().putBoolean("rememberMe", b).apply();
            preferences.edit().putString("email", email).apply();
            preferences.edit().putString("password", pass).apply();
        }else{
            clearRememberMe();
        }
    }
    public void clearRememberMe() {
        preferences = getSharedPreferences();
        preferences.edit().remove("rememberMe").
        remove("email").
        remove("password").apply();
    }
    public Boolean rememberMe() {
        preferences = getSharedPreferences();
        return preferences.getBoolean("rememberMe", false);
    }
    public String rememberEmail() {
        preferences = getSharedPreferences();
        return preferences.getString("email", "");
    }
    public String rememberPassword() {
        preferences = getSharedPreferences();
        return preferences.getString("password", "");
    }

    public void setFirstTimeView(boolean b) {
        preferences = getSharedPreferences();
        preferences.edit().putBoolean("firstView", b).apply();
    }
    public boolean getFirstTimeView() {
        preferences = getSharedPreferences();
        return preferences.getBoolean("firstView", false);
    }
}