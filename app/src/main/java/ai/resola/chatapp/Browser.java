package ai.resola.chatapp;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import java.lang.ref.WeakReference;

import ai.resola.chatapp.support.customtabs.CustomTabsCallback;
import ai.resola.chatapp.support.customtabs.CustomTabsClient;
import ai.resola.chatapp.support.customtabs.CustomTabsHelper;
import ai.resola.chatapp.support.customtabs.CustomTabsIntent;
import ai.resola.chatapp.support.customtabs.CustomTabsServiceConnection;
import ai.resola.chatapp.support.customtabs.CustomTabsSession;
import ai.resola.chatapp.support.customtabs.ServiceConnection;
import ai.resola.chatapp.support.customtabs.ServiceConnectionCallback;
import ai.resola.chatapp.ui.Theme;

/**
 * Created by dotuan on 2017/03/30.
 */

public class Browser {
    private static WeakReference<CustomTabsSession> customTabsCurrentSession;
    private static CustomTabsClient customTabsClient;
    private static CustomTabsSession customTabsSession;
    private static CustomTabsServiceConnection customTabsServiceConnection;
    private static String customTabsPackageToBind;
    private static WeakReference<Activity> currentCustomTabsActivity;

    private static void setCurrentSession(CustomTabsSession session) {
        customTabsCurrentSession = new WeakReference<>(session);
    }

    private static CustomTabsSession getSession() {
        if (customTabsClient == null) {
            customTabsSession = null;
        } else if (customTabsSession == null) {
            customTabsSession = customTabsClient.newSession(new NavigationCallback());
            setCurrentSession(customTabsSession);
        }
        return customTabsSession;
    }

    public static void bindCustomTabsService(Activity activity) {
        if (Build.VERSION.SDK_INT < 15) {
            return;
        }
        Activity currentActivity = currentCustomTabsActivity == null ? null : currentCustomTabsActivity.get();
        if (currentActivity != null && currentActivity != activity) {
            unbindCustomTabsService(currentActivity);
        }
        if (customTabsClient != null) {
            return;
        }
        currentCustomTabsActivity = new WeakReference<>(activity);
        try {
            if (TextUtils.isEmpty(customTabsPackageToBind)) {
                customTabsPackageToBind = CustomTabsHelper.getPackageNameToUse(activity);
                if (customTabsPackageToBind == null) {
                    return;
                }
            }
            customTabsServiceConnection = new ServiceConnection(new ServiceConnectionCallback() {
                @Override
                public void onServiceConnected(CustomTabsClient client) {
                    customTabsClient = client;
                    if (true) { //MediaController.getInstance().canCustomTabs()
                        if (customTabsClient != null) {
                            try {
                                customTabsClient.warmup(0);
                            } catch (Exception e) {
                                //FileLog.e("tmessages", e);
                            }
                        }
                    }
                }

                @Override
                public void onServiceDisconnected() {
                    customTabsClient = null;
                }
            });
            if (!CustomTabsClient.bindCustomTabsService(activity, customTabsPackageToBind, customTabsServiceConnection)) {
                customTabsServiceConnection = null;
            }
        } catch (Exception e) {
            //FileLog.e("tmessages", e);
        }
    }

    public static void unbindCustomTabsService(Activity activity) {
        if (Build.VERSION.SDK_INT < 15 || customTabsServiceConnection == null) {
            return;
        }
        Activity currentActivity = currentCustomTabsActivity == null ? null : currentCustomTabsActivity.get();
        if (currentActivity == activity) {
            currentCustomTabsActivity.clear();
        }
        try {
            activity.unbindService(customTabsServiceConnection);
        } catch (Exception e) {
            //FileLog.e("tmessages", e);
        }
        customTabsClient = null;
        customTabsSession = null;
    }

    private static class NavigationCallback extends CustomTabsCallback {
        @Override
        public void onNavigationEvent(int navigationEvent, Bundle extras) {
            //FileLog.e("tmessages", "code = " + navigationEvent + " extras " + extras);
        }
    }

    public static void openUrl(Context context, String url, boolean allowCustom) {
        if (context == null || url == null) {
            return;
        }
        openUrl(context, Uri.parse(url), allowCustom);
    }

    public static void openUrl(Context context, Uri uri, boolean allowCustom) {
        if (context == null || uri == null) {
            return;
        }
        try {
            String scheme = uri.getScheme() != null ? uri.getScheme().toLowerCase() : "";
            Log.d("OPENURL", "Build.VERSION.SDK_INT: " + Build.VERSION.SDK_INT + ", allow:" + allowCustom + ", scheme: " + scheme);
            if (Build.VERSION.SDK_INT >= 15 && allowCustom && !scheme.equals("tel")) {
                Intent share = new Intent(ApplicationLoader.applicationContext, ShareBroadcastReceiver.class);
                share.setAction(Intent.ACTION_SEND);

                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder(getSession());
                builder.setToolbarColor(Theme.ACTION_BAR_COLOR);
                builder.setShowTitle(true);
                Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.abc_ic_menu_share_mtrl_alpha);
                PendingIntent pi = PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 0, share, 0);
                builder.setActionButton(icon, "Share", pi, false);
                CustomTabsIntent intent = builder.build();
                intent.launchUrl((Activity) context, uri);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.putExtra(android.provider.Browser.EXTRA_APPLICATION_ID, context.getPackageName());
                context.startActivity(intent);
            }
        } catch (Exception e) {
            //FileLog.e("tmessages", e);
            Log.e("ERROR URL", e.toString());
        }
    }
}
