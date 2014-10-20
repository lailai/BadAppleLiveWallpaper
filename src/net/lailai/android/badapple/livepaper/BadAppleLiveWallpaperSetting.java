/**
 * Copyright (C) 2011-2014 lailai.
 */
package net.lailai.android.badapple.livepaper;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * ライブ壁紙設定アクティビティクラス
 * @author lailai
 */
public class BadAppleLiveWallpaperSetting extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    /* (非 Javadoc)
     * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName(BadAppleLiveWallpaper.SHARED_PREFS_NAME);
        addPreferencesFromResource(R.xml.settings);
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    /* (非 Javadoc)
     * @see android.preference.PreferenceActivity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    /* (非 Javadoc)
     * @see android.content.SharedPreferences.OnSharedPreferenceChangeListener#onSharedPreferenceChanged(android.content.SharedPreferences, java.lang.String)
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }

}
