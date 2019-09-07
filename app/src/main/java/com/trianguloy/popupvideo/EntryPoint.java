package com.trianguloy.popupvideo;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

/**
 * Activity for youtube links
 */
public class EntryPoint extends Activity {

    private final String FORCE_FULLSCREEN = "force_fullscreen";
    private String FINISH_ON_ENDED = "finish_on_ended";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {

            // vars
            Preferences prefs = new Preferences(this);

            // convert intent
            // https://stackoverflow.com/questions/28145142/what-intent-extra-to-make-youtube-android-app-automatically-close-when-reach-the/31779476
            Intent convertedIntent = new Intent(getIntent());
            convertedIntent.setComponent(null);
            convertedIntent.putExtra(FINISH_ON_ENDED, true);
            if (prefs.getFullScreen()) {
                convertedIntent.putExtra(FORCE_FULLSCREEN, true);
            }
            Log.d("INTENT", convertedIntent.toUri(0));

            // get wanted app
            String app = prefs.getApp();
            if (app != null) {
                // app selected
                try {
                    if (Packages.areWeTheDefault(this) && !getIntent().hasExtra(FINISH_ON_ENDED)) {
                        // we are the first called app, let the user choose between the original app or ours again (for opening with the flag)
                        Intent chooser = Intent.createChooser(new Intent(getIntent()).setComponent(null).setPackage(app), getString(R.string.title_chooser));
                        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{convertedIntent});
                        startActivity(chooser);
                    } else {
                        // not the first called app, just open
                        startActivity(new Intent(convertedIntent).setPackage(app));
                    }
                    return;
                } catch (ActivityNotFoundException ignored) {
                    // can't open the selected app, notify and unselect
                    Toast.makeText(this, getString(R.string.toast_appNotFound, app), Toast.LENGTH_LONG).show();
                    prefs.setApp(null);
                }
            }

            // no app selected, or previous failed, show choose
            Intent chooser = Packages.chooserForOtherApps(convertedIntent, this);
            if (chooser == null) {
                // no apps to choosefrom
                Toast.makeText(this, getString(R.string.toast_noapps), Toast.LENGTH_LONG).show();
                return;
            }

            try {
                startActivity(chooser);
            } catch (ActivityNotFoundException e) {
                // can't open chooser? strange
                Toast.makeText(this, getString(R.string.toast_nochooser), Toast.LENGTH_LONG).show();
            }

        } finally {
            // the activity must be finished
            finish();
        }
    }

}
