package de.hhu.droidprog17.finances.view;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import de.hhu.droidprog17.finances.R;

/**
 * Created by baber101 on 21.07.2017.
 */

public class SettingsFragment extends PreferenceFragment {
    public static final String USERNAME_KEY = "settings_username";
    public static final String INCOGNITO_KEY = "settings_privacy_switch";

    @Override
    public void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        addPreferencesFromResource(R.xml.settings);
    }
}
