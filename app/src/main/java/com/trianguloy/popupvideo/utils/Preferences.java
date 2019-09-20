package com.trianguloy.popupvideo.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Class to store and retrieve preferences
 * Sort of a wrapper of SharedPreferences
 */
public class Preferences {

    //the android SharedPreferences
    private static final String PREF_NAME = "pref";
    private SharedPreferences preferences;

    /**
     * Constructor, initializates the preferences
     *
     * @param context the context from where load the SharedPreferences class
     */
    public Preferences(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // ------------------- elements -------------------

    /**
     * App selected: what to open, null if choose
     */
    private static final String KEY_APP = "app";
    private static final String DEFAULT_APP = "com.google.android.youtube";

    /**
     * Getter for the app
     *
     * @return the app
     */
    public String getApp() {
        return preferences.getString(KEY_APP, DEFAULT_APP);
    }

    /**
     * Setter for the app
     *
     * @param app the new selected app, null to choose
     */
    public void setApp(String app) {
        preferences.edit().putString(KEY_APP, app).apply();
    }


    /**
     * Fullscreen
     */
    private static final String KEY_FULLSCREEN= "fullscreen";
    private static final boolean DEFAULT_FULLSCREEN = false;

    /**
     * Getter for the fullscreen
     *
     * @return if fullscreen
     */
    public boolean getFullScreen() {
        return preferences.getBoolean(KEY_FULLSCREEN, DEFAULT_FULLSCREEN);
    }

    /**
     * Setter for the fullscreen
     *
     * @param fullscreen if fullscreen
     */
    public void setFullscreen(boolean fullscreen) {
        preferences.edit().putBoolean(KEY_FULLSCREEN, fullscreen).apply();
    }
}
