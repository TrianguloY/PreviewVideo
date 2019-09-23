package com.trianguloy.popupvideo.activities.entry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.trianguloy.popupvideo.R;
import com.trianguloy.popupvideo.utils.PackageManager;
import com.trianguloy.popupvideo.utils.Preferences;

import java.util.List;

/**
 * Activity for youtube links
 */
public class EntryPoint extends Activity {

    static private final String FORCE_FULLSCREEN = "force_fullscreen";
    static private final String FINISH_ON_ENDED = "finish_on_ended";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            run();
        } catch (Throwable e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.toast_error), Toast.LENGTH_SHORT).show();
        } finally {
            // the activity must be finished
            finish();
        }
    }

    private void run() {
        // vars
        Preferences prefs = new Preferences(this);

        // convert intent
        // https://stackoverflow.com/questions/28145142/what-intent-extra-to-make-youtube-android-app-automatically-close-when-reach-the/31779476
        Intent convertedIntent = getOpenedIntent();
        convertedIntent.putExtra(FINISH_ON_ENDED, true);
        if (prefs.getFullScreen()) {
            convertedIntent.putExtra(FORCE_FULLSCREEN, true);
        }
        Log.d("INTENT", convertedIntent.toUri(0));

        // get app
        String app = prefs.getApp();
        if (!app.isEmpty() && getPackageManager().resolveActivity(new Intent(convertedIntent).setPackage(app), 0) == null) {
            // the selected app is invalid, remove
            Toast.makeText(this, getString(R.string.toast_appNotFound, app), Toast.LENGTH_LONG).show();
            prefs.setApp("");
            app = "";
        }

        //logic
        if (PackageManager.amITheDefault(this) && !getIntent().hasExtra(FINISH_ON_ENDED)) {
            // we are the default app (and first time opened), let the user choose open as preview or not

            Intent chooser = Intent.createChooser(new Intent(convertedIntent).setPackage(getPackageName()), getString(R.string.title_chooser));

            if (!app.isEmpty()) {
                // an app was chosen, show that only

                PackageManager.startActivity(
                        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{getOpenedIntent().setPackage(app)}),
                        R.string.toast_error,
                        this
                );

            } else {
                // no app selected, show choose for all

                List<Intent> intents = PackageManager.intentsForOtherApps(getOpenedIntent(), this);

                if (intents == null) {
                    // no apps to choose from
                    Toast.makeText(this, getString(R.string.toast_noApps), Toast.LENGTH_LONG).show();
                    return;
                }

                PackageManager.startActivity(
                        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents.toArray(new Parcelable[0])),
                        R.string.toast_noChooser,
                        this
                );

            }

        } else {
            // we are not the default app (or second time the user chooses), open the user selection

            if (!app.isEmpty()) {
                // an app was chosen, open that

                PackageManager.startActivity(
                        new Intent(convertedIntent).setPackage(app),
                        R.string.toast_error,
                        this
                );

            } else {
                // no app selected, show choose for all

                List<Intent> intents = PackageManager.intentsForOtherApps(convertedIntent, this);

                if (intents == null) {
                    // no apps to choose from
                    Toast.makeText(this, getString(R.string.toast_noApps), Toast.LENGTH_LONG).show();
                    return;
                }

                // create chooser
                Intent chooser = Intent.createChooser(intents.remove(0), getString(R.string.title_chooser));
                chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents.toArray(new Parcelable[0]));

                PackageManager.startActivity(chooser, R.string.toast_noChooser, this);

            }

        }

    }

    private Intent getOpenedIntent(){
        return new Intent(getIntent()).setComponent(null).setPackage(null);
    }

}
