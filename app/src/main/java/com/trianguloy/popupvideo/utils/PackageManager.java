package com.trianguloy.popupvideo.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.widget.Toast;

import com.trianguloy.popupvideo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilities for packages
 */
public class PackageManager {

    public static final Intent YOUTUBE_INTENT = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=00000000000"));

    /**
     * The info about packages, struct
     */
    static public class PackageInfo {
        public String packageName;
        public String label;

        public PackageInfo(String packageName, String label) {
            this.packageName = packageName;
            this.label = label;
        }

        @Override
        public String toString() {
            // for the main activity spinner
            return label;
        }
    }

    // ------------------- Functions -------------------

    /**
     * Returns a list of all the apps that can open the intent, without including ours
     *
     * @param intent intent used for the query
     * @param all    if true, all apps, if false only default
     * @param cntx   base context
     * @return the list of intents, can be empty
     */
    static public List<PackageInfo> getOtherApps(Intent intent, boolean all, Context cntx) {
        // vars
        android.content.pm.PackageManager pm = cntx.getPackageManager();

        // query activities
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent,
                all
                        ? (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M ? android.content.pm.PackageManager.MATCH_ALL : 0)
                        : android.content.pm.PackageManager.MATCH_DEFAULT_ONLY
        );

        // remove ours (if found), and convert to PackageInfo list
        String ourPackage = cntx.getPackageName();
        List<PackageInfo> filteredList = new ArrayList<>(resolveInfos.size());

        for (ResolveInfo resolveInfo : resolveInfos) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if (activityInfo != null && activityInfo.packageName != null && !activityInfo.packageName.equals(ourPackage)) {
                // a valid app, and not ours, add
                filteredList.add(new PackageInfo(activityInfo.packageName, activityInfo.loadLabel(pm).toString()));
            }
        }
        return filteredList;
    }

    /**
     * Returns a list of intents to open the specific intent for a chooser
     *
     * @param intent intent to open
     * @param cntx   bsae context
     * @return list of intents to use with a chooser
     */
    static public List<Intent> intentsForOtherApps(Intent intent, Context cntx) {
        // get apps
        List<PackageInfo> otherApps = getOtherApps(intent, true, cntx);

        if (otherApps.isEmpty()) {
            // no apps? null
            return null;
        }

        // create intents for the chooser
        List<Intent> intents = new ArrayList<>(otherApps.size());
        for (PackageInfo otherApp : otherApps) {
            intents.add(new Intent(intent).setPackage(otherApp.packageName));
        }

        return intents;
    }

    /**
     * @return true iff we are the default app for the intent that opened us (so we were called directly)
     */
    static public boolean amITheDefault(Activity cntx) {
        // vars
        android.content.pm.PackageManager pm = cntx.getPackageManager();

        ResolveInfo resolveInfo = pm.resolveActivity(new Intent(cntx.getIntent()).setComponent(null), 0);
        return resolveInfo != null
                && resolveInfo.activityInfo != null
                && resolveInfo.activityInfo.packageName != null
                && resolveInfo.activityInfo.packageName.equals(cntx.getPackageName())
                ;
    }

    /**
     * Returns whether we are available for a youtube link
     *
     * @param cntx base context
     * @return true if we are available, false otherwise
     */
    static public boolean amIAvailable(Context cntx) {
        // vars
        android.content.pm.PackageManager pm = cntx.getPackageManager();

        // query activities
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(YOUTUBE_INTENT, android.content.pm.PackageManager.MATCH_DEFAULT_ONLY);

        // find ours
        String ourPackage = cntx.getPackageName();

        for (ResolveInfo resolveInfo : resolveInfos) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if (activityInfo != null && activityInfo.packageName != null && activityInfo.packageName.equals(ourPackage)) {
                return true;
            }
        }
        return false;
    }

    static public PackageInfo isOtherApp(Context cntx) {
        try {
            // vars
            android.content.pm.PackageManager pm = cntx.getPackageManager();

            ResolveInfo resolveInfo = pm.resolveActivity(YOUTUBE_INTENT, android.content.pm.PackageManager.MATCH_DEFAULT_ONLY);
            if (resolveInfo == null) {
                // No activity? strange
                return null;
            }

            ActivityInfo activityInfo = resolveInfo.activityInfo;
            String packageName = activityInfo.packageName;

            if (packageName.equals(cntx.getPackageName())) {
                // We are the default, isn't other app
                return null;
            }

            if (packageName.equals(pm.resolveActivity(Intent.createChooser(YOUTUBE_INTENT, ""), 0).activityInfo.packageName)) {
                // There is no default
                return null;
            }

            // is other app
            return new PackageInfo(packageName, activityInfo.loadLabel(pm).toString());

        } catch (NullPointerException npe) {
            // something was missing? strange
            return null;
        }
    }

    /**
     * Wrapper for startActivity to catch ActivityNotFoundException
     *
     * @param intent intent to open
     * @param string toast shown on ActivityNotFoundException
     * @param cntx   base context
     */
    static public void startActivity(Intent intent, int string, Context cntx) {
        try {
            cntx.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(cntx, string, Toast.LENGTH_SHORT).show();
        }
    }

//            String resolvePackage = pm.resolveActivity(intent, 0).activityInfo.packageName;

//            if(Objects.equals(resolvePackage, getPackageName())){
//                // we are the default app!, do what the user choosed
//                startActivity(whatTheUserChoosed(intent));
//            }else if(Objects.equals(resolvePackage, pm.resolveActivity(Intent.createChooser(new Intent(),""),0).activityInfo.packageName)){
//                // The choose dialog will be shown! so the user choosed us, do what the user choosed
//                startActivity(whatTheUserChoosed(intent));
//            }else{
//                // another activity was the default one, probably that one redirected to us, so do what the user choosed
//                startActivity(whatTheUserChoosed(intent));
//            }

}
