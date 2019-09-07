package com.trianguloy.popupvideo;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilities for packages
 */
public class Packages {

    public static final Intent YOUTUBE_INTENT = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=00000000000"));

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
        PackageManager pm = cntx.getPackageManager();

        // query activities
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent,
                all
                        ? (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M ? PackageManager.MATCH_ALL : 0)
                        : PackageManager.MATCH_DEFAULT_ONLY
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
     * Returns the intent chooser for apps except ours.
     *
     * @param intent intent to open when the apps are clicked, used for query too
     * @param cntx   base context
     * @return the intent chooser, or null if no apps can open the intent
     */
    static public Intent chooserForOtherApps(Intent intent, Context cntx) {

        // get apps
        List<PackageInfo> otherApps = getOtherApps(intent, true, cntx);

        if (otherApps.isEmpty()) {
            // no apps? null
            return null;
        }

        // create intent for the chooser
        List<Intent> intents = new ArrayList<>(otherApps.size());
        for (PackageInfo otherApp : otherApps) {
            intents.add(new Intent(intent).setPackage(otherApp.packageName));
        }

        // create chooser
        Intent chooserIntent = Intent.createChooser( intents.get(0), cntx.getString(R.string.title_chooser));
        intents.remove(0);
        chooserIntent.putExtra( Intent.EXTRA_INITIAL_INTENTS, intents.toArray(new Parcelable[0]));

        // return
        return chooserIntent;
    }

    /**
     * @return true iff we are the default app for youtube links
     */
    static public boolean areWeTheDefault(Activity cntx){
        // vars
        PackageManager pm = cntx.getPackageManager();

        ResolveInfo resolveInfo = pm.resolveActivity(new Intent(cntx.getIntent()).setComponent(null), 0);
        return resolveInfo != null
                && resolveInfo.activityInfo != null
                && resolveInfo.activityInfo.packageName != null
                && resolveInfo.activityInfo.packageName.equals(cntx.getPackageName())
                ;
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
