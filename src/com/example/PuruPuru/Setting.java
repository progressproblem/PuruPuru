package com.example.PuruPuru;

import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Setting extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);

        CheckBoxPreference useAccelerometer = (CheckBoxPreference)findPreference("useAccelerometer");
        useAccelerometer.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String summary;
                if ((Boolean) newValue) {
                    summary = getText(R.string.use_accelerometer_summary_on).toString();
                } else {
                    summary = getText(R.string.use_accelerometerm_summary_off).toString();
                }
                preference.setSummary(summary);
                return true;
            }
        });

        CheckBoxPreference useEyeglass = (CheckBoxPreference)findPreference("useEyeglass");
        useEyeglass.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String summary;
                if ((Boolean) newValue) {
                    summary = getText(R.string.use_eyeglass_summary_on).toString();
                } else {
                    summary = getText(R.string.use_eyeglass_summary_off).toString();
                }
                preference.setSummary(summary);
                return true;
            }
        });
    }

    public static boolean useAccelerometer(Context con){
        return PreferenceManager.getDefaultSharedPreferences(con).getBoolean("useAccelerometer", false);
    }

    public static boolean useEyeglass(Context con){
        return PreferenceManager.getDefaultSharedPreferences(con).getBoolean("useEyeglass", false);
    }

}
