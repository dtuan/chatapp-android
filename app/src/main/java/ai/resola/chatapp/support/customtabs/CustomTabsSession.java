package ai.resola.chatapp.support.customtabs;

import android.content.ComponentName;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by dotuan on 2017/03/30.
 */

public class CustomTabsSession {
    private static final String TAG = "CustomTabsSession";
    private final ICustomTabsService mService;
    private final ICustomTabsCallback mCallback;
    private final ComponentName mComponentName;

    CustomTabsSession(ICustomTabsService service, ICustomTabsCallback callback, ComponentName componentName) {
        this.mService = service;
        this.mCallback = callback;
        this.mComponentName = componentName;
    }

    public boolean mayLaunchUrl(Uri url, Bundle extras, List<Bundle> otherLikelyBundles) {
        try {
            return this.mService.mayLaunchUrl(this.mCallback, url, extras, otherLikelyBundles);
        } catch (RemoteException var5) {
            return false;
        }
    }

    public boolean setActionButton(@NonNull Bitmap icon, @NonNull String description) {
        return this.setToolbarItem(0, icon, description);
    }

    public boolean setToolbarItem(int id, @NonNull Bitmap icon, @NonNull String description) {
        Bundle bundle = new Bundle();
        bundle.putInt("android.support.customtabs.customaction.ID", id);
        bundle.putParcelable("android.support.customtabs.customaction.ICON", icon);
        bundle.putString("android.support.customtabs.customaction.DESCRIPTION", description);
        Bundle metaBundle = new Bundle();
        metaBundle.putBundle("android.support.customtabs.extra.ACTION_BUTTON_BUNDLE", bundle);

        try {
            return this.mService.updateVisuals(this.mCallback, metaBundle);
        } catch (RemoteException var7) {
            return false;
        }
    }

    IBinder getBinder() {
        return this.mCallback.asBinder();
    }

    ComponentName getComponentName() {
        return this.mComponentName;
    }
}
