package com.luzi82.screenstream.screenstream;


import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class MainActivity extends PreferenceActivity {

    public ActivityRuntime activityRuntime;

    MainServiceConn serviceConn;

//    ScreenshotManager screenshotManager;

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
//    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
//        @Override
//        public boolean onPreferenceChange(Preference preference, Object value) {
//            String stringValue = value.toString();
//
//            if (preference instanceof ListPreference) {
//                // For list preferences, look up the correct display value in
//                // the preference's 'entries' list.
//                ListPreference listPreference = (ListPreference) preference;
//                int index = listPreference.findIndexOfValue(stringValue);
//
//                // Set the summary to reflect the new value.
//                preference.setSummary(
//                        index >= 0
//                                ? listPreference.getEntries()[index]
//                                : null);
//
//            } else if (preference instanceof RingtonePreference) {
//                // For ringtone preferences, look up the correct display value
//                // using RingtoneManager.
//                if (TextUtils.isEmpty(stringValue)) {
//                    // Empty values correspond to 'silent' (no ringtone).
//                    preference.setSummary(R.string.pref_ringtone_silent);
//
//                } else {
//                    Ringtone ringtone = RingtoneManager.getRingtone(
//                            preference.getContext(), Uri.parse(stringValue));
//
//                    if (ringtone == null) {
//                        // Clear the summary if there was a lookup error.
//                        preference.setSummary(null);
//                    } else {
//                        // Set the summary to reflect the new ringtone display
//                        // name.
//                        String name = ringtone.getTitle(preference.getContext());
//                        preference.setSummary(name);
//                    }
//                }
//
//            } else {
//                // For all other preferences, set the summary to the value's
//                // simple string representation.
//                preference.setSummary(stringValue);
//            }
//            return true;
//        }
//    };
//
//    /**
//     * Helper method to determine if the device has an extra-large screen. For
//     * example, 10" tablets are extra-large.
//     */
//    private static boolean isXLargeTablet(Context context) {
//        return (context.getResources().getConfiguration().screenLayout
//                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
//    }
//
//    /**
//     * Binds a preference's summary to its value. More specifically, when the
//     * preference's value is changed, its summary (line of text below the
//     * preference title) is updated to reflect the value. The summary is also
//     * immediately updated upon calling this method. The exact display format is
//     * dependent on the type of preference.
//     *
//     * @see #sBindPreferenceSummaryToValueListener
//     */
//    private static void bindPreferenceSummaryToValue(Preference preference) {
//        // Set the listener to watch for value changes.
//        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
//
//        // Trigger the listener immediately with the preference's
//        // current value.
//        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
//                PreferenceManager
//                        .getDefaultSharedPreferences(preference.getContext())
//                        .getString(preference.getKey(), ""));
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"EGDXSXRZ MainActivity.onCreate");
        super.onCreate(savedInstanceState);
//        setupActionBar();

//        if(screenshotManager==null) {
//            screenshotManager = new ScreenshotManager(this, scheduledExecutorService);
//            screenshotManager.start();
//        }
//        PreferenceManager.setDefaultValues(this,getPackageName(),MODE_PRIVATE,R.id.);

    }

    @Override
    protected void onStart() {
        Log.d(TAG,"OHIWHYPG MainActivity.onStart");
        super.onStart();
        if(activityRuntime ==null){
            activityRuntime =new ActivityRuntime(this);
        }
        {
            Intent intent = new Intent(this, MainService.class);
            startService(intent);
        }
        if(serviceConn==null) {
            serviceConn = new MainServiceConn();
            Intent intent = new Intent(this, MainService.class);
            bindService(intent, serviceConn, Context.BIND_AUTO_CREATE);
        }
    }

    //    /**
//     * Set up the {@link android.app.ActionBar}, if the API is available.
//     */
//    private void setupActionBar() {
//        ActionBar actionBar = getActionBar();
//        if (actionBar != null) {
//            // Show the Up button in the action bar.
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
//    }

//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public boolean onIsMultiPane() {
//        return isXLargeTablet(this);
//    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
//        return PreferenceFragment.class.getName().equals(fragmentName)
//                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
//                || DataSyncPreferenceFragment.class.getName().equals(fragmentName)
//                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
        return MainFragment.class.getName().equals(fragmentName);
    }

