package ai.resola.chatapp.support.customtabs;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.BundleCompat;

import java.util.ArrayList;

/**
 * Created by dotuan on 2017/03/30.
 */

public class CustomTabsIntent {

    @NonNull
    public final Intent intent;
    @Nullable
    public final Bundle startAnimationBundle;

    public void launchUrl(Activity context, Uri url) {
        this.intent.setData(url);
        ActivityCompat.startActivity(context, this.intent, this.startAnimationBundle);
    }

    private CustomTabsIntent(Intent intent, Bundle startAnimationBundle) {
        this.intent = intent;
        this.startAnimationBundle = startAnimationBundle;
    }

    public static final class Builder {
        private final Intent mIntent;
        private ArrayList<Bundle> mMenuItems;
        private Bundle mStartAnimationBundle;
        private ArrayList<Bundle> mActionButtons;

        public Builder() {
            this(null);
        }

        public Builder(@Nullable CustomTabsSession session) {
            this.mIntent = new Intent("android.intent.action.VIEW");
            this.mMenuItems = null;
            this.mStartAnimationBundle = null;
            this.mActionButtons = null;
            if (session != null) {
                this.mIntent.setPackage(session.getComponentName().getPackageName());
            }

            Bundle bundle = new Bundle();
            BundleCompat.putBinder(bundle, "android.support.customtabs.extra.SESSION", session == null ? null : session.getBinder());
            this.mIntent.putExtras(bundle);
        }

        public CustomTabsIntent.Builder setToolbarColor(@ColorInt int color) {
            this.mIntent.putExtra("android.support.customtabs.extra.TOOLBAR_COLOR", color);
            return this;
        }

        public CustomTabsIntent.Builder setShowTitle(boolean showTitle) {
            this.mIntent.putExtra("android.support.customtabs.extra.TITLE_VISIBILITY", showTitle ? 1 : 0);
            return this;
        }

        public CustomTabsIntent.Builder setActionButton(@NonNull Bitmap icon, @NonNull String description, @NonNull PendingIntent pendingIntent, boolean shouldTint) {
            Bundle bundle = new Bundle();
            bundle.putInt("android.support.customtabs.customaction.ID", 0);
            bundle.putParcelable("android.support.customtabs.customaction.ICON", icon);
            bundle.putString("android.support.customtabs.customaction.DESCRIPTION", description);
            bundle.putParcelable("android.support.customtabs.customaction.PENDING_INTENT", pendingIntent);
            this.mIntent.putExtra("android.support.customtabs.extra.ACTION_BUTTON_BUNDLE", bundle);
            this.mIntent.putExtra("android.support.customtabs.extra.TINT_ACTION_BUTTON", shouldTint);
            return this;
        }

        public CustomTabsIntent build() {
            if (this.mMenuItems != null) {
                this.mIntent.putParcelableArrayListExtra("android.support.customtabs.extra.MENU_ITEMS", this.mMenuItems);
            }

            if (this.mActionButtons != null) {
                this.mIntent.putParcelableArrayListExtra("android.support.customtabs.extra.TOOLBAR_ITEMS", this.mActionButtons);
            }

            return new CustomTabsIntent(this.mIntent, this.mStartAnimationBundle);
        }
    }
}