//    /**
//     * This fragment shows general preferences only. It is used when the
//     * context is showing a two-pane settings UI.
//     */
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public static class GeneralPreferenceFragment extends PreferenceFragment {
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            addPreferencesFromResource(R.xml.pref_general);
//            setHasOptionsMenu(true);
//
//            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
//            // to their values. When their values change, their summaries are
//            // updated to reflect the new value, per the Android Design
//            // guidelines.
//            bindPreferenceSummaryToValue(findPreference("example_text"));
//            bindPreferenceSummaryToValue(findPreference("example_list"));
//        }
//
//        @Override
//        public boolean onOptionsItemSelected(MenuItem item) {
//            int id = item.getItemId();
//            if (id == android.R.id.home) {
//                startActivity(new Intent(getActivity(), MainActivity.class));
//                return true;
//            }
//            return super.onOptionsItemSelected(item);
//        }
//    }
//
//    /**
//     * This fragment shows notification preferences only. It is used when the
//     * context is showing a two-pane settings UI.
//     */
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public static class NotificationPreferenceFragment extends PreferenceFragment {
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            addPreferencesFromResource(R.xml.pref_notification);
//            setHasOptionsMenu(true);
//
//            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
//            // to their values. When their values change, their summaries are
//            // updated to reflect the new value, per the Android Design
//            // guidelines.
//            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
//        }
//
//        @Override
//        public boolean onOptionsItemSelected(MenuItem item) {
//            int id = item.getItemId();
//            if (id == android.R.id.home) {
//                startActivity(new Intent(getActivity(), MainActivity.class));
//                return true;
//            }
//            return super.onOptionsItemSelected(item);
//        }
//    }
//
//    /**
//     * This fragment shows data and sync preferences only. It is used when the
//     * context is showing a two-pane settings UI.
//     */
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public static class DataSyncPreferenceFragment extends PreferenceFragment {
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            addPreferencesFromResource(R.xml.pref_data_sync);
//            setHasOptionsMenu(true);
//
//            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
//            // to their values. When their values change, their summaries are
//            // updated to reflect the new value, per the Android Design
//            // guidelines.
//            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
//        }
//
//        @Override
//        public boolean onOptionsItemSelected(MenuItem item) {
//            int id = item.getItemId();
//            if (id == android.R.id.home) {
//                startActivity(new Intent(getActivity(), MainActivity.class));
//                return true;
//            }
//            return super.onOptionsItemSelected(item);
//        }
//    }

//    public interface ActivityResultListener{
//        void onActivityResult(int requestCode, int resultCode, Intent data);
//    }
//    WeakReference<ActivityResultListener> activityResultListenerWeakReference;
//
//    public void setActivityResultListener(ActivityResultListener activityResultListener){
//        activityResultListenerWeakReference=new WeakReference<ActivityResultListener>(activityResultListener);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            serviceConn.mainService.serviceRuntime.onActivityResult(requestCode,resultCode,data);
        }
//        if(activityResultListenerWeakReference!=null){
//            ActivityResultListener arl=activityResultListenerWeakReference.get();
//            if(arl!=null){
//                arl.onActivityResult(requestCode,resultCode,data);
//            }
//            activityResultListenerWeakReference = null;
//        }
//        screenshotManager.onActivityResult(requestCode,resultCode,data);
    }

    /**
     * Created by luzi82 on 16年3月28日.
     */
    public static class MainFragment extends PreferenceFragment {

        Preference socket_enable_preference;
        Preference socket_port_preference;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);

            socket_enable_preference=findPreference("socket_enable");
            socket_port_preference=findPreference("socket_port");
        }

    //    @Override
    //    public void onConfigurationChanged(Configuration newConfig) {
    //        super.onConfigurationChanged(newConfig);
    //        Log.d(TAG, "JJDUJSXA onConfigurationChanged");
    //    }


    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"OYEDQLPH MainActivity.onStop");
        if(activityRuntime !=null){
            activityRuntime.release();
        }
        activityRuntime =null;
        if(serviceConn!=null){
            unbindService(serviceConn);
        }
        serviceConn=null;
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG,"BARFMQQW MainActivity.onDestroy");
        super.onDestroy();
    }

    private static final String TAG = MainActivity.class.getName();

    class MainServiceConn extends MainService.Conn{

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            super.onServiceConnected(name, service);
            mainService.serviceRuntime.preparePermissionIntent(MainActivity.this);
        }
    }

}
